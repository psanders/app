package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;

@Since("1.0")
public class CallRequest {
    @NotNull
    private String appId;
    private String callId;
    @NotNull
    private String accountId;
    @NotNull
    private String from;
    @NotNull
    private String to;
    private String callerId;
    private CallDetailRecord.AnswerBy answerBy;
    // Call will be drop after timeout
    // Default is 60
    private long timeout;
    private String sendDigits;
    // Default is false
    private boolean record;
    private boolean billable;
    @NotNull
    private String apiVersion;

    public CallRequest() {
        timeout = 60;
        record = false;
        billable = true;
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    public CallRequest(
        @JsonProperty("appId") String appId,
        @JsonProperty("callId") String callId,
        @JsonProperty("accountId") String accountId,
        @JsonProperty("from") String from,
        @JsonProperty("to") String to,
        @JsonProperty("callerId") String callerId,
        @JsonProperty("timeout") Integer timeout,
        @JsonProperty("sendDigits") String sendDigits,
        @JsonProperty("record") boolean record,
        @JsonProperty("apiVersion") String apiVersion,
        @JsonProperty("billable") Boolean billable) {

        this.setAppId(appId);
        this.setCallId(callId);
        this.setAccountId(accountId);
        this.setFrom(from);
        this.setTo(to);
        this.setCallerId(callerId);
        this.setSendDigits(sendDigits);
        this.setApiVersion(apiVersion);
        this.setRecord(record);

        if (timeout == null) this.timeout = 60;

        if (billable == null) {
            this.billable = true;
        } else {
            this.billable = billable;
        }

        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public CallDetailRecord.AnswerBy getAnswerBy() {
        return answerBy;
    }

    public void setAnswerBy(CallDetailRecord.AnswerBy answerBy) {
        this.answerBy = answerBy;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getSendDigits() {
        return sendDigits;
    }

    public void setSendDigits(String sendDigits) {
        this.sendDigits = sendDigits;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
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
}