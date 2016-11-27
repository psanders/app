package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Set;

@Since("1.0")
@Entity
@XmlRootElement
public class CallStats {
    @Id
    private ObjectId id;
    @Reference
    private Account account;
    private Period period;
    private DateTime datetime;
    private LinkedHashMap<String, TrafficInfo> stats = new LinkedHashMap<> (0, 0.75F, false);
    @NotNull
    private String apiVersion;

    public CallStats() {
    }

    public CallStats(Account account, Period period) {
        this.setAccount(account);
        this.setPeriod(period);
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    public CallStats(Account account, Period period, DateTime dt) {
        this.setAccount(account);
        this.setPeriod(period);
        this.setDatetime(dt);
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    // Unconventional as fuck!
    public LinkedHashMap<String, TrafficInfo> getStats() {
        LinkedHashMap<String, TrafficInfo> s = new LinkedHashMap<String, TrafficInfo>(0, 0.75F, false);
        Set<String> keys = stats.keySet();

        // Ignore old data
        // TODO: Try replacing BigInteger with Long
        if (getPeriod().equals(Period.REALTIME)) {
            for(String k: keys) {
                if (DateTime
                        .now()
                        .minusSeconds(getSamplesFor(Period.REALTIME))
                        .isBefore(new BigInteger(k).longValue())) {
                    s.put(k, stats.get(k));
                }
            }
        }

        if (getPeriod().equals(Period.HOUR)) {
            for(String k: keys) {
                if (DateTime
                        .now()
                        .minusMinutes(getSamplesFor(Period.HOUR))
                        .isBefore(new BigInteger(k).longValue())) {
                    s.put(k, stats.get(k));
                }
            }
        }

        if (getPeriod().equals(Period.DAY)) {
            for(String k: keys) {
                if (DateTime
                        .now().minusHours(getSamplesFor(Period.DAY))
                        .isBefore(new BigInteger(k).longValue())) {
                    s.put(k, stats.get(k));
                }
            }
        }

        if (getPeriod().equals(Period.MONTH)) {
            for(String k: keys) {
                if (DateTime
                        .now().minusDays(getSamplesFor(Period.MONTH))
                        .isBefore(new BigInteger(k).longValue())) {
                    s.put(k, stats.get(k));
                }
            }
        }

        return s;
    }

    public void aggregate(TrafficInfo t) {
        String k = getKeyFor(getPeriod());

        if (!stats.containsKey(k)) {
            stats.put(k, t);
        } else {
            TrafficInfo a = stats.get(k);
            a.setAnswerByHuman(a.getAnswerByHuman() + t.getAnswerByHuman());
            a.setAnswerByMachine(a.getAnswerByMachine() + t.getAnswerByMachine());
            a.setAnswerByUnknown(a.getAnswerByUnknown() + t.getAnswerByUnknown());
            a.setCompletedCalls(a.getCompletedCalls() + t.getCompletedCalls());
            a.setCost(a.getCost().add(t.getCost()));
            a.setInbnd(a.getInbnd() + t.getInbnd());
            a.setIncompleteCalls(a.getIncompleteCalls() + t.getIncompleteCalls());
            a.setOutbndApi(a.getOutbndApi() + t.getOutbndApi());
            a.setOutbndDial(a.getOutbndDial() + t.getOutbndDial());
            stats.put(k, a);
        }

        if (stats.size() >  getSamplesFor(getPeriod())) {
            Object key = stats.keySet().iterator().next();
            stats.remove(key);
        }
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public DateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(DateTime datetime) {
        this.datetime = datetime;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public enum Period {
        REALTIME,
        HOUR,
        DAY,
        MONTH
    }

    public String getKeyFor(Period period) {

        DateTime dt = DateTime.now();

        if (period.equals(Period.REALTIME)) {
            dt = DateTime.now()
                .withMillisOfSecond(0);
        } else if (period.equals(Period.HOUR)) {
            dt = DateTime.now()
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        } else if (period.equals(Period.DAY)) {
            dt = DateTime.now()
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        } else if (period.equals(Period.MONTH)) {
            dt = DateTime.now()
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        }

        return dt.toDate().getTime() + "";
    }

    public int getSamplesFor(Period period) {

        int samples = 0;

        if (period.equals(Period.REALTIME)) {
            samples = 60;
        } else if (period.equals(Period.HOUR)){
            samples = 60;
        } else if (period.equals(Period.DAY)){
            samples = 24;
        } else if (period.equals(Period.MONTH)){
            samples = 30;
        }

        return samples;
    }

    // Creates toString using reflection
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

