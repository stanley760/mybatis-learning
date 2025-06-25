package org.apache.ibatis.copyright.session;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-24 11:37
 * @version: 1.0
 */
public interface SqlSession {

    <T> T selectOne(String statementId);

    <T> T selectOne(String statementId, Object parameter);

    <T> T getMapper(Class<T> type);

    Configuration getConfiguration();
}

