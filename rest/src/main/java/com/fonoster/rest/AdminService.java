/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fonoster.annotations.Since;
import com.fonoster.core.api.*;
import com.fonoster.exception.ApiException;
import com.fonoster.model.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.List;

@Since("1.0")
@RolesAllowed({"ADMIN"})
@Path("/admin")
public class AdminService {

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/dids")
  public Response addDIDNumber(
          DIDRequest didRequest, @Context HttpServletRequest httpRequest)
      throws ApiException {

    Account account = AuthUtil.getAccount(httpRequest);

    DIDNumber didNumber = DIDNumbersAPI.getInstance().getDIDNumber("tel:" + didRequest.getNumber());
    didNumber.setUser(account.getUser());

    DIDNumbersAPI.getInstance().updateDIDNumber(account.getUser(), didNumber);

    UsersAPI.getInstance()
        .createActivity(
            account.getUser(),
            "Added number: " + didRequest.getNumber(),
            Activity.Type.SYS);

    return Response.ok(didNumber).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/domains")
  public Response getDomains(
      @QueryParam("filter") String filter, @Context HttpServletRequest httpRequest)
      throws ApiException {

      List<Domain> domains = SipIOResourcesAPI.getInstance().getDomains(filter);

     // See: http://stackoverflow.com/a/6081716/1320815
      GenericEntity<List<Domain>> result =
          new GenericEntity<List<Domain>>(domains) {};

    return Response.ok(result).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/domains/{uri}")
  public Response getDomainsByUri(
      @PathParam("uri") URI uri, @Context HttpServletRequest httpRequest) throws ApiException {
    Domain domain = SipIOResourcesAPI.getInstance().getDomainByUri(uri);
    return Response.ok(domain).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/agents")
  public Response getAgents(
      @QueryParam("domainUri") URI domainUri,
      @QueryParam("filter") String filter,
      @Context HttpServletRequest httpRequest)
      throws ApiException {
      List<Agent> agents = SipIOResourcesAPI.getInstance().getAgents(domainUri, filter);

      // See: http://stackoverflow.com/a/6081716/1320815
      GenericEntity<List<Agent>> result =
        new GenericEntity<List<Agent>>(agents) {};

    return Response.ok(result).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/dids")
  public Response getDIDNumbers(
          @QueryParam("filter") String filter,
          @Context HttpServletRequest httpRequest)
          throws ApiException {
    List<DIDNumber> didNumbers = SipIOResourcesAPI.getInstance().getDIDNumbers(filter);

    // See: http://stackoverflow.com/a/6081716/1320815
    GenericEntity<List<DIDNumber>> result =
            new GenericEntity<List<DIDNumber>>(didNumbers) {};

    return Response.ok(result).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/gateways")
  public Response getGateways(
          @QueryParam("filter") String filter,
          @Context HttpServletRequest httpRequest)
          throws ApiException {
    List<Gateway> gateways = SipIOResourcesAPI.getInstance().getGateways(filter);

    // See: http://stackoverflow.com/a/6081716/1320815
    GenericEntity<List<Gateway>> result =
            new GenericEntity<List<Gateway>>(gateways) {};

    return Response.ok(result).build();
  }

  // For media type "xml", this inner class must be static have the @XmlRootElement annotation
  // and a no-argument constructor.
  @XmlRootElement
  static class DIDRequest {
    // Service Provider ID
    private String spId;
    private String number;
    private String countryISOCode;
    private boolean voiceEnabled;
    private boolean smsEnabled;
    private boolean mmsEnabled;

    // Must have no-argument constructor
    public DIDRequest() {}

    // Not marking this with JsonProperty was causing;
    // No suitable constructor found for type [simple type,
    // class CredentialsService$CredentialsRequest]:
    // can not instantiate from JSON object (need to add/enable type information?)
    public DIDRequest(
        // Warning: Are this JsonProperty necessary
        @JsonProperty("spId") String spId,
        @JsonProperty("number") String number,
        @JsonProperty("voiceEnabled") boolean voiceEnabled,
        @JsonProperty("smsEnabled") boolean smsEnabled,
        @JsonProperty("mmsEnabled") boolean mmsEnabled) {
      this.setSpId(spId);
      this.setNumber(number);
      this.setCountryISOCode(getCountryISOCode());
      this.setVoiceEnabled(voiceEnabled);
      this.setSmsEnabled(smsEnabled);
      this.setMmsEnabled(mmsEnabled);
    }

    public String getSpId() {
      return spId;
    }

    public void setSpId(String spId) {
      this.spId = spId;
    }

    public String getNumber() {
      return number;
    }

    public void setNumber(String number) {
      this.number = number;
    }

    public String getCountryISOCode() {
      return countryISOCode;
    }

    public void setCountryISOCode(String countryISOCode) {
      this.countryISOCode = countryISOCode;
    }

    public boolean isVoiceEnabled() {
      return voiceEnabled;
    }

    public void setVoiceEnabled(boolean voiceEnabled) {
      this.voiceEnabled = voiceEnabled;
    }

    public boolean isSmsEnabled() {
      return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
      this.smsEnabled = smsEnabled;
    }

    public boolean isMmsEnabled() {
      return mmsEnabled;
    }

    public void setMmsEnabled(boolean mmsEnabled) {
      this.mmsEnabled = mmsEnabled;
    }
  }
}
