package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.InvalidPhoneNumberException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Locale;

@Entity
@Since("1.0")
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhoneNumber {
    @Id
    private ObjectId id;
    @Reference
    @NotNull
    private User user;
    @NotNull
    @NotEmpty
    private String number;
    @NotNull
    @NotEmpty
    private String countryISOCode;
    @NotNull
    @Reference
    private ServiceProvider provider;
    @NotNull
    private DateTime created;
    private DateTime modified;
    // Should expire every month
    private DateTime expired;
    private boolean voiceEnabled;
    private boolean smsEnabled;
    private boolean mmsEnabled;
    private Status status;
    // Inbound calls can only be directed to this app if set
    @Reference
    private App inbndApp;
    // If set will be use to bill the incoming call, otherwise 'main' will be billed
    @Reference
    private Account inbndAcct;
    // Use for outbound calls from the apps editor
    private boolean preferred;
    @NotNull
    private String apiVersion;

    public PhoneNumber() {
    }

    public PhoneNumber(User user,
                       ServiceProvider provider,
                       String number,
                       String countryISOCode) throws ApiException {
        // Move this to a jb validator
        boolean validCode = false;
        for (String cc : Locale.getISOCountries()) {
            if (countryISOCode.equals(cc)) {
                validCode = true;
                break;
            }
        }

        if (!validCode) throw new ApiException("Invalid countryCode: " + countryISOCode);

        Phonenumber.PhoneNumber pNumber;

        try {
            pNumber = PhoneNumberUtil.getInstance().parse(number, countryISOCode);
        } catch (NumberParseException e) {
            throw new InvalidPhoneNumberException("Unable to parse number: " + number);
        }

        if (!PhoneNumberUtil.getInstance().isValidNumber(pNumber)) throw new InvalidPhoneNumberException();

        // Because we want to store the number using the ISO standard
        this.number = PhoneNumberUtil.getInstance().format(pNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        this.countryISOCode = countryISOCode;
        this.created = DateTime.now();
        this.modified = DateTime.now();
        this.setStatus(Status.ACTIVE);
        this.user = user;
        this.id = new ObjectId();
        this.provider = provider;
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountryISOCode() {
        return countryISOCode;
    }

    public void setCountryISOCode(String countryISOCode) {
        this.countryISOCode = countryISOCode;
    }

    @XmlTransient
    public ServiceProvider getProvider() {
        return provider;
    }

    public void setProvider(ServiceProvider provider) {
        this.provider = provider;
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

    public DateTime getExpired() {
        return expired;
    }

    public void setExpired(DateTime expired) {
        this.expired = expired;
    }

    public boolean isVoiceEnabled() {
        return voiceEnabled;
    }

    public void setVoiceEnabled(boolean voiceEnabled) {
        this.voiceEnabled = voiceEnabled;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public boolean isMmsEnabled() {
        return mmsEnabled;
    }

    public void setMmsEnabled(boolean mmsEnabled) {
        this.mmsEnabled = mmsEnabled;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public App getInbndApp() {
        return inbndApp;
    }

    public void setInbndApp(App inbndApp) {
        this.inbndApp = inbndApp;
    }

    @XmlTransient
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getInbndAcct() {
        return inbndAcct;
    }

    public void setInbndAcct(Account inbndAcct) {
        this.inbndAcct = inbndAcct;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public enum Status {
        ACTIVE,
        AWAITING_REGISTRATION,
        EXPIRING_SOON,
        EXPIRED,
        TERMINATED
    }

    // Creates toString using reflection
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

