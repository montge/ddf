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
package ddf.catalog.filter.delegate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilterToTextDelegateTest {

  private FilterToTextDelegate delegate;

  @BeforeEach
  void setUp() {
    delegate = new FilterToTextDelegate();
  }

  @Test
  void testInclude() {
    assertThat(delegate.include(), is("true"));
  }

  @Test
  void testExclude() {
    assertThat(delegate.exclude(), is("false"));
  }

  @Test
  void testNot() {
    assertThat(delegate.not("operand"), is("not(operand)"));
  }

  @Test
  void testAnd() {
    String result = delegate.and(Arrays.asList("a", "b", "c"));
    assertThat(result, is("and(a,b,c)"));
  }

  @Test
  void testOr() {
    String result = delegate.or(Arrays.asList("x", "y"));
    assertThat(result, is("or(x,y)"));
  }

  @Test
  void testPropertyIsNull() {
    assertThat(delegate.propertyIsNull("title"), is("null(title)"));
  }

  @Test
  void testPropertyIsLike() {
    assertThat(delegate.propertyIsLike("name", "pat*", true), is("like(name,pat*)"));
  }

  @Test
  void testPropertyIsFuzzy() {
    assertThat(delegate.propertyIsFuzzy("description", "test"), is("fuzzy(description,test)"));
  }

  @Test
  void testPropertyIsEqualToString() {
    assertThat(delegate.propertyIsEqualTo("title", "value", true), is("title=value"));
  }

  @Test
  void testPropertyIsEqualToInt() {
    assertThat(delegate.propertyIsEqualTo("count", 42), is("count=42i"));
  }

  @Test
  void testPropertyIsEqualToShort() {
    assertThat(delegate.propertyIsEqualTo("num", (short) 10), is("num=10s"));
  }

  @Test
  void testPropertyIsEqualToLong() {
    assertThat(delegate.propertyIsEqualTo("bignum", 100L), is("bignum=100l"));
  }

  @Test
  void testPropertyIsEqualToDouble() {
    assertThat(delegate.propertyIsEqualTo("decimal", 3.14d), is("decimal=3.14d"));
  }

  @Test
  void testPropertyIsEqualToFloat() {
    assertThat(delegate.propertyIsEqualTo("floatval", 2.5f), is("floatval=2.5f"));
  }

  @Test
  void testPropertyIsEqualToBoolean() {
    assertThat(delegate.propertyIsEqualTo("flag", true), is("flag=true"));
  }

  @Test
  void testPropertyIsEqualToBytes() {
    byte[] bytes = new byte[] {1, 2, 3};
    assertThat(delegate.propertyIsEqualTo("data", bytes), is("data={1,2,3}b"));
  }

  @Test
  void testPropertyIsEqualToObject() {
    assertThat(delegate.propertyIsEqualTo("obj", "objValue"), is("obj=objValueo"));
  }

  @Test
  void testPropertyIsNotEqualToString() {
    assertThat(delegate.propertyIsNotEqualTo("title", "value", false), is("title!=value"));
  }

  @Test
  void testPropertyIsNotEqualToInt() {
    assertThat(delegate.propertyIsNotEqualTo("count", 5), is("count!=5i"));
  }

  @Test
  void testPropertyIsGreaterThanString() {
    assertThat(delegate.propertyIsGreaterThan("name", "abc"), is("name>abc"));
  }

  @Test
  void testPropertyIsGreaterThanInt() {
    assertThat(delegate.propertyIsGreaterThan("age", 18), is("age>18i"));
  }

  @Test
  void testPropertyIsGreaterThanOrEqualToString() {
    assertThat(delegate.propertyIsGreaterThanOrEqualTo("level", "high"), is("level>=high"));
  }

  @Test
  void testPropertyIsGreaterThanOrEqualToLong() {
    assertThat(delegate.propertyIsGreaterThanOrEqualTo("size", 1000L), is("size>=1000l"));
  }

  @Test
  void testPropertyIsLessThanString() {
    assertThat(delegate.propertyIsLessThan("priority", "z"), is("priority<z"));
  }

  @Test
  void testPropertyIsLessThanDouble() {
    assertThat(delegate.propertyIsLessThan("rate", 99.9d), is("rate<99.9d"));
  }

  @Test
  void testPropertyIsLessThanOrEqualToString() {
    assertThat(delegate.propertyIsLessThanOrEqualTo("rank", "5"), is("rank<=5"));
  }

  @Test
  void testPropertyIsLessThanOrEqualToFloat() {
    assertThat(delegate.propertyIsLessThanOrEqualTo("score", 100.0f), is("score<=100.0f"));
  }

  @Test
  void testPropertyIsBetweenString() {
    assertThat(delegate.propertyIsBetween("name", "a", "z"), is("a<=name<=z"));
  }

  @Test
  void testPropertyIsBetweenInt() {
    assertThat(delegate.propertyIsBetween("value", 1, 10), is("1i<=value<=10i"));
  }

  @Test
  void testPropertyIsBetweenLong() {
    assertThat(delegate.propertyIsBetween("id", 100L, 200L), is("100l<=id<=200l"));
  }

  @Test
  void testPropertyIsDivisibleBy() {
    assertThat(delegate.propertyIsDivisibleBy("number", 5L), is("number%5l=0"));
  }

  @Test
  void testXpathExists() {
    assertThat(delegate.xpathExists("/root/element"), is("xpath(/root/element)"));
  }

  @Test
  void testXpathIsLike() {
    assertThat(
        delegate.xpathIsLike("/root/name", "pattern*", true), is("xpath(/root/name,pattern*)"));
  }

  @Test
  void testXpathIsFuzzy() {
    assertThat(
        delegate.xpathIsFuzzy("/root/text", "fuzzyterm"), is("xpath(/root/text,fuzzy(fuzzyterm))"));
  }

  @Test
  void testBeyond() {
    assertThat(
        delegate.beyond("location", "POINT(0 0)", 100.0),
        is("beyond(location,wkt(POINT(0 0)),100.0)"));
  }

  @Test
  void testContains() {
    assertThat(
        delegate.contains("geometry", "POLYGON((0 0, 1 0, 1 1, 0 1, 0 0))"),
        is("contains(geometry,wkt(POLYGON((0 0, 1 0, 1 1, 0 1, 0 0))))"));
  }

  @Test
  void testDwithin() {
    assertThat(
        delegate.dwithin("loc", "POINT(1 1)", 50.0), is("dwithin(loc,wkt(POINT(1 1)),50.0)"));
  }

  @Test
  void testIntersects() {
    assertThat(
        delegate.intersects("area", "LINESTRING(0 0, 1 1)"),
        is("intersects(area,wkt(LINESTRING(0 0, 1 1)))"));
  }

  @Test
  void testNearestNeighbor() {
    assertThat(delegate.nearestNeighbor("point", "POINT(5 5)"), is("nn(point,wkt(POINT(5 5)))"));
  }

  @Test
  void testWithin() {
    assertThat(
        delegate.within("region", "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))"),
        is("within(region,wkt(POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))))"));
  }

  @Test
  void testCrosses() {
    assertThat(
        delegate.crosses("path", "LINESTRING(0 0, 5 5)"),
        is("crosses(path,wkt(LINESTRING(0 0, 5 5)))"));
  }

  @Test
  void testDisjoint() {
    assertThat(delegate.disjoint("zone", "POINT(0 0)"), is("disjoint(zone,wkt(POINT(0 0)))"));
  }

  @Test
  void testOverlaps() {
    assertThat(
        delegate.overlaps("area", "POLYGON((0 0, 1 0, 1 1, 0 1, 0 0))"),
        is("overlaps(area,wkt(POLYGON((0 0, 1 0, 1 1, 0 1, 0 0))))"));
  }

  @Test
  void testTouches() {
    assertThat(delegate.touches("boundary", "POINT(0 0)"), is("touches(boundary,wkt(POINT(0 0)))"));
  }

  @Test
  void testAfter() {
    Date date = new Date(0);
    String result = delegate.after("created", date);
    assertThat(result, is("after(created," + date + ")"));
  }

  @Test
  void testBefore() {
    Date date = new Date(1000);
    String result = delegate.before("modified", date);
    assertThat(result, is("before(modified," + date + ")"));
  }

  @Test
  void testDuring() {
    Date start = new Date(0);
    Date end = new Date(1000);
    String result = delegate.during("effective", start, end);
    assertThat(result, is("during(effective," + start + "," + end + ")"));
  }

  @Test
  void testRelative() {
    assertThat(delegate.relative("timestamp", 3600000L), is("relative(timestamp,3600000)"));
  }

  @Test
  void testPropertyIsInProximityTo() {
    assertThat(
        delegate.propertyIsInProximityTo("content", 5, "search term"),
        is("proximity(content,5,search term)"));
  }

  @Test
  void testAndWithSingleOperand() {
    String result = delegate.and(Arrays.asList("single"));
    assertThat(result, is("and(single)"));
  }

  @Test
  void testOrWithSingleOperand() {
    String result = delegate.or(Arrays.asList("only"));
    assertThat(result, is("or(only)"));
  }
}
