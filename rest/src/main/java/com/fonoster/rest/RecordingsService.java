package com.fonoster.rest;

import com.fonoster.rest.filters.AuthUtil;
import com.fonoster.core.api.RecordingsAPI;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.Recording;
import com.sun.xml.txw2.annotation.XmlElement;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.List;

@Path("/accounts/{accountId}/recordings")
public class RecordingsService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getRecordings(@QueryParam("start") String start,
        @QueryParam("end") String end,
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("pageSize") @DefaultValue("1000") int pageSize,
        @Context HttpServletRequest httpRequest) {

        Account account;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        List<Recording> recordings;
        Recordings r;

        try {
            DateTime jStart = null;
            DateTime jEnd = null;

            DateTimeZone dtz = DateTimeZone.forID(account.getUser().getTimezone());

            if (start != null && !start.isEmpty()) jStart = new DateTime(start, dtz);
            // End of day
            if (end != null && !end.isEmpty()) jEnd = new DateTime(end, dtz)
                    .withHourOfDay(23)
                    .withMinuteOfHour(59)
                    .withSecondOfMinute(59)
                    .withZone(dtz);

            recordings = RecordingsAPI.getInstance().getRecordings(account,
                jStart,
                jEnd,
                page,
                page * pageSize);

            // XXX: This not very intelligent
            int total = RecordingsAPI.getInstance().getRecordings(account, jStart, jEnd).size();

            r = new Recordings(page, page * pageSize, total, recordings);

        } catch (ApiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity( e.getMessage()).build();
        }

        return Response.ok(r).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM})
    @Path("/{recordingId}")
    // Note: This method was left insecure to allow dev to give its customer the audio url
    // However the url is almost impossible to guest.
    public Response getRecording(@PathParam("recordingId") String recordingId,
        @PathParam("accountId") String accountId,
        @Context HttpServletRequest httpRequest) {
        Recording recording;
        String result = httpRequest.getParameter("result");

        Account account = UsersAPI.getInstance().getAccountById(new ObjectId(accountId));

        try {
            if (!ObjectId.isValid(recordingId)) return Response.status(Response.Status.NOT_FOUND).build();

            recording = RecordingsAPI.getInstance().getRecordingById(account, new ObjectId(recordingId));
            Recording.AudioFormat aFormat = Recording.AudioFormat.getByValue(result);

            if (result == null || result.isEmpty() || aFormat != null) {
                File file = RecordingsAPI.getInstance().getRecordingFileById(account, new ObjectId(recordingId), aFormat);
                String ext = "wav";
                if (aFormat != null) {
                    ext = aFormat.toString().toLowerCase();
                }

                Response.ResponseBuilder response = Response.ok(file);
                StringBuilder sb = new StringBuilder("attachment; filename=\"");
                sb.append(recordingId);
                sb.append(".");
                sb.append(ext);
                sb.append("\"");
                response.header("Content-Disposition", sb.toString());
                return response.build();
            } else {
                // This will return META-INFO and not the audio itself
                return Response.ok(recording).build();
            }
        } catch (ApiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity( e.getMessage()).build();
        }
    }

    @XmlElement
    class Recordings {
        private int page;
        private int total;
        private int pageSize;
        private List<Recording> recordings;

        private Recordings(int page, int pageSize, int total, List<Recording> recordings) {
            this.page = page;
            this.pageSize = pageSize;
            this.total = total;
            this.recordings = recordings;
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

        public List<Recording> getRecordings() {
            return recordings;
        }

        public void setRecordings(List<Recording> recordings) {
            this.recordings = recordings;
        }
    }
}