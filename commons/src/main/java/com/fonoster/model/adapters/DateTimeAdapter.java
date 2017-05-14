/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.joda.time.DateTime;

public class DateTimeAdapter extends XmlAdapter<String, DateTime> {

  @Override
  public String marshal(DateTime dt) throws Exception {
    return dt.toString();
  }

  @Override
  public DateTime unmarshal(String dt) throws Exception {
    return new DateTime(dt);
  }
}
