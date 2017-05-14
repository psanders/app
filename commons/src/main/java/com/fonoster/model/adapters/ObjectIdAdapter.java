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
import org.bson.types.ObjectId;

public class ObjectIdAdapter extends XmlAdapter<String, ObjectId> {

  @Override
  public String marshal(ObjectId obj) throws Exception {
    return obj.toString();
  }

  @Override
  public ObjectId unmarshal(String obj) throws Exception {
    return new ObjectId(obj);
  }
}
