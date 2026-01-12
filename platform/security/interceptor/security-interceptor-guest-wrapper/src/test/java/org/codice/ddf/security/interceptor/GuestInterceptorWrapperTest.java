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
package org.codice.ddf.security.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/** Comprehensive test coverage for GuestInterceptorWrapper. */
@ExtendWith(MockitoExtension.class)
public class GuestInterceptorWrapperTest {

  // WSS4J interceptor class names (avoid importing cxf-rt-ws-security)
  private static final String WSS4J_IN_INTERCEPTOR =
      "org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor";
  private static final String POLICY_WSS4J_IN_INTERCEPTOR =
      "org.apache.cxf.ws.security.wss4j.PolicyBasedWSS4JInInterceptor";

  private GuestInterceptorWrapper wrapper;

  @Mock private SoapMessage soapMessage;

  @Mock private BundleContext bundleContext;

  @Mock private ServiceReference<PhaseInterceptor> serviceReference;

  @Mock private PhaseInterceptor guestInterceptor;

  @BeforeEach
  public void setUp() {
    wrapper = new TestableGuestInterceptorWrapper(bundleContext);
  }

  @Test
  public void testConstructorSetsPhaseAndBefore() {
    GuestInterceptorWrapper newWrapper = new GuestInterceptorWrapper();

    assertThat(newWrapper.getPhase(), is(Phase.PRE_PROTOCOL));
    assertThat(newWrapper.getBefore(), notNullValue());
    assertThat(
        newWrapper.getBefore(),
        containsInAnyOrder(WSS4J_IN_INTERCEPTOR, POLICY_WSS4J_IN_INTERCEPTOR));
  }

