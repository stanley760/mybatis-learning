package org.apache.ibatis.copyright.session;

import org.apache.ibatis.copyright.binding.MapperRegistry;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-24 11:39
 * @version: 1.0
 */
public class DefaultSqlSession implements SqlSession {

    private final MapperRegistry mapperRegistry;

    public DefaultSqlSession(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public <T> T selectOne(String statementId) {
        return null;
    }

    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        return (T) ("it proxys the method: " + statementId + ", parameter: " + parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return mapperRegistry.getMapper(type, this);
    }
}
