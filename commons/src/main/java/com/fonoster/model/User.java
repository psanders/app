/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.fonoster.model.services.Service;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@Since("1.0")
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
public class User {
    @Id
    private String email;
    private DateTime created;
    private DateTime modified;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String password;
    private boolean disabled;
    @NotNull
    private String apiVersion;
    @Reference
    private Account account;
    private String phone;
    private String company;
    private String timezone;
    private String countryCode;
    @NotNull
    private PaymentInfo pmntInfo;
    private List<Service> services;

    // Use to verify if user has close a global alert.
    private boolean checkedGlobalMessage;

    public User() {
    }

    public User(String firstName, String lastName, String email, String phone, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.created = DateTime.now();
        this.modified = DateTime.now();
        this.disabled = false;
        this.apiVersion  = CommonsConfig.getInstance().getCurrentVersion();
        this.timezone = "America/New_York";
        this.countryCode = "US";
        this.pmntInfo = new PaymentInfo();
        this.services = new ArrayList<>();
        // We don't know yet if there is any global msg
        this.checkedGlobalMessage = false;
    }

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.created = DateTime.now();
        this.modified = DateTime.now();
        this.disabled = false;
        this.apiVersion  = CommonsConfig.getInstance().getCurrentVersion();
        this.timezone = "America/New_York";
        this.countryCode = "US";
        this.pmntInfo = new PaymentInfo();
        this.checkedGlobalMessage = false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlTransient
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public PaymentInfo getPmntInfo() {
        return pmntInfo;
    }

    public void setPmntInfo(PaymentInfo pmntInfo) {
        this.pmntInfo = pmntInfo;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public boolean hasCheckedGlobalMessage() {
        return checkedGlobalMessage;
    }

    public void setCheckedGlobalMessage(boolean checkedGlobalMessage) {
        this.checkedGlobalMessage = checkedGlobalMessage;
    }

    // Creates toString using reflection
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
