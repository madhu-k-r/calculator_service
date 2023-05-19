package com.gx.grpc.gxDataStore;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.gx.grpc.Gxdata.GxData;
import com.gx.grpc.service.ProcessCCA;

import lombok.extern.slf4j.Slf4j;
import tmt.generic.cache.LocalCache.CacheEvictionPolicy;
import tmt.generic.cache.LocalCache.CacheProvider;
import tmt.generic.cache.LocalCache.CacheStoreMiss;
import tmt.generic.cache.LocalCache.CacheStoreMode;
import tmt.generic.cache.LocalCache.CacheSyncSrtategy;
import tmt.generic.cache.LocalCache.ReconnectionStrategy;
import tmt.generic.cache.LocalCacheImpl;

@Component
@Service
@Slf4j
public class GxDataStore {

	public static LocalCacheImpl<String, GxData> localcache = new LocalCacheImpl<String, GxData>();
/**
//	@Value("${tmt.localCache.redisConfigfileorPath1}")
	private String redisConfigFileNameorPath = "redis-dev.yml";
	@Value("${tmt.localCache.CacheName}")
	private String cacheName;
	@Value("${tmt.localCache.cacheSize}")
	private int cacheSize;
	@Value("${tmt.localCache.ttl}")
	private long ttl;
	@Value("${tmt.localCache.maxIdle}")
	private long maxIdle;
	@Value("${tmt.localCache.evictionPolicy}")
	private CacheEvictionPolicy evictionPolicy;
	@Value("${tmt.localCache.cacheSyncStrategy}")
	private CacheSyncSrtategy cacheSyncStrategy;
	@Value("${tmt.localCache.cacheStoreMode}")
	private CacheStoreMode cacheStoreMode;
	@Value("${tmt.localCache.cacheStoreMiss}")
	private CacheStoreMiss cacheStoreMiss;
	@Value("${tmt.localCache.cacheProvider}")
	private CacheProvider cacheProvider;
	@Value("${tmt.localCache.reconnectionStrategy}")
	private ReconnectionStrategy reconnectionStrategy;
*/
	
	private static final Logger log = LoggerFactory.getLogger(GxDataStore.class.getName());
	
	// @Value("${tmt.localCache.redisConfigfileorPath1}")
	private String redisConfigFileNameorPath = "redis-dev.yml";// @Value("${tmt.localCache.CacheName}")
	private String cacheName = "myCacheName";// @Value("${tmt.localCache.cacheSize}")
	private int cacheSize;// @Value("${tmt.localCache.ttl}")
	private long ttl;// @Value("${tmt.localCache.maxIdle}")
	private long maxIdle;// @Value("${tmt.localCache.evictionPolicy}")
	private CacheEvictionPolicy evictionPolicy =CacheEvictionPolicy.NONE;// @Value("${tmt.localCache.cacheSyncStrategy}")
	private CacheSyncSrtategy cacheSyncStrategy = CacheSyncSrtategy.UPDATE;// @Value("${tmt.localCache.cacheStoreMode}")
	private CacheStoreMode cacheStoreMode = CacheStoreMode.LOCALCACHE;// @Value("${tmt.localCache.cacheStoreMiss}")
	private CacheStoreMiss cacheStoreMiss = CacheStoreMiss.TRUE;// @Value("${tmt.localCache.cacheProvider}")
	private CacheProvider cacheProvider = CacheProvider.REDISSON;// @Value("${tmt.localCache.reconnectionStrategy}")
	private ReconnectionStrategy reconnectionStrategy = ReconnectionStrategy.LOAD;
	
	public boolean InitializeGxDataStore() {
		// Create Txn and store to datastore (DS = localCache)
		boolean localCacheInitilization = false;
		try {
				log.info("Initializing DS {}",cacheName);
			localCacheInitilization = localcache.intilize(redisConfigFileNameorPath, cacheName, cacheStoreMode,
					cacheSize, ttl, maxIdle, evictionPolicy, cacheSyncStrategy, cacheStoreMiss, cacheProvider,
					reconnectionStrategy);
			return true;
//			}else {localCacheInitilization=true;}
//			log.info("DS Initialized {}..... TBR",cacheName);
		} catch (Exception e) {
			log.error("DS {} Init Exception: ",cacheName);
			//e.printStackTrace();
			return localCacheInitilization;
		}
	}

	public static boolean addGxContextData(String sessionId, GxData gxData) throws Exception {
		try {
			if (false == localcache.addData(sessionId, gxData)) {
				log.error("SID:{} SubscriptionId:{} addData failed", sessionId, gxData.getSubscriptionId());
				// throw new StringNullException("addData failure");
				return false;
			}
			log.info("SID:{} SubscriptionId:{} addData success", sessionId, gxData.getSubscriptionId());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("SID:{} addData Exception:{}", sessionId, e.getMessage());
			// throw new StringNullException("addData failure");
			return false;
		}
	}

	public static boolean updateGxContextData(String sessionId, GxData gxData) throws Exception {
		try {
			if (false == localcache.updateData(sessionId, gxData)) {
				log.error("SID:{} SubscriptionId:{} updateData failed", sessionId, gxData.getSubscriptionId());
				// throw new StringNullException("updateData failure");
				return false;
			}
			log.info("SID:{} SubscriptionId:{} updateData success", sessionId, gxData.getSubscriptionId());
			return true;
		} catch (Exception e) {
			log.error("SID:{} updateData Exception:{}", sessionId, e.getMessage());
			// throw new StringNullException("updateData failure");
			return false;
		}
	}

	public static boolean deleteGxContextData(String sessionId, GxData gxData) throws Exception {
		try {
			if (false == localcache.deleteData(sessionId)) {
				log.error("SID:{} SubscriptionId:{} deleteData failed", sessionId, gxData.getSubscriptionId());
				// throw new StringNullException("deleteData failure");
			}
			log.info("SID:{} SubscriptionId:{} deleteData success", sessionId, gxData.getSubscriptionId());
			return true;
		} catch (Exception e) {
			log.error("SID:{} deleteData Exception:{}", sessionId, e.getMessage());
			// throw new StringNullException("deleteData failure");
			return false;
		}
	}

	public static GxData getGxContextData(String sessionId) throws Exception {
		GxData gxData = new GxData();
		try {
			gxData = localcache.getData(sessionId);
			if (null == gxData) {
				log.error("SID:{} SubscriptionId:{} getData failed", sessionId, gxData.getSubscriptionId());
				return null;
				// throw new StringNullException("getData failure");
			}
			log.info("SID:{} SubscriptionId:{} getData success", sessionId);
			return gxData;
		} catch (Exception e) {
			log.error("SID:{} getData Exception:{}", sessionId, e.getMessage());
			// throw new StringNullException("getData failure");
			return null;
		}
	}
}
