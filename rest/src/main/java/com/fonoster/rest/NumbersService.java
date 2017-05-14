/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.rest;

import com.fonoster.annotations.Since;
import com.fonoster.core.api.NumbersAPI;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.Activity;
import com.fonoster.model.PhoneNumber;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Since("1.0")
@RolesAllowed({"USER"})
@Path("/accounts/{accountId}/numbers")
public class NumbersService {

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Response getNumbers(
      @QueryParam("page") @DefaultValue("0") int page,
      @QueryParam("pageSize") @DefaultValue("1000") int pageSize,
      @QueryParam("status") PhoneNumber.Status status,
      @Context HttpServletRequest httpRequest)
      throws ApiException {

    Account account = AuthUtil.getAccount(httpRequest);

    List<PhoneNumber> numbersList =
        NumbersAPI.getInstance()
            .getPhoneNumbersFor(account.getUser(), status, pageSize, pageSize * page);

    int total = NumbersAPI.getInstance().getPhoneNumbersFor(account.getUser(), status).size();

    Numbers numbers = new Numbers(page, pageSize, total, numbersList);

    return Response.ok(numbers).build();
  }

  @POST
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response addNumber(NumberRequest numberRequest, @Context HttpServletRequest httpRequest) {
    throw new UnsupportedOperationException();
  }

  @POST
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/preferred")
  public Response setPreferred(PhoneNumber phone, @Context HttpServletRequest httpRequest)
      throws UnauthorizedAccessException {

    Account account = AuthUtil.getAccount(httpRequest);

    try {
      // This is just to  ensure that he owns the number
      PhoneNumber pn =
          NumbersAPI.getInstance().getPhoneNumber(account.getUser(), phone.getNumber());
      NumbersAPI.getInstance().setDefault(account.getUser(), pn);

      UsersAPI.getInstance()
          .createActivity(
              account.getUser(),
              "Your test number changed to ".concat(pn.getNumber()),
              Activity.Type.SETTING);

      return Response.ok().build();
    } catch (ApiException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/preferred")
  public Response getPreferred(@Context HttpServletRequest httpRequest) throws ApiException {

    Account account = AuthUtil.getAccount(httpRequest);

    PhoneNumber pn = NumbersAPI.getInstance().getDefault(account.getUser());
    return Response.ok(pn).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/regions")
  // TODO: This should be in a different service
  public Response getRegions(@Context HttpServletRequest httpRequest) {
    throw new UnsupportedOperationException();
  }

  class NumberRequest {
    private String countryISO;
    private String cityId;
    private boolean autoRenew;

    public NumberRequest(String countryISO, String cityId, boolean autoRenew) {
      this.countryISO = countryISO;
      this.cityId = cityId;
      this.autoRenew = autoRenew;
    }

    public String getCountryISO() {
      return countryISO;
    }

    public void setCountryISO(String countryISO) {
      this.countryISO = countryISO;
    }

    public String getCityId() {
      return cityId;
    }

    public void setCityId(String cityId) {
      this.cityId = cityId;
    }

    public boolean isAutoRenew() {
      return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
      this.autoRenew = autoRenew;
    }
  }

  class Numbers {
    private int page;
    private int total;
    private int pageSize;
    private List<PhoneNumber> phoneNumbers;

    private Numbers(int page, int pageSize, int total, List<PhoneNumber> phoneNumbers) {
      this.page = page;
      this.pageSize = pageSize;
      this.total = total;
      this.phoneNumbers = phoneNumbers;
    }

    public int getPage() {
      return page;
    }

    public void setPage(int page) {
      this.page = page;
    }

    public int getTotal() {
      return total;
    }

    public void setTotal(int total) {
      this.total = total;
    }

    public int getPageSize() {
      return pageSize;
    }

    public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
    }

    public List<PhoneNumber> getPhoneNumbers() {
      return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
      this.phoneNumbers = phoneNumbers;
    }
  }
}
