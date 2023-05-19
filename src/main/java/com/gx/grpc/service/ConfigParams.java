package com.gx.grpc.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ConfigParams {

    private String vendorId;

    private String originRealmData;

    private String redisConfigfileorPath;

    private String cacheName;

    private String cacheSize;

    private String ttl;

    private String maxIdle;

    private String evictionPolicy;

    private String cacheSyncStrategy;

    private String cacheStoreMode;

    private String cacheStoreMiss;

    private String cacheProvider;

    private String reconnectionStrategy;

    private String smVersion;

    private String policydata;

    private String gxTdfRadiusEnabled;

    private String originHost;

    private String originRealm;

    private String templateDataPath;

    private String templateDataFile;

    private String inputDataFile;

    private String ipAddress;

    private String sharedSecret;

    private String radiusPort;

    @Value("${VENDOR_ID}")
    public void setVendorId(String id){
        this.vendorId=id;
    }


    @Value("${tmt.origin-RealmData}")
    public void setOriginRealmData(String originRealmData) {
        this.originRealmData = originRealmData;
    }

    @Value("${tmt.localCache.redisConfigfileorPath}")
    public void setRedisConfigfileorPath(String redisConfigfileorPath) {
        this.redisConfigfileorPath = redisConfigfileorPath;
    }

    @Value("${tmt.localCache.CacheName}")
    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    @Value("${tmt.localCache.cacheSize}")
    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Value("${tmt.localCache.ttl}")
    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    @Value("${tmt.localCache.maxIdle}")
    public void setMaxIdle(String maxIdle) {
        this.maxIdle = maxIdle;
    }

    @Value("${tmt.localCache.evictionPolicy}")
    public void setEvictionPolicy(String evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
    }

    @Value("${tmt.localCache.cacheSyncStrategy}")
    public void setCacheSyncStrategy(String cacheSyncStrategy) {
        this.cacheSyncStrategy = cacheSyncStrategy;
    }

    @Value("${tmt.localCache.cacheStoreMode}")
    public void setCacheStoreMode(String cacheStoreMode) {
        this.cacheStoreMode = cacheStoreMode;
    }

    @Value("${tmt.localCache.cacheStoreMiss}")
    public void setCacheStoreMiss(String cacheStoreMiss) {
        this.cacheStoreMiss = cacheStoreMiss;
    }

    @Value("${tmt.localCache.cacheProvider}")
    public void setCacheProvider(String cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @Value("${tmt.localCache.reconnectionStrategy}")
    public void setReconnectionStrategy(String reconnectionStrategy) {
        this.reconnectionStrategy = reconnectionStrategy;
    }
//---------------------------------->
    @Value("${tmt.GxData.smVersion}")
    public void setSmVersion(String smVersion) {
        this.smVersion = smVersion;
    }

    @Value("${tmt.GxData.policydata}")
    public void setPolicydata(String policydata) {
        this.policydata = policydata;
    }

    @Value("${tmt.gx.gxTdfRadiusEnabled}")
    public void setGxTdfRadiusEnabled(String gxTdfRadiusEnabled) {
        this.gxTdfRadiusEnabled = gxTdfRadiusEnabled;
    }

    @Value("${ORIGIN_HOST}")
    public void setOriginHost(String originHost) {
        this.originHost = originHost;
    }

    @Value("${ORIGIN_REALM}")
    public void setOriginRealm(String originRealm) {
        this.originRealm = originRealm;
    }

    @Value("${templateDataPath}")
    public void setTemplateDataPath(String templateDataPath) {
        this.templateDataPath = templateDataPath;
    }

    @Value("${templateDataFile}")
    public void setTemplateDataFile(String templateDataFile) {
        this.templateDataFile = templateDataFile;
    }

    @Value("${inputDataFile}")
    public void setInputDataFile(String inputDataFile) {
        this.inputDataFile = inputDataFile;
    }

    @Value("${ipAddress}")
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Value("${sharedSecret}")
    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @Value("${port}")
    public void setRadiusPort(String radiusPort) {
        this.radiusPort = radiusPort;
    }
}

