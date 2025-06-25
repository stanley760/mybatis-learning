package org.apache.ibatis.copyright.session;

import org.apache.ibatis.copyright.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.copyright.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * @description:
 * @author      ywb
 * @datetime    2025-06-25 9:00
 * @version:    1.0
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }

    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }
}
