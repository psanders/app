package com.fonoster.core;

import com.fonoster.core.api.*;
import com.fonoster.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ITAstived {
    // The only thing this test needs to prove is that we have access to astived
    @Test
    public void tesCall() throws Exception {
        User john = UsersAPI.getInstance().getUserByEmail("john@doe.com");

        //App app = AppsAPI.getInstance().createApp(john, "Angry Monkeys", "play('tt-monkeys')");
        App app = AppsAPI.getInstance().createApp(john, "Monkey App", "loadJS('lib.js'); monkeys();");

        // Adding support script
        Script lib = new Script("lib.js");
        lib.setSource("function monkeys() {play('tt-monkeys');}");
        List<Script> scripts = app.getScripts();
        scripts.add(lib);
        app.setScripts(scripts);

        DBManager.getInstance().getDS().save(app);

        PhoneNumber number = NumbersAPI.getInstance().getDefault(john);

        String to = "+17853178070";

        int cntBefore = CallsAPI.getInstance().getCDRs(john.getAccount(),
            null,
            null,
            number.getNumber(),
            to,
            1000,
            0,
            null,
            null).size();

        CallRequest cr = new CallRequest();
        cr.setAccountId(john.getAccount().getId().toString());
        cr.setFrom(number.getNumber());
        cr.setTo(to);
        cr.setAppId(app.getId().toString());
        cr.setBillable(true);
        CallsAPI.getInstance().call(cr);

        int cntAfter = CallsAPI.getInstance().getCDRs(john.getAccount(),
            null,
            null,
            number.getNumber(),
            to,
            1000,
            0,
            null,
            null).size ();

        Assert.assertTrue("Verifies that a cdr was generated", cntBefore == (cntAfter - 1));
    }
}
