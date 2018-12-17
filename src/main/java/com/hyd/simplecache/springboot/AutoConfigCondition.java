package com.hyd.simplecache.springboot;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;
import java.util.Set;

public class AutoConfigCondition implements Condition {

    private static Boolean configFound = null;

    /**
     * Somehow this method is called twice by Spring, so I cached the result in {@link #configFound}.
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        if (configFound != null) {
            return configFound;
        }

        Environment environment = context.getEnvironment();

        if (environment instanceof AbstractEnvironment) {
            MutablePropertySources propertySources = ((AbstractEnvironment) environment).getPropertySources();
            configFound = searchPropertyPrefix(propertySources, "simple-cache.");
        } else {
            configFound = false;
        }

        return configFound;
    }

    private boolean searchPropertyPrefix(MutablePropertySources propertySources, String prefix) {
        for (PropertySource<?> propertySource : propertySources) {
            Object source = propertySource.getSource();

            if (source instanceof Map) {
                Set<Map.Entry> entrySet = ((Map) source).entrySet();

                for (Map.Entry entry : entrySet) {
                    if (entry.getKey() instanceof String) {
                        if (((String) entry.getKey()).startsWith(prefix)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
