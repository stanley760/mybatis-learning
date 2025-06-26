package org.apache.ibatis.copyright.session.defaults;

import org.apache.ibatis.copyright.mapping.MappedStatement;
import org.apache.ibatis.copyright.session.Configuration;
import org.apache.ibatis.copyright.session.SqlSession;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-24 11:39
 * @version: 1.0
 */
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statementId) {
        return null;
    }

    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        return (T) ("it proxys the method: " + statementId + ", parameter: " + parameter + " sql: " + mappedStatement.getSql());
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
