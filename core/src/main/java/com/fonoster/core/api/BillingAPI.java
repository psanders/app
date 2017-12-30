/*
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
package com.fonoster.core.api;

import com.fonoster.annotations.Since;
import com.fonoster.exception.ApiException;
import com.fonoster.model.*;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Since("1.0")
public class BillingAPI {
    private static final Logger LOG = LoggerFactory.getLogger(BillingAPI.class);
    private static BillingAPI instance;
    private static Datastore ds;

    private BillingAPI() {
    }

    public static BillingAPI getInstance() throws ApiException {
        if (instance == null || ds == null) {
            try {
                ds = DBManager.getInstance().getDS();
                instance = new BillingAPI();
            } catch (UnknownHostException e) {
                throw new ApiException();
            }
        }
        return instance;
    }

    // WARNING: Modo bestia sin cache
    public Rate getRate(ServiceProvider provider, String dest) throws ApiException {

        if (provider == null) throw new ApiException("Can't find service provider");
        if (dest == null || dest.equals("")) throw new ApiException("You must indicate the destination number");

        Query<Rate> q = ds.createQuery(Rate.class).field("provider").equal(provider);
        List<Rate> rates = q.asList();

        List<Rate> match = new ArrayList<>();

        for (Rate r : rates) {
            if (dest.replace("+", "").matches(r.getPrefix().concat("(.*)"))) {
                match.add(r);
            }
        }

        if (match.size() == 1) {
            return match.get(0);
        }

        match.sort((o1, o2) -> (o1.getPrefix().length() < o2.getPrefix().length()) ? 1 : -1);

        // TODO: Add default/flat rate for numbers not in the rate list
        if (match.size() == 0) {
            //LOG.error("Unable to find rate for this number. spId => ".concat(provider.getId().toString()).concat(" dest => ").concat(dest));
            //throw new ApiException("Unable to find rate for this number");
            Rate rate = new Rate(provider, dest, "Flat rate");
            rate.setBuying(new BigDecimal(1));
            rate.setSelling(new BigDecimal(1));
            return rate;
        }

        return match.get(0);
    }

    public BigDecimal getPrice(DIDNumber origin, String dest, long time) throws ApiException {
        // WARNING: Perhaps we may want to add a "connection" rate
        if (time == 0) return new BigDecimal("0");

        Rate rate = getRate(origin.getGateway().getProvider(), dest);
        // Up to minute billing
        BigDecimal minutes = new BigDecimal(Math.ceil((double) time / 60));
        return rate.getSelling().multiply(minutes);
    }

    // Maximum time allowed(approximate)
    public long maxAllowTime(Account account, DIDNumber origin, String dest) throws ApiException {
        Rate rate = getRate(origin.getGateway().getProvider(), dest);
        BigDecimal balance = account.getUser().getPmntInfo().getBalance();

        if (balance.compareTo(new BigDecimal("0")) <= 0) return 0;

        BigDecimal seconds = balance.divide(rate.getSelling(), 5, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(60));

        return seconds.longValue();
    }

    public synchronized void applyAmount(Account account, BigDecimal amount) throws ApiException {
        // Ensure is from database
        User u = account.getUser();
        u = UsersAPI.getInstance().getUserByEmail(u.getEmail());

        u.getPmntInfo().setBalance(u.getPmntInfo().getBalance().subtract(amount));
        ds.save(u);
    }
}