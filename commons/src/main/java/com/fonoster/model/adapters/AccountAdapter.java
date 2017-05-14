/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model.adapters;

import com.fonoster.model.Account;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AccountAdapter extends XmlAdapter<String, Account> {

  @Override
  public String marshal(Account account) throws Exception {
    return account.getId().toString();
  }

  @Override
  // We don't need to unmarshal for now
  public Account unmarshal(String account) throws Exception {
    return null;
  }
}
