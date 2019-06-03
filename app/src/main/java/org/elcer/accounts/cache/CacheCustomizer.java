package org.elcer.accounts.cache;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;



public class CacheCustomizer implements DescriptorCustomizer {
    @Override
    public void customize(ClassDescriptor descriptor) {
        descriptor.setCacheInterceptorClass(AccountCacheInterceptor.class);
    }
}
