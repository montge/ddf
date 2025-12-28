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
package ddf.catalog.source.solr;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.AttributeType.AttributeFormat;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.types.Core;
import ddf.catalog.impl.filter.DivisibleByFunction;
import ddf.catalog.impl.filter.ProximityFunction;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;
import org.apache.solr.client.solrj.SolrQuery;
import org.geotools.api.filter.sort.SortBy;
import org.geotools.api.filter.sort.SortOrder;
import org.geotools.filter.SortByImpl;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.operation.union.UnaryUnionOp;

public class SolrFilterDelegateTest {

  private static final String TOKENIZED_METADATA_FIELD =
      Metacard.METADATA + SchemaFields.TEXT_SUFFIX + SchemaFields.TOKENIZED;

  private DynamicSchemaResolver mockResolver = mock(DynamicSchemaResolver.class);

  private SolrFilterDelegate toTest = new SolrFilterDelegate(mockResolver, Collections.emptyMap());

  @Test
  public void intersectsWithNullWkt() {
    // given null WKT and a valid property name
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    // when the delegate intersects, then the operation is unsupported
    assertThrows(
        UnsupportedOperationException.class, () -> toTest.intersects("testProperty", null));
  }

  @Test
  public void intersectsWithNullPropertyName() {
    // given null property name, when the delegate intersects, then the operation is unsupported
    assertThrows(UnsupportedOperationException.class, () -> toTest.intersects(null, "wkt"));
  }

  @Test
  public void intersectsWithInvalidJtsWkt() {
    // given a geospatial property
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");

    // when the delegate intersects on WKT not handled by JTS
    SolrQuery query = toTest.intersects("testProperty", "invalid JTS wkt");

    // then return a valid Solr query using the given WKT
    assertThat(query.getQuery(), is("testProperty_geohash_index:\"Intersects(invalid JTS wkt)\""));
  }

  @Test
  public void intersectsWithValidJtsWkt() {
    // given a geospatial property
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");

    // when the delegate intersects on WKT not handled by JTS
    SolrQuery query = toTest.intersects("testProperty", "POINT(1 0)");

    // then return a valid Solr query using the given WKT
    assertThat(
        query.getQuery(),
        startsWith("testProperty_geohash_index:\"Intersects(BUFFER(POINT(1.0 0.0), "));
  }

  @Test
  public void selfIntersectingPolygon() {
    String wkt = "POLYGON((0 0, 10 0, 10 20, 5 -5, 0 20, 0 0))";
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery query = toTest.contains("testProperty", wkt);
    assertThat(
        query.getQuery(),
        startsWith(
            "testProperty_geohash_index:\"Contains(POLYGON ((5 -5, 0 0, 0 20, 10 20, 10 0, 5 -5)))\""));
  }

  @Test
  public void squarePolygon() {
    String wkt = "POLYGON ((0 10, 0 30, 20 30, 20 10, 0 10))";
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery query = toTest.contains("testProperty", wkt);
    assertThat(
        query.getQuery(),
        startsWith(
            "testProperty_geohash_index:\"Contains(POLYGON ((0 10, 0 30, 20 30, 20 10, 0 10)))\""));
  }

  @Test
  public void nonIntersectingPolygon() {
    String wkt = "POLYGON((5 -5, 0 0, 0 20, 10 20, 10 0, 5 -5))";
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery query = toTest.contains("testProperty", wkt);
    assertThat(
        query.getQuery(),
        startsWith(
            "testProperty_geohash_index:\"Contains(POLYGON ((5 -5, 0 0, 0 20, 10 20, 10 0, 5 -5)))\""));
  }

  @Test
  public void polygonWithHoleAndSelfIntersecting() {
    // in the case of a self-intersecting polygon with a hole the hole is lost in the conversion
    String wkt = "POLYGON ((0 0, 0 10, 13 3, 13 9, 0 0), (1 4, 1 7, 3 6, 3 4, 1 4))";
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery query = toTest.contains("testProperty", wkt);
    assertThat(
        query.getQuery(),
        startsWith(
            "testProperty_geohash_index:\"Contains(POLYGON ((0 0, 0 10, 13 9, 13 3, 0 0)))\""));
  }

  @Test
  public void multiPolygon() throws ParseException {
    String wkt =
        "MULTIPOLYGON (((30 20, 45 40, 10 40, 30 20)), ((15 5, 40 10, 10 20, 5 10, 15 5)))";
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery query = toTest.contains("testProperty", wkt);
    MultiPolygon multiPolygon = (MultiPolygon) new WKTReader().read(wkt);
    // allowMultiOverlap is enabled, so spatial4j will calculate the union of any MultiPolygon.
    // This means we need to calculate the union of the expected WKT for the assertion.
    MultiPolygon unioned = (MultiPolygon) new UnaryUnionOp(multiPolygon).union();
    assertThat(
        query.getQuery(),
        startsWith(String.format("testProperty_geohash_index:\"Contains(%s)\"", unioned.toText())));
  }

