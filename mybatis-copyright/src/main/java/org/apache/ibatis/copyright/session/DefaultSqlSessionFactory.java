package org.apache.ibatis.copyright.session;

import org.apache.ibatis.copyright.binding.MapperRegistry;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-24 11:53
 * @version: 1.0
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final MapperRegistry mapperRegistry;

    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
