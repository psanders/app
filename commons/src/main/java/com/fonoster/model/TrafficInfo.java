package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@Since("1.0")
@Entity
@Embedded
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficInfo {
    private int completedCalls;
    private int incompleteCalls;
    private int answerByMachine;
    private int answerByHuman;
    private int answerByUnknown;
    private int inbnd;
    private int outbndApi;
    private int outbndDial;
    private BigDecimal cost;
    @NotNull
    private String apiVersion;

    public TrafficInfo() {
        cost = new BigDecimal("0.0");
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    public int getCompletedCalls() {
        return completedCalls;
    }

    public void setCompletedCalls(int completedCalls) {
        this.completedCalls = completedCalls;
    }

    public int getIncompleteCalls() {
        return incompleteCalls;
    }

    public void setIncompleteCalls(int incompleteCalls) {
        this.incompleteCalls = incompleteCalls;
    }

    public int getAnswerByMachine() {
        return answerByMachine;
    }

    public void setAnswerByMachine(int answerByMachine) {
        this.answerByMachine = answerByMachine;
    }

    public int getAnswerByHuman() {
        return answerByHuman;
    }

    public void setAnswerByHuman(int answerByHuman) {
        this.answerByHuman = answerByHuman;
    }

    public int getAnswerByUnknown() {
        return answerByUnknown;
    }

    public void setAnswerByUnknown(int answerByUnknown) {
        this.answerByUnknown = answerByUnknown;
    }

    public int getInbnd() {
        return inbnd;
    }

    public void setInbnd(int inbnd) {
        this.inbnd = inbnd;
    }

    public int getOutbndApi() {
        return outbndApi;
    }

    public void setOutbndApi(int outbndApi) {
        this.outbndApi = outbndApi;
    }

    public int getOutbndDial() {
        return outbndDial;
    }

    public void setOutbndDial(int outbndDial) {
        this.outbndDial = outbndDial;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    // Creates toString using reflection
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}