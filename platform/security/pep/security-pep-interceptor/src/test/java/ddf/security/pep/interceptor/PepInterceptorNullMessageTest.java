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
package ddf.security.pep.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.security.audit.SecurityLogger;
import org.apache.cxf.interceptor.security.AccessDeniedException;
import org.junit.jupiter.api.Test;

public class PepInterceptorNullMessageTest {

  @Test
  public void testNullMessage() {
    PEPAuthorizingInterceptor interceptor = new PEPAuthorizingInterceptor();
    interceptor.setSecurityLogger(mock(SecurityLogger.class));

    AccessDeniedException exception =
        assertThrows(AccessDeniedException.class, () -> interceptor.handleMessage(null));
    assertThat(exception.getMessage(), containsString("Unauthorized"));
  }
}
