package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.sun.xml.txw2.annotation.XmlElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Range;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Since("1.0")
@XmlElement
@Entity
@Embedded
public class PaymentInfo {
    private BigDecimal balance;
    private PaymentMethod method;
    private List<Transaction> transactions;
    private boolean autopay;
    private float lastTrans;
    @NotNull
    private String apiVersion;

    public PaymentInfo() {
        balance = new BigDecimal("0.00");
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public boolean isAutopay() {
        return autopay;
    }

    public void setAutopay(boolean autopay) {
        this.autopay = autopay;
    }

    public float getLastTrans() {
        return lastTrans;
    }

    public void setLastTrans(float lastFunds) {
        this.lastTrans = lastFunds;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public static enum PaymentType {
        PAYPAL,
        CREDIT_CARD
    }

    @XmlElement
    @JsonIgnoreProperties(ignoreUnknown = true)
    static public class PaymentMethod {
        @NotNull
        private PaymentType type;
        @NotNull
        private String description;
        // From 1 to 12
        @Range(min = 1, max = 12)
        private int expMonth;
        // From current year and ahead
        @Range(min = 15, max = 100)
        private int expYear;
        private String countryCode;
        private String postalCode;
        // If applies
        private String token;

        public PaymentMethod() {}

        public PaymentType getType() {
            return type;
        }

        public void setType(PaymentType type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getExpMonth() {
            return expMonth;
        }

        public void setExpMonth(int expMonth) {
            this.expMonth = expMonth;
        }

        public int getExpYear() {
            return expYear;
        }

        public void setExpYear(int expYear) {
            this.expYear = expYear;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }
    }

    @XmlElement
    // May need to un-embed in the future
    static public class Transaction {
        private String id;
        private DateTime created;
        private BigDecimal amount;
        private String status;
        private String description;
        private String method;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public DateTime getCreated() {
            return created;
        }

        public void setCreated(DateTime created) {
            this.created = created;
        }

        // Usually payment method plus id
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }
}
