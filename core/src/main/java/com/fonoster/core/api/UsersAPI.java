/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
package com.fonoster.core.api;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.fonoster.exception.ApiException;
import com.fonoster.model.Account;
import com.fonoster.model.Activity;
import com.fonoster.model.User;
import com.fonoster.model.services.Service;
import com.fonoster.services.MailManager;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Since("1.0")
public class UsersAPI {
    private static final CommonsConfig commonsConfig = CommonsConfig.getInstance();
    private static final UsersAPI INSTANCE = new UsersAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private UsersAPI() {
    }

    public static UsersAPI getInstance() {
        return INSTANCE;
    }

    public User createUser(String firstName, String lastName, String email, String password) throws ApiException {
        return createUser(firstName, lastName, email, "", password);
    }

    public User createUser(String firstName, String lastName, String email, String phone, String password) throws ApiException {
        if (getUserByEmail(email) != null) throw new ApiException("User already exist");

        User user = new User(firstName, lastName, email, phone, password);
        createAccount(user, "Main");
        ds.save(user);

        // Add sample app
        AppsAPI.getInstance().createApp(user, "monkeys.js", "play('tt-monkeys');");

        MailManager.getInstance().sendMsg(commonsConfig.getTeamMail(), user.getEmail(), "Welcome to Fonoster", "Thanks for trying out Fonoster. We are excited to have you with us. Happy coding!");
        MailManager.getInstance().sendMsg(commonsConfig.getTeamMail(), commonsConfig.getAdminMail(), "New Fonoster account", user.getFirstName() + "<" + user.getEmail() + "> just sign-up for an account.");

        createActivity(user, "Welcome to Fonoster", Activity.Type.INFO);

        return user;
    }

    public User updateUser(User user) {
        ds.save(user);
        return user;
    }

    public User getUserByEmail(String email) {
        try {
            return ds.createQuery(User.class).field("email").equal(email).get();
        } catch (Exception e) {
            return null;
        }
    }

    private Account createAccount(User user, String name) throws ApiException {
        Account account = new Account(user, name);

        // JavaBean validation
        if (!validator.validate(account).isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Set<ConstraintViolation<Account>> validate = validator.validate(account);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException(sb.toString());
        }

        ds.save(account);
        return account;
    }

    // Should put accounts in cache
    public Account getMainAccount(User user) {
        return ds.createQuery(Account.class).field("user").equal(user)
                .field("name").equal("Main")
                .field("deleted").equal(false).get();
    }


    // Should put accounts in cache
    public List<Account> getAccountsFor(User user) {
        return ds.createQuery(Account.class).field("user").equal(user).field("deleted").equal(false).asList();
    }

    // Should put accounts in cache
    public Account getAccountById(ObjectId id) {
        return ds.createQuery(Account.class).field("_id").equal(id).field("deleted").equal(false).get();
    }

    // Only sub-accounts can set deleted = true;
    public Account updateAccount(Account account) {
        account.setModified(DateTime.now());
        ds.save(account);
        return account;
    }

    public Account createSubAccount(Account account, String name) throws ApiException {
        if (account.isSubAccount()) throw new ApiException("Can only add sub-accounts to main accounts.");
        Account sub = new Account(account.getUser(), name);
        ds.save(sub);
        return account;
    }

    public Account removeSubAccount(Account account) throws ApiException {
        if (!account.isSubAccount()) throw new ApiException("This is not a sub-account and can't be deleted.");
        account.setModified(DateTime.now());
        account.setDeleted(true);
        ds.save(account);
        return account;
    }

    public List<Activity> getActivitiesFor(User user, int maxResults) throws ApiException {
        return ds.find(Activity.class).order("-created").field("user").equal(user).limit(maxResults).asList();
    }

    // For internal use only
    public Activity createActivity(User user, String description, Activity.Type type) {
        Activity activity = new Activity(user, description, type);
        ds.save(activity);
        return activity;
    }

    public List<Service> addService(User user, Service service) throws ApiException {
        if (user.getServices() == null || user.getServices().isEmpty()) {
            user.setServices(new ArrayList<>());
        }

        for (Service s : user.getServices()) {
            if (s.getName().equals(service.getName())) throw new ApiException("Service name must be unique");
        }

        user.getServices().add(service);
        ds.save(user);
        return user.getServices();
    }

    public Service getService(User user, String name) throws ApiException {

        for (Service service : user.getServices()) {
            if (service.getName().equals(name)) return service;
        }

        throw new ApiException("Service " + name + " does not exist.");
    }
}
