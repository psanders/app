@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = DateTime.class),
        @XmlJavaTypeAdapter(value = ObjectIdAdapter.class, type = ObjectId.class),
        @XmlJavaTypeAdapter(value = AccountAdapter.class, type = Account.class),
        @XmlJavaTypeAdapter(value = AppAdapter.class, type = App.class),
        @XmlJavaTypeAdapter(value = CDRAdapter.class, type = CallDetailRecord.class)
}) package com.fonoster.model;

import com.fonoster.model.adapters.*;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

