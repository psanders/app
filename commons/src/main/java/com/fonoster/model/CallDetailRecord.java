package com.fonoster.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.fonoster.exception.ApiException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"id",
        "accountId",
        "subAccountId",
        "appId",
        "from",
        "to",
        "direction",
        "status",
        "created",
        "modified",
        "start",
        "end",
        "duration",
        "cost"})
@Since("1.0")
@Entity
@XmlRootElement
public class CallDetailRecord {
    @Id
    private ObjectId id;
    // Internal(telephone engine) identifier for this call
    private String channelId;
    //@NotNull it maybe null if the inbound number does not have set an app
    @Reference
    private App app;
    @Reference
    @NotNull
    private Account account;
    private Account subAccount;
    @NotNull
    private DateTime created;
    @NotNull
    private DateTime modified;
    @NotNull
    private String from;
    @NotNull
    private String to;
    @NotNull
    private Direction direction;
    @NotNull
    private Status status;
    private String forwardedFrom;
    private String callerName;
    private DateTime start;
    private DateTime end;
    private BigDecimal cost;
    @NotNull
    private Long duration;
    private AnswerBy answerBy;
    private Boolean recorded;
    private Long recordDuration;
    private Map<String, String> vars;
    private List<Log> logs;
    @NotNull
    private URI uri;
    @NotNull
    private URI recordingsUri;
    @NotNull
    private String apiVersion;
    private boolean billable;

    public CallDetailRecord() {
    }

    public CallDetailRecord(Account account,
        App app,
        String from,
        String to,
        // Warning: Unused parameter
        PhoneNumber parent,
        Direction direction) throws ApiException {

        this.id = new ObjectId();
        this.account = account;
        this.from = from;
        this.to = to;
        this.app = app;
        this.created = DateTime.now();
        this.modified = DateTime.now();
        this.direction = direction;
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
        try {
            this.uri = CommonsConfig.getInstance().getCdrURI(this);
            this.recordingsUri = CommonsConfig.getInstance().getCallRecordingsURI(this);
        } catch (URISyntaxException e) {
            throw new ApiException(e.getMessage());
        }
        this.duration = 0L;
        this.cost = BigDecimal.ZERO;
        this.vars = new LinkedHashMap<>();
        this.logs = new ArrayList<>();
        setBillable(true);
    }

    @XmlElement(name = "callId")
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @XmlElement(name = "appId")
    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    @XmlElement(name = "accountId")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getModified() {
        return modified;
    }

    public void setModified(DateTime modified) {
        this.modified = modified;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getForwardedFrom() {
        return forwardedFrom;
    }

    public void setForwardedFrom(String forwardedFrom) {
        this.forwardedFrom = forwardedFrom;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public AnswerBy getAnswerBy() {
        return answerBy;
    }

    public void setAnswerBy(AnswerBy answerBy) {
        this.answerBy = answerBy;
    }

    public Boolean getRecorded() {
        return recorded;
    }

    public void setRecorded(Boolean recorded) {
        this.recorded = recorded;
    }

    public Long getRecordDuration() {
        return recordDuration;
    }

    public void setRecordDuration(Long recordDuration) {
        this.recordDuration = recordDuration;
    }

    public Map<String, String> getVars() {
        return vars;
    }

    protected void setVars(LinkedHashMap<String, String> vars) {
        this.vars = vars;
    }

    public void addVar(String name, String value) {
        if (vars == null) vars = new LinkedHashMap<String, String>();
        vars.put(name, value);
    }

    public List<Log> getLogs() {
        return logs;
    }

    protected void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public void addLog(Level level, String message) {
        if (logs == null) logs = new ArrayList<>();
        Log log = new Log(DateTime.now(), level.toString(), message);
        logs.add(log);
    }

    @XmlTransient
    @JsonIgnore
    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public URI getRecordingsUri() {
        return recordingsUri;
    }

    public void setRecordingsUri(URI recordingsUri) {
        this.recordingsUri = recordingsUri;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @XmlElement(name = "subAccountId")
    public Account getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(Account subAccount) {
        this.subAccount = subAccount;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    // Creates toString using reflection
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * QUEUED - The call is ready and waiting in line before going out. (OUTBOUND)
     * IN_PROGRESS - The call was answered and is currently in progress. (OUTBOUND|INBOUND)
     * RINGING - // I must research about this status and whether or not make sense to have it. //
     * COMPLETED - The call was answered and has ended normally. (OUTBOUND|INBOUND)
     * NO_ANSWER -	The call ended without being answered. (OUTBOUND)
     * CANCELED - The call was canceled via the REST API while QUEUED. (OUTBOUND)
     * FAILED - The call could not be completed as dialed, most likely because the phone number was non-existent. (OUTBOUND)
     * BUSY - The caller received a busy signal. (OUTBOUND)
     */
    @XmlType(name = "callStatus")
    public static enum Status {
        QUEUED,
        IN_PROGRESS,
        RINGING,
        COMPLETED,
        NO_ANSWER,
        CANCELED,
        FAILED,
        BUSY;

        static public Status getByValue(String value) {
            if (value == null) return null;
            value = value.toUpperCase();
            switch (value) {
                case "QUEUED":
                    return QUEUED;
                case "IN_PROGRESS":
                    return IN_PROGRESS;
                case "RINGING":
                    return RINGING;
                case "COMPLETED":
                    return COMPLETED;
                case "NO_ANSWER":
                    return NO_ANSWER;
                case "CANCELED":
                    return CANCELED;
                case "FAILED":
                    return FAILED;
                case "BUSY":
                    return BUSY;
            }
            return null;
        }
    }

    public static enum AnswerBy {
        HUMAN("HUMAN"),
        MACHINE("MACHINE"),
        NOT_SURE("NOTSURE"),
        NONE("HANGUP");

        private String value;

        private AnswerBy(String value) {
            this.value = value;
        }

        static public AnswerBy getByValue(String value) {
            if (value == null) return null;
            value = value.toUpperCase();
            switch (value) {
                case "HUMAN":
                    return HUMAN;
                case "MACHINE":
                    return MACHINE;
                case "HANGUP":
                    return NONE;
                case "NOTSURE":
                    return NOT_SURE;
            }
            return null;
        }

        public String getValue() {
            return value;
        }
    }

    public static enum Direction {
        INBOUND,
        OUTBOUND_API,
        OUTBOUND_DIAL
    }

    public static class Log {
        private String level;
        private DateTime created;
        private String message;

        public Log() {
        }

        public Log(DateTime date, String level, String message) {
            this.created = date;
            this.level = level;
            this.message = message;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public DateTime getCreated() {
            return created;
        }

        public void setCreated(DateTime created) {
            this.created = created;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Deprecated
    public static class Var {
        private String varname;
        private String value;

        public Var() {
        }

        public Var(String varname, String value) {
            this.varname = varname;
            this.value = value;
        }

        public String getVarname() {
            return varname;
        }

        public void setVarname(String varname) {
            this.varname = varname;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
