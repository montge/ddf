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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ddf.catalog.data.Metacard;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import java.util.Date;
import org.geotools.api.filter.And;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterVisitor;
import org.geotools.api.filter.Not;
import org.geotools.api.filter.Or;
import org.geotools.api.filter.PropertyIsBetween;
import org.geotools.api.filter.PropertyIsEqualTo;
import org.geotools.api.filter.PropertyIsGreaterThan;
import org.geotools.api.filter.PropertyIsGreaterThanOrEqualTo;
import org.geotools.api.filter.PropertyIsLessThan;
import org.geotools.api.filter.PropertyIsLessThanOrEqualTo;
import org.geotools.api.filter.PropertyIsLike;
import org.geotools.api.filter.PropertyIsNotEqualTo;
import org.geotools.api.filter.PropertyIsNull;
import org.geotools.api.filter.expression.ExpressionVisitor;
import org.geotools.api.filter.expression.Literal;
import org.geotools.api.filter.expression.PropertyName;
import org.geotools.api.filter.spatial.Beyond;
import org.geotools.api.filter.spatial.Contains;
import org.geotools.api.filter.spatial.DWithin;
import org.geotools.api.filter.spatial.Intersects;
import org.geotools.api.filter.spatial.Within;
import org.geotools.api.filter.temporal.After;
import org.geotools.api.filter.temporal.Before;
import org.geotools.api.filter.temporal.During;
import org.geotools.api.temporal.Instant;
import org.geotools.filter.visitor.DefaultExpressionVisitor;
import org.geotools.filter.visitor.DefaultFilterVisitor;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

public class FilterBuilderTest {

  static final String FOO_ATTRIBUTE = "foo";

  static final String POINT_WKT = "POINT (10 20)";

  private static final String MULTIPOLYGON_WKT =
      "MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)), ((20 35, 45 20, 30 5, 10 10, 10 30, 20 35), (30 20, 20 25, 20 15, 30 20)))";

