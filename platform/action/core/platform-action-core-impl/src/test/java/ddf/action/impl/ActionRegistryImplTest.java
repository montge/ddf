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
package ddf.action.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import ddf.action.Action;
import ddf.action.ActionProvider;
import ddf.action.ActionRegistry;
import ddf.action.MultiActionProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner.Silent;

/** Tests for {@link ActionRegistryImpl} class. */
@RunWith(Silent.class)
public class ActionRegistryImplTest {

  private static final String SUBJECT = "test-subject";
  private static final String PROVIDER_ID_1 = "provider-1";
  private static final String PROVIDER_ID_2 = "provider-2";
  private static final String MULTI_PROVIDER_ID_1 = "multi-provider-1";

  @Mock private ActionProvider actionProvider1;

  @Mock private ActionProvider actionProvider2;

  @Mock private MultiActionProvider multiActionProvider1;

  @Mock private Action action1;

  @Mock private Action action2;

  @Mock private Action action3;

  private ActionRegistryImpl registry;

  @BeforeEach
  public void setUp() {
    registry = new ActionRegistryImpl();

    when(actionProvider1.getId()).thenReturn(PROVIDER_ID_1);
    when(actionProvider2.getId()).thenReturn(PROVIDER_ID_2);
    when(multiActionProvider1.getId()).thenReturn(MULTI_PROVIDER_ID_1);
  }

  @Test
  public void testImplementsActionRegistry() {
    assertThat(registry instanceof ActionRegistry, is(true));
  }

  @Test
  public void testDefaultConstructor() {
    ActionRegistryImpl defaultRegistry = new ActionRegistryImpl();
    assertThat(defaultRegistry, is(notNullValue()));
  }

  @Test
  public void testConstructorWithProviders() {
    List<ActionProvider> actionProviders = Collections.singletonList(actionProvider1);
    List<MultiActionProvider> multiActionProviders =
        Collections.singletonList(multiActionProvider1);

    ActionRegistryImpl registryWithProviders =
        new ActionRegistryImpl(actionProviders, multiActionProviders);

    assertThat(registryWithProviders, is(notNullValue()));
  }

