/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model.adapters;

import com.fonoster.model.DIDNumber;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DIDAdapter extends XmlAdapter<String, DIDNumber> {

  @Override
  public String marshal(DIDNumber did) throws Exception {
    return did.getSpec().getLocation().getTelUrl();
  }

  @Override
  // We don't need to unmarshal for now
  public DIDNumber unmarshal(String phoneNumber) throws Exception {
    return null;
  }
}
