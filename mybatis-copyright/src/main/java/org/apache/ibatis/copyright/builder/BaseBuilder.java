package org.apache.ibatis.copyright.builder;

import org.apache.ibatis.copyright.session.Configuration;
import org.apache.ibatis.copyright.type.TypeAliasRegistry;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-25 9:11
 * @version: 1.0
 */
public class BaseBuilder {

    protected final Configuration configuration;

    protected final TypeAliasRegistry typeAliasRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }


}
