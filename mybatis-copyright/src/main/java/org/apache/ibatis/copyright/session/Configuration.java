package org.apache.ibatis.copyright.session;

import org.apache.ibatis.copyright.binding.MapperRegistry;
import org.apache.ibatis.copyright.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-25 9:12
 * @version: 1.0
 */
public class Configuration {


    /**
     * 映射注册机
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);



    /**
     * 映射的语句，存在Map里
     */

    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public void addMapper(String packageName) {
        mapperRegistry.addMapper(packageName);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public void addMappedStatement(String id, MappedStatement ms) {
        mappedStatements.put(id, ms);
    }

    public MappedStatement getMappedStatement(String statementId) {
        return mappedStatements.get(statementId);
    }

    public boolean hasStatement(String statementName) {
        return mappedStatements.containsKey(statementName);
    }
}
