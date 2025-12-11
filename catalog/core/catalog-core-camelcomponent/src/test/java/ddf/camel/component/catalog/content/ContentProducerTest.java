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
package ddf.camel.component.catalog.content;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ddf.catalog.Constants;
import ddf.catalog.source.IngestException;
import ddf.catalog.source.SourceUnavailableException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.codice.ddf.platform.util.uuidgenerator.UuidGenerator;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableRuleMigrationSupport
@ExtendWith(MockitoExtension.class)
public class ContentProducerTest {

  @Mock private ContentEndpoint mockEndpoint;

  @Mock private ContentComponent mockContentComponent;

  @Mock private UuidGenerator mockUuidGenerator;

  @Mock private ContentProducerDataAccessObject mockContentProducerDao;

  @Rule public TemporaryFolder testDir = new TemporaryFolder();

  private ContentProducer contentProducer;

  @BeforeEach
  public void setUp() {
    when(mockUuidGenerator.generateUuid()).thenReturn(UUID.randomUUID().toString());
    when(mockEndpoint.getComponent()).thenReturn(mockContentComponent);
    when(mockContentComponent.getUuidGenerator()).thenReturn(mockUuidGenerator);

    contentProducer = new ContentProducer(mockEndpoint);
    contentProducer.contentProducerDataAccessObject = mockContentProducerDao;
  }

  @Test
  public void testConstructor() {
    ContentProducer producer = new ContentProducer(mockEndpoint);
    assertThat(producer, is(notNullValue()));
  }

  @Test
  public void testConstructorWithNullComponent() {
    when(mockEndpoint.getComponent()).thenReturn(null);
    ContentProducer producer = new ContentProducer(mockEndpoint);
    assertThat(producer, is(notNullValue()));
  }

  @Test
  public void testProcessWithStoreReferenceKey() throws Exception {
    File testFile = testDir.newFile("test.xml");

    Message mockMessage = mock(Message.class);
    Map<String, Object> headers = new HashMap<>();
    headers.put(Constants.STORE_REFERENCE_KEY, true);
    doReturn(headers).when(mockMessage).getHeaders();

    WatchEvent.Kind<Path> eventType = StandardWatchEventKinds.ENTRY_CREATE;

    doReturn(testFile).when(mockContentProducerDao).getFileUsingRefKey(true, mockMessage);
    doReturn(eventType).when(mockContentProducerDao).getEventType(true, mockMessage);
    doReturn("application/xml").when(mockContentProducerDao).getMimeType(mockEndpoint, testFile);

    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOnly).when(mockExchange).getPattern();
    doReturn(mockMessage).when(mockExchange).getIn();

    contentProducer.process(mockExchange);

