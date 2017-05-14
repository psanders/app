package com.fonoster.core.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fonoster.exception.ApiException;
import com.fonoster.model.Agent;
import com.fonoster.model.User;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AgentsAPI {
    private static final Logger LOG = LoggerFactory.getLogger(AgentsAPI.class);
    private static final AgentsAPI INSTANCE = new AgentsAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private ObjectMapper mapper = new ObjectMapper();
    private Configuration conf;

    private AgentsAPI() {
        conf = Configuration.defaultConfiguration();
        conf.addOptions(Option.ALWAYS_RETURN_LIST);
    }

    public static AgentsAPI getInstance() {
        return INSTANCE;
    }

    public Agent createAgent(User user, String name, String username, String secret) throws ApiException {
       // if(getDomainByUri(uri) != null) throw new ApiException("This domain already exist.");

        Agent.Spec.Credentials credentials = new Agent.Spec.Credentials(username, secret);
        Agent agent = new Agent(user, name, credentials);

        // JavaBean validation
        if (!validator.validate(agent).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<Agent>> validate = validator.validate(agent);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }

        ds.save(agent);
        return agent;
    }

    static public void main(String... args) throws ApiException {
        User user = UsersAPI.getInstance().getUserByEmail("john@doe.com");
        Agent agent = AgentsAPI.getInstance().createAgent(user, "Janie Doe", "janie", "1234");

        ArrayList<String> domains = new ArrayList<String>();
        domains.add("sip.ocean.com");

        Agent.Spec spec = agent.getSpec();
        spec.setDomains(domains);

        AgentsAPI.getInstance().updateAgent(agent);
    }

    public Agent updateAgent(Agent agent) {
        ds.save(agent);
        return agent;
    }

    public Agent getAgent(URI uri, String username) {
        List<Agent> agents = ds.createQuery(Agent.class)
            .field("spec.access.username").equal(username)
                .field("deleted").equal(false).asList();

        return agents.stream()
            .filter(agent -> hasDomain(agent.getSpec().getDomains(), uri.toString()))
                .findFirst().get();
    }

    // Only for admin account (Including Sip I/O integration)
    public List<Agent> getAgents(URI domainUri, String f) throws ApiException {
        String filter;
        String jsonInString;

        if (f == null || f.isEmpty()) {
            filter = "*";
        } else {
            filter = "*.[?(@." + f + ")]";
        }

        List<Agent> agents = ds.createQuery(Agent.class).field("deleted").equal(false).asList();

        if (domainUri != null) {
            agents = agents.stream()
                .filter(agent -> hasDomain(agent.getSpec().getDomains(), domainUri.toString()))
                    .collect(Collectors.toList());
        }

        List<Agent> result;

        try {
            jsonInString= mapper.writeValueAsString(agents);
            result = JsonPath.parse(jsonInString).read(filter);
        } catch (JsonProcessingException e) {
            throw new ApiException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

        return result;
    }

    public List<Agent> getAgents() {
        return ds.createQuery(Agent.class).field("deleted").equal(false)
            .asList();
    }

    public List<Agent> getAgentsFor(User user) {
        if (user == null) return new ArrayList<>();

        return ds.createQuery(Agent.class).field("user").equal(user)
            .field("deleted").equal(false).asList();
    }

    public boolean agentExist(URI domainUri, String username) {
        return getAgent(domainUri, username) != null;
    }

    public void deleteAgent(URI domainUri, String username) {
        Agent agent = getAgent(domainUri, username);
        agent.setDeleted(true);
        updateAgent(agent);
    }

    private boolean hasDomain(List<String> domains, String domainUri) {
        for (String dUri: domains) {
            if (domainUri.equals(dUri)) return true;
        }
        return false;
    }
}
