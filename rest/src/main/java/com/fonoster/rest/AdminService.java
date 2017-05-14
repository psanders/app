/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
package com.fonoster.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fonoster.core.api.AgentsAPI;
import com.fonoster.core.api.DomainsAPI;
import com.fonoster.core.api.NumbersAPI;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.ResourceNotFoundException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.*;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/admin")
// TODO: This service should be accessible only from valid hosts(ie.: localhost)
public class AdminService {

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/numbers")
    public Response addNumber(PhoneNumberRequest phoneNumberRequest,
        @Context HttpServletRequest httpRequest) throws ApiException {

        Account account = AuthUtil.getAccount(httpRequest);

        ServiceProvider sp = NumbersAPI.getInstance().getServiceProviderById(new ObjectId(phoneNumberRequest.getSpId()));
        PhoneNumber pn = NumbersAPI.getInstance().createPhoneNumber(account.getUser(), sp, phoneNumberRequest.getNumber(),
            phoneNumberRequest.getCountryISOCode());
        pn.setVoiceEnabled(phoneNumberRequest.voiceEnabled);
        pn.setSmsEnabled(phoneNumberRequest.smsEnabled);
        pn.setMmsEnabled(phoneNumberRequest.mmsEnabled);

        NumbersAPI.getInstance().updatePhoneNumber(account.getUser(), pn);

        UsersAPI.getInstance().createActivity(account.getUser(), "Added number: " + phoneNumberRequest.getNumber(),
                Activity.Type.SYS);

        return Response.ok(pn).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/domains")
    public Response getDomains(@QueryParam("filter") String filter,
        @Context HttpServletRequest httpRequest) throws ApiException {

        if (!AuthUtil.isAdmin(httpRequest)) throw new UnauthorizedAccessException();

        List<Domain> result = DomainsAPI.getInstance().getDomains(filter);

        if (result == null || result.isEmpty()) throw new ResourceNotFoundException();

        return Response.ok(result).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/domains/{uri}")
    public Response getDomainsByUri(@PathParam("uri") URI uri,
        @Context HttpServletRequest httpRequest) throws ApiException {

        if (!AuthUtil.isAdmin(httpRequest)) throw new UnauthorizedAccessException();

        Domain result = DomainsAPI.getInstance().getDomainByUri(uri);

        if (result == null) throw new ResourceNotFoundException();

        return Response.ok(result).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/agents")
    public Response getAgents(@QueryParam("domainUri") URI domainUri,
        @QueryParam("filter") String filter,
            @Context HttpServletRequest httpRequest) throws ApiException {

        if (!AuthUtil.isAdmin(httpRequest)) throw new UnauthorizedAccessException();

        List<Agent> result = AgentsAPI.getInstance().getAgents(domainUri, filter);
        if (result == null || result.isEmpty()) throw new ResourceNotFoundException();

        return Response.ok(result).build();
    }

    // Yes this class must be static or else it will cause a:
    // java.lang.ArrayIndexOutOfBoundsException: 3
    // at org.codehaus.jackson.map.introspect.AnnotatedWithParams.getParameter(AnnotatedWithParams.java:138)
    // Solution found here: http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
   static class PhoneNumberRequest {
        // Service Provider ID
        private String spId;
        private String number;
        private String countryISOCode;
        private boolean voiceEnabled;
        private boolean smsEnabled;
        private boolean mmsEnabled;

        // Not marking this with JsonProperty was causing;
        //  No suitable constructor found for type [simple type,
        // class CredentialsService$CredentialsRequest]:
        // can not instantiate from JSON object (need to add/enable type information?)
        public PhoneNumberRequest(
            // Warning: Are this JsonProperty necessary
            @JsonProperty("spId") String spId,
            @JsonProperty("number") String number,
            @JsonProperty("voiceEnabled") boolean voiceEnabled,
            @JsonProperty("smsEnabled") boolean smsEnabled,
            @JsonProperty("mmsEnabled") boolean mmsEnabled){
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