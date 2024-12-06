package com.automation.center.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Key;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigCache;

@LoadPolicy(Config.LoadType.MERGE)
@Sources({"system:properties", "system:env", "classpath:config.properties"})
public interface IConfig extends Config {
    static IConfig getConfig() {
        return ConfigCache.getOrCreate(IConfig.class);
    }

    @Key("lighthouse.url")
    String lighthouseUrl();

    @Key("automation.center.gateway.base.url")
    String gatewayBaseUrl();

    @Key("suite.id")
    long suiteId();
}

