/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = DateTime.class),
    @XmlJavaTypeAdapter(value = ObjectIdAdapter.class, type = ObjectId.class),
    @XmlJavaTypeAdapter(value = AccountAdapter.class, type = Account.class),
    @XmlJavaTypeAdapter(value = CDRAdapter.class, type = CallDetailRecord.class),
    // @XmlJavaTypeAdapter(value = AppAdapter.class, type = App.class)
})
package com.fonoster.model;

import com.fonoster.model.adapters.*;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
