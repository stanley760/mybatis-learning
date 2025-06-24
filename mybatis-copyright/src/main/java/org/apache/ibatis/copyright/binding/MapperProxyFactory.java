package org.apache.ibatis.copyright.binding;

import org.apache.ibatis.copyright.session.SqlSession;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-23 17:52
 * @version: 1.0
 */
public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }
}