    verify(mockContentProducerDao, times(1)).getFileUsingRefKey(true, mockMessage);
    verify(mockContentProducerDao, times(1)).getEventType(true, mockMessage);
    verify(mockContentProducerDao, times(1)).getMimeType(mockEndpoint, testFile);
    verify(mockContentProducerDao, times(1))
        .createContentItem(
            any(),
            eq(mockEndpoint),
            eq(testFile),
            eq(eventType),
            eq("application/xml"),
            eq(headers));
  }

  @Test
  public void testProcessWithoutStoreReferenceKey() throws Exception {
    File testFile = testDir.newFile("test.txt");

    Message mockMessage = mock(Message.class);
    Map<String, Object> headers = new HashMap<>();
    doReturn(headers).when(mockMessage).getHeaders();

    WatchEvent.Kind<Path> eventType = StandardWatchEventKinds.ENTRY_CREATE;

    doReturn(testFile).when(mockContentProducerDao).getFileUsingRefKey(false, mockMessage);
    doReturn(eventType).when(mockContentProducerDao).getEventType(false, mockMessage);
    doReturn("text/plain").when(mockContentProducerDao).getMimeType(mockEndpoint, testFile);

    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOnly).when(mockExchange).getPattern();
    doReturn(mockMessage).when(mockExchange).getIn();

    contentProducer.process(mockExchange);

    verify(mockContentProducerDao, times(1)).getFileUsingRefKey(false, mockMessage);
    verify(mockContentProducerDao, times(1)).getEventType(false, mockMessage);
    verify(mockContentProducerDao, times(1)).getMimeType(mockEndpoint, testFile);
    verify(mockContentProducerDao, times(1))
        .createContentItem(
            any(), eq(mockEndpoint), eq(testFile), eq(eventType), eq("text/plain"), eq(headers));
  }

  @Test
  public void testProcessWithNullMimeType() throws Exception {
    File testFile = testDir.newFile("test.bin");

    Message mockMessage = mock(Message.class);
    Map<String, Object> headers = new HashMap<>();
    doReturn(headers).when(mockMessage).getHeaders();

    WatchEvent.Kind<Path> eventType = StandardWatchEventKinds.ENTRY_CREATE;

    doReturn(testFile).when(mockContentProducerDao).getFileUsingRefKey(false, mockMessage);
    doReturn(eventType).when(mockContentProducerDao).getEventType(false, mockMessage);
    doReturn(null).when(mockContentProducerDao).getMimeType(mockEndpoint, testFile);

    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOnly).when(mockExchange).getPattern();
    doReturn(mockMessage).when(mockExchange).getIn();

    contentProducer.process(mockExchange);

    verify(mockContentProducerDao, times(1))
        .createContentItem(
            any(),
            eq(mockEndpoint),
            eq(testFile),
            eq(eventType),
            eq("application/octet-stream"),
            eq(headers));
  }

  @Test
  public void testProcessWithEmptyMimeType() throws Exception {
    File testFile = testDir.newFile("test.unknown");

    Message mockMessage = mock(Message.class);
    Map<String, Object> headers = new HashMap<>();
    doReturn(headers).when(mockMessage).getHeaders();

    WatchEvent.Kind<Path> eventType = StandardWatchEventKinds.ENTRY_CREATE;

    doReturn(testFile).when(mockContentProducerDao).getFileUsingRefKey(false, mockMessage);
    doReturn(eventType).when(mockContentProducerDao).getEventType(false, mockMessage);
    doReturn("").when(mockContentProducerDao).getMimeType(mockEndpoint, testFile);

    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOnly).when(mockExchange).getPattern();
    doReturn(mockMessage).when(mockExchange).getIn();

    contentProducer.process(mockExchange);

    verify(mockContentProducerDao, times(1))
        .createContentItem(
            any(),
            eq(mockEndpoint),
            eq(testFile),
            eq(eventType),
            eq("application/octet-stream"),
            eq(headers));
  }

  @Test
  public void testProcessWithNonInOnlyExchangePattern() throws Exception {
    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOut).when(mockExchange).getPattern();

    contentProducer.process(mockExchange);

    verifyNoInteractions(mockContentProducerDao);
  }

  @Test
  public void testProcessWithInOptionalOutExchangePattern() throws Exception {
    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOptionalOut).when(mockExchange).getPattern();

    contentProducer.process(mockExchange);

    verifyNoInteractions(mockContentProducerDao);
  }

  @Test
  public void testProcessThrowsSourceUnavailableException() throws Exception {
    File testFile = testDir.newFile("test.txt");

    Message mockMessage = mock(Message.class);
    Map<String, Object> headers = new HashMap<>();
    doReturn(headers).when(mockMessage).getHeaders();

    WatchEvent.Kind<Path> eventType = StandardWatchEventKinds.ENTRY_CREATE;

    doReturn(testFile).when(mockContentProducerDao).getFileUsingRefKey(false, mockMessage);
    doReturn(eventType).when(mockContentProducerDao).getEventType(false, mockMessage);
    doReturn("text/plain").when(mockContentProducerDao).getMimeType(mockEndpoint, testFile);
    doThrow(new SourceUnavailableException("Source unavailable"))
        .when(mockContentProducerDao)
        .createContentItem(any(), any(), any(), any(), any(), any());

    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOnly).when(mockExchange).getPattern();
    doReturn(mockMessage).when(mockExchange).getIn();

    assertThrows(SourceUnavailableException.class, () -> contentProducer.process(mockExchange));
  }

  @Test
  public void testProcessThrowsIngestException() throws Exception {
    File testFile = testDir.newFile("test.txt");

    Message mockMessage = mock(Message.class);
    Map<String, Object> headers = new HashMap<>();
    doReturn(headers).when(mockMessage).getHeaders();

    WatchEvent.Kind<Path> eventType = StandardWatchEventKinds.ENTRY_CREATE;

    doReturn(testFile).when(mockContentProducerDao).getFileUsingRefKey(false, mockMessage);
    doReturn(eventType).when(mockContentProducerDao).getEventType(false, mockMessage);
    doReturn("text/plain").when(mockContentProducerDao).getMimeType(mockEndpoint, testFile);
    doThrow(new IngestException("Ingest failed"))
        .when(mockContentProducerDao)
        .createContentItem(any(), any(), any(), any(), any(), any());

    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOnly).when(mockExchange).getPattern();
    doReturn(mockMessage).when(mockExchange).getIn();

    assertThrows(IngestException.class, () -> contentProducer.process(mockExchange));
  }

  @Test
  public void testProcessThrowsContentComponentException() throws Exception {
    File testFile = testDir.newFile("test.txt");

    Message mockMessage = mock(Message.class);
    Map<String, Object> headers = new HashMap<>();
    doReturn(headers).when(mockMessage).getHeaders();

    WatchEvent.Kind<Path> eventType = StandardWatchEventKinds.ENTRY_CREATE;

    doReturn(testFile).when(mockContentProducerDao).getFileUsingRefKey(false, mockMessage);
    doReturn(eventType).when(mockContentProducerDao).getEventType(false, mockMessage);
    doThrow(new ContentComponentException("Content error"))
        .when(mockContentProducerDao)
        .getMimeType(mockEndpoint, testFile);

    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOnly).when(mockExchange).getPattern();
    doReturn(mockMessage).when(mockExchange).getIn();

    assertThrows(ContentComponentException.class, () -> contentProducer.process(mockExchange));
  }

  @Test
  public void testProcessWithModifyEvent() throws Exception {
    File testFile = testDir.newFile("modify-test.txt");

    Message mockMessage = mock(Message.class);
    Map<String, Object> headers = new HashMap<>();
    headers.put(Constants.STORE_REFERENCE_KEY, true);
    doReturn(headers).when(mockMessage).getHeaders();

    WatchEvent.Kind<Path> eventType = StandardWatchEventKinds.ENTRY_MODIFY;

    doReturn(testFile).when(mockContentProducerDao).getFileUsingRefKey(true, mockMessage);
    doReturn(eventType).when(mockContentProducerDao).getEventType(true, mockMessage);
    doReturn("text/plain").when(mockContentProducerDao).getMimeType(mockEndpoint, testFile);

    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOnly).when(mockExchange).getPattern();
    doReturn(mockMessage).when(mockExchange).getIn();

    contentProducer.process(mockExchange);

    verify(mockContentProducerDao, times(1))
        .createContentItem(
            any(), eq(mockEndpoint), eq(testFile), eq(eventType), eq("text/plain"), eq(headers));
  }

  @Test
  public void testProcessWithDeleteEvent() throws Exception {
    File testFile = testDir.newFile("delete-test.txt");

    Message mockMessage = mock(Message.class);
    Map<String, Object> headers = new HashMap<>();
    headers.put(Constants.STORE_REFERENCE_KEY, true);
    doReturn(headers).when(mockMessage).getHeaders();

    WatchEvent.Kind<Path> eventType = StandardWatchEventKinds.ENTRY_DELETE;

    doReturn(testFile).when(mockContentProducerDao).getFileUsingRefKey(true, mockMessage);
    doReturn(eventType).when(mockContentProducerDao).getEventType(true, mockMessage);
    doReturn("text/plain").when(mockContentProducerDao).getMimeType(mockEndpoint, testFile);

    Exchange mockExchange = mock(Exchange.class);
    doReturn(ExchangePattern.InOnly).when(mockExchange).getPattern();
    doReturn(mockMessage).when(mockExchange).getIn();

    contentProducer.process(mockExchange);

    verify(mockContentProducerDao, times(1))
        .createContentItem(
            any(), eq(mockEndpoint), eq(testFile), eq(eventType), eq("text/plain"), eq(headers));
  }
}
