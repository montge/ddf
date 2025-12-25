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
package org.codice.solr.factory.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.solr.client.solrj.SolrClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SolrClientFactoryImplTest {

  @Mock private SolrClient mockClient;

  @Test
  public void newClientWithNullCoreName() {
    SolrClientFactoryImpl factory = new SolrClientFactoryImpl();
    // Validate.notNull() throws NullPointerException, not IllegalArgumentException
    assertThrows(NullPointerException.class, () -> factory.newClient(null));
  }

  @Test
  public void newCloudSolrClient() {
    System.setProperty("solr.client", "CloudSolrClient");
    SolrClientFactoryImpl factory = new SolrClientFactoryImpl();

    assertThat(factory.getFactory(), is(instanceOf(SolrCloudClientFactory.class)));
  }

  @Test
  public void newClientWithUnknownClientType() {
    System.setProperty("solr.client", "Unknown");
    SolrClientFactoryImpl factory = new SolrClientFactoryImpl();

    assertThat(factory.getFactory(), is(instanceOf(SolrCloudClientFactory.class)));
  }
}