  @Test
  public void testListReturnsEmptyListWhenNoProviders() {
    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, is(notNullValue()));
    assertThat(actions, is(empty()));
  }

  @Test
  public void testListReturnsActionsFromActionProvider() {
    when(actionProvider1.getAction(SUBJECT)).thenReturn(action1);
    registry.addActionProvider(actionProvider1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, hasSize(1));
    assertThat(actions.get(0), is(action1));
  }

  @Test
  public void testListReturnsActionsFromMultipleActionProviders() {
    when(actionProvider1.getAction(SUBJECT)).thenReturn(action1);
    when(actionProvider2.getAction(SUBJECT)).thenReturn(action2);
    registry.addActionProvider(actionProvider1);
    registry.addActionProvider(actionProvider2);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, hasSize(2));
  }

  @Test
  public void testListFiltersNullActionsFromActionProviders() {
    when(actionProvider1.getAction(SUBJECT)).thenReturn(action1);
    when(actionProvider2.getAction(SUBJECT)).thenReturn(null);
    registry.addActionProvider(actionProvider1);
    registry.addActionProvider(actionProvider2);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, hasSize(1));
    assertThat(actions.get(0), is(action1));
  }

  @Test
  public void testListReturnsActionsFromMultiActionProvider() {
    when(multiActionProvider1.canHandle(SUBJECT)).thenReturn(true);
    when(multiActionProvider1.getActions(SUBJECT)).thenReturn(Arrays.asList(action1, action2));
    registry.addMultiActionProvider(multiActionProvider1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, hasSize(2));
  }

  @Test
  public void testListSkipsMultiActionProviderThatCannotHandle() {
    when(multiActionProvider1.canHandle(SUBJECT)).thenReturn(false);
    registry.addMultiActionProvider(multiActionProvider1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, is(empty()));
  }

  @Test
  public void testListFiltersNullActionsFromMultiActionProvider() {
    when(multiActionProvider1.canHandle(SUBJECT)).thenReturn(true);
    when(multiActionProvider1.getActions(SUBJECT))
        .thenReturn(Arrays.asList(action1, null, action2));
    registry.addMultiActionProvider(multiActionProvider1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, hasSize(2));
  }

  @Test
  public void testListCombinesActionsFromBothProviderTypes() {
    when(actionProvider1.getAction(SUBJECT)).thenReturn(action1);
    when(multiActionProvider1.canHandle(SUBJECT)).thenReturn(true);
    when(multiActionProvider1.getActions(SUBJECT)).thenReturn(Collections.singletonList(action2));
    registry.addActionProvider(actionProvider1);
    registry.addMultiActionProvider(multiActionProvider1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, hasSize(2));
  }

  @Test
  public void testAddActionProvider() {
    registry.addActionProvider(actionProvider1);
    when(actionProvider1.getAction(SUBJECT)).thenReturn(action1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, hasSize(1));
  }

  @Test
  public void testRemoveActionProvider() {
    when(actionProvider1.getAction(SUBJECT)).thenReturn(action1);
    registry.addActionProvider(actionProvider1);
    registry.removeActionProvider(actionProvider1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, is(empty()));
  }

  @Test
  public void testRemoveActionProviderById() {
    when(actionProvider1.getAction(SUBJECT)).thenReturn(action1);
    ActionProvider differentProviderSameId = org.mockito.Mockito.mock(ActionProvider.class);
    when(differentProviderSameId.getId()).thenReturn(PROVIDER_ID_1);

    registry.addActionProvider(actionProvider1);
    registry.removeActionProvider(differentProviderSameId);

    List<Action> actions = registry.list(SUBJECT);
    assertThat(actions, is(empty()));
  }

  @Test
  public void testAddMultiActionProvider() {
    when(multiActionProvider1.canHandle(SUBJECT)).thenReturn(true);
    when(multiActionProvider1.getActions(SUBJECT)).thenReturn(Collections.singletonList(action1));
    registry.addMultiActionProvider(multiActionProvider1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, hasSize(1));
  }

  @Test
  public void testRemoveMultiActionProvider() {
    when(multiActionProvider1.canHandle(SUBJECT)).thenReturn(true);
    when(multiActionProvider1.getActions(SUBJECT)).thenReturn(Collections.singletonList(action1));
    registry.addMultiActionProvider(multiActionProvider1);
    registry.removeMultiActionProvider(multiActionProvider1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, is(empty()));
  }

  @Test
  public void testRemoveMultiActionProviderById() {
    when(multiActionProvider1.canHandle(SUBJECT)).thenReturn(true);
    when(multiActionProvider1.getActions(SUBJECT)).thenReturn(Collections.singletonList(action1));
    MultiActionProvider differentProviderSameId =
        org.mockito.Mockito.mock(MultiActionProvider.class);
    when(differentProviderSameId.getId()).thenReturn(MULTI_PROVIDER_ID_1);

    registry.addMultiActionProvider(multiActionProvider1);
    registry.removeMultiActionProvider(differentProviderSameId);

    List<Action> actions = registry.list(SUBJECT);
    assertThat(actions, is(empty()));
  }

  @Test
  public void testListWithDifferentSubjectTypes() {
    Integer intSubject = 42;
    when(actionProvider1.getAction(intSubject)).thenReturn(action1);
    registry.addActionProvider(actionProvider1);

    List<Action> actions = registry.list(intSubject);

    assertThat(actions, hasSize(1));
  }

  @Test
  public void testListWithNullSubject() {
    when(actionProvider1.getAction(null)).thenReturn(null);
    registry.addActionProvider(actionProvider1);

    List<Action> actions = registry.list(null);

    assertThat(actions, is(empty()));
  }

  @Test
  public void testListReturnsDedupedActionsFromMultiActionProviders() {
    when(multiActionProvider1.canHandle(SUBJECT)).thenReturn(true);
    when(multiActionProvider1.getActions(SUBJECT)).thenReturn(Arrays.asList(action1, action1));
    registry.addMultiActionProvider(multiActionProvider1);

    List<Action> actions = registry.list(SUBJECT);

    assertThat(actions, hasSize(1));
  }
}
