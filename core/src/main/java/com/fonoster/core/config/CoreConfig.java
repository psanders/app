package com.fonoster.core.config;

import com.braintreegateway.Environment;
import com.fonoster.config.AbstractConfig;
import org.apache.commons.configuration.PropertiesConfiguration;

public class CoreConfig extends AbstractConfig {
    private static final CoreConfig INSTANCE = new CoreConfig();
    private PropertiesConfiguration config;

    private CoreConfig() {
        super("fonoster.conf");
        config = super.getConfig();

        // Overwrite the default values if JVM values are used
        if (System.getProperty("manager.host") != null) {
            config.setProperty("manager.host", System.getProperty("manager.host"));
        }

        if (System.getProperty("manager.port") != null) {
            config.setProperty("manager.port", System.getProperty("manager.port"));
        }


        if (System.getProperty("astived.host") != null) {
            config.setProperty("astived.host", System.getProperty("astived.host"));
        }

        if (System.getProperty("astived.port") != null) {
            config.setProperty("astived.port", System.getProperty("astived.port"));
        }

        if (System.getProperty("db.host") != null) {
            config.setProperty("db.host", System.getProperty("db.host"));
        }

        if (System.getProperty("db.port") != null) {
            config.setProperty("db.port", System.getProperty("db.port"));
        }

        // Overwrite the default values if ENV values are used
        if (System.getenv("MANAGER_HOST") != null) {
            config.setProperty("manager.host", System.getenv("MANAGER_HOST"));
        }

        if (System.getenv("MANAGER_PORT") != null) {
            config.setProperty("manager.port", System.getenv("MANAGER_PORT"));
        }

        if (System.getenv("ASTIVED_HOST") != null) {
            config.setProperty("astived.host", System.getenv("ASTIVED_HOST"));
        }

        if (System.getenv("ASTIVED_PORT") != null) {
            config.setProperty("astived.port", System.getenv("ASTIVED_PORT"));
        }

        if (System.getenv("DB_HOST") != null) {
            config.setProperty("db.host", System.getenv("DB_HOST"));
        }

        if (System.getenv("DB_PORT") != null) {
            config.setProperty("db.port", System.getenv("DB_PORT"));
        }
    }

    public String getManagerHost() {
        return config.getString("manager.host");
    }

    public int getManagerPort() {
        return config.getInt("manager.port");
    }

    public String getManagerUsername() {
        return config.getString("manager.username");
    }

    public String getManagerSecret() {
        return config.getString("manager.secret");
    }

    public String getAstivedHost() {
        return config.getString("astived.host");
    }

    public int getAstivedPort() {
        return config.getInt("astived.port");
    }

    public String getDbHost() {
        return config.getString("db.host");
    }

    public int getDbPort() {
        return config.getInt("db.port");
    }

    public String getDbUsername() {
        return config.getString("db.username");
    }

    public String getDbSecret() {
        return config.getString("db.secret");
    }

    public String getDbName() {
        return config.getString("db.name");
    }

    public Environment getBraintreeEnvironment() {
        String env = config.getString("braintree.environment");

        if (env.equals("SANDBOX")) {
            return Environment.SANDBOX;
        } else if (env.equals("DEVELOPMENT")) {
            return Environment.DEVELOPMENT;
        } else if (env.equals("PRODUCTION")) {
            return Environment.PRODUCTION;
        }
        return null;
    }

    public String getBraintreeMerchantId() {
        return config.getString("braintree.merchantId");
    }

    public String getBraintreePublicKey() {
        return config.getString("braintree.publicKey");
    }

    public String getBraintreePrivateKey() {
        return config.getString("braintree.privateKey");
    }

    public String getBluemixUsername() {
        return config.getString("bluemix.username");
    }

    public String getBluemixPassword() {
        return config.getString("bluemix.password");
    }

    public static CoreConfig getInstance() {
        return INSTANCE;
    }
}
