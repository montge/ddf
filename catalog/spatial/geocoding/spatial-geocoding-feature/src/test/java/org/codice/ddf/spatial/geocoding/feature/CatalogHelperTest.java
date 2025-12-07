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
package org.codice.ddf.spatial.geocoding.feature;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.Query;
import org.junit.Before;
import org.junit.Test;
import org.opengis.filter.Filter;

/** Unit tests for CatalogHelper class. */
public class CatalogHelperTest {

  private CatalogHelper catalogHelper;

  private FilterBuilder filterBuilder;

  @Before
  public void setUp() {
    filterBuilder = new GeotoolsFilterBuilder();
    catalogHelper = new CatalogHelper(filterBuilder);
  }

  @Test
  public void testConstructorWithFilterBuilder() {
    CatalogHelper helper = new CatalogHelper(filterBuilder);
    assertThat(helper, is(notNullValue()));
  }

  @Test
  public void testGetQueryForAllCountries() {
    Query query = catalogHelper.getQueryForAllCountries();

    assertThat(query, is(notNullValue()));
    assertThat(query.getFilter(), is(notNullValue()));
  }

  @Test
  public void testGetQueryForAllCountriesReturnsValidFilter() {
    Query query = catalogHelper.getQueryForAllCountries();

    Filter filter = query.getFilter();
    assertThat(filter, is(notNullValue()));
  }

  @Test
  public void testGetQueryForName() {
    String countryCode = "USA";
    Query query = catalogHelper.getQueryForName(countryCode);

    assertThat(query, is(notNullValue()));
    assertThat(query.getFilter(), is(notNullValue()));
  }

  @Test
  public void testGetQueryForNameWithValidCountryCode() {
    String countryCode = "GBR";
    Query query = catalogHelper.getQueryForName(countryCode);

    assertThat(query, is(notNullValue()));
    Filter filter = query.getFilter();
    assertThat(filter, is(notNullValue()));
  }

  @Test
  public void testGetQueryForNameWithDifferentCountryCodes() {
    String[] countryCodes = {"USA", "GBR", "CAN", "FRA", "DEU", "JPN", "CHN", "AUS"};

    for (String countryCode : countryCodes) {
      Query query = catalogHelper.getQueryForName(countryCode);
      assertThat(query, is(notNullValue()));
      assertThat(query.getFilter(), is(notNullValue()));
    }
  }

  @Test
  public void testGetQueryForNameWithLowercaseCountryCode() {
    String countryCode = "usa";
    Query query = catalogHelper.getQueryForName(countryCode);

    assertThat(query, is(notNullValue()));
    assertThat(query.getFilter(), is(notNullValue()));
  }

  @Test
  public void testGetQueryForNameWithEmptyString() {
    String countryCode = "";
    Query query = catalogHelper.getQueryForName(countryCode);

    assertThat(query, is(notNullValue()));
    assertThat(query.getFilter(), is(notNullValue()));
  }

  @Test
  public void testGetQueryForNameWithNull() {
    Query query = catalogHelper.getQueryForName(null);

    assertThat(query, is(notNullValue()));
    assertThat(query.getFilter(), is(notNullValue()));
  }

  @Test
  public void testGetQueryForNameWithNumericCountryCode() {
    String countryCode = "123";
    Query query = catalogHelper.getQueryForName(countryCode);

    assertThat(query, is(notNullValue()));
    assertThat(query.getFilter(), is(notNullValue()));
  }

  @Test
  public void testGetQueryForNameWithSpecialCharacters() {
    String countryCode = "U$A";
    Query query = catalogHelper.getQueryForName(countryCode);

    assertThat(query, is(notNullValue()));
    assertThat(query.getFilter(), is(notNullValue()));
  }

  @Test
  public void testMultipleCallsToGetQueryForAllCountries() {
    Query query1 = catalogHelper.getQueryForAllCountries();
    Query query2 = catalogHelper.getQueryForAllCountries();

    assertThat(query1, is(notNullValue()));
    assertThat(query2, is(notNullValue()));
  }

  @Test
  public void testMultipleCallsToGetQueryForNameWithSameCountryCode() {
    String countryCode = "USA";
    Query query1 = catalogHelper.getQueryForName(countryCode);
    Query query2 = catalogHelper.getQueryForName(countryCode);

    assertThat(query1, is(notNullValue()));
    assertThat(query2, is(notNullValue()));
  }

  @Test
  public void testMultipleCallsToGetQueryForNameWithDifferentCountryCodes() {
    Query query1 = catalogHelper.getQueryForName("USA");
    Query query2 = catalogHelper.getQueryForName("GBR");
    Query query3 = catalogHelper.getQueryForName("CAN");

    assertThat(query1, is(notNullValue()));
    assertThat(query2, is(notNullValue()));
    assertThat(query3, is(notNullValue()));
  }

  @Test
  public void testCatalogHelperWithDifferentFilterBuilders() {
    FilterBuilder builder1 = new GeotoolsFilterBuilder();
    FilterBuilder builder2 = new GeotoolsFilterBuilder();

    CatalogHelper helper1 = new CatalogHelper(builder1);
    CatalogHelper helper2 = new CatalogHelper(builder2);

    assertThat(helper1, is(notNullValue()));
    assertThat(helper2, is(notNullValue()));

    Query query1 = helper1.getQueryForAllCountries();
    Query query2 = helper2.getQueryForAllCountries();

    assertThat(query1, is(notNullValue()));
    assertThat(query2, is(notNullValue()));
  }
}
