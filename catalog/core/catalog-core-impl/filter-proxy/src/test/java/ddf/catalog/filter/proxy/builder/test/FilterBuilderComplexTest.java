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
package ddf.catalog.filter.proxy.builder.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import ddf.catalog.data.Metacard;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.geotools.api.filter.And;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterVisitor;
import org.geotools.api.filter.Not;
import org.geotools.api.filter.Or;
import org.geotools.api.filter.PropertyIsEqualTo;
import org.geotools.api.filter.PropertyIsGreaterThan;
import org.geotools.api.filter.PropertyIsLike;
import org.geotools.api.filter.spatial.DWithin;
import org.geotools.api.filter.spatial.Intersects;
import org.geotools.api.filter.temporal.During;
import org.geotools.filter.visitor.DefaultFilterVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

/** Comprehensive tests for complex filter combinations (AND, OR, NOT with multiple criteria) */
@ExtendWith(MockitoExtension.class)
public class FilterBuilderComplexTest {

  private static final String TITLE_ATTRIBUTE = "title";
  private static final String CREATED_ATTRIBUTE = "created";
  private static final String PRICE_ATTRIBUTE = "price";
  private static final String WKT_POINT = "POINT (10 20)";
  private static final String WKT_POLYGON =
      "POLYGON ((1.1 1.1, 1.1 2.1, 2.1 2.1, 2.1 1.1, 1.1 1.1))";

  private FilterBuilder builder;

  @BeforeEach
  public void setUp() {
    builder = new GeotoolsFilterBuilder();
  }

  @Test
  public void testComplexAndFilter() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter titleFilter = builder.attribute(TITLE_ATTRIBUTE).is().like().text("test");
    Filter dateFilter = builder.attribute(CREATED_ATTRIBUTE).after().date(new Date());
    Filter priceFilter = builder.attribute(PRICE_ATTRIBUTE).greaterThan().number(100);

    Filter complexFilter = builder.allOf(titleFilter, dateFilter, priceFilter);