  @Test
  public void polygonWithHole() {
    String wkt = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30))";
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery query = toTest.contains("testProperty", wkt);
    assertThat(
        query.getQuery(),
        startsWith(
            "testProperty_geohash_index:\"Contains(POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30)))\""));
  }

  @Test
  public void bufferedPolygonHolesRemovedIfCrossingDateline() {
    String wkt =
        "POLYGON ((170 10, -170 10, -170 0, 170 0, 170 10), (171 9, 172 9, 172 8, 172 8, 171 9))";
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    // buffer of 0 so the final WKT is easy to calculate
    SolrQuery query = toTest.dwithin("testProperty", wkt, 0);
    assertThat(
        query.getQuery(),
        startsWith(
            "testProperty_geohash_index:\"Intersects(MULTIPOLYGON (((-180 0, -180 10, -170 10, -170 0, -180 0)), ((180 10, 180 0, 170 0, 170 10, 180 10))))\""));
  }

  @Test
  public void bufferedMultiPolygonHolesRemovedIfCrossingDateline() {
    String wkt =
        "MULTIPOLYGON (((170 10, -170 10, -170 0, 170 0, 170 10), (171 9, 172 9, 172 8, 172 8, 171 9)), ((170 30, -170 30, -170 20, 170 20, 170 30), (171 29, 172 29, 172 28, 172 28, 171 29)))";
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    // buffer of 0 so the final WKT is easy to calculate
    SolrQuery query = toTest.dwithin("testProperty", wkt, 0);
    assertThat(
        query.getQuery(),
        startsWith(
            "testProperty_geohash_index:\"Intersects(MULTIPOLYGON (((-180 0, -180 10, -170 10, -170 0, -180 0)), ((180 10, 180 0, 170 0, 170 10, 180 10)), ((-180 20, -180 30, -170 30, -170 20, -180 20)), ((180 30, 180 20, 170 20, 170 30, 180 30))))\""));
  }

  @Test
  public void reservedSpecialCharactersIsEqual() {
    // given a text property
    when(mockResolver.getField(
            "testProperty", AttributeFormat.STRING, true, Collections.emptyMap()))
        .thenReturn("testProperty_txt_index");

    // when searching for exact reserved characters
    SolrQuery equalQuery =
        toTest.propertyIsEqualTo("testProperty", "+ - && || ! ( ) { } [ ] ^ \" ~ :", true);

    // then return escaped special characters in the query
    assertThat(
        equalQuery.getQuery(),
        is(
            "testProperty_txt_index:\"\\+ \\- \\&& \\|| \\! \\( \\) \\{ \\} \\[ \\] \\^ \\\" \\~ \\:\""));
  }

  @Test
  public void reservedSpecialCharactersIsLike() {
    // given a tokenized text property
    when(mockResolver.getField(
            "testProperty", AttributeFormat.STRING, true, Collections.emptyMap()))
        .thenReturn("testProperty_txt");
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);
    when(mockResolver.getCaseSensitiveField("testProperty_txt_tokenized", Collections.emptyMap()))
        .thenReturn("testProperty_txt_tokenized_tokenized");

    // when searching for like reserved characters
    SolrQuery likeQuery =
        toTest.propertyIsLike("testProperty", "+ - && || ! ( ) { } [ ] ^ \" ~ : \\*?", true);

    // then return escaped special characters in the query
    assertThat(
        likeQuery.getQuery(),
        is(
            "(testProperty_txt_tokenized_tokenized:(\\+ \\- \\&& \\|| \\! \\( \\) \\{ \\} \\[ \\] \\^ \\\" \\~ \\: \\*?))"));
  }

  /*
    DDF-314: COmmented out until the ANY_TEXT functionality is added back
    in - then these tests can be activated.

  @Test
  public void testPropertyIsEqualTo_AnyText_CaseSensitive() {
      String expectedQuery = "any_text:\"mySearchPhrase\"";
      String searchPhrase = "mySearchPhrase";
      boolean isCaseSensitive = true;
      SolrQuery equalToQuery = toTest.propertyIsEqualTo(Metacard.ANY_TEXT, searchPhrase,
              isCaseSensitive);
      assertThat(equalToQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualTo_AnyText_CaseInsensitive() {
      String searchPhrase = "mySearchPhrase";
      boolean isCaseSensitive = false;
      assertThrows(UnsupportedOperationException.class,
          () -> toTest.propertyIsEqualTo(Metacard.ANY_TEXT, searchPhrase, isCaseSensitive));
  }

  @Test
  public void testPropertyIsFuzzy_AnyText() {
      String expectedQuery = "+any_text:mysearchphrase~ ";
      String searchPhrase = "mySearchPhrase";
      SolrQuery fuzzyQuery = toTest.propertyIsFuzzy(Metacard.ANY_TEXT, searchPhrase);
      assertThat(fuzzyQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsLike_AnyText_CaseInsensitive() {
      String expectedQuery = "any_text:\"mySearchPhrase\"";
      String searchPhrase = "mySearchPhrase";
      boolean isCaseSensitive = false;
      SolrQuery isLikeQuery = toTest.propertyIsLike(Metacard.ANY_TEXT, searchPhrase,
              isCaseSensitive);
      assertThat(isLikeQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsLike_AnyText_CaseSensitive() {
      String expectedQuery = "any_text_has_case:\"mySearchPhrase\"";
      String searchPhrase = "mySearchPhrase";
      boolean isCaseSensitive = true;
      when(mockResolver.getCaseSensitiveField("any_text")).thenReturn(
              "any_text" + ddf.catalog.source.solr.SchemaFields.HAS_CASE);
      SolrQuery isLikeQuery = toTest.propertyIsLike(Metacard.ANY_TEXT, searchPhrase,
              isCaseSensitive);
      assertThat(isLikeQuery.getQuery(), is(expectedQuery));
  }
  END OMIT per DDF-314*/

  @Test
  public void testPropertyIsEqualToEmpty() {
    when(mockResolver.getField("title", AttributeFormat.STRING, true, Collections.emptyMap()))
        .thenReturn("title_txt");

    String searchPhrase = "";
    String expectedQuery = "-title_txt:[\"\" TO *]";
    boolean isCaseSensitive = true;

    SolrQuery isEqualTo = toTest.propertyIsEqualTo("title", searchPhrase, isCaseSensitive);

    assertThat(isEqualTo.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualToInteger() {
    when(mockResolver.getField("anumber", AttributeFormat.INTEGER, true, Collections.emptyMap()))
        .thenReturn("anumber_int");

    int searchPhrase = 1;
    String expectedQuery = "anumber_int:1";

    SolrQuery isEqualTo = toTest.propertyIsEqualTo("anumber", searchPhrase);

    assertThat(isEqualTo.getQuery().trim(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualToShort() {
    when(mockResolver.getField("anumber", AttributeFormat.SHORT, true, Collections.emptyMap()))
        .thenReturn("anumber_shr");

    short searchPhrase = 1;
    String expectedQuery = "anumber_shr:1";

    SolrQuery isEqualTo = toTest.propertyIsEqualTo("anumber", searchPhrase);

    assertThat(isEqualTo.getQuery().trim(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualToLong() {
    when(mockResolver.getField("anumber", AttributeFormat.LONG, true, Collections.emptyMap()))
        .thenReturn("anumber_lng");

    long searchPhrase = 1;
    String expectedQuery = "anumber_lng:1";

    SolrQuery isEqualTo = toTest.propertyIsEqualTo("anumber", searchPhrase);

    assertThat(isEqualTo.getQuery().trim(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualToFloat() {
    when(mockResolver.getField("anumber", AttributeFormat.FLOAT, true, Collections.emptyMap()))
        .thenReturn("anumber_flt");

    float searchPhrase = 1;
    String expectedQuery = "anumber_flt:1.0";

    SolrQuery isEqualTo = toTest.propertyIsEqualTo("anumber", searchPhrase);

    assertThat(isEqualTo.getQuery().trim(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualToNegativeFloat() {
    when(mockResolver.getField("anumber", AttributeFormat.FLOAT, true, Collections.emptyMap()))
        .thenReturn("anumber_flt");

    float searchPhrase = -1;
    String expectedQuery = "anumber_flt:\\-1.0";

    SolrQuery isEqualTo = toTest.propertyIsEqualTo("anumber", searchPhrase);

    assertThat(isEqualTo.getQuery().trim(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualToDouble() {
    when(mockResolver.getField("anumber", AttributeFormat.FLOAT, true, Collections.emptyMap()))
        .thenReturn("anumber_dbl");

    double searchPhrase = 1;
    String expectedQuery = "anumber_dbl:1.0";

    SolrQuery isEqualTo = toTest.propertyIsEqualTo("anumber", searchPhrase);

    assertThat(isEqualTo.getQuery().trim(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualToNegativeInteger() {
    when(mockResolver.getField("anumber", AttributeFormat.INTEGER, true, Collections.emptyMap()))
        .thenReturn("anumber_int");

    int searchPhrase = -1;
    String expectedQuery = "anumber_int:\\-1";

    SolrQuery isEqualTo = toTest.propertyIsEqualTo("anumber", searchPhrase);

    assertThat(isEqualTo.getQuery().trim(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualToBoolean() {
    when(mockResolver.getField("aboolean", AttributeFormat.BOOLEAN, true, Collections.emptyMap()))
        .thenReturn("aboolean_bln");

    boolean searchPhrase = true;
    String expectedQuery = "aboolean_bln:true";

    SolrQuery isEqualTo = toTest.propertyIsEqualTo("aboolean", searchPhrase);

    assertThat(isEqualTo.getQuery().trim(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsEqualToNull() {
    when(mockResolver.getField("title", AttributeFormat.STRING, true, Collections.emptyMap()))
        .thenReturn("title_txt");

    String searchPhrase = null;
    boolean isCaseSensitive = true;

    assertThrows(
        UnsupportedOperationException.class,
        () -> toTest.propertyIsEqualTo("title", searchPhrase, isCaseSensitive));
  }

  @Test
  public void testPropertyIsLikeWildcard() {
    when(mockResolver.anyTextFields())
        .thenReturn(Collections.singletonList("metadata_txt").stream());

    String searchPhrase = "*";
    String expectedQuery = "*:*";
    boolean isCaseSensitive = false;

    SolrQuery isLikeQuery = toTest.propertyIsLike(Metacard.ANY_TEXT, searchPhrase, isCaseSensitive);

    assertThat(isLikeQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsLikeTermAndWildcard() {
    when(mockResolver.anyTextFields())
        .thenReturn(Collections.singletonList("metadata_txt").stream());
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);

    String searchPhrase = "abc-123*";
    String expectedQuery = "(" + TOKENIZED_METADATA_FIELD + ":(abc\\-123*))";
    boolean isCaseSensitive = false;

    SolrQuery isLikeQuery = toTest.propertyIsLike(Metacard.ANY_TEXT, searchPhrase, isCaseSensitive);

    assertThat(isLikeQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsLikeEmpty() {
    when(mockResolver.getField("title", AttributeFormat.STRING, false, Collections.emptyMap()))
        .thenReturn("title_txt");

    String searchPhrase = "";
    String expectedQuery = "-title_txt:[\"\" TO *]";
    boolean isCaseSensitive = false;

    SolrQuery isLikeQuery = toTest.propertyIsLike("title", searchPhrase, isCaseSensitive);

    assertThat(isLikeQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsNull() {
    when(mockResolver.getField("title", AttributeFormat.STRING, false, Collections.emptyMap()))
        .thenReturn("title_txt");

    String searchPhrase = null;
    boolean isCaseSensitive = false;

    assertThrows(
        UnsupportedOperationException.class,
        () -> toTest.propertyIsLike("title", searchPhrase, isCaseSensitive));
  }

  @Test
  public void testPropertyIsLikeWildcardNoTokens() {
    when(mockResolver.anyTextFields())
        .thenReturn(Collections.singletonList("metadata_txt").stream());
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);

    String searchPhrase = "title*";
    String expectedQuery = "(" + TOKENIZED_METADATA_FIELD + ":(title*))";
    boolean isCaseSensitive = false;

    SolrQuery isLikeQuery = toTest.propertyIsLike(Metacard.ANY_TEXT, searchPhrase, isCaseSensitive);

    assertThat(isLikeQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsLikeMultipleTermsWithWildcard() {
    when(mockResolver.anyTextFields())
        .thenReturn(Collections.singletonList("metadata_txt").stream());
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);

    String searchPhrase = "abc 123*";
    String expectedQuery = "(" + TOKENIZED_METADATA_FIELD + ":(abc 123*))";

    SolrQuery isLikeQuery = toTest.propertyIsLike(Metacard.ANY_TEXT, searchPhrase, false);

    assertThat(isLikeQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsLikeCaseSensitiveWildcard() {
    when(mockResolver.anyTextFields())
        .thenReturn(Collections.singletonList("metadata_txt").stream());
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);
    when(mockResolver.getCaseSensitiveField("metadata_txt_tokenized", Collections.emptyMap()))
        .thenReturn("metadata_txt_tokenized_has_case");

    String searchPhrase = "abc-123*";
    String expectedQuery =
        "(" + TOKENIZED_METADATA_FIELD + SchemaFields.HAS_CASE + ":(abc\\-123*))";

    SolrQuery isLikeQuery = toTest.propertyIsLike(Metacard.ANY_TEXT, searchPhrase, true);

    assertThat(isLikeQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testTemporalBefore() {
    when(mockResolver.getField("created", AttributeFormat.DATE, false, Collections.emptyMap()))
        .thenReturn("created_date");

    String expectedQuery = " created_date:[ * TO 1995-11-24T23:59:56.765Z } ";
    SolrQuery temporalQuery = toTest.before(Metacard.CREATED, getCannedTime());
    assertThat(temporalQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testTemporalAfter() {
    when(mockResolver.getField("created", AttributeFormat.DATE, false, Collections.emptyMap()))
        .thenReturn("created_date");

    String expectedQuery = " created_date:{ 1995-11-24T23:59:56.765Z TO * ] ";
    SolrQuery temporalQuery = toTest.after(Metacard.CREATED, getCannedTime());
    assertThat(temporalQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testDatePropertyGreaterThan() {
    when(mockResolver.getField("created", AttributeFormat.DATE, false, Collections.emptyMap()))
        .thenReturn("created_date");

    String expectedQuery = " created_date:{ 1995-11-24T23:59:56.765Z TO * ] ";
    SolrQuery temporalQuery = toTest.propertyIsGreaterThan(Metacard.CREATED, getCannedTime());
    assertThat(temporalQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testDatePropertyGreaterThanOrEqualTo() {
    when(mockResolver.getField("created", AttributeFormat.DATE, false, Collections.emptyMap()))
        .thenReturn("created_date");

    String expectedQuery = " created_date:[ 1995-11-24T23:59:56.765Z TO * ] ";
    SolrQuery temporalQuery =
        toTest.propertyIsGreaterThanOrEqualTo(Metacard.CREATED, getCannedTime());
    assertThat(temporalQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testDatePropertyLessThan() {
    when(mockResolver.getField("created", AttributeFormat.DATE, false, Collections.emptyMap()))
        .thenReturn("created_date");

    String expectedQuery = " created_date:[ * TO 1995-11-24T23:59:56.765Z } ";
    SolrQuery temporalQuery = toTest.propertyIsLessThan(Metacard.CREATED, getCannedTime());
    assertThat(temporalQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testDatePropertyLessThanOrEqualTo() {
    when(mockResolver.getField("created", AttributeFormat.DATE, false, Collections.emptyMap()))
        .thenReturn("created_date");

    String expectedQuery = " created_date:[ * TO 1995-11-24T23:59:56.765Z ] ";
    SolrQuery temporalQuery = toTest.propertyIsLessThanOrEqualTo(Metacard.CREATED, getCannedTime());
    assertThat(temporalQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testDatePropertyIsBetween() {
    when(mockResolver.getField("created", AttributeFormat.DATE, false, Collections.emptyMap()))
        .thenReturn("created_date");

    String expectedQuery =
        " created_date:[ 1995-11-24T23:59:56.765Z TO 1995-11-27T04:59:56.765Z ] ";
    SolrQuery temporalQuery =
        toTest.propertyIsBetween(
            Metacard.CREATED, getCannedTime(), getCannedTime(1995, Calendar.NOVEMBER, 27, 4));
    assertThat(temporalQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testNumberIsBetweenLongs() {
    when(mockResolver.getField("altitude", AttributeFormat.LONG, true, Collections.emptyMap()))
        .thenReturn("altitude");

    String expectedQuery = " altitude:[ -100 TO 100] ";
    Number lowerBoundary = -100;
    Number upperBoundary = 100;
    SolrQuery numberQuery =
        toTest.propertyIsBetween("altitude", lowerBoundary.longValue(), upperBoundary.longValue());
    assertThat(numberQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testNumberIsBetweenFloats() {

    when(mockResolver.getField("altitude", AttributeFormat.FLOAT, true, Collections.emptyMap()))
        .thenReturn("altitude");

    String expectedQuery = " altitude:[ -100.3 TO 0.4] ";
    Number lowerBoundary = -100.3;
    Number upperBoundary = 0.4;
    SolrQuery numberQuery =
        toTest.propertyIsBetween(
            "altitude", lowerBoundary.floatValue(), upperBoundary.floatValue());
    assertThat(numberQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testNumberIsBetweenInts() {

    when(mockResolver.getField("altitude", AttributeFormat.INTEGER, true, Collections.emptyMap()))
        .thenReturn("altitude");

    String expectedQuery = " altitude:[ -100 TO 0] ";
    Number lowerBoundary = -100;
    Number upperBoundary = 0;
    SolrQuery numberQuery =
        toTest.propertyIsBetween("altitude", lowerBoundary.intValue(), upperBoundary.intValue());
    assertThat(numberQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testNumberIsBetweenShorts() {

    when(mockResolver.getField("altitude", AttributeFormat.SHORT, true, Collections.emptyMap()))
        .thenReturn("altitude");

    String expectedQuery = " altitude:[ 0 TO 50] ";
    Number lowerBoundary = 0;
    Number upperBoundary = 50;
    SolrQuery numberQuery =
        toTest.propertyIsBetween(
            "altitude", lowerBoundary.shortValue(), upperBoundary.shortValue());
    assertThat(numberQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testNumberIsBetweenFloatAndInt() {

    when(mockResolver.getField("altitude", AttributeFormat.FLOAT, true, Collections.emptyMap()))
        .thenReturn("altitude");

    String expectedQuery = " altitude:[ -100.567 TO 0.0] ";
    Number lowerBoundary = -100.567;
    Number upperBoundary = 0;
    SolrQuery numberQuery =
        toTest.propertyIsBetween("altitude", lowerBoundary.floatValue(), upperBoundary.intValue());
    assertThat(numberQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testBetweenCastException() {
    String nonNumber = "Not A Number";
    assertThrows(
        IllegalArgumentException.class,
        () -> toTest.propertyIsBetween("altitude", (Object) nonNumber, (Object) nonNumber));
  }

  @Test
  public void testPropertyIsInProximityTo() {
    when(mockResolver.getField("title", AttributeFormat.STRING, true, Collections.emptyMap()))
        .thenReturn("title_txt");
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);

    String expectedQuery = "(title_txt_tokenized:\"a proximity string\" ~2)";
    SolrQuery solrQuery = toTest.propertyIsInProximityTo(Core.TITLE, 2, "a proximity string");

    assertThat(solrQuery.getQuery(), is(expectedQuery));
  }

  @Test
  public void testPropertyIsDivisibleBy() {
    when(mockResolver.getAnonymousField(Core.RESOURCE_SIZE))
        .thenReturn(Collections.singletonList("resource-size_lng"));

    long divisibleBy = 2L;
    String expectedQuery = "_val_:\"{!frange l=0 u=0}mod(field(resource-size_lng,min),2)\"";

    SolrQuery isLikeQuery = toTest.propertyIsDivisibleBy(Core.RESOURCE_SIZE, divisibleBy);

    assertThat(isLikeQuery.getQuery(), is(expectedQuery));
  }

  private Date getCannedTime() {
    return getCannedTime(1995, Calendar.NOVEMBER, 24, 23);
  }

  private Date getCannedTime(int year, int month, int day, int hour) {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.clear();
    calendar.set(year, month, day, hour, 59, 56);
    calendar.set(Calendar.MILLISECOND, 765);
    return calendar.getTime();
  }

  @Test
  public void testAndOperator() {
    SolrQuery query1 = new SolrQuery("field1:value1");
    SolrQuery query2 = new SolrQuery("field2:value2");
    SolrQuery result = toTest.and(Arrays.asList(query1, query2));
    assertThat(result.getQuery(), containsString("AND"));
    assertThat(result.getQuery(), containsString("field1:value1"));
    assertThat(result.getQuery(), containsString("field2:value2"));
  }

  @Test
  public void testOrOperator() {
    SolrQuery query1 = new SolrQuery("field1:value1");
    SolrQuery query2 = new SolrQuery("field2:value2");
    SolrQuery result = toTest.or(Arrays.asList(query1, query2));
    assertThat(result.getQuery(), containsString("OR"));
    assertThat(result.getQuery(), containsString("field1:value1"));
    assertThat(result.getQuery(), containsString("field2:value2"));
  }

  @Test
  public void testNotOperator() {
    SolrQuery operand = new SolrQuery("field:value");
    SolrQuery result = toTest.not(operand);
    assertThat(result.getQuery(), is("(*:* NOT field:value)"));
  }

  @Test
  public void testAndWithEmptyOperands() {
    assertThrows(UnsupportedOperationException.class, () -> toTest.and(Collections.emptyList()));
  }

  @Test
  public void testOrWithEmptyOperands() {
    assertThrows(UnsupportedOperationException.class, () -> toTest.or(Collections.emptyList()));
  }

  @Test
  public void testAndWithNullOperand() {
    SolrQuery query1 = new SolrQuery("field1:value1");
    assertThrows(
        UnsupportedOperationException.class, () -> toTest.and(Arrays.asList(query1, null)));
  }

  @Test
  public void testOrWithNullOperand() {
    SolrQuery query1 = new SolrQuery("field1:value1");
    assertThrows(UnsupportedOperationException.class, () -> toTest.or(Arrays.asList(query1, null)));
  }

  @Test
  public void testPropertyIsFuzzy() {
    when(mockResolver.getField("title", AttributeFormat.STRING, false, Collections.emptyMap()))
        .thenReturn("title_txt");
    SolrQuery result = toTest.propertyIsFuzzy("title", "searchPhrase");
    assertThat(result.getQuery(), containsString("title_txt"));
    assertThat(result.getQuery(), containsString("~"));
  }

  @Test
  public void testPropertyIsFuzzyWithWildcard() {
    when(mockResolver.anyTextFields())
        .thenReturn(Collections.singletonList("metadata_txt").stream());
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);
    SolrQuery result = toTest.propertyIsFuzzy(Metacard.ANY_TEXT, "search*phrase");
    assertThat(result.getQuery(), containsString("~"));
  }

  @Test
  public void testPropertyIsEqualToDate() {
    when(mockResolver.getField("created", AttributeFormat.DATE, true, Collections.emptyMap()))
        .thenReturn("created_date");
    Date testDate = getCannedTime();
    SolrQuery result = toTest.propertyIsEqualTo(Metacard.CREATED, testDate);
    assertThat(result.getQuery(), containsString("created_date"));
    assertThat(result.getQuery(), containsString("1995-11-24"));
  }

  @Test
  public void testPropertyIsGreaterThanInt() {
    when(mockResolver.getField("count", AttributeFormat.INTEGER, true, Collections.emptyMap()))
        .thenReturn("count_int");
    SolrQuery result = toTest.propertyIsGreaterThan("count", 5);
    assertThat(result.getQuery(), containsString("count_int"));
    assertThat(result.getQuery(), containsString("{ 5"));
    assertThat(result.getQuery(), containsString("TO *"));
  }

  @Test
  public void testPropertyIsGreaterThanShort() {
    when(mockResolver.getField("count", AttributeFormat.SHORT, true, Collections.emptyMap()))
        .thenReturn("count_shr");
    short value = 5;
    SolrQuery result = toTest.propertyIsGreaterThan("count", value);
    assertThat(result.getQuery(), containsString("count_shr"));
    assertThat(result.getQuery(), containsString("{ 5"));
  }

  @Test
  public void testPropertyIsGreaterThanLong() {
    when(mockResolver.getField("count", AttributeFormat.LONG, true, Collections.emptyMap()))
        .thenReturn("count_lng");
    SolrQuery result = toTest.propertyIsGreaterThan("count", 5L);
    assertThat(result.getQuery(), containsString("count_lng"));
    assertThat(result.getQuery(), containsString("{ 5"));
  }

  @Test
  public void testPropertyIsGreaterThanFloat() {
    when(mockResolver.getField("count", AttributeFormat.FLOAT, true, Collections.emptyMap()))
        .thenReturn("count_flt");
    SolrQuery result = toTest.propertyIsGreaterThan("count", 5.5f);
    assertThat(result.getQuery(), containsString("count_flt"));
    assertThat(result.getQuery(), containsString("{ 5.5"));
  }

  @Test
  public void testPropertyIsGreaterThanDouble() {
    when(mockResolver.getField("count", AttributeFormat.DOUBLE, true, Collections.emptyMap()))
        .thenReturn("count_dbl");
    SolrQuery result = toTest.propertyIsGreaterThan("count", 5.5);
    assertThat(result.getQuery(), containsString("count_dbl"));
    assertThat(result.getQuery(), containsString("{ 5.5"));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToShort() {
    when(mockResolver.getField("count", AttributeFormat.SHORT, true, Collections.emptyMap()))
        .thenReturn("count_shr");
    short value = 5;
    SolrQuery result = toTest.propertyIsGreaterThanOrEqualTo("count", value);
    assertThat(result.getQuery(), containsString("count_shr"));
    assertThat(result.getQuery(), containsString("[ 5"));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToInt() {
    when(mockResolver.getField("count", AttributeFormat.INTEGER, true, Collections.emptyMap()))
        .thenReturn("count_int");
    SolrQuery result = toTest.propertyIsGreaterThanOrEqualTo("count", 5);
    assertThat(result.getQuery(), containsString("count_int"));
    assertThat(result.getQuery(), containsString("[ 5"));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToLong() {
    when(mockResolver.getField("count", AttributeFormat.LONG, true, Collections.emptyMap()))
        .thenReturn("count_lng");
    SolrQuery result = toTest.propertyIsGreaterThanOrEqualTo("count", 5L);
    assertThat(result.getQuery(), containsString("count_lng"));
    assertThat(result.getQuery(), containsString("[ 5"));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToFloat() {
    when(mockResolver.getField("count", AttributeFormat.FLOAT, true, Collections.emptyMap()))
        .thenReturn("count_flt");
    SolrQuery result = toTest.propertyIsGreaterThanOrEqualTo("count", 5.5f);
    assertThat(result.getQuery(), containsString("count_flt"));
    assertThat(result.getQuery(), containsString("[ 5.5"));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToDouble() {
    when(mockResolver.getField("count", AttributeFormat.DOUBLE, true, Collections.emptyMap()))
        .thenReturn("count_dbl");
    SolrQuery result = toTest.propertyIsGreaterThanOrEqualTo("count", 5.5);
    assertThat(result.getQuery(), containsString("count_dbl"));
    assertThat(result.getQuery(), containsString("[ 5.5"));
  }

  @Test
  public void testPropertyIsLessThanShort() {
    when(mockResolver.getField("count", AttributeFormat.SHORT, true, Collections.emptyMap()))
        .thenReturn("count_shr");
    short value = 5;
    SolrQuery result = toTest.propertyIsLessThan("count", value);
    assertThat(result.getQuery(), containsString("count_shr"));
    assertThat(result.getQuery(), containsString("TO 5"));
    assertThat(result.getQuery(), containsString("}"));
  }

  @Test
  public void testPropertyIsLessThanLong() {
    when(mockResolver.getField("count", AttributeFormat.LONG, true, Collections.emptyMap()))
        .thenReturn("count_lng");
    SolrQuery result = toTest.propertyIsLessThan("count", 5L);
    assertThat(result.getQuery(), containsString("count_lng"));
    assertThat(result.getQuery(), containsString("TO 5"));
  }

  @Test
  public void testPropertyIsLessThanFloat() {
    when(mockResolver.getField("count", AttributeFormat.FLOAT, true, Collections.emptyMap()))
        .thenReturn("count_flt");
    SolrQuery result = toTest.propertyIsLessThan("count", 5.5f);
    assertThat(result.getQuery(), containsString("count_flt"));
    assertThat(result.getQuery(), containsString("TO 5.5"));
  }

  @Test
  public void testPropertyIsLessThanDouble() {
    when(mockResolver.getField("count", AttributeFormat.DOUBLE, true, Collections.emptyMap()))
        .thenReturn("count_dbl");
    SolrQuery result = toTest.propertyIsLessThan("count", 5.5);
    assertThat(result.getQuery(), containsString("count_dbl"));
    assertThat(result.getQuery(), containsString("TO 5.5"));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToShort() {
    when(mockResolver.getField("count", AttributeFormat.SHORT, true, Collections.emptyMap()))
        .thenReturn("count_shr");
    short value = 5;
    SolrQuery result = toTest.propertyIsLessThanOrEqualTo("count", value);
    assertThat(result.getQuery(), containsString("count_shr"));
    assertThat(result.getQuery(), containsString("TO 5"));
    assertThat(result.getQuery(), containsString("]"));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToLong() {
    when(mockResolver.getField("count", AttributeFormat.LONG, true, Collections.emptyMap()))
        .thenReturn("count_lng");
    SolrQuery result = toTest.propertyIsLessThanOrEqualTo("count", 5L);
    assertThat(result.getQuery(), containsString("count_lng"));
    assertThat(result.getQuery(), containsString("TO 5"));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToFloat() {
    when(mockResolver.getField("count", AttributeFormat.FLOAT, true, Collections.emptyMap()))
        .thenReturn("count_flt");
    SolrQuery result = toTest.propertyIsLessThanOrEqualTo("count", 5.5f);
    assertThat(result.getQuery(), containsString("count_flt"));
    assertThat(result.getQuery(), containsString("TO 5.5"));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToDouble() {
    when(mockResolver.getField("count", AttributeFormat.DOUBLE, true, Collections.emptyMap()))
        .thenReturn("count_dbl");
    SolrQuery result = toTest.propertyIsLessThanOrEqualTo("count", 5.5);
    assertThat(result.getQuery(), containsString("count_dbl"));
    assertThat(result.getQuery(), containsString("TO 5.5"));
  }

  @Test
  public void testPropertyIsBetweenDoubles() {
    when(mockResolver.getField("altitude", AttributeFormat.DOUBLE, true, Collections.emptyMap()))
        .thenReturn("altitude_dbl");
    SolrQuery result = toTest.propertyIsBetween("altitude", 10.5, 50.5);
    assertThat(result.getQuery(), containsString("altitude_dbl"));
    assertThat(result.getQuery(), containsString("10.5"));
    assertThat(result.getQuery(), containsString("50.5"));
  }

  @Test
  public void testDuringDateRange() {
    when(mockResolver.getField("created", AttributeFormat.DATE, false, Collections.emptyMap()))
        .thenReturn("created_date");
    Date start = getCannedTime();
    Date end = getCannedTime(1995, Calendar.NOVEMBER, 27, 4);
    SolrQuery result = toTest.during(Metacard.CREATED, start, end);
    assertThat(result.getQuery(), containsString("created_date"));
    assertThat(result.getQuery(), containsString("{"));
    assertThat(result.getQuery(), containsString("}"));
  }

  @Test
  public void testPropertyIsNullQuery() {
    when(mockResolver.getAnonymousField("title"))
        .thenReturn(Collections.singletonList("title_txt"));
    SolrQuery result = toTest.propertyIsNull("title");
    assertThat(result.getQuery(), containsString("-title_txt"));
    assertThat(result.getQuery(), containsString("[* TO *]"));
  }

  @Test
  public void testPropertyIsNullQueryEmptyFields() {
    when(mockResolver.getAnonymousField("unknown")).thenReturn(Collections.emptyList());
    assertThrows(UnsupportedOperationException.class, () -> toTest.propertyIsNull("unknown"));
  }

  @Test
  public void testPropertyIsEqualToWithDivisibleByFunction() {
    when(mockResolver.getAnonymousField(Core.RESOURCE_SIZE))
        .thenReturn(Collections.singletonList("resource-size_lng"));
    List<Object> arguments = Arrays.asList(Core.RESOURCE_SIZE, 2L);
    SolrQuery result =
        toTest.propertyIsEqualTo(DivisibleByFunction.FUNCTION_NAME_STRING, arguments, true);
    assertThat(result.getQuery(), containsString("frange"));
    assertThat(result.getQuery(), containsString("mod"));
  }

  @Test
  public void testPropertyIsEqualToWithDivisibleByFunctionNegated() {
    when(mockResolver.getAnonymousField(Core.RESOURCE_SIZE))
        .thenReturn(Collections.singletonList("resource-size_lng"));
    List<Object> arguments = Arrays.asList(Core.RESOURCE_SIZE, 2L);
    SolrQuery result =
        toTest.propertyIsEqualTo(DivisibleByFunction.FUNCTION_NAME_STRING, arguments, false);
    assertThat(result.getQuery(), startsWith("!"));
  }

  @Test
  public void testPropertyIsEqualToWithProximityFunction() {
    when(mockResolver.getField("title", AttributeFormat.STRING, true, Collections.emptyMap()))
        .thenReturn("title_txt");
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);
    List<Object> arguments = Arrays.asList("title", 5, "hello world");
    SolrQuery result =
        toTest.propertyIsEqualTo(ProximityFunction.FUNCTION_NAME_STRING, arguments, true);
    assertThat(result.getQuery(), containsString("~5"));
  }

  @Test
  public void testPropertyIsEqualToWithUnsupportedFunction() {
    List<Object> arguments = Arrays.asList("prop", 123);
    assertThrows(
        UnsupportedOperationException.class,
        () -> toTest.propertyIsEqualTo("unknownFunction", arguments, true));
  }

  @Test
  public void testWithinWithPropertyName() {
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery result = toTest.within("testProperty", "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");
    assertThat(result.getQuery(), containsString("IsWithin"));
    assertThat(result.getQuery(), containsString("testProperty_geohash_index"));
  }

  @Test
  public void testWithinWithAnyGeo() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result = toTest.within(Metacard.ANY_GEO, "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");
    assertThat(result.getQuery(), containsString("IsWithin"));
  }

  @Test
  public void testDisjointWithPropertyName() {
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery result = toTest.disjoint("testProperty", "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");
    assertThat(result.getQuery(), containsString("IsDisjointTo"));
  }

  @Test
  public void testDisjointWithAnyGeo() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result = toTest.disjoint(Metacard.ANY_GEO, "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");
    assertThat(result.getQuery(), containsString("IsDisjointTo"));
  }

  @Test
  public void testOverlapsWithPropertyName() {
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery result = toTest.overlaps("testProperty", "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");
    assertThat(result.getQuery(), containsString("Overlaps"));
  }

  @Test
  public void testOverlapsWithAnyGeo() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result = toTest.overlaps(Metacard.ANY_GEO, "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");
    assertThat(result.getQuery(), containsString("Overlaps"));
  }

  @Test
  public void testContainsWithAnyGeo() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result = toTest.contains(Metacard.ANY_GEO, "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");
    assertThat(result.getQuery(), containsString("Contains"));
  }

  @Test
  public void testDwithinWithPoint() {
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery result = toTest.dwithin("testProperty", "POINT(1 1)", 1000);
    assertThat(result.getQuery(), containsString("Intersects"));
    assertThat(result.getQuery(), containsString("BUFFER"));
  }

  @Test
  public void testDwithinWithAnyGeoPoint() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result = toTest.dwithin(Metacard.ANY_GEO, "POINT(1 1)", 1000);
    assertThat(result.getQuery(), containsString("Intersects"));
    assertThat(result.getQuery(), containsString("BUFFER"));
  }

  @Test
  public void testDwithinWithPolygon() {
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery result =
        toTest.dwithin("testProperty", "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))", 1000);
    assertThat(result.getQuery(), containsString("Intersects"));
  }

  @Test
  public void testDwithinWithAnyGeoPolygon() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result =
        toTest.dwithin(Metacard.ANY_GEO, "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))", 1000);
    assertThat(result.getQuery(), containsString("Intersects"));
  }

  @Test
  public void testDwithinWithInvalidWkt() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> toTest.dwithin("testProperty", "INVALID WKT", 1000));
  }

  @Test
  public void testNearestNeighborWithPoint() {
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    SolrQuery result = toTest.nearestNeighbor("testProperty", "POINT(1 1)");
    assertThat(result.getQuery(), containsString("Intersects"));
    assertThat(result.getQuery(), containsString("BUFFER"));
  }

  @Test
  public void testNearestNeighborWithAnyGeo() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result = toTest.nearestNeighbor(Metacard.ANY_GEO, "POINT(1 1)");
    assertThat(result.getQuery(), containsString("Intersects"));
  }

  @Test
  public void testNearestNeighborWithPolygon() {
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    // Polygon should use centroid
    SolrQuery result =
        toTest.nearestNeighbor("testProperty", "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");
    assertThat(result.getQuery(), containsString("Intersects"));
  }

  @Test
  public void testNearestNeighborWithInvalidWkt() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> toTest.nearestNeighbor("testProperty", "INVALID WKT"));
  }

  @Test
  public void testIntersectsWithAnyGeo() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result =
        toTest.intersects(Metacard.ANY_GEO, "POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");
    assertThat(result.getQuery(), containsString("Intersects"));
  }

  @Test
  public void testIntersectsWithAnyGeoPoint() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result = toTest.intersects(Metacard.ANY_GEO, "POINT(1 1)");
    assertThat(result.getQuery(), containsString("Intersects"));
    assertThat(result.getQuery(), containsString("BUFFER"));
  }

  @Test
  public void testIntersectsWithMultiPointSingleCoord() {
    when(mockResolver.anyGeoFields()).thenAnswer(invocation -> Stream.of("location_geohash_index"));
    when(mockResolver.getField(
            "location_geohash_index", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("location_geohash_index");
    SolrQuery result = toTest.intersects(Metacard.ANY_GEO, "MULTIPOINT((1 1))");
    assertThat(result.getQuery(), containsString("Intersects"));
    assertThat(result.getQuery(), containsString("BUFFER"));
  }

  @Test
  public void testRemoveHolesFromPolygon() throws ParseException {
    String wktWithHole =
        "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30))";
    WKTReader reader = new WKTReader();
    Geometry geo = reader.read(wktWithHole);
    Geometry result = SolrFilterDelegate.removeHoles(geo);
    assertThat(result.getNumGeometries(), is(1));
    // Result should be just the exterior ring (no interior rings / holes)
  }

  @Test
  public void testRemoveHolesFromMultiPolygon() throws ParseException {
    String wktWithHoles =
        "MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)), "
            + "((20 35, 10 30, 10 10, 30 5, 45 20, 20 35), (30 20, 20 15, 20 25, 30 20)))";
    WKTReader reader = new WKTReader();
    Geometry geo = reader.read(wktWithHoles);
    Geometry result = SolrFilterDelegate.removeHoles(geo);
    assertThat(result.getNumGeometries(), is(2));
  }

  @Test
  public void testRemoveHolesFromPoint() throws ParseException {
    String wkt = "POINT(1 1)";
    WKTReader reader = new WKTReader();
    Geometry geo = reader.read(wkt);
    Geometry result = SolrFilterDelegate.removeHoles(geo);
    // Point should be returned unchanged
    assertThat(result.getGeometryType(), is("Point"));
  }

  @Test
  public void testSetSortPolicy() {
    SortByImpl sortBy =
        new SortByImpl(
            new org.geotools.filter.AttributeExpressionImpl("title"), SortOrder.ASCENDING);
    toTest.setSortPolicy(new SortBy[] {sortBy});
    // Just verify no exception is thrown
    assertFalse(toTest.isSortedByDistance());
  }

  @Test
  public void testSetSortPolicyWithNull() {
    toTest.setSortPolicy(null);
    assertFalse(toTest.isSortedByDistance());
  }

  @Test
  public void testIsSortedByDistanceInitiallyFalse() {
    assertFalse(toTest.isSortedByDistance());
  }

  @Test
  public void testGetSortedDistancePointInitiallyNull() {
    assertThat(toTest.getSortedDistancePoint(), nullValue());
  }

  @Test
  public void testIsIdQueryInitiallyFalse() {
    assertFalse(toTest.isIdQuery());
  }

  @Test
  public void testGetIdsInitiallyEmpty() {
    assertThat(toTest.getIds(), is(empty()));
  }

  @Test
  public void testPropertyIsEqualToSetsIdQuery() {
    when(mockResolver.getField(Metacard.ID, AttributeFormat.STRING, true, Collections.emptyMap()))
        .thenReturn("id_txt");
    toTest.propertyIsEqualTo(Metacard.ID, "test-id-123", true);
    assertTrue(toTest.isIdQuery());
    assertThat(toTest.getIds().size(), is(1));
    assertTrue(toTest.getIds().contains("test-id-123"));
  }

  @Test
  public void testPropertyIsEqualToCaseInsensitiveThrows() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> toTest.propertyIsEqualTo("title", "value", false));
  }

  @Test
  public void testPropertyIsInProximityToWithNullPropertyName() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> toTest.propertyIsInProximityTo(null, 5, "search terms"));
  }

  @Test
  public void testPropertyIsInProximityToWithNegativeDistance() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> toTest.propertyIsInProximityTo("title", -1, "search terms"));
  }

  @Test
  public void testOperationToQueryWithEmptyWkt() {
    when(mockResolver.getField(
            "testProperty", AttributeFormat.GEOMETRY, false, Collections.emptyMap()))
        .thenReturn("testProperty_geohash_index");
    assertThrows(UnsupportedOperationException.class, () -> toTest.contains("testProperty", ""));
  }

  @Test
  public void testPropertyIsDivisibleByWithNonExistentField() {
    when(mockResolver.getAnonymousField("unknown")).thenReturn(Collections.emptyList());
    assertThrows(
        UnsupportedOperationException.class, () -> toTest.propertyIsDivisibleBy("unknown", 2L));
  }

  @Test
  public void testPropertyIsLikeWithQuotedPhrase() {
    when(mockResolver.getField("title", AttributeFormat.STRING, true, Collections.emptyMap()))
        .thenReturn("title_txt");
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);
    when(mockResolver.getCaseSensitiveField("title_txt_tokenized", Collections.emptyMap()))
        .thenReturn("title_txt_tokenized_has_case");
    SolrQuery result = toTest.propertyIsLike("title", "\"exact phrase\"", true);
    assertThat(result.getQuery(), containsString("*"));
    assertThat(result.getQuery(), containsString("exact\\ phrase"));
  }

  @Test
  public void testPropertyIsLikeWithAnyTextCaseSensitive() {
    when(mockResolver.anyTextFields())
        .thenReturn(Collections.singletonList("metadata_txt").stream());
    when(mockResolver.getSpecialIndexSuffix(AttributeFormat.STRING, Collections.emptyMap()))
        .thenReturn(SchemaFields.TOKENIZED);
    when(mockResolver.getCaseSensitiveField("metadata_txt_tokenized", Collections.emptyMap()))
        .thenReturn("metadata_txt_tokenized_has_case");
    SolrQuery result = toTest.propertyIsLike(Metacard.ANY_TEXT, "search*", true);
    assertThat(result.getQuery(), containsString("has_case"));
  }

  @Test
  public void testPropertyIsEqualToWithAnyText() {
    when(mockResolver.anyTextFields())
        .thenReturn(Collections.singletonList("metadata_txt").stream());
    when(mockResolver.getField(
            Metacard.ANY_TEXT, AttributeFormat.STRING, true, Collections.emptyMap()))
        .thenReturn("any_text");
    SolrQuery result = toTest.propertyIsEqualTo(Metacard.ANY_TEXT, "exact value", true);
    assertThat(result.getQuery(), containsString("metadata_txt"));
  }

  @Test
  public void testRelativeTimeQuery() {
    when(mockResolver.getField("created", AttributeFormat.DATE, false, Collections.emptyMap()))
        .thenReturn("created_date");
    // 1 hour in milliseconds
    long duration = 3600000L;
    SolrQuery result = toTest.relative(Metacard.CREATED, duration);
    assertThat(result.getQuery(), containsString("created_date"));
    assertThat(result.getQuery(), containsString("["));
    assertThat(result.getQuery(), containsString("]"));
  }
}
