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
package ddf.security.samlp.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;

public class RelayStatesConverterTest {

  private RelayStatesConverter converter;
  private ReifiedType targetType;

  @BeforeEach
  public void setUp() {
    converter = new RelayStatesConverter();
    targetType = mock(ReifiedType.class);
  }

  @Test
  public void testImplementsConverterInterface() {
    assertThat(converter, is(instanceOf(Converter.class)));
  }

  @Test
  public void testCanConvertWithRelayStatesReturnsTrue() {
    RelayStates<String> relayStates = new RelayStates<>();

    boolean result = converter.canConvert(relayStates, targetType);

    assertThat(result, is(true));
  }

  @Test
  public void testCanConvertWithNonRelayStatesReturnsFalse() {
    String notRelayStates = "not a relay states object";

    boolean result = converter.canConvert(notRelayStates, targetType);

    assertThat(result, is(false));
  }

  @Test
  public void testCanConvertWithNullReturnsFalse() {
    boolean result = converter.canConvert(null, targetType);

    assertThat(result, is(false));
  }

  @Test
  public void testCanConvertWithOtherObjectsReturnsFalse() {
    assertThat(converter.canConvert(123, targetType), is(false));
    assertThat(converter.canConvert(new Object(), targetType), is(false));
    assertThat(converter.canConvert(new java.util.HashMap<>(), targetType), is(false));
  }

  @Test
  public void testConvertReturnsSameObject() throws Exception {
    RelayStates<String> relayStates = new RelayStates<>();

    Object result = converter.convert(relayStates, targetType);

    assertThat(result, is(sameInstance(relayStates)));
  }

  @Test
  public void testConvertWithNullReturnsNull() throws Exception {
    Object result = converter.convert(null, targetType);

    assertThat(result, is((Object) null));
  }

  @Test
  public void testConvertPreservesRelayStatesData() throws Exception {
    RelayStates<String> relayStates = new RelayStates<>();
    String key = relayStates.encode("test data");

    Object result = converter.convert(relayStates, targetType);

    assertThat(result, is(instanceOf(RelayStates.class)));
    @SuppressWarnings("unchecked")
    RelayStates<String> convertedRelayStates = (RelayStates<String>) result;
    assertThat(convertedRelayStates.decode(key), is("test data"));
  }

  @Test
  public void testConverterCanBeInstantiated() {
    RelayStatesConverter newConverter = new RelayStatesConverter();

    assertThat(newConverter, is(instanceOf(RelayStatesConverter.class)));
  }
}
