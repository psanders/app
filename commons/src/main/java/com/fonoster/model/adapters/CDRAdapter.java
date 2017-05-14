/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model.adapters;

import com.fonoster.model.CallDetailRecord;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CDRAdapter extends XmlAdapter<String, CallDetailRecord> {

  @Override
  public String marshal(CallDetailRecord cdr) throws Exception {
    return cdr.getId().toString();
  }

  @Override
  // We don't need to unmarshal for now
  public CallDetailRecord unmarshal(String cdr) throws Exception {
    return null;
  }
}
