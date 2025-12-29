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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import net.opengis.cat.csw.v_2_0_2.CapabilitiesType;
import net.opengis.cat.csw.v_2_0_2.DescribeRecordResponseType;
import net.opengis.cat.csw.v_2_0_2.DescribeRecordType;
import net.opengis.cat.csw.v_2_0_2.GetCapabilitiesType;
import net.opengis.cat.csw.v_2_0_2.GetRecordByIdType;
import net.opengis.cat.csw.v_2_0_2.GetRecordsType;
import net.opengis.cat.csw.v_2_0_2.TransactionResponseType;
import org.codice.ddf.spatial.ogc.csw.catalog.common.transaction.CswTransactionRequest;

/** JAX-RS Interface to define an OGC Catalogue Service for Web (CSW). */
@Path("/")
public interface Csw {

  /**
   * GetCapabilities - HTTP GET
   *
   * @param request
   * @return
   */
  @GET
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  CapabilitiesType getCapabilities(@QueryParam("") GetCapabilitiesRequest request)
      throws CswException;

  /**
   * GetCapabilities - HTTP POST
   *
   * @param request
   * @return
   */
  @POST
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  CapabilitiesType getCapabilities(GetCapabilitiesType request) throws CswException;

  /**
   * DescribeRecord - HTTP GET
   *
   * @param request
   * @return
   */
  @GET
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  DescribeRecordResponseType describeRecord(@QueryParam("") DescribeRecordRequest request)
      throws CswException;

  /**
   * DescribeRecord - HTTP POST
   *
   * @param request
   * @return
   */
  @POST
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  DescribeRecordResponseType describeRecord(DescribeRecordType request) throws CswException;

  /**
   * ' GetRecords - HTTP GET
   *
   * @param request
   * @return
   */
  @GET
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  CswRecordCollection getRecords(@QueryParam("") GetRecordsRequest request) throws CswException;

  /**
   * GetRecords - HTTP POST
   *
   * @param request
   * @return
   */
  @POST
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  CswRecordCollection getRecords(GetRecordsType request) throws CswException;

  /**
   * GetRecordById - HTTP GET
   *
   * @param request
   * @return
   */
  @GET
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  CswRecordCollection getRecordById(
      @QueryParam("") GetRecordByIdRequest request,
      @HeaderParam(CswConstants.RANGE_HEADER) String rangeValue)
      throws CswException;

  /**
   * GetRecordById - HTTP POST
   *
   * @param request
   * @return
   */
  @POST
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  CswRecordCollection getRecordById(
      GetRecordByIdType request, @HeaderParam(CswConstants.RANGE_HEADER) String rangeValue)
      throws CswException;

  /**
   * Transaction - HTTP POST
   *
   * @param request
   * @return
   */
  @POST
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  TransactionResponseType transaction(CswTransactionRequest request) throws CswException;
}
