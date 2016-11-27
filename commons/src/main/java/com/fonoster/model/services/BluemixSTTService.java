package com.fonoster.model.services;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;

import javax.validation.constraints.NotNull;

@Since("1.0")
public class BluemixSTTService extends Service {

    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String apiVersion;

    public BluemixSTTService() {
    }

    public BluemixSTTService(String name) {
        super(name, Type.SST);
        this.setApiVersion(CommonsConfig.getInstance().getCurrentVersion());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
