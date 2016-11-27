package com.fonoster.model.services;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;

import javax.validation.constraints.NotNull;

@Since("1.0")
public class IvonaTTSService extends Service {

    @NotNull
    private String secretKey;
    @NotNull
    private String accessKey;
    @NotNull
    private String apiVersion;

    public IvonaTTSService() {
    }

    public IvonaTTSService(String name) {
        super(name, Type.TTS);
        this.setApiVersion (CommonsConfig.getInstance().getCurrentVersion());
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
