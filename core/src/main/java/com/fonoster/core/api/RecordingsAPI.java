/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.core.api;

import com.fonoster.config.CommonsConfig;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.InvalidParameterException;
import com.fonoster.exception.ResourceNotFoundException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.*;
import com.fonoster.utils.BeanValidatorUtil;
import com.fonoster.utils.WavToMp3Util;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.util.List;
import java.util.Set;

public class RecordingsAPI {
    private static final RecordingsAPI INSTANCE = new RecordingsAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();
    private static final CommonsConfig config = CommonsConfig.getInstance();

    private RecordingsAPI() {
    }

    public static RecordingsAPI getInstance() {
        return INSTANCE;
    }

    public Recording createRecording(CallDetailRecord callDetailRecord) throws ApiException {
        Recording recording = new Recording(callDetailRecord);
        try {
            recording.setUri(config.getRecordingURI(recording));

            if (!BeanValidatorUtil.isValidBean(recording))
                throw new InvalidParameterException(BeanValidatorUtil.getValidationError(recording));

            ds.save(recording);
            return recording;
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    public void updateRecordingsDuration() throws ApiException {

        List<Recording> rs = ds.createQuery(Recording.class).field("duration").equal(0f).asList();

        for (Recording r : rs) {

            float durationInSeconds;

            try {
                File file = new File(config.getRecordingsPath().concat("/").concat(r.getId().toString()) + ".wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioInputStream.getFormat();
                long audioFileLength = file.length();
                int frameSize = format.getFrameSize();
                float frameRate = format.getFrameRate();
                durationInSeconds = (audioFileLength / (frameSize * frameRate));
            } catch (Exception e) {
                throw new ApiException("Could not calculate recording duration. Ensure the file is in the same server.");
            }

            r.setDuration(durationInSeconds);
            ds.save(r);
        }
    }

    public Recording getRecordingById(Account account, ObjectId id) throws ApiException {
        if (account == null || id == null)
            throw new ApiException("You must provide this resource with a valid accountId and callId");
        return ds.createQuery(Recording.class)
            .field("account").equal(account)
            .field("_id").equal(id).get();
    }

    public File getRecordingFileById(Account account, ObjectId recordingId, Recording.AudioFormat format) throws ApiException {
        if (account == null || recordingId == null)
            throw new ApiException("You must provide this resource with a valid accountId and callId");

        if (format == null) format = Recording.AudioFormat.WAV;
        String ext = format.toString().toLowerCase();

        String path = new String(config.getRecordingsPath());
        path = path.concat("/");
        path = path.concat(recordingId.toString());

        // Not tested yet
        if (format.equals(Recording.AudioFormat.MP3)) {
            // Then file must be converted
            boolean success = WavToMp3Util.convert(
                path.concat(".")
                .concat(Recording.AudioFormat.WAV.toString().toLowerCase()));
            if (!success) {
                throw new ApiException("Temporarily unable to provide this file in '" + ext + "' format");
            }
        }

        File recording = new File(path.toString() + "." + ext);

        if (!recording.exists()) throw new ResourceNotFoundException("Can't find file: " + recording.getAbsolutePath());
        return recording;
    }

    public List<Recording> getRecordings(Account account, CallDetailRecord callDetailRecord) throws ApiException {
        if (account == null || callDetailRecord == null)
            throw new ApiException("You must provide this resource with a valid accountId and callId");
        return ds.createQuery(Recording.class)
            .field("account").equal(account)
            .field("callDetailRecord").equal(callDetailRecord)
            .asList();
    }

    public List<Recording> getRecordings(Account account,
        DateTime start, DateTime end) throws ApiException {

        if (account == null) throw new ApiException("Invalid account.");

        Query<Recording> q = ds.createQuery(Recording.class)
        .field("account").equal(account);

        // All recordings from start date
        if (start != null) {
        q.filter("created >=", start);
        }

        // All recordings until end date
        if (end != null) {
        q.filter("created <=", end);
        }

        return q.limit(1000).asList();
    }

    public List<Recording> getRecordings(Account account,
        DateTime start,
        DateTime end,
        int maxResults,
        int firstResult) throws ApiException {

        if (account == null) throw new ApiException("Invalid account.");

        if (maxResults < 0) maxResults = 0;
        if (maxResults > 1000) maxResults = 1000;

        if (firstResult < 0) firstResult = 0;
        if (firstResult > 1000) firstResult = 1000;

        Query<Recording> q = ds.createQuery(Recording.class)
                .field("account").equal(account);

        // All recordings from start date
        if (start != null) {
            q.filter("created >=", start);
        }

        // All recordings until end date
        if (end != null) {
            q.filter("created <=", end);
        }

        return q.limit(maxResults).offset(firstResult).asList();
    }

    public List<Recording> getRecordingsFor(Account account) throws ApiException {
        if (account == null)
            throw new ApiException("Invalid account");
        return ds.createQuery(Recording.class).field("account").equal(account).asList();
    }

    public void sendBroadcast(String message) {
        Broadcast g = new Broadcast();
        g.setMessage(message);

        List<User> users = ds.createQuery(User.class).asList();
        for (User user : users) {
            user.setCheckedGlobalMessage(false);
            ds.save(user);
        }

        ds.save(g);
    }

    public Broadcast getBroadcast() {
        return ds.find(Broadcast.class).order("-created").get();
    }

    public void updatePhoneNumber(User user, PhoneNumber phoneNumber) throws ApiException {

        if (!user.getEmail().equals(phoneNumber.getUser().getEmail())) {
            throw new UnauthorizedAccessException();
        }

        // JavaBean validation
        if (!validator.validate(phoneNumber).isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Set<ConstraintViolation<PhoneNumber>> validate = validator.validate(phoneNumber);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException(sb.toString());
        }
        ds.save(phoneNumber);
    }
}
