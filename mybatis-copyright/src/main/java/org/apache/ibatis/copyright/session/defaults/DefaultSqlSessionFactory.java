package org.apache.ibatis.copyright.session.defaults;

import org.apache.ibatis.copyright.session.Configuration;
import org.apache.ibatis.copyright.session.SqlSession;
import org.apache.ibatis.copyright.session.SqlSessionFactory;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-24 11:53
 * @version: 1.0
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration config;

    public DefaultSqlSessionFactory(Configuration config) {
        this.config = config;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(config);
    }
}