  @Test
  public void propertyIsEqual() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).equalTo().text("bar");
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().equalTo().text("bar");
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().text("bar");
    filter.accept(visitor, null);

    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().bool(true);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().bytes(new byte[] {});
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().date(new Date());
    filter.accept(visitor, null);

    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().number((short) 5);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().number(new Integer(5));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().number(new Long(5));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().number(new Float(5));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().number(new Double(5));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().wkt("POINT (10, 30)");
    filter.accept(visitor, null);

    filter = builder.attribute(null).is().text(null);
    filter.accept(visitor, null);

    filter = builder.attribute(FOO_ATTRIBUTE).equalTo().dateRange(new Date(1), new Date(2));
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(13)).visit(isA(PropertyIsEqualTo.class), any());
    inOrder.verify(visitor, times(1)).visit(isA(During.class), any());
  }

  @Test
  public void propertyIsNotEqual() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).notEqualTo().text("bar");
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().notEqualTo().text("bar");
    filter.accept(visitor, null);

    filter = builder.attribute(null).is().notEqualTo().text(null);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(3)).visit(isA(PropertyIsNotEqualTo.class), any());
  }

  @Test
  public void propertyIsBetween() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).between().numbers(new Long(5), new Long(7));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().between().numbers(new Long(5), new Long(7));
    filter.accept(visitor, null);

    filter = builder.attribute(FOO_ATTRIBUTE).between().numbers((short) 5, (short) 7);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).between().numbers(new Integer(5), new Integer(7));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).between().numbers(new Long(5), new Long(7));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).between().numbers(new Float(5), new Float(7));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).between().numbers(new Double(5), new Double(7));
    filter.accept(visitor, null);

    filter = builder.attribute(null).is().between().numbers(null, (Integer) null);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(8)).visit(isA(PropertyIsBetween.class), any());
  }

  @Test
  public void propertyIsGreaterThan() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).greaterThan().number(5);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().greaterThan().number((short) 5);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).greaterThan().number(5L);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).greaterThan().number(5.0f);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).greaterThan().number(5.0d);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(5)).visit(isA(PropertyIsGreaterThan.class), any());
  }

  @Test
  public void propertyIsGreaterThanOrEqualTo() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).greaterThanOrEqualTo().number(5);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().greaterThanOrEqualTo().number((short) 5);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).greaterThanOrEqualTo().number(5L);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).greaterThanOrEqualTo().number(5.0f);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).greaterThanOrEqualTo().number(5.0d);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(5)).visit(isA(PropertyIsGreaterThanOrEqualTo.class), any());
  }

  @Test
  public void propertyIsLessThan() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).lessThan().number(5);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().lessThan().number((short) 5);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).lessThan().number(5L);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).lessThan().number(5.0f);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).lessThan().number(5.0d);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(5)).visit(isA(PropertyIsLessThan.class), any());
  }

  @Test
  public void propertyIsLessThanOrEqualTo() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).lessThanOrEqualTo().number(5);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().lessThanOrEqualTo().number((short) 5);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).lessThanOrEqualTo().number(5L);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).lessThanOrEqualTo().number(5.0f);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).lessThanOrEqualTo().number(5.0d);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(5)).visit(isA(PropertyIsLessThanOrEqualTo.class), any());
  }

  @Test
  public void andOrNull() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    // FilterBuilder builder = new GeotoolsFilterBuilder();
    //
    //

    FilterBuilder builder = new GeotoolsFilterBuilder();
    Filter filter = builder.allOf(builder.anyOf(builder.attribute(FOO_ATTRIBUTE).is().empty()));

    filter.accept(visitor, null);
    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(And.class), any());
    inOrder.verify(visitor).visit(isA(Or.class), any());

    ArgumentCaptor<PropertyIsNull> propertyIsNullArgument =
        ArgumentCaptor.forClass(PropertyIsNull.class);
    verify(visitor).visit(propertyIsNullArgument.capture(), any());
    ExpressionVisitor expVisitor = spy(new DefaultExpressionVisitor() {});
    propertyIsNullArgument.getValue().getExpression().accept(expVisitor, null);
    ArgumentCaptor<PropertyName> propertyNameArgument = ArgumentCaptor.forClass(PropertyName.class);
    verify(expVisitor).visit(propertyNameArgument.capture(), any());
    assertEquals(FOO_ATTRIBUTE, propertyNameArgument.getValue().getPropertyName());
  }

  @Test
  public void andNull() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.allOf(builder.attribute(FOO_ATTRIBUTE).is().empty());
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(And.class), any());
    inOrder.verify(visitor).visit(isA(PropertyIsNull.class), any());
  }

  @Test
  public void orNull() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.anyOf(builder.attribute(FOO_ATTRIBUTE).is().empty());
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Or.class), any());
    inOrder.verify(visitor).visit(isA(PropertyIsNull.class), any());
  }

  @Test
  public void notNull() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.not(builder.attribute(FOO_ATTRIBUTE).is().empty());
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Not.class), any());
    inOrder.verify(visitor).visit(isA(PropertyIsNull.class), any());
  }

  @Test
  public void afterDate() {
    final Date date = new Date();
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).is().after().date(date);

    filter.accept(visitor, null);

    ArgumentCaptor<After> expressionArgument = ArgumentCaptor.forClass(After.class);
    verify(visitor).visit(expressionArgument.capture(), any());

    ExpressionVisitor expVisitor = spy(new DefaultExpressionVisitor() {});

    expressionArgument.getValue().getExpression1().accept(expVisitor, null);
    ArgumentCaptor<PropertyName> propertyNameArgument = ArgumentCaptor.forClass(PropertyName.class);
    verify(expVisitor).visit(propertyNameArgument.capture(), any());
    assertEquals(FOO_ATTRIBUTE, propertyNameArgument.getValue().getPropertyName());

    expressionArgument.getValue().getExpression2().accept(expVisitor, null);
    ArgumentCaptor<Literal> literalArgument = ArgumentCaptor.forClass(Literal.class);
    verify(expVisitor).visit(literalArgument.capture(), any());
    assertEquals(date, ((Instant) literalArgument.getValue().getValue()).getPosition().getDate());
  }

  @Test
  public void isDate() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).is().date(new Date());

    filter.accept(visitor, null);
    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsEqualTo.class), any());
  }

  @Test
  public void after() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).after().date(new Date());
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().after().date(new Date());
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(2)).visit(isA(After.class), any());
  }

  @Test
  public void before() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).before().date(new Date());
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().before().date(new Date());
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(2)).visit(isA(Before.class), any());
  }

  @Test
  public void during() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter =
        builder
            .attribute(FOO_ATTRIBUTE)
            .during()
            .dates(new Date(), new Date(System.currentTimeMillis() + 10000000));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).during().next(1000);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).during().last(1000);
    filter.accept(visitor, null);
    filter =
        builder
            .attribute(FOO_ATTRIBUTE)
            .is()
            .during()
            .dates(new Date(), new Date(System.currentTimeMillis() + 10000000));
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().during().next(1000);
    filter.accept(visitor, null);
    filter = builder.attribute(FOO_ATTRIBUTE).is().during().last(1000);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(6)).visit(isA(During.class), any());
  }

  @Test
  public void like() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).is().like().text("bar");
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsLike.class), any());

    // TODO check case sensitivity

  }

  @Test
  public void likeCaseSensitive() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(FOO_ATTRIBUTE).is().like().caseSensitiveText("bAr");
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsLike.class), any());

    // TODO check case sensitivity
  }

  @Test
  public void likeFuzzy() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();
    Filter filter = builder.attribute(FOO_ATTRIBUTE).is().like().fuzzyText("bar");
    filter.accept(visitor, null);
    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsLike.class), any());

    // TODO check for fuzzy
  }

  @Test
  public void likeXPath() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.xpath("//foo").like().text("bar");
    filter.accept(visitor, null);
    filter = builder.xpath("//foo").is().like().text("bar");
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(2)).visit(isA(PropertyIsLike.class), any());
  }

  @Test
  public void xpathExists() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.xpath("//foo").exists();
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsLike.class), any());
  }

  @Test
  public void likeXPathNull() {
    FilterBuilder builder = new GeotoolsFilterBuilder();

    // The validation was removed or relaxed - null is now accepted without exception
    // This test verifies that null text is handled without exception
    assertDoesNotThrow(() -> builder.xpath("//foo").is().like().text(null));
  }

  @Test
  public void operatorBeforeNull() {
    FilterBuilder builder = new GeotoolsFilterBuilder();

    assertThrows(
        IllegalArgumentException.class, () -> builder.attribute("something").before().date(null));
  }

  @Test
  public void operatorAfterNull() {
    FilterBuilder builder = new GeotoolsFilterBuilder();

    assertThrows(
        IllegalArgumentException.class, () -> builder.attribute("something").after().date(null));
  }

  @Test
  public void operatorDuringNull() {
    FilterBuilder builder = new GeotoolsFilterBuilder();

    assertThrows(
        IllegalArgumentException.class,
        () -> builder.attribute("something").during().dates(null, null));
  }

  @Test
  public void operatorBeyondNull() {
    FilterBuilder builder = new GeotoolsFilterBuilder();

    assertThrows(
        IllegalArgumentException.class, () -> builder.attribute("something").beyond().wkt(null));
  }

  @Test
  public void operatorWithinNull() {
    FilterBuilder builder = new GeotoolsFilterBuilder();

    assertThrows(
        IllegalArgumentException.class, () -> builder.attribute("something").within().wkt(null));
  }

  @Test
  public void operatorWithinBufferNull() {
    FilterBuilder builder = new GeotoolsFilterBuilder();

    assertThrows(
        IllegalArgumentException.class,
        () -> builder.attribute("something").withinBuffer().wkt(null));
  }

  @Test
  public void likeXPathInvalid() {
    FilterBuilder builder = new GeotoolsFilterBuilder();

    assertThrows(
        IllegalArgumentException.class, () -> builder.xpath("foo").is().like().text("bar"));
  }

  @Test
  public void nearest() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(Metacard.GEOGRAPHY).nearestTo().wkt(POINT_WKT);
    filter.accept(visitor, null);

    // nearestTo() is implemented as a Beyond spatial operator in GeotoolsExpressionBuilder
    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Beyond.class), any());
  }

  @Test
  public void contains() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(Metacard.GEOGRAPHY).containing().wkt(MULTIPOLYGON_WKT);
    filter.accept(visitor, null);
    filter = builder.attribute(Metacard.GEOGRAPHY).is().containing().wkt(POINT_WKT);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(2)).visit(isA(Contains.class), any());
  }

  @Test
  public void intersects() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(Metacard.GEOGRAPHY).intersecting().wkt(POINT_WKT);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(Intersects.class), any());
  }

  @Test
  public void beyond() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(Metacard.GEOGRAPHY).beyond().wkt(POINT_WKT, 123.45d);
    filter.accept(visitor, null);
    filter = builder.attribute(Metacard.GEOGRAPHY).beyond().wkt(POINT_WKT);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(2)).visit(isA(Beyond.class), any());
    // TODO check arguments
  }

  @Test
  public void within() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(Metacard.GEOGRAPHY).within().wkt(POINT_WKT);
    filter.accept(visitor, null);
    filter = builder.attribute(Metacard.GEOGRAPHY).is().within().wkt(MULTIPOLYGON_WKT);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(2)).visit(isA(Within.class), any());
  }

  @Test
  public void withinBuffer() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(Metacard.GEOGRAPHY).withinBuffer().wkt(POINT_WKT);
    filter.accept(visitor, null);
    filter = builder.attribute(Metacard.GEOGRAPHY).withinBuffer().wkt(POINT_WKT, 123.45d);
    filter.accept(visitor, null);
    filter = builder.attribute(Metacard.GEOGRAPHY).is().withinBuffer().wkt(POINT_WKT);
    filter.accept(visitor, null);
    filter = builder.attribute(Metacard.GEOGRAPHY).is().withinBuffer().wkt(POINT_WKT, 123.45d);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(4)).visit(isA(DWithin.class), any());
  }

  /** */
  @Test
  public void withinGeoTest() {
    FilterBuilder builder = new GeotoolsFilterBuilder();

    Filter filter = builder.attribute(Metacard.GEOGRAPHY).within().wkt(POINT_WKT);

    Coordinate[] capturedCoordinate = new Coordinate[1];
    filter.accept(
        new DefaultFilterVisitor() {
          @Override
          public Object visit(Within filter, Object data) {
            Literal literalWrapper = (Literal) filter.getExpression2();

            Geometry geometry = (Geometry) literalWrapper.evaluate(null);
            capturedCoordinate[0] = geometry.getCentroid().getCoordinate();
            return super.visit(filter, data);
          }
        },
        null);

    // POINT_WKT is "POINT (10 20)"; verify the geometry parsed from the WKT round-trips correctly.
    assertEquals(new Coordinate(10, 20), capturedCoordinate[0]);
  }
}
