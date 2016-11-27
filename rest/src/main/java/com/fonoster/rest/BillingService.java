package com.fonoster.rest;

import com.braintreegateway.*;
import com.fonoster.rest.filters.AuthUtil;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.core.config.CoreConfig;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.Activity;
import com.fonoster.model.PaymentInfo;
import com.fonoster.model.User;
import com.sun.xml.txw2.annotation.XmlElement;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;

@Path("/billing")
public class BillingService {
    private final CoreConfig config = CoreConfig.getInstance();
    private BraintreeGateway gateway = new BraintreeGateway(
            config.getBraintreeEnvironment(),
            config.getBraintreeMerchantId(),
            config.getBraintreePublicKey(),
            config.getBraintreePrivateKey()
    );

    @GET
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    @Path("/braintree_token")
    public Response getClientToken(@Context HttpServletRequest httpRequest)  {

        try {
            AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        String clientToken = gateway.clientToken().generate();

        // WARNING: Why are we doing this?
        JSONObject json = new JSONObject();

        try {
            json.put("token", clientToken);
        } catch (JSONException e) {
            return ResponseUtil.getResponse(ResponseUtil.NOT_FOUND);
        }

        return Response.ok(json).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{email}/funds/{amount}")
    public Response addFunds(@PathParam("amount") Float amount,
        @Context HttpServletRequest httpRequest) throws ApiException {

        Account account;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        User user = account.getUser();

        TransactionRequest request = new TransactionRequest()
            .amount(new BigDecimal(amount))
            .paymentMethodToken(user.getPmntInfo().getMethod().getToken());

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess()) {
            user.getPmntInfo().setBalance(user.getPmntInfo().getBalance().add(new BigDecimal(amount)));
            user.getPmntInfo().setLastTrans(amount);

            if (user.getPmntInfo().getTransactions() == null) {
                user.getPmntInfo().setTransactions(new ArrayList<PaymentInfo.Transaction>());
            }

            PaymentInfo.Transaction t = new PaymentInfo.Transaction();
            t.setAmount(result.getTarget().getAmount());
            t.setCreated(new DateTime(result.getTarget().getCreatedAt().getTime()));
            t.setId(result.getTarget().getId());
            t.setDescription(user.getPmntInfo().getMethod().getDescription());
            t.setStatus(result.getTarget().getStatus().name());
            t.setMethod(result.getTarget().getPaymentInstrumentType());

            user.getPmntInfo().getTransactions().add(t);

            UsersAPI.getInstance().updateUser(user);

            UsersAPI.getInstance().createActivity(user, "We've received your payment for USD$" + amount,
                    Activity.Type.PAYMENT);
        }

        return Response.ok().build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{email}/autopay/{autopay}")
    public Response autoCharge(@PathParam("autopay")Boolean autopay,
        @Context HttpServletRequest httpRequest) {

        Account account;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        User u = account.getUser();
        u.getPmntInfo().setAutopay(autopay);
        UsersAPI.getInstance().updateUser(u);

        String status = "off";

        if (autopay)  {
            status = "on";
        } else {
            status = "off";
        }

        UsersAPI.getInstance().createActivity(account.getUser(), "Autopay turned " + status,
                Activity.Type.SETTING);

        return Response.ok().build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{email}/payment_method/{nonce}")
    public Response addMethod(PaymentMethodRequest pr,
        @Context HttpServletRequest httpRequest) {

        Account account;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        User user = account.getUser();
        Customer customer = null;

        CustomerSearchRequest csq =
            new CustomerSearchRequest()
                .email().contains(user.getEmail());

        try {
            customer = gateway.customer().search(csq).getFirst();
        } catch (IndexOutOfBoundsException e) {
            // WARNING: Present proper message for this case.
        }

        CustomerRequest request = new CustomerRequest()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .company(user.getCompany())
            .email(user.getEmail())
            .paymentMethodNonce(pr.getNonce())
            .phone(user.getPhone());

        Result<Customer> r = gateway.customer().create(request);
        customer = r.getTarget();

        if (r.isSuccess()) {
            PaymentInfo.PaymentMethod pm = new PaymentInfo.PaymentMethod();
            pm.setCountryCode(pr.getCountryCode());
            pm.setDescription(pr.getDescription());
            pm.setExpMonth(pr.getExpMonth());
            pm.setExpYear(pr.getExpYear());
            pm.setPostalCode(pr.getPostalCode());
            pm.setToken(customer.getPaymentMethods().get(0).getToken());
            pm.setType(pr.getType());

            if (user.getPmntInfo() == null) {
                PaymentInfo pntInfo = new PaymentInfo();
                pntInfo.setMethod(pm);
                user.setPmntInfo(pntInfo);
            } else {
                user.getPmntInfo().setMethod(pm);
            }

            UsersAPI.getInstance().updateUser(user);
        } else {
            return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, "Invalid payment method.");
        }

        UsersAPI.getInstance().createActivity(account.getUser(), "Added new payment method",
                Activity.Type.INFO);

        return Response.ok(user.getPmntInfo()).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{email}/payment_method")
    public Response getPntInfo(@Context HttpServletRequest httpRequest) {

        Account account;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        User user = account.getUser();

        return Response.ok(user.getPmntInfo()).build();
    }

    @XmlElement
    // Yes this class must be static or it will cause a :
    // java.lang.ArrayIndexOutOfBoundsException: 3
    // at org.codehaus.jackson.map.introspect.AnnotatedWithParams.getParameter(AnnotatedWithParams.java:138)
    // Solution found here: http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
    static class PaymentMethodRequest extends PaymentInfo.PaymentMethod {
        private String nonce;

        public PaymentMethodRequest() {}

        // Not marking this with JsonProperty was causing;
        //  No suitable constructor found for type [simple type,
        // class CredentialsService$CredentialsRequest]:
        // can not instantiate from JSON object (need to add/enable type information?)
        public PaymentMethodRequest(@JsonProperty("nonce") String nonce,
            @JsonProperty("type") PaymentInfo.PaymentType type,
            @JsonProperty("description") String description,
            @JsonProperty("expMonth") int expMonth,
            @JsonProperty("expYear") int expYear,
            @JsonProperty("countryCode") String countryCode,
            @JsonProperty("postalCode") String postalCode) {

            this.setNonce(nonce);
            this.setType(type);
            this.setDescription(description);
            this.setExpMonth(expMonth);
            this.setExpYear(expYear);
            this.setCountryCode(countryCode);
            this.setPostalCode(postalCode);
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }
    }
}
