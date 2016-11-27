package com.fonoster.rest;

import com.fonoster.core.api.NumbersAPI;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Activity;
import com.fonoster.rest.filters.AuthUtil;
import com.fonoster.model.Account;
import com.fonoster.model.PhoneNumber;
import com.fonoster.model.ServiceProvider;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/admin")
// TODO: This service be accessible only from valid hosts(ie.: localhost)
public class AdminService {

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/numbers")
    public Response addNumber(PhoneNumberRequest phoneNumberRequest,
        @Context HttpServletRequest httpRequest) throws ApiException {

        Account account;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        if (!ObjectId.isValid(phoneNumberRequest.getSpId())) {
            return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, "This service provider doesn't exist. Please verify Id.");
        }

        ServiceProvider sp = NumbersAPI.getInstance().getServiceProviderById(new ObjectId(phoneNumberRequest.getSpId()));
        PhoneNumber pn;

        try {
            pn = NumbersAPI.getInstance().createPhoneNumber(account.getUser(),
                sp,
                phoneNumberRequest.getNumber(),
                phoneNumberRequest.getCountryISOCode());

            pn.setVoiceEnabled(phoneNumberRequest.voiceEnabled);
            pn.setSmsEnabled(phoneNumberRequest.smsEnabled);
            pn.setMmsEnabled(phoneNumberRequest.mmsEnabled);

            NumbersAPI.getInstance().updatePhoneNumber(account.getUser(), pn);
        } catch (ApiException e) {
            return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, e.getMessage());
        }

        UsersAPI.getInstance().createActivity(account.getUser(), "Added number: " + phoneNumberRequest.getNumber(),
                Activity.Type.SYS);

        return Response.ok(pn).build();
    }

    // Yes this class must be static or it will cause a :
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