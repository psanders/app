/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.voice.js;

import static java.util.logging.Level.WARNING;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.fonoster.core.api.*;
import com.fonoster.exception.ApiException;
import com.fonoster.model.*;
import com.fonoster.voice.asr.ASRFactory;
import com.fonoster.voice.tts.TTSFactory;
import com.google.common.base.Strings;
import java.math.BigDecimal;
import javax.script.*;
import org.astivetoolkit.agi.AgiException;
import org.astivetoolkit.astivlet.Astivlet;
import org.astivetoolkit.astivlet.AstivletRequest;
import org.astivetoolkit.astivlet.AstivletResponse;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Since("1.0")
public class FonosterJS extends Astivlet {
  // Any JS-Script engine available (ie.: Nashorn or Rhino)
  // Eventually I will migrate this to any js engine supporting EMACS 6
  private static final Logger LOG = LoggerFactory.getLogger(FonosterJS.class);
  private static final CommonsConfig commonsConfig = CommonsConfig.getInstance();

  @Override
  public void service(AstivletRequest request, AstivletResponse response) {
    // Start time
    DateTime start = null;
    DateTime end;
    long st = 0;
    long et;
    CallDetailRecord callDetailRecord = null;
    CallDetailRecord.AnswerBy answerBy;
    CallDetailRecord.Direction direction = null;
    App app;

    try {
      // Getting all query parameters
      if (request.getQueryParameter("callId") != null
          && !request.getQueryParameter("callId").isEmpty()) {
        callDetailRecord =
            CallsAPI.getInstance().getCDRById(new ObjectId(request.getQueryParameter("callId")));
      } else {
        String didNum = request.getQueryParameter("didNum");
        DIDNumber destNumber = DIDNumbersAPI.getInstance().getDIDNumber(didNum);
        Account account = destNumber.getIngressAcct();
        app = destNumber.getIngressApp();
        direction = CallDetailRecord.Direction.INBOUND;

        // Then use the main account
        if (account == null) {
          account = UsersAPI.getInstance().getMainAccount(destNumber.getUser());
        }

        callDetailRecord =
            new CallDetailRecord(account, app, request.getCallerId(), didNum, direction);
        callDetailRecord.setStatus(CallDetailRecord.Status.IN_PROGRESS);

        if (account.isSubAccount()) {
          callDetailRecord.setAccount(account.getParentAccount());
          callDetailRecord.setSubAccount(account);
        } else {
          callDetailRecord.setAccount(account);
        }

        if (app == null) {
          LOG.warn("This phone number does not have a related inbound app");
          callDetailRecord.setStatus(CallDetailRecord.Status.FAILED);
          response.hangup();
        }
      }

      if (request.getQueryParameter("amdStatus") != null
          && !request.getQueryParameter("amdStatus").isEmpty()) {
        answerBy = CallDetailRecord.AnswerBy.getByValue(request.getQueryParameter("amdStatus"));
      } else {
        answerBy = CallDetailRecord.AnswerBy.NOT_SURE;
      }

      // Human or Machine?
      callDetailRecord.setAnswerBy(answerBy);

      // If the business logic changes to have appId == null this will have to be adapted
      app =
          AppsAPI.getInstance()
              .getAppById(
                  callDetailRecord.getAccount().getUser(),
                  callDetailRecord.getApp().getId(),
                  false);

      String initDigits = request.getQueryParameter("initDigits");
      long timeout = 0;
      Boolean record = true;

      if (!Strings.isNullOrEmpty(request.getQueryParameter("timeout"))) {
        timeout = new Integer(request.getQueryParameter("timeout"));
      }

      if (!Strings.isNullOrEmpty(request.getQueryParameter("record"))) {
        record = Boolean.valueOf(request.getQueryParameter("record"));
      }

      CallRequest cRequest = new CallRequest();
      cRequest.setAccountId(callDetailRecord.getAccount().getId().toString());
      cRequest.setAppId(callDetailRecord.getApp().getId().toString());
      cRequest.setAnswerBy(callDetailRecord.getAnswerBy());
      cRequest.setApiVersion(callDetailRecord.getApiVersion());
      cRequest.setCallerId(callDetailRecord.getCallerName());
      cRequest.setCallId(callDetailRecord.getId().toString());
      cRequest.setFrom(callDetailRecord.getFrom());
      cRequest.setTo(callDetailRecord.getTo());
      cRequest.setSendDigits(initDigits);
      cRequest.setTimeout(timeout);
      cRequest.setRecord(record);

      // Start time
      start = DateTime.now();
      st = System.nanoTime();
      callDetailRecord.setStart(start);
      CallsAPI.getInstance().updateCDR(callDetailRecord);

      // Hangup the call if the user does not have enough balance
      // This business rule must be reviewed
      if (direction != CallDetailRecord.Direction.INBOUND) {
        DIDNumber origin = DIDNumbersAPI.getInstance().getDIDNumber(callDetailRecord.getFrom());
        long maxAllowedTime =
            BillingAPI.getInstance()
                .maxAllowTime(callDetailRecord.getAccount(), origin, callDetailRecord.getTo());
        response.setAutoHangup((int) maxAllowedTime);
      }

      if (!callDetailRecord.isBillable()) {
        response.streamFile("beep");
      }

      TTSFactory ttsFactory = new TTSFactory(callDetailRecord.getAccount().getUser());
      ASRFactory asrFactory = new ASRFactory(callDetailRecord.getAccount().getUser());

      // Call context
      ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
      ScriptContext callContext = new SimpleScriptContext();
      callContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
      Bindings engineScope = callContext.getBindings(ScriptContext.ENGINE_SCOPE);

      engineScope.put("RECORD_56579084eaa1f291d1c99900", commonsConfig.getRecordingsPath());
      engineScope.put("API_56579084eaa1f291d1c99900", RecordingsAPI.getInstance());
      engineScope.put("$request", cRequest);
      engineScope.put("$response", response);
      engineScope.put("CDR_56579084eaa1f291d1c99900", callDetailRecord);
      engineScope.put("APP_56579084eaa1f291d1c99900", app);
      engineScope.put("TTS_56579084eaa1f291d1c99900", ttsFactory);
      engineScope.put("ASR_56579084eaa1f291d1c99900", asrFactory);
      // Loading built-in libraries
      engineScope.put("LOADER_56579084eaa1f291d1c99900", new Loader(engine, engineScope));
      engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:loader.js')", engineScope);
      engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:core.js')", engineScope);
      engine.eval(getEntryPointSource(app), engineScope);
    } catch (AgiException e) {
      LOG.debug("Channel error/disconnected, cause by: ", e);
    } catch (ScriptException e) {
      // WARNING: Is this the best object Level that we can get?
      callDetailRecord.addLog(WARNING, e.getMessage());
      LOG.debug("Script error, cause by: ", e);
    } catch (ApiException e) {
      LOG.warn("Exception cause by: ", e);
    } catch (RuntimeException e) {
      // Perhaps is a server error, such as unable to find an audio file or connect with a service provider
      // This type of exception will only be reported to the staff.
      LOG.warn("Server error, cause by: ", e);
    }

    // WARNING: Not yet tested
    try {
      response.hangup();
    } catch (AgiException e) {
      LOG.debug("Hang up call: Double kill");
    }

    LOG.debug("Call complete. Performing post call computations...");

    end = DateTime.now();
    et = System.nanoTime();

    long duration = (et - st) / 1000000000;

    assert callDetailRecord != null;
    callDetailRecord.setDuration(duration);
    callDetailRecord.setStart(start);
    callDetailRecord.setEnd(end);
    callDetailRecord.setModified(DateTime.now());

    // This will be true for OUTBOUND calling but not for INBOUND. It must be reviewed.
    if (callDetailRecord.isBillable()) {
      try {
        DIDNumber origin = DIDNumbersAPI.getInstance().getDIDNumber(callDetailRecord.getFrom());
        BigDecimal cost =
            BillingAPI.getInstance().getPrice(origin, callDetailRecord.getTo(), duration);
        callDetailRecord.setCost(cost);
        // Charge the user
        BillingAPI.getInstance().applyAmount(callDetailRecord.getAccount(), cost);
      } catch (ApiException e) {
        LOG.error("Unable to bill call: " + callDetailRecord.getId());
      }
    }

    // XXX: This does not answer to BUSY and CANCEL status. Please research.
    if (!callDetailRecord.getStatus().equals(CallDetailRecord.Status.FAILED)) {
      callDetailRecord.setStatus(CallDetailRecord.Status.COMPLETED);
    }

    CallsAPI.getInstance().updateCDR(callDetailRecord);

    AnalyticsAPI.getInstance()
        .aggregateCall(
            callDetailRecord.getAccount(),
            callDetailRecord.getStatus(),
            callDetailRecord.getAnswerBy(),
            callDetailRecord.getDirection());
  }

  private String getEntryPointSource(App app) throws ApiException {
    for (Script script : app.getScripts()) {
      if (script.getName().equals("main.js")) {
        return script.getSource();
      }
    }
    throw new ApiException("App -> " + app.getId() + " does not have a script " + app.getName());
  }
}
