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
package ddf.catalog.metacard.versioning;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.core.versioning.DeletedMetacard;
import ddf.catalog.core.versioning.MetacardVersion;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.operation.Query;
import ddf.catalog.operation.ResourceRequest;
import ddf.catalog.operation.ResourceResponse;
import ddf.catalog.plugin.PolicyPlugin;
import ddf.catalog.plugin.PolicyResponse;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link HistorianPolicyPlugin} class. */
@RunWith(MockitoJUnitRunner.class)
public class HistorianPolicyPluginTest {

  @Mock private Query query;

  @Mock private Result result;

  @Mock private ResourceRequest resourceRequest;

  @Mock private ResourceResponse resourceResponse;

  private HistorianPolicyPlugin plugin;

  private Map<String, Serializable> properties;

  private Metacard regularMetacard;

  @Before
  public void setUp() {
    plugin = new HistorianPolicyPlugin();
    properties = new HashMap<>();

    // Regular metacard - no version or deleted tags, using real MetacardImpl
    regularMetacard = new MetacardImpl();
    ((MetacardImpl) regularMetacard).setTags(Collections.singleton("resource"));
  }

  @Test
  public void testImplementsPolicyPlugin() {
    assertThat(plugin instanceof PolicyPlugin, is(true));
  }

  @Test
  public void testHistoryRoleConstant() {
    assertThat(HistorianPolicyPlugin.HISTORY_ROLE, is("system-history"));
  }

  @Test
  public void testRoleClaimConstant() {
    assertThat(
        HistorianPolicyPlugin.ROLE_CLAIM,
        is("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role"));
  }

  @Test
  public void testVersionTagConstant() {
    assertThat(MetacardVersion.VERSION_TAG, is("revision"));
  }

  @Test
  public void testDeletedTagConstant() {
    assertThat(DeletedMetacard.DELETED_TAG, is("deleted"));
  }

  // Test predicate behavior via reflection

