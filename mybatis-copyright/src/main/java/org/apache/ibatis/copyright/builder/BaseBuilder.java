package org.apache.ibatis.copyright.builder;

import org.apache.ibatis.copyright.session.Configuration;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-25 9:11
 * @version: 1.0
 */
public class BaseBuilder {

    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }


}
