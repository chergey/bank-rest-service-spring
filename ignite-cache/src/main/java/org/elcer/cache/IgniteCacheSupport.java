package org.elcer.cache;

import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.client.ClientCacheConfiguration;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;

public class IgniteCacheSupport implements CacheSupport {

    private IgniteClient igniteClient;


    public IgniteCacheSupport(String url) {
        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .setAddresses(url);

        igniteClient = Ignition.startClient(clientConfiguration);
    }


    public <K, V> Cache<K, V> getOrCreateCache(String name) {
        ClientCacheConfiguration ccfg = new ClientCacheConfiguration();

        ccfg.setName(name);
        ccfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        ccfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);

        return new IgniteCacheImpl<>(igniteClient.getOrCreateCache(ccfg));
    }

}