  @Test
  @SuppressWarnings("unchecked")
  public void testIsMetacardHistoryPredicateDetectsVersionTag() throws Exception {
    MetacardImpl versionedMetacard = new MetacardImpl();
    versionedMetacard.setTags(Collections.singleton("revision"));

    Field field = HistorianPolicyPlugin.class.getDeclaredField("isMetacardHistory");
    field.setAccessible(true);
    Predicate<Metacard> predicate = (Predicate<Metacard>) field.get(plugin);

    assertThat(predicate.test(versionedMetacard), is(true));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testIsDeletedMetacardPredicateDetectsDeletedTag() throws Exception {
    MetacardImpl deletedMetacard = new MetacardImpl();
    deletedMetacard.setTags(Collections.singleton("deleted"));

    Field field = HistorianPolicyPlugin.class.getDeclaredField("isDeletedMetacard");
    field.setAccessible(true);
    Predicate<Metacard> predicate = (Predicate<Metacard>) field.get(plugin);

    assertThat(predicate.test(deletedMetacard), is(true));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testIsHistoryOrDeletedPredicateDetectsVersionTag() throws Exception {
    MetacardImpl versionedMetacard = new MetacardImpl();
    versionedMetacard.setTags(Collections.singleton("revision"));

    Field field = HistorianPolicyPlugin.class.getDeclaredField("isHistoryOrDeleted");
    field.setAccessible(true);
    Predicate<Metacard> predicate = (Predicate<Metacard>) field.get(plugin);

    assertThat(predicate.test(versionedMetacard), is(true));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testIsHistoryOrDeletedPredicateDetectsDeletedTag() throws Exception {
    MetacardImpl deletedMetacard = new MetacardImpl();
    deletedMetacard.setTags(Collections.singleton("deleted"));

    Field field = HistorianPolicyPlugin.class.getDeclaredField("isHistoryOrDeleted");
    field.setAccessible(true);
    Predicate<Metacard> predicate = (Predicate<Metacard>) field.get(plugin);

    assertThat(predicate.test(deletedMetacard), is(true));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testIsHistoryOrDeletedPredicateReturnsFalseForRegularMetacard() throws Exception {
    Field field = HistorianPolicyPlugin.class.getDeclaredField("isHistoryOrDeleted");
    field.setAccessible(true);
    Predicate<Metacard> predicate = (Predicate<Metacard>) field.get(plugin);

    assertThat(predicate.test(regularMetacard), is(false));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testIsHistoryOrDeletedPredicateReturnsFalseForNullMetacard() throws Exception {
    Field field = HistorianPolicyPlugin.class.getDeclaredField("isHistoryOrDeleted");
    field.setAccessible(true);
    Predicate<Metacard> predicate = (Predicate<Metacard>) field.get(plugin);

    assertThat(predicate.test(null), is(false));
  }

  // processPreCreate tests

  @Test
  public void testProcessPreCreateWithRegularMetacard() throws Exception {
    PolicyResponse response = plugin.processPreCreate(regularMetacard, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.itemPolicy().entrySet(), is(empty()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  @Test
  public void testProcessPreCreateWithNullMetacard() throws Exception {
    PolicyResponse response = plugin.processPreCreate(null, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.itemPolicy().entrySet(), is(empty()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  @Test
  public void testProcessPreCreateWithNullTags() throws Exception {
    Metacard metacardWithNullTags = mock(Metacard.class);
    when(metacardWithNullTags.getTags()).thenReturn(null);

    PolicyResponse response = plugin.processPreCreate(metacardWithNullTags, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  @Test
  public void testProcessPreCreateWithEmptyTags() throws Exception {
    MetacardImpl metacardWithEmptyTags = new MetacardImpl();
    metacardWithEmptyTags.setTags(Collections.emptySet());

    PolicyResponse response = plugin.processPreCreate(metacardWithEmptyTags, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  // processPreUpdate tests

  @Test
  public void testProcessPreUpdateWithRegularMetacard() throws Exception {
    PolicyResponse response = plugin.processPreUpdate(regularMetacard, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.itemPolicy().entrySet(), is(empty()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  @Test
  public void testProcessPreUpdateWithNullMetacard() throws Exception {
    PolicyResponse response = plugin.processPreUpdate(null, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  // processPreDelete tests

  @Test
  public void testProcessPreDeleteWithRegularMetacards() throws Exception {
    List<Metacard> metacards = Collections.singletonList(regularMetacard);

    PolicyResponse response = plugin.processPreDelete(metacards, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.itemPolicy().entrySet(), is(empty()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  @Test
  public void testProcessPreDeleteWithEmptyList() throws Exception {
    List<Metacard> metacards = Collections.emptyList();

    PolicyResponse response = plugin.processPreDelete(metacards, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  // Policy methods that always return empty PolicyResponse

  @Test
  public void testProcessPostDeleteReturnsEmptyPolicy() throws Exception {
    PolicyResponse response = plugin.processPostDelete(regularMetacard, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.itemPolicy().entrySet(), is(empty()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  @Test
  public void testProcessPreQueryReturnsEmptyPolicy() throws Exception {
    PolicyResponse response = plugin.processPreQuery(query, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.itemPolicy().entrySet(), is(empty()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  @Test
  public void testProcessPostQueryReturnsEmptyPolicy() throws Exception {
    PolicyResponse response = plugin.processPostQuery(result, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.itemPolicy().entrySet(), is(empty()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  @Test
  public void testProcessPreResourceReturnsEmptyPolicy() throws Exception {
    PolicyResponse response = plugin.processPreResource(resourceRequest);

    assertThat(response, is(notNullValue()));
    assertThat(response.itemPolicy().entrySet(), is(empty()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  @Test
  public void testProcessPostResourceReturnsEmptyPolicy() throws Exception {
    PolicyResponse response = plugin.processPostResource(resourceResponse, regularMetacard);

    assertThat(response, is(notNullValue()));
    assertThat(response.itemPolicy().entrySet(), is(empty()));
    assertThat(response.operationPolicy().entrySet(), is(empty()));
  }

  // MetacardImpl.getTags test helper

  @Test
  public void testMetacardImplSetAndGetTags() {
    MetacardImpl metacard = new MetacardImpl();
    Set<String> tags = Collections.singleton("test-tag");
    metacard.setTags(tags);

    assertThat(metacard.getTags(), is(tags));
    assertThat(metacard.getTags().contains("test-tag"), is(true));
  }
}
