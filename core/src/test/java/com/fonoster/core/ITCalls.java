/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.core;

import com.fonoster.core.api.CallsAPI;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.model.Account;
import com.fonoster.model.CallRequest;
import com.fonoster.model.User;
import org.junit.Test;

public class ITCalls {

    @Test
    public void call() throws Exception {
        User john = UsersAPI.getInstance().getUserByEmail("john@doe.com");
        Account account = UsersAPI.getInstance().getMainAccount(john);

        CallRequest cr = new CallRequest("594f376324aa9a007266ef43",
                "",
                account.getId().toString(),
                "+17066041487",
                "+17853178070",
                "Test",
                60,
                "",
                false,
                "v1.0",
                false);

        CallsAPI.getInstance().call(cr);
    }
}