    assertThat(complexFilter, is(notNullValue()));
    assertThat(complexFilter, instanceOf(And.class));
    complexFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(And.class), any());
  }

  @Test
  public void testComplexOrFilter() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter titleFilter = builder.attribute(TITLE_ATTRIBUTE).is().like().text("test");
    Filter dateFilter = builder.attribute(CREATED_ATTRIBUTE).after().date(new Date());
    Filter priceFilter = builder.attribute(PRICE_ATTRIBUTE).lessThan().number(50);

    Filter complexFilter = builder.anyOf(titleFilter, dateFilter, priceFilter);

    assertThat(complexFilter, is(notNullValue()));
    assertThat(complexFilter, instanceOf(Or.class));
    complexFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Or.class), any());
  }

  @Test
  public void testNestedAndOrFilters() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    // (title LIKE 'test' OR title LIKE 'sample') AND price > 100
    Filter titleFilter1 = builder.attribute(TITLE_ATTRIBUTE).is().like().text("test");
    Filter titleFilter2 = builder.attribute(TITLE_ATTRIBUTE).is().like().text("sample");
    Filter orFilter = builder.anyOf(titleFilter1, titleFilter2);

    Filter priceFilter = builder.attribute(PRICE_ATTRIBUTE).greaterThan().number(100);

    Filter complexFilter = builder.allOf(orFilter, priceFilter);

    assertThat(complexFilter, is(notNullValue()));
    assertThat(complexFilter, instanceOf(And.class));
    complexFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(And.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(Or.class), any());
    inOrder.verify(visitor, times(2)).visit(isA(PropertyIsLike.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsGreaterThan.class), any());
  }

  @Test
  public void testNestedOrAndFilters() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    // (title LIKE 'test' AND price > 100) OR (title LIKE 'sample' AND price < 50)
    Filter titleFilter1 = builder.attribute(TITLE_ATTRIBUTE).is().like().text("test");
    Filter priceFilter1 = builder.attribute(PRICE_ATTRIBUTE).greaterThan().number(100);
    Filter andFilter1 = builder.allOf(titleFilter1, priceFilter1);

    Filter titleFilter2 = builder.attribute(TITLE_ATTRIBUTE).is().like().text("sample");
    Filter priceFilter2 = builder.attribute(PRICE_ATTRIBUTE).lessThan().number(50);
    Filter andFilter2 = builder.allOf(titleFilter2, priceFilter2);

    Filter complexFilter = builder.anyOf(andFilter1, andFilter2);

    assertThat(complexFilter, is(notNullValue()));
    assertThat(complexFilter, instanceOf(Or.class));
    complexFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Or.class), any());
    inOrder.verify(visitor, times(2)).visit(isA(And.class), any());
  }

  @Test
  public void testNotWithAndFilter() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter titleFilter = builder.attribute(TITLE_ATTRIBUTE).is().like().text("test");
    Filter priceFilter = builder.attribute(PRICE_ATTRIBUTE).greaterThan().number(100);
    Filter andFilter = builder.allOf(titleFilter, priceFilter);

    Filter notFilter = builder.not(andFilter);

    assertThat(notFilter, is(notNullValue()));
    assertThat(notFilter, instanceOf(Not.class));
    notFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Not.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(And.class), any());
  }

  @Test
  public void testNotWithOrFilter() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter titleFilter = builder.attribute(TITLE_ATTRIBUTE).is().like().text("test");
    Filter priceFilter = builder.attribute(PRICE_ATTRIBUTE).lessThan().number(50);
    Filter orFilter = builder.anyOf(titleFilter, priceFilter);

    Filter notFilter = builder.not(orFilter);

    assertThat(notFilter, is(notNullValue()));
    assertThat(notFilter, instanceOf(Not.class));
    notFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Not.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(Or.class), any());
  }

  @Test
  public void testComplexSpatialAndTemporalFilter() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    // Spatial filter: within buffer
    Filter spatialFilter =
        builder.attribute(Metacard.GEOGRAPHY).withinBuffer().wkt(WKT_POINT, 1000);

    // Temporal filter: during date range
    Date startDate = new Date(System.currentTimeMillis() - 86400000L); // 1 day ago
    Date endDate = new Date();
    Filter temporalFilter = builder.attribute(Metacard.MODIFIED).during().dates(startDate, endDate);

    // Text filter
    Filter textFilter = builder.attribute(TITLE_ATTRIBUTE).is().like().text("test");

    Filter complexFilter = builder.allOf(spatialFilter, temporalFilter, textFilter);

    assertThat(complexFilter, is(notNullValue()));
    assertThat(complexFilter, instanceOf(And.class));
    complexFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(And.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(DWithin.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(During.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsLike.class), any());
  }

  @Test
  public void testAllOfWithList() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    List<Filter> filters = new ArrayList<>();
    filters.add(builder.attribute(TITLE_ATTRIBUTE).is().like().text("test"));
    filters.add(builder.attribute(PRICE_ATTRIBUTE).greaterThan().number(100));
    filters.add(builder.attribute(CREATED_ATTRIBUTE).after().date(new Date()));

    Filter complexFilter = builder.allOf(filters);

    assertThat(complexFilter, is(notNullValue()));
    assertThat(complexFilter, instanceOf(And.class));
    complexFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(And.class), any());
  }

  @Test
  public void testAnyOfWithList() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    List<Filter> filters =
        Arrays.asList(
            builder.attribute(TITLE_ATTRIBUTE).is().like().text("test"),
            builder.attribute(TITLE_ATTRIBUTE).is().like().text("sample"),
            builder.attribute(TITLE_ATTRIBUTE).is().like().text("example"));

    Filter complexFilter = builder.anyOf(filters);

    assertThat(complexFilter, is(notNullValue()));
    assertThat(complexFilter, instanceOf(Or.class));
    complexFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Or.class), any());
    inOrder.verify(visitor, times(3)).visit(isA(PropertyIsLike.class), any());
  }

  @Test
  public void testTripleNesting() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    // ((A AND B) OR (C AND D)) AND E
    Filter filterA = builder.attribute("attr1").is().equalTo().text("valueA");
    Filter filterB = builder.attribute("attr2").is().equalTo().text("valueB");
    Filter filterC = builder.attribute("attr3").is().equalTo().text("valueC");
    Filter filterD = builder.attribute("attr4").is().equalTo().text("valueD");
    Filter filterE = builder.attribute("attr5").is().equalTo().text("valueE");

    Filter andFilter1 = builder.allOf(filterA, filterB);
    Filter andFilter2 = builder.allOf(filterC, filterD);
    Filter orFilter = builder.anyOf(andFilter1, andFilter2);
    Filter complexFilter = builder.allOf(orFilter, filterE);

    assertThat(complexFilter, is(notNullValue()));
    complexFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(3)).visit(isA(And.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(Or.class), any());
    inOrder.verify(visitor, times(5)).visit(isA(PropertyIsEqualTo.class), any());
  }

  @Test
  public void testMultipleSpatialFiltersWithOr() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter intersectsFilter = builder.attribute(Metacard.GEOGRAPHY).intersecting().wkt(WKT_POLYGON);
    Filter withinBufferFilter =
        builder.attribute(Metacard.GEOGRAPHY).withinBuffer().wkt(WKT_POINT, 5000);

    Filter complexFilter = builder.anyOf(intersectsFilter, withinBufferFilter);

    assertThat(complexFilter, is(notNullValue()));
    assertThat(complexFilter, instanceOf(Or.class));
    complexFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Or.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(Intersects.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(DWithin.class), any());
  }

  @Test
  public void testEmptyAndFilter() {
    Filter emptyAnd = builder.allOf();
    assertThat(emptyAnd, is(notNullValue()));
    assertThat(emptyAnd, instanceOf(And.class));
  }

  @Test
  public void testEmptyOrFilter() {
    Filter emptyOr = builder.anyOf();
    assertThat(emptyOr, is(notNullValue()));
    assertThat(emptyOr, instanceOf(Or.class));
  }

  @Test
  public void testSingleFilterInAnd() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter singleFilter = builder.attribute(TITLE_ATTRIBUTE).is().like().text("test");
    Filter andFilter = builder.allOf(singleFilter);

    assertThat(andFilter, is(notNullValue()));
    andFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(And.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsLike.class), any());
  }

  @Test
  public void testSingleFilterInOr() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter singleFilter = builder.attribute(TITLE_ATTRIBUTE).is().like().text("test");
    Filter orFilter = builder.anyOf(singleFilter);

    assertThat(orFilter, is(notNullValue()));
    orFilter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Or.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsLike.class), any());
  }
}
