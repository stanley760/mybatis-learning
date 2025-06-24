package org.apache.ibatis.copyright.binding;

import cn.hutool.core.lang.ClassScanner;
import org.apache.ibatis.copyright.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author stanley
 * @description:
 * @datetime 2025-06-24 11:26
 * @version: 1.0
 */
public class MapperRegistry {

    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }

        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating MapperProxyFactory for type " + type, e);
        }
    }

    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new RuntimeException("type " + type +" already exists!");
            }
            knownMappers.put(type, new MapperProxyFactory<>(type));
        }
    }

    public void addMapper(String packageName) {
        Set<Class<?>> classes = ClassScanner.scanPackage(packageName);
        classes.forEach(this::addMapper);
    }


    private <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }
}
