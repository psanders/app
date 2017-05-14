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
import com.fonoster.core.api.CallsAPI;
import com.fonoster.core.api.RecordingsAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.ResourceNotFoundException;
import com.fonoster.model.Account;
import com.fonoster.model.CallDetailRecord;
import com.fonoster.model.CallDetailRecord.AnswerBy;
import com.fonoster.model.CallDetailRecord.Status;
import com.fonoster.model.CallRequest;
import com.fonoster.model.Recording;
import java.security.InvalidParameterException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@Since("1.0")
@Path("/accounts/{accountId}/calls")
public class CallsService {

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Response getCDRs(
      @QueryParam("start") String start,
      @QueryParam("end") String end,
      @QueryParam("from") String from,
      @QueryParam("to") String to,
      @QueryParam("page") @DefaultValue("0") int page,
      @QueryParam("pageSize") @DefaultValue("1000") int pageSize,
      @QueryParam("status") String status,
      @QueryParam("answerBy") String answerBy,
      @Context HttpServletRequest httpRequest)
      throws ApiException {

    Account account = AuthUtil.getAccount(httpRequest);

    DateTime jStart = null;
    DateTime jEnd = null;

    DateTimeZone dtz = DateTimeZone.forID(account.getUser().getTimezone());

    if (start != null && !start.isEmpty()) jStart = new DateTime(start, dtz);
    // End of day
    if (end != null && !end.isEmpty())
      jEnd =
          new DateTime(end, dtz)
              .withHourOfDay(23)
              .withMinuteOfHour(59)
              .withSecondOfMinute(59)
              .withZone(dtz);

    if (status != null && Status.getByValue(status) == null)
      throw new InvalidParameterException("status");

    if (answerBy != null && AnswerBy.getByValue(answerBy) == null)
      throw new InvalidParameterException("answerBy");

    List<CallDetailRecord> calls =
        CallsAPI.getInstance()
            .getCDRs(
                account,
                jStart,
                jEnd,
                from,
                to,
                pageSize,
                pageSize * page,
                Status.getByValue(status),
                AnswerBy.getByValue(answerBy));

    int total =
        CallsAPI.getInstance()
            .getCDRsTotal(
                account,
                jStart,
                jEnd,
                from,
                to,
                Status.getByValue(status),
                AnswerBy.getByValue(answerBy));

    CDRs cdrs = new CDRs(page, pageSize, total, calls);

    return Response.ok(cdrs).build();
  }

  @GET
  @Path("/{callId}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Response getCDR(@PathParam("callId") String id, @Context HttpServletRequest httpRequest)
      throws ApiException {

    Account account = AuthUtil.getAccount(httpRequest);

    CallDetailRecord result = CallsAPI.getInstance().getCDRById(account, new ObjectId(id));
    if (result == null) throw new ResourceNotFoundException();

    return Response.ok(result).build();
  }

  @GET
  @Path("/{callId}/recordings")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Response getCallRecordings(
      @PathParam("callId") String id, @Context HttpServletRequest httpRequest) throws ApiException {

    Account account = AuthUtil.getAccount(httpRequest);

    CallDetailRecord cdr = CallsAPI.getInstance().getCDRById(account, new ObjectId(id));
    List<Recording> result = RecordingsAPI.getInstance().getRecordings(account, cdr);
    if (result == null) throw new ResourceNotFoundException();

    return Response.ok(result).build();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response call(CallRequest request, @Context HttpServletRequest httpRequest)
      throws ApiException {

    Account account = AuthUtil.getAccount(httpRequest);
    request.setAccountId(account.getId().toString());
    CallDetailRecord callDetailRecord = CallsAPI.getInstance().call(request);
    return Response.ok(callDetailRecord).build();
  }

  class CDRs {
    private int page;
    private int total;
    private int pageSize;
    private List<CallDetailRecord> calls;

    private CDRs(int page, int pageSize, int total, List<CallDetailRecord> calls) {
      this.page = page;
      this.pageSize = pageSize;
      this.total = total;
      this.calls = calls;
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

    public List<CallDetailRecord> getCalls() {
      return calls;
    }

    public void setCalls(List<CallDetailRecord> calls) {
      this.calls = calls;
    }
  }
}
