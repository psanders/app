package com.fonoster.core.api;

import com.fonoster.config.CommonsConfig;
import com.fonoster.core.config.CoreConfig;
import com.fonoster.exception.*;
import com.fonoster.model.*;
import com.fonoster.utils.BeanValidatorUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.asteriskjava.live.AsteriskChannel;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.LiveException;
import org.asteriskjava.live.OriginateCallback;
import org.asteriskjava.manager.action.OriginateAction;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class CallsAPI {
    private static final Logger LOG = LoggerFactory.getLogger(CallsAPI.class);
    private static final CallsAPI INSTANCE = new CallsAPI();
    private static final CommonsConfig config = CommonsConfig.getInstance();
    private static final CoreConfig coreConfig = CoreConfig.getInstance();
    private static final Datastore ds = DBManager.getInstance().getDS();

    private CallsAPI() {
    }

    public static CallsAPI getInstance() {
        return INSTANCE;
    }

    public CallDetailRecord getCDRById(ObjectId id) throws ApiException {
        if (id == null)
            throw new ResourceNotFoundException("Not found.");
        return ds.createQuery(CallDetailRecord.class)
                .field("_id").equal(id).get();
    }

    public CallDetailRecord getCDRById(Account account, ObjectId id) throws ApiException {
        if (account == null) throw new ApiException("Invalid account.");

        if (id == null) throw new ResourceNotFoundException("Not found.");

        Query<CallDetailRecord> q = ds.createQuery(CallDetailRecord.class);
        q.field("_id").equal(id);

        if (account.isSubAccount()) {
            q.field("subAccount").equal(account);
        } else {
            q.field("account").equal(account);
        }

        return q.get();
    }

    public List<CallDetailRecord> getCDRs(Account account,
        DateTime start,
        DateTime end,
        String from,
        String to,
        int maxResults,
        int firstResult,
        CallDetailRecord.Status status,
        CallDetailRecord.AnswerBy answerBy) throws ApiException {

        if (account == null) throw new ApiException("Invalid account.");

        if (maxResults < 0) maxResults = 0;
        if (maxResults > 1000) maxResults = 1000;

        if (firstResult < 0) firstResult = 0;
        if (firstResult > 1000) firstResult = 1000;

        Query<CallDetailRecord> q = ds.createQuery(CallDetailRecord.class);

        // Then is a sub-account
        if (account.isSubAccount()) {
            q.field("subAccount").equal(account);
        } else {
            q.field("account").equal(account);
        }

        if (status != null) {
            q.field("status").equal(status);
        }

        if (answerBy != null) {
            q.field("answerBy").equal(answerBy);
        }

        if (from != null) {
            q.field("from").equal(from);
        }

        if (to != null) {
            q.field("to").equal(to);
        }

        // All crds from start date
        if (start != null) {
            q.field("created").greaterThanOrEq(start);
        }

        // All crds until end date
        if (end != null) {
            q.field("created").lessThanOrEq(end);
        }

        return q.limit(maxResults).offset(firstResult).asList();
    }

    // Not the optimal solution
    public int getCDRsTotal(Account account,
        DateTime start,
        DateTime end,
        String from,
        String to,
        CallDetailRecord.Status status,
        CallDetailRecord.AnswerBy answerBy) throws ApiException {

        if (account == null) throw new ApiException("Invalid account.");

        Query<CallDetailRecord> q = ds.createQuery(CallDetailRecord.class);

        // Then is a sub-account
        if (account.isSubAccount()) {
            q.field("subAccount").equal(account);
        } else {
            q.field("account").equal(account);
        }

        if (status != null) {
            q.field("status").equal(status);
        }

        if (answerBy != null) {
            q.field("answerBy").equal(answerBy);
        }

        if (from != null) {
            q.field("from").equal(from);
        }

        if (to != null) {
            q.field("to").equal(to);
        }

        // All crds from start date
        if (start != null) {
            q.filter("created >=", start);
        }

        // All crds until end date
        if (end != null) {
            q.filter("created <=", end);
        }

        return q.asList().size();
    }

    // Not the optimal solution
    public BigDecimal getCallsCost(Account account,
        DateTime start,
        DateTime end,
        String from,
        String to,
        CallDetailRecord.Status status,
        CallDetailRecord.AnswerBy answerBy) throws ApiException {

        if (account == null) throw new ApiException("Invalid account.");

        Query<CallDetailRecord> q = ds.createQuery(CallDetailRecord.class);

        // Then is a sub-account
        if (account.isSubAccount()) {
            q.field("subAccount").equal(account);
        } else {
            q.field("account").equal(account);
        }

        if (status != null) {
            q.field("status").equal(status);
        }

        if (answerBy != null) {
            q.field("answerBy").equal(answerBy);
        }

        if (from != null) {
            q.field("from").equal(from);
        }

        if (to != null) {
            q.field("to").equal(to);
        }

        // All crds from start date
        if (start != null) {
            q.filter("created >=", start);
        }

        // All crds until end date
        if (end != null) {
        q.filter("created <=", end);
        }

        BigDecimal cost = new BigDecimal("0");
        List<CallDetailRecord> cdrs = q.asList();

        for (CallDetailRecord cdr : cdrs) {
        cost = cost.add(cdr.getCost());
        }

        return cost;
        }


    public CallDetailRecord updateCDR(CallDetailRecord callDetailRecord) {
        callDetailRecord.setModified(DateTime.now());
        ds.save(callDetailRecord);
        return callDetailRecord;
    }

    public CallDetailRecord hangup(Account account, ObjectId id) throws ApiException {
        // Get call details
        CallDetailRecord cdr = getCDRById(account, id);

        if (cdr == null) throw new ResourceNotFoundException();

        AsteriskServer as = ManagerProvider.getInstance().getAsteriskServer();

        try {
            AsteriskChannel ac = as.getChannelById(cdr.getChannelId());
            ac.hangup();
            if (cdr.getStatus().equals(CallDetailRecord.Status.QUEUED) ||
                    cdr.getStatus().equals(CallDetailRecord.Status.RINGING)) {
                cdr.setStatus(CallDetailRecord.Status.CANCELED);
            } else if (cdr.getStatus().equals(CallDetailRecord.Status.IN_PROGRESS)) {
                cdr.setStatus(CallDetailRecord.Status.COMPLETED);
                cdr.setEnd(DateTime.now());
            }
            updateCDR(cdr);
        } catch (NullPointerException e) {
            // This channel is not live anymore.
        }
        return cdr;
    }

    // What is this function for?
    public CallDetailRecord redirectToApp(Account account, ObjectId id, App app) throws ApiException {
        // Get call details
        CallDetailRecord cdr = getCDRById(account, id);

        if (cdr == null || app == null) throw new ResourceNotFoundException();

        AsteriskServer as = ManagerProvider.getInstance().getAsteriskServer();

        try {
            AsteriskChannel ac = as.getChannelById(cdr.getChannelId());
            ac.hangup();
            // Too easy
            if (cdr.getStatus().equals(CallDetailRecord.Status.QUEUED) ||
                    cdr.getStatus().equals(CallDetailRecord.Status.RINGING)) cdr.setApp(new App());
            else if (cdr.getStatus().equals(CallDetailRecord.Status.IN_PROGRESS)) {
                //
            }
            updateCDR(cdr);
        } catch (NullPointerException e) {
            // This channel is not live anymore.
        }
        return cdr;
    }

    public CallDetailRecord call(CallRequest request) throws ApiException {

        LOG.debug("call.request => " + request);

        if (!BeanValidatorUtil.isValidBean(request)) {
            throw new ApiException("Invalid parameter/s. "
                    + BeanValidatorUtil.getValidationError(request));
        }

        PhoneNumber phoneNumber = NumbersAPI.getInstance().getPhoneNumber(request.getFrom());
        Account account = UsersAPI.getInstance().getAccountById(new ObjectId(request.getAccountId()));
        App app = AppsAPI.getInstance().getAppById(account.getUser(), new ObjectId(request.getAppId()), false);

        // Ensure user has enough balance
        long maxAllowedTime = BillingAPI.getInstance().maxAllowTime(account, phoneNumber, request.getTo());

        if (request.isBillable() && maxAllowedTime <=0 ) throw new InsufficientFundsException();

        // WARNING: Avoid this dom stuff by either creating more numbers all putting here a better note
        //if (phoneNumber == null || !phoneNumber.getUser().getEmail()
        //        .equals(account.getUser().getEmail())) {
        //    throw new ResourceNotFoundException("Unable to find phone number '" + request.getFrom() + "' in your records");
        //}
        if (phoneNumber == null) {
            throw new ResourceNotFoundException("Unable to find phone number '" + request.getFrom() + "' in your records");
        }

        if (request.getSendDigits() != null && !request.getSendDigits().isEmpty() && !request.getSendDigits().matches("^[a-d0-9w/*/#]*$"))
            throw new InvalidParameterException("Invalid sendDigit: " + request.getSendDigits() + ". It must match with 0-9, *#abcd, w (.5s pause)");

        String reformattedTo;

        // Try to reformat the to parameter
        try {
            reformattedTo = reformatNumber(phoneNumber, request.getTo());
        } catch (NumberParseException e) {
            throw new InvalidPhoneNumberException("Unable to format 'To' number. Try using a E.164 formatted number.");
        }

        final CallDetailRecord callDetailRecord = new CallDetailRecord(account,
            app,
            phoneNumber.getNumber(),
            reformattedTo,
            null,
            CallDetailRecord.Direction.OUTBOUND_API);

        callDetailRecord.setStatus(CallDetailRecord.Status.QUEUED);
        callDetailRecord.setAnswerBy(CallDetailRecord.AnswerBy.NONE);
        callDetailRecord.setCost(new BigDecimal("0"));
        callDetailRecord.setBillable(request.isBillable());

        // Then a sub-account was used
        if (account.isSubAccount()) {
            callDetailRecord.setAccount(account.getParentAccount());
            callDetailRecord.setSubAccount(account);
        } else {
            callDetailRecord.setAccount(account);
        }

        // Stash it in the database
        updateCDR(callDetailRecord);

        // Set a delay base on server-load
        // or perhaps a better algorithm
        final OriginateAction originateAction;

        // Setting up the channel info
        String channel = "SIP/"
            .concat(reformattedTo.replace("+", ""))
            .concat("@" + phoneNumber.getProvider().getTrunk());

        originateAction = new OriginateAction();
        originateAction.setChannel(channel);
        originateAction.setContext(phoneNumber.getProvider().getContext());
        originateAction.setExten(reformattedTo.replace("+", ""));
        originateAction.setPriority(1);
        originateAction.setVariable("astivedHost", coreConfig.getAstivedHost());
        originateAction.setVariable("astivedPort", "" + coreConfig.getAstivedPort());
        originateAction.setVariable("callId", callDetailRecord.getId().toString());
        originateAction.setVariable("initDigits", request.getSendDigits());
        originateAction.setVariable("record", "" + request.isRecord());
        originateAction.setVariable("timeout", "" + request.getTimeout() * 1000);
        originateAction.setAccount(callDetailRecord.getId().toString());
        originateAction.setActionId(callDetailRecord.getId().toString());
        originateAction.setTimeout(request.getTimeout() * 1000);

        ManagerProvider
            .getInstance()
            .getAsteriskServer()
            .originateAsync(originateAction, new OriginateCallback() {
                @Override
                public void onDialing(AsteriskChannel channel) {
                    LOG.info("call.id: ".concat(callDetailRecord.getId().toString()).concat(" ringing"));
                    callDetailRecord.setStatus(CallDetailRecord.Status.RINGING);
                    callDetailRecord.setChannelId(channel.getId());
                    updateCDR(callDetailRecord);
                }

                @Override
                public void onSuccess(AsteriskChannel channel) {
                    LOG.info("call.id: " + callDetailRecord.getId() + " in progress");
                    if (request.isRecord()) {
                        // By default save it as wav and mix in/out.
                        // What happen if I don't call stopMonitoring?
                        Recording recording = null;
                        try {
                            recording = RecordingsAPI.getInstance().createRecording(callDetailRecord);
                        } catch (ApiException e) {
                            LOG.error(e.getMessage());
                        }

                        // Ensures both sides of the conversation get mix.
                        channel.startMonitoring(
                            config.getRecordingsPath()
                            .concat("/")
                            .concat(recording.getId().toString())
                            , "wav", true);
                    }
                    callDetailRecord.setStatus(CallDetailRecord.Status.IN_PROGRESS);
                    updateCDR(callDetailRecord);
                }

                @Override
                public void onNoAnswer(AsteriskChannel channel) {
                    LOG.info("call.id: " + callDetailRecord.getId() + "  no answer");
                    callDetailRecord.setStatus(CallDetailRecord.Status.NO_ANSWER);
                    updateCDR(callDetailRecord);
                    AnalyticsAPI.getInstance().aggregateCall(callDetailRecord.getAccount(),
                            callDetailRecord.getStatus(), callDetailRecord.getAnswerBy(), callDetailRecord.getDirection());
                }

                @Override
                public void onBusy(AsteriskChannel channel) {
                    LOG.info("call.id: " + callDetailRecord.getId() + " busy");
                    callDetailRecord.setStatus(CallDetailRecord.Status.BUSY);
                    updateCDR(callDetailRecord);
                    AnalyticsAPI.getInstance().aggregateCall(callDetailRecord.getAccount(),
                            callDetailRecord.getStatus(), callDetailRecord.getAnswerBy(), callDetailRecord.getDirection());
                }

                @Override
                public void onFailure(LiveException cause) {
                    LOG.info("call.id: " + callDetailRecord.getId()
                            + " fail (cause->" + cause.getMessage() + ")");
                    callDetailRecord.setStatus(CallDetailRecord.Status.FAILED);
                    updateCDR(callDetailRecord);
                    AnalyticsAPI.getInstance().aggregateCall(callDetailRecord.getAccount(),
                            callDetailRecord.getStatus(), callDetailRecord.getAnswerBy(), callDetailRecord.getDirection());
                }
            });

        return callDetailRecord;
    }

    public String reformatNumber(PhoneNumber referenceNumber, String to) throws NumberParseException {
        Phonenumber.PhoneNumber toPn = PhoneNumberUtil.getInstance().parse(to, referenceNumber.getCountryISOCode());
        return PhoneNumberUtil.getInstance().format(toPn, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}
