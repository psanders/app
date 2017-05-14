/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model.adapters;

import com.fonoster.model.PhoneNumber;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PhoneNumberAdapter extends XmlAdapter<String, PhoneNumber> {

  @Override
  public String marshal(PhoneNumber phoneNumber) throws Exception {
    return phoneNumber.getNumber();
  }

  @Override
  // We don't need to unmarshal for now
  public PhoneNumber unmarshal(String phoneNumber) throws Exception {
    return null;
  }
}
