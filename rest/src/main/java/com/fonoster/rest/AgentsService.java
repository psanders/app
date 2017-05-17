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
import com.fonoster.core.api.AgentsAPI;
import com.fonoster.core.api.DBManager;
import com.fonoster.core.api.DomainsAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Agent;
import com.fonoster.model.Domain;
import com.fonoster.model.User;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.List;
import java.util.Objects;


@Since("1.0")
@RolesAllowed({"USER"})
@Path("/accounts/{accountId}/agents")
public class AgentsService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public javax.ws.rs.core.Response getAgents(
            @QueryParam("start") String start,
            @QueryParam("end") String end,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("1000") int pageSize,
            @Context HttpServletRequest httpRequest)
            throws ApiException {

        User user = AuthUtil.getUser(httpRequest);

        DateTime jStart = null;
        DateTime jEnd = null;

        if (start != null && !start.isEmpty()) jStart = new DateTime(start);
        if (end != null && !end.isEmpty()) jEnd = new DateTime(end);

        List<Agent> agents =
            AgentsAPI.getInstance()
                .getAgents(user, jStart, jEnd, pageSize, pageSize * page);

        int total =
                AgentsAPI.getInstance()
                .getAgents(
                    user,
                    jStart,
                    jEnd,
                    // Max allow
                    1000,
                    // To ensure that there is a least 1000 elements
                    0).size();

        Agents agentsPages = new Agents(page, pageSize, total, agents);

        return javax.ws.rs.core.Response.ok(agentsPages).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{domainUri}")
    public javax.ws.rs.core.Response getAgent(@PathParam("domainUri") URI domainUri, @Context HttpServletRequest httpRequest)
            throws ApiException {
        User user = AuthUtil.getUser(httpRequest);
        Domain domain = DomainsAPI.getInstance().getDomainByUri(domainUri);

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
    public javax.ws.rs.core.Response saveAgent(Agent agent, @Context HttpServletRequest httpRequest) throws ApiException {
        User user = AuthUtil.getUser(httpRequest);

        Agent agentFromDB;
        String name = agent.getMetadata().get("name");
        Agent.Spec.Credentials credentials =  agent.getSpec().getCredentials();
        URI domainUri = agent.getSpec().getDomains().get(0);
        String username = credentials.getUsername();
        String secret = credentials.getSecret();

        if (agent.getId() == null) {
            agentFromDB = AgentsAPI.getInstance().createAgent(user, domainUri, name, username, secret);
            DBManager.getInstance().getDS().save(agentFromDB);
        } else {
            agentFromDB = AgentsAPI.getInstance().getAgent(user, agent.getId());
            agentFromDB.getMetadata().put("name", agent.getMetadata().get("name"));
            agentFromDB.getSpec().getCredentials().setSecret(secret);
            DBManager.getInstance().getDS().save(agentFromDB);
        }

        return javax.ws.rs.core.Response.ok(agentFromDB).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public javax.ws.rs.core.Response deleteAgent(
            @PathParam("id") ObjectId agentId, @Context HttpServletRequest httpRequest)
            throws ApiException {
        User user = AuthUtil.getUser(httpRequest);
        Agent agent = AgentsAPI.getInstance().getAgent(user, agentId);
        agent.setDeleted(true);
        DBManager.getInstance().getDS().save(agent);
        return javax.ws.rs.core.Response.ok().build();
    }

    // For media type "xml", this inner class must be static have the @XmlRootElement annotation
    // and a no-argument constructor.
    @XmlRootElement
    static class Agents {
        private int page;
        private int total;
        private int pageSize;
        private List<Agent> agents;

        // Must have no-argument constructor
        public Agents() {}

        private Agents(int page, int pageSize, int total, List<Agent> agents) {
            this.page = page;
            this.pageSize = pageSize;
            this.total = total;
            this.agents = agents;
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

        public List<Agent> getAgents() {
            return agents;
        }

        public void setAgents(List<Agent> agents) {
            this.agents = agents;
        }
    }
}
