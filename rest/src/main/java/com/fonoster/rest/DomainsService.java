/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.rest;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fonoster.annotations.Since;
import com.fonoster.core.api.DBManager;
import com.fonoster.core.api.DIDNumbersAPI;
import com.fonoster.core.api.DomainsAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.DomainHasAgentsException;
import com.fonoster.exception.InvalidParameterException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.DIDNumber;
import com.fonoster.model.Domain;
import com.fonoster.model.User;
import com.google.common.base.Strings;
import org.joda.time.DateTime;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;


@Since("1.0")
@RolesAllowed({"USER"})
@Path("/accounts/{accountId}/domains")
public class DomainsService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public javax.ws.rs.core.Response getDomains(
            @QueryParam("start") String start,
            @QueryParam("end") String end,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("1000") int pageSize,
            @Context HttpServletRequest httpRequest)
            throws ApiException {

        User user = AuthUtil.getAccount(httpRequest).getUser();

        DateTime jStart = null;
        DateTime jEnd = null;

        if (start != null && !start.isEmpty()) jStart = new DateTime(start);
        if (end != null && !end.isEmpty()) jEnd = new DateTime(end);

        List<Domain> domains =
            DomainsAPI.getInstance()
                .getDomains(user, jStart, jEnd, pageSize, pageSize * page);

        int total =
                DomainsAPI.getInstance()
                .getDomains(
                    user,
                    jStart,
                    jEnd,
                    // Max allow
                    1000,
                    // To ensure that there is a least 1000 elements
                    0).size();

        Domains domainPages = new Domains(page, pageSize, total, domains);

        return javax.ws.rs.core.Response.ok(domainPages).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{domainUri}")
    public javax.ws.rs.core.Response getDomain(@PathParam("domainUri") URI domainUri, @Context HttpServletRequest httpRequest)
            throws ApiException {
        User user = AuthUtil.getAccount(httpRequest).getUser();
        Domain domain = DomainsAPI.getInstance().getDomain(user, domainUri, false);

        if (!Objects.equals(domain.getUser().getEmail(), user.getEmail())) throw new UnauthorizedAccessException();

        return javax.ws.rs.core.Response.ok(domain).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{domainUri}/exist")
    public javax.ws.rs.core.Response domainExist(@PathParam("domainUri") URI domainUri, @Context HttpServletRequest httpRequest)
            throws ApiException {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode json = factory.objectNode();
        json.put("exist", DomainsAPI.getInstance().domainExist(domainUri));
        return javax.ws.rs.core.Response.ok(json).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public javax.ws.rs.core.Response saveDomain(Domain domain, @Context HttpServletRequest httpRequest) throws ApiException, URISyntaxException {
        User user = AuthUtil.getAccount(httpRequest).getUser();
        Domain domainFromDB;
        String name = domain.getMetadata().get("name");
        String egressRule = null;
        String egressDIDRef = null;

        if (Strings.isNullOrEmpty(name)) { throw new InvalidParameterException("name"); }

        try {
            egressRule = domain.getSpec().getContext().getEgressPolicy().getRule();
            egressDIDRef = domain.getSpec().getContext().getEgressPolicy().getDidRef();
        } catch (Exception ignored) {}

        if (Strings.isNullOrEmpty(egressRule) != Strings.isNullOrEmpty(egressDIDRef)) {
            throw new InvalidParameterException("Parameters `egressRule` and `egressDIDRef` must both be present for egress routing to work.");
        }

        if (domain.getId() == null) {
            URI domainUri = new URI("sip." +
                domain.getSpec().getContext().getDomainUri().toString().toLowerCase()
                    + ".fonoster.com");
            domainFromDB = DomainsAPI.getInstance().createDomain(user, domainUri, name, egressRule, egressDIDRef);
            DBManager.getInstance().getDS().save(domainFromDB);
        } else {
            // Update object
            domainFromDB = DomainsAPI.getInstance().getDomain(user, domain.getSpec().getContext().getDomainUri(), true);

            // User does not own domain
            if (!Objects.equals(domainFromDB.getUser().getEmail(), user.getEmail())) throw new UnauthorizedAccessException();

            // Both parameters must be present for it to work.
            if (!Strings.isNullOrEmpty(egressRule) && !Strings.isNullOrEmpty(egressDIDRef)) {
                DIDNumber did = DIDNumbersAPI.getInstance().getDIDNumberByRef(user, egressDIDRef);
                // Verify didOwner
                if (did == null || !Objects.equals(did.getUser().getEmail(), user.getEmail())) {
                    throw new UnauthorizedAccessException("This DID is not assigned to you");
                }

                domainFromDB.getSpec().getContext().setEgressPolicy(new Domain.Spec.Context.EgressPolicy(egressRule, egressDIDRef));
            }
            domainFromDB.getMetadata().put("name", name);
            domainFromDB.setModified(DateTime.now());
            domainFromDB.setDeleted(domain.isDeleted());
        }

        DBManager.getInstance().getDS().save(domainFromDB);

        return javax.ws.rs.core.Response.ok(domainFromDB).build();
    }

    @DELETE
    @Path("/{domainUri}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public javax.ws.rs.core.Response deleteDomain(
            @PathParam("domainUri") URI domainUri, @Context HttpServletRequest httpRequest)
            throws ApiException {
        Account account = AuthUtil.getAccount(httpRequest);
        Domain domain = DomainsAPI.getInstance().getDomain(account.getUser(), domainUri, false);

        if (DomainsAPI.getInstance().domainHasAgents(account.getUser(), domainUri)) {
            throw new DomainHasAgentsException();
        }

        domain.setDeleted(true);
        DomainsAPI.getInstance().updateDomain(domain);
        return javax.ws.rs.core.Response.ok().build();
    }

    // For media type "xml", this inner class must be static have the @XmlRootElement annotation
    // and a no-argument constructor.
    @XmlRootElement
    static class Domains {
        private int page;
        private int total;
        private int pageSize;
        private List<Domain> domains;

        // Must have no-argument constructor
        public Domains() {}

        private Domains(int page, int pageSize, int total, List<Domain> domains) {
            this.page = page;
            this.pageSize = pageSize;
            this.total = total;
            this.domains = domains;
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

        public List<Domain> getDomains() {
            return domains;
        }

        public void setDomains(List<Domain> domains) {
            this.domains = domains;
        }
    }
}
