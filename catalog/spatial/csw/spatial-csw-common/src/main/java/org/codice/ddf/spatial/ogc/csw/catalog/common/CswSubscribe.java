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
package org.codice.ddf.spatial.ogc.csw.catalog.common;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.opengis.cat.csw.v_2_0_2.GetRecordsResponseType;
import net.opengis.cat.csw.v_2_0_2.GetRecordsType;

/** JAX-RS Interface to define an CSW Subscription service. */
@Path("/")
public interface CswSubscribe {

  /**
   * Deletes an active subscription
   *
   * @param requestId the requestId of the subscription to be removed
   * @return Response will contain a CSW Acknowledgment message with the subscription that was
   *     deleted or an empty 404 if none are found
   * @throws CswException for validation errors returns a 400
   */
  @DELETE
  @Path("/{requestId}")
  @Produces({MediaType.WILDCARD})
  Response deleteRecordsSubscription(@PathParam("requestId") String requestId) throws CswException;

  /**
   * Get an active subscription
   *
   * @param requestId the requestId of the subscription to get
   * @return returns a response containing a CSW Acknowledgment message with the subscription that
   *     was found or an empty 404 if none are found
   * @throws CswException for validation errors returns a 400
   */
  @GET
  @Path("/{requestId}")
  @Produces({MediaType.WILDCARD})
  Response getRecordsSubscription(@PathParam("requestId") String requestId) throws CswException;

  /**
   * Updates an active subscription
   *
   * @param requestId the requestId of the subscription to get
   * @param request the GetRocordsType request which contains the filter that the subscription is
   *     subscribing too and a ResponseHandler URL that will handle the CswRecordCollection response
   *     messages. When a create, update or delete event is received a CswRecordCollection will be
   *     sent via a POST, PUT or DELETE to the ResponseHandler URL.
   * @return Response will contain a CSW Acknowledgment message with the subscription that was
   *     updated or added
   * @throws CswException for validation errors returns a 400
   */
  @PUT
  @Path("/{requestId}")
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.WILDCARD})
  Response updateRecordsSubscription(
      @PathParam("requestId") String requestId, GetRecordsType request) throws CswException;

  /**
   * Create a subscription
   *
   * @param request the GetRecordsRequest request which contains the filter that the subscription is
   *     subscribing too and a ResponseHandler URL that will handle the CswRecordCollection response
   *     messages. When a create, update or delete event is received a CswRecordCollection will be
   *     sent via a POST, PUT or DELETE to the ResponseHandler URL.
   * @return Response will contain a CSW Acknowledgment message with the subscription that was added
   * @throws CswException for validation errors returns a 400
   */
  @GET
  @Produces({MediaType.WILDCARD})
  Response createRecordsSubscription(@QueryParam("") GetRecordsRequest request) throws CswException;

  /**
   * Create a subscription
   *
   * @param request the GetRecordsType request which contains the filter that the subscription is
   *     subscribing too and a ResponseHandler URL that will handle the CswRecordCollection response
   *     messages. When a create, update or delete event is received a CswRecordCollection will be
   *     sent via a POST, PUT or DELETE to the ResponseHandler URL
   * @return Response will contain a CSW Acknowledgment message with the subscription that was added
   * @throws CswException for validation errors returns a 400
   */
  @POST
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.WILDCARD})
  Response createRecordsSubscription(GetRecordsType request) throws CswException;

  /**
   * Consume a create event
   *
   * @param recordsResponse the GetRecordsResponseType search results must be urn:catalog:metacard
   *     format
   * @throws CswException for validation errors returns a 400
   */
  @POST
  @Path("/event")
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  Response createEvent(GetRecordsResponseType recordsResponse) throws CswException;

  /**
   * Consume an update event
   *
   * @param recordsResponse the GetRecordsResponseType search results must be urn:catalog:metacard
   *     format with two metacards the updated one being the first and the previous version being
   *     the seconds
   * @throws CswException for validation errors returns a 400
   */
  @PUT
  @Path("/event")
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  Response updateEvent(GetRecordsResponseType recordsResponse) throws CswException;

  /**
   * Consume a delete event
   *
   * @param recordsResponse the GetRecordsResponseType search results must be urn:catalog:metacard
   *     format
   * @throws CswException for validation errors returns a 400
   */
  @DELETE
  @Path("/event")
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  Response deleteEvent(GetRecordsResponseType recordsResponse) throws CswException;

  /** Consume a ping event */
  @HEAD
  @Path("/event")
  Response ping();
}
