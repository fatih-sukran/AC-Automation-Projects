package com.automation.center.config

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.Key
import org.aeonbits.owner.Config.LoadPolicy
import org.aeonbits.owner.Config.Sources
import org.aeonbits.owner.ConfigCache


@LoadPolicy(Config.LoadType.MERGE)
@Sources("system:properties", "system:env", "classpath:config.properties")
interface IConfig: Config {
    companion object {
        fun getConfig(): IConfig {
            return ConfigCache.getOrCreate(IConfig::class.java)
        }
    }

    @Key("lighthouse.url")
    fun lighthouseUrl(): String

    @Key("automation.center.gateway.base.url")
    fun gatewayBaseUrl(): String

    @Key("suite.id")
    fun suiteId(): Long
}