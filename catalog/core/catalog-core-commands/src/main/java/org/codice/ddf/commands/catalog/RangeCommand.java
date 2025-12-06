/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.commands.catalog;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.types.Core;
import ddf.catalog.filter.impl.SortByImpl;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.operation.impl.QueryImpl;
import ddf.catalog.operation.impl.QueryRequestImpl;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.Row;
import org.apache.karaf.shell.support.table.ShellTable;
import org.joda.time.DateTime;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortOrder;

@Service
@Command(
    scope = CatalogCommands.NAMESPACE,
    name = "range",
    description = "Searches by the given date range arguments (exclusively).")
public class RangeCommand extends CatalogCommands {

  private static final int MAX_LENGTH = 40;

  private static final int MAX_RESULTS = 30;

  private static final String ID = "ID ";

  private static final String TITLE = "Title ";

  private static final String NUMBER = "#";

  private static final String DATE_FORMAT = "MM-dd-yyyy";

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

  @Argument(
      name = "ATTRIBUTE_NAME",
      description =
          "The attribute to query on. Valid values are \""
              + Core.MODIFIED
              + "\", \""
              + Core.CREATED
              + "\", \""
              + Metacard.EFFECTIVE
              + "\", and"
              + " \""
              + Core.EXPIRATION
              + "\".",
      index = 0,
      multiValued = false,
      required = true)
  String attributeName = Core.MODIFIED;

  @Argument(
      name = "RANGE_BEGINNING",
      description =
          "The first parameter can be a Date or an asterisk (*). Dates should be formatted as MM-dd-yyyy such as 01-23-2009.",
      index = 1,
      multiValued = false,
      required = true)
  String rangeBeginning = null;

  @Argument(
      name = "RANGE_END",
      description =
          "The second parameter can be a Date or an asterisk (*). Dates should be formatted as MM-dd-yyyy such as 01-23-2009.",
      index = 2,
      multiValued = false,
      required = true)
  String rangeEnd = null;

  @Override
  protected Object executeWithSubject() throws Exception {
    Filter filter;

    Date wayInTheFuture = new DateTime().plusYears(5000).toDate();
    Date endDate = wayInTheFuture;

    if (WILDCARD.equals(rangeBeginning) && WILDCARD.equals(rangeEnd)) {
      filter = filterBuilder.attribute(attributeName).before().date(endDate);
    } else if (WILDCARD.equals(rangeBeginning) && !WILDCARD.equals(rangeEnd)) {
      try {
        LocalDate localDate = LocalDate.parse(rangeEnd, DATE_FORMATTER);
        endDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
      } catch (DateTimeParseException e) {
        throw new InterruptedException("Could not parse second parameter [" + rangeEnd + "]");
      }
      filter = filterBuilder.attribute(attributeName).before().date(endDate);
    } else if (!WILDCARD.equals(rangeBeginning) && WILDCARD.equals(rangeEnd)) {
      try {
        LocalDate localDate = LocalDate.parse(rangeBeginning, DATE_FORMATTER);
        Date startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        filter = filterBuilder.attribute(attributeName).during().dates(startDate, endDate);
      } catch (DateTimeParseException e) {
        throw new InterruptedException("Could not parse first parameter [" + rangeBeginning + "]");
      }
    } else {
      try {
        LocalDate startLocalDate = LocalDate.parse(rangeBeginning, DATE_FORMATTER);
        LocalDate endLocalDate = LocalDate.parse(rangeEnd, DATE_FORMATTER);
        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        filter = filterBuilder.attribute(attributeName).during().dates(startDate, endDate);
      } catch (DateTimeParseException e) {
        throw new InterruptedException("Could not parse date parameters.");
      }
    }

    QueryImpl query = new QueryImpl(filter);

    query.setPageSize(MAX_RESULTS);

    query.setSortBy(new SortByImpl(attributeName, SortOrder.DESCENDING.name()));

    QueryRequest queryRequest = new QueryRequestImpl(query);

    SourceResponse response = getCatalog().query(queryRequest);

    List<Result> results = response.getResults();

    final ShellTable table = new ShellTable();
    table.column(NUMBER);
    table.column(ID);
    table.column(attributeName);
    table.column(TITLE).maxSize(MAX_LENGTH);
    table.emptyTableText("No results");

    int i = 1;
    for (Result result : results) {
      Attribute attribute = result.getMetacard().getAttribute(attributeName);
      if (attribute != null && attribute.getValue() != null) {
        String returnedDate = new DateTime(attribute.getValue()).toString(DATETIME_FORMATTER);
        String title = result.getMetacard().getTitle();

        final Row row = table.addRow();
        row.addContent(i, result.getMetacard().getId(), returnedDate, title);
      }

      i++;
    }

    table.print(console, true);

    return null;
  }
}
