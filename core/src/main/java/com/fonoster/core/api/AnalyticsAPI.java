/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.core.api;

import com.fonoster.model.Account;
import com.fonoster.model.CallDetailRecord;
import com.fonoster.model.CallStats;
import com.fonoster.model.TrafficInfo;
import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

public class AnalyticsAPI {
    private static final AnalyticsAPI INSTANCE = new AnalyticsAPI();
    final private Datastore ds;

    private AnalyticsAPI() {
        ds = DBManager.getInstance().getDS();
    }

    public static AnalyticsAPI getInstance() {
        return INSTANCE;
    }

    // NOTICE: This method could add more data, like duration avg etc.
    // WARN: Should I remove synchronized from this method?
    public synchronized void aggregateCall(Account account,
        CallDetailRecord.Status status,
        CallDetailRecord.AnswerBy answerBy,
        CallDetailRecord.Direction dir) {

        TrafficInfo tInfo = new TrafficInfo();

        if (status.equals(CallDetailRecord.Status.COMPLETED)) {
            tInfo.setCompletedCalls(1);
        } else {
            tInfo.setIncompleteCalls(1);
        }

        if (answerBy.equals(CallDetailRecord.AnswerBy.HUMAN)) {
            tInfo.setAnswerByHuman(1);
        } else if (answerBy.equals(CallDetailRecord.AnswerBy.MACHINE)) {
            tInfo.setAnswerByMachine(1);
        } else {
            tInfo.setAnswerByUnknown(1);
        }

        if (dir.equals(CallDetailRecord.Direction.INBOUND)) {
            tInfo.setInbnd(1);
        } else if (dir.equals(CallDetailRecord.Direction.OUTBOUND_API)) {
            tInfo.setOutbndApi(1);
        } else {
            tInfo.setOutbndDial(1);
        }

        CallStats realtime = getStats(account, CallStats.Period.REALTIME, null);
        realtime.aggregate(tInfo);

        CallStats hour = getStats(account, CallStats.Period.HOUR, null);
        hour.aggregate(tInfo);

        CallStats day = getStats(account, CallStats.Period.DAY, DateTime.now());
        day.aggregate(tInfo);

        CallStats month = getStats(account, CallStats.Period.MONTH, DateTime.now());
        month.aggregate(tInfo);

        ds.save(realtime);
        ds.save(hour);
        ds.save(day);
        ds.save(month);
    }

    // TODO: Add support for datetime range
    public CallStats getStats(Account account, CallStats.Period period, DateTime dt) {

        Query<CallStats> q = ds.createQuery(CallStats.class)
                .field("account").equal(account)
                .field("period").equal(period);

        if (period.equals(CallStats.Period.DAY)) {
            dt = dt.withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
            q.field("datetime").equal(dt);
        }

        if (period.equals(CallStats.Period.MONTH)) {
            dt = dt.withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
            q.field("datetime").equal(dt);
        }

        CallStats cs = (CallStats) q.get();

        if (cs != null) {
            return cs;
        }

        if (period.equals(CallStats.Period.DAY) || period.equals(CallStats.Period.MONTH)) {
            return new CallStats(account, period, dt);
        } else {
            return new CallStats(account, period);
        }
    }

}
