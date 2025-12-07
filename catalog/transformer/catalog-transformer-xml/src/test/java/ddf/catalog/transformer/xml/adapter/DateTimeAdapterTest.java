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
package ddf.catalog.transformer.xml.adapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.transformer.xml.binding.DateTimeElement;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Before;
import org.junit.Test;

public class DateTimeAdapterTest {

  private DateTimeAdapter adapter;

  private DatatypeFactory datatypeFactory;

  @Before
  public void setUp() throws Exception {
    adapter = new DateTimeAdapter();
    datatypeFactory = DatatypeFactory.newInstance();
  }

  @Test
  public void testMarshalDateAttribute() throws Exception {
    Date testDate = new Date();
    Attribute attribute = new AttributeImpl("created", testDate);

    DateTimeElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("created"));
    assertThat(element.getValue().size(), is(1));

    XMLGregorianCalendar xmlCal = element.getValue().get(0);
    Date resultDate = xmlCal.toGregorianCalendar().getTime();

    // Dates should be equal within a second (accounting for millisecond precision)
    assertThat(Math.abs(resultDate.getTime() - testDate.getTime()) < 1000, is(true));
  }

  @Test
  public void testUnmarshalDateTimeElement() throws Exception {
    DateTimeElement element = new DateTimeElement();
    element.setName("modified");

    GregorianCalendar cal = new GregorianCalendar(2023, 5, 15, 10, 30, 45);
    XMLGregorianCalendar xmlCal = datatypeFactory.newXMLGregorianCalendar(cal);
    element.getValue().add(xmlCal);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("modified"));
    assertThat(attribute.getValue(), is(notNullValue()));
    assertThat(attribute.getValue() instanceof Date, is(true));

    Date resultDate = (Date) attribute.getValue();
    assertThat(resultDate.getTime(), is(cal.getTimeInMillis()));
  }

  @Test
  public void testMarshalMultipleDates() throws Exception {
    Date date1 = new Date(1000000000000L);
    Date date2 = new Date(2000000000000L);

    AttributeImpl attribute = new AttributeImpl("dates", date1);
    attribute.addValue(date2);

    DateTimeElement element = adapter.marshal(attribute);

    assertThat(element.getValue().size(), is(2));

    Date result1 = element.getValue().get(0).toGregorianCalendar().getTime();
    Date result2 = element.getValue().get(1).toGregorianCalendar().getTime();

    assertThat(result1.getTime(), is(date1.getTime()));
    assertThat(result2.getTime(), is(date2.getTime()));
  }

  @Test
  public void testUnmarshalMultipleDates() throws Exception {
    DateTimeElement element = new DateTimeElement();
    element.setName("dates");

    GregorianCalendar cal1 = new GregorianCalendar(2020, 0, 1);
    GregorianCalendar cal2 = new GregorianCalendar(2021, 0, 1);

    element.getValue().add(datatypeFactory.newXMLGregorianCalendar(cal1));
    element.getValue().add(datatypeFactory.newXMLGregorianCalendar(cal2));

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute.getValues().size(), is(2));
  }

  @Test
  public void testRoundTrip() throws Exception {
    Date originalDate = new Date();
    Attribute originalAttribute = new AttributeImpl("timestamp", originalDate);

    DateTimeElement element = adapter.marshal(originalAttribute);
    Attribute resultAttribute = adapter.unmarshal(element);

    assertThat(resultAttribute.getName(), is(originalAttribute.getName()));
    assertThat(resultAttribute.getValue(), is(notNullValue()));

    // Verify the date is preserved (within millisecond precision)
    Date resultDate = (Date) resultAttribute.getValue();
    assertThat(Math.abs(resultDate.getTime() - originalDate.getTime()) < 1000, is(true));
  }

  @Test
  public void testMarshalFromStaticMethod() throws Exception {
    Date testDate = new Date();
    Attribute attribute = new AttributeImpl("effectiveDate", testDate);

    DateTimeElement element = DateTimeAdapter.marshalFrom(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("effectiveDate"));
    assertThat(element.getValue().size(), is(1));
  }

  @Test
  public void testUnmarshalFromStaticMethod() throws Exception {
    DateTimeElement element = new DateTimeElement();
    element.setName("expirationDate");

    GregorianCalendar cal = new GregorianCalendar(2025, 11, 31);
    XMLGregorianCalendar xmlCal = datatypeFactory.newXMLGregorianCalendar(cal);
    element.getValue().add(xmlCal);

    Attribute attribute = DateTimeAdapter.unmarshalFrom(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("expirationDate"));
  }

  @Test
  public void testMarshalWithNonDateValue() {
    // Create attribute with a non-Date value (should be skipped)
    AttributeImpl attribute = new AttributeImpl("mixed", "not a date");
    attribute.addValue(new Date());

    DateTimeElement element = adapter.marshal(attribute);

    // Should only contain the actual Date value
    assertThat(element.getValue().size(), is(1));
  }

  @Test
  public void testMarshalPrecisionPreservation() throws Exception {
    // Test that millisecond precision is preserved
    Date preciseDate = new Date(1234567890123L);
    Attribute attribute = new AttributeImpl("precise", preciseDate);

    DateTimeElement element = adapter.marshal(attribute);
    Attribute result = adapter.unmarshal(element);

    Date resultDate = (Date) result.getValue();
    assertThat(resultDate.getTime(), is(preciseDate.getTime()));
  }
}