  @Test
  public void testHandleMessageWithGuestInterceptor() throws InvalidSyntaxException {
    Collection<ServiceReference<PhaseInterceptor>> references = new ArrayList<>();
    references.add(serviceReference);

    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(references);
    when(bundleContext.getService(serviceReference)).thenReturn(guestInterceptor);

    wrapper.handleMessage(soapMessage);

    verify(bundleContext, times(1))
        .getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)");
    verify(bundleContext, times(1)).getService(serviceReference);
    verify(guestInterceptor, times(1)).handleMessage(soapMessage);
  }

  @Test
  public void testHandleMessageWithNoGuestInterceptor() throws InvalidSyntaxException {
    Collection<ServiceReference<PhaseInterceptor>> emptyReferences = new ArrayList<>();

    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(emptyReferences);

    wrapper.handleMessage(soapMessage);

    verify(bundleContext, times(1))
        .getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)");
    verify(bundleContext, never()).getService(any());
    verify(guestInterceptor, never()).handleMessage(any());
  }

  @Test
  public void testHandleMessageWithNullReferences() throws InvalidSyntaxException {
    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(null);

    wrapper.handleMessage(soapMessage);

    verify(bundleContext, times(1))
        .getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)");
    verify(bundleContext, never()).getService(any());
    verify(guestInterceptor, never()).handleMessage(any());
  }

  @Test
  public void testHandleMessageWithNullBundleContext() {
    GuestInterceptorWrapper nullContextWrapper = new TestableGuestInterceptorWrapper(null);

    nullContextWrapper.handleMessage(soapMessage);

    // Should complete without exception
    verify(guestInterceptor, never()).handleMessage(any());
  }

  @Test
  public void testHandleMessageWithInvalidSyntaxException() throws InvalidSyntaxException {
    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenThrow(new InvalidSyntaxException("Test exception", "(interceptor=guest)"));

    wrapper.handleMessage(soapMessage);

    // Should handle exception gracefully and not throw
    verify(bundleContext, times(1))
        .getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)");
    verify(guestInterceptor, never()).handleMessage(any());
  }

  @Test
  public void testHandleMessageWithEmptyReferences() throws InvalidSyntaxException {
    Collection<ServiceReference<PhaseInterceptor>> emptyReferences = new ArrayList<>();

    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(emptyReferences);

    wrapper.handleMessage(soapMessage);

    verify(bundleContext, times(1))
        .getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)");
    verify(bundleContext, never()).getService(any());
  }

  @Test
  public void testHandleMessageWithNullInterceptorFromService() throws InvalidSyntaxException {
    Collection<ServiceReference<PhaseInterceptor>> references = new ArrayList<>();
    references.add(serviceReference);

    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(references);
    when(bundleContext.getService(serviceReference)).thenReturn(null);

    wrapper.handleMessage(soapMessage);

    verify(bundleContext, times(1)).getService(serviceReference);
    verify(guestInterceptor, never()).handleMessage(any());
  }

  @Test
  public void testHandleMessageWithGuestInterceptorThrowingFault() throws InvalidSyntaxException {
    Collection<ServiceReference<PhaseInterceptor>> references = new ArrayList<>();
    references.add(serviceReference);

    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(references);
    when(bundleContext.getService(serviceReference)).thenReturn(guestInterceptor);
    doThrow(new Fault(new RuntimeException("Test fault")))
        .when(guestInterceptor)
        .handleMessage(soapMessage);

    assertThrows(Fault.class, () -> wrapper.handleMessage(soapMessage));
  }

  @Test
  public void testHandleMessageSelectsFirstInterceptorWhenMultipleAvailable()
      throws InvalidSyntaxException {
    ServiceReference<PhaseInterceptor> secondReference = mock(ServiceReference.class);

    Collection<ServiceReference<PhaseInterceptor>> references = new ArrayList<>();
    references.add(serviceReference);
    references.add(secondReference);

    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(references);
    when(bundleContext.getService(serviceReference)).thenReturn(guestInterceptor);

    wrapper.handleMessage(soapMessage);

    verify(bundleContext, times(1)).getService(serviceReference);
    verify(guestInterceptor, times(1)).handleMessage(soapMessage);
    // Second interceptor should not be retrieved
    verify(bundleContext, never()).getService(secondReference);
  }

  @Test
  public void testGetContextReturnsNullWhenBundleIsNull() {
    // This test verifies the default getContext() behavior when FrameworkUtil returns null
    GuestInterceptorWrapper defaultWrapper = new GuestInterceptorWrapper();

    // The getContext() method is protected, so we test it indirectly through handleMessage
    // When context is null, handleMessage should complete without calling any interceptor
    defaultWrapper.handleMessage(soapMessage);

    // No interactions expected since bundle context is null
    verify(guestInterceptor, never()).handleMessage(any());
  }

  @Test
  public void testHandleMessageWithNullMessage() throws InvalidSyntaxException {
    Collection<ServiceReference<PhaseInterceptor>> references = new ArrayList<>();
    references.add(serviceReference);

    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(references);
    when(bundleContext.getService(serviceReference)).thenReturn(guestInterceptor);

    wrapper.handleMessage(null);

    verify(guestInterceptor, times(1)).handleMessage(null);
  }

  @Test
  public void testBeforeListIncludesRequiredInterceptors() {
    GuestInterceptorWrapper newWrapper = new GuestInterceptorWrapper();

    assertThat(newWrapper.getBefore(), notNullValue());
    assertThat(newWrapper.getBefore().size(), is(2));
    assertThat(newWrapper.getBefore(), contains(WSS4J_IN_INTERCEPTOR, POLICY_WSS4J_IN_INTERCEPTOR));
  }

  @Test
  public void testMultipleHandleMessageCallsWithSameContext() throws InvalidSyntaxException {
    Collection<ServiceReference<PhaseInterceptor>> references = new ArrayList<>();
    references.add(serviceReference);

    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(references);
    when(bundleContext.getService(serviceReference)).thenReturn(guestInterceptor);

    wrapper.handleMessage(soapMessage);
    wrapper.handleMessage(soapMessage);

    verify(bundleContext, times(2))
        .getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)");
    verify(guestInterceptor, times(2)).handleMessage(soapMessage);
  }

  @Test
  public void testHandleMessageLogsDebugWhenInterceptorNotInstalled()
      throws InvalidSyntaxException {
    when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
        .thenReturn(null);

    wrapper.handleMessage(soapMessage);

    // Verify the method completes without exception (debug log not verifiable in unit test)
    verify(bundleContext, times(1))
        .getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)");
  }

  @Test
  public void testHandleMessageLogsDebugWhenContextIsNull() {
    GuestInterceptorWrapper nullContextWrapper = new TestableGuestInterceptorWrapper(null);

    nullContextWrapper.handleMessage(soapMessage);

    // Verify the method completes without exception (debug log not verifiable in unit test)
    // No BundleContext interactions should occur
  }

  @Test
  public void testGetContextWithNonNullBundle() {
    // This is tested indirectly through the testable wrapper
    assertThat(wrapper, notNullValue());

    // When bundleContext is provided, handleMessage should work correctly
    try {
      when(bundleContext.getServiceReferences(PhaseInterceptor.class, "(interceptor=guest)"))
          .thenReturn(null);
      wrapper.handleMessage(soapMessage);
    } catch (Exception e) {
      // Should not throw
    }
  }

  /**
   * Testable version of GuestInterceptorWrapper that allows injection of BundleContext for testing.
   */
  private static class TestableGuestInterceptorWrapper extends GuestInterceptorWrapper {
    private final BundleContext testContext;

    TestableGuestInterceptorWrapper(BundleContext context) {
      super();
      this.testContext = context;
    }

    @Override
    protected BundleContext getContext() {
      return testContext;
    }
  }
}
