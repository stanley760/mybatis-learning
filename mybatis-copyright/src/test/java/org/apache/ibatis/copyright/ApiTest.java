package org.apache.ibatis.copyright;

import org.apache.ibatis.copyright.binding.MapperProxyFactory;
import org.apache.ibatis.copyright.binding.MapperRegistry;
import org.apache.ibatis.copyright.dao.IUserDao;
import org.apache.ibatis.copyright.session.DefaultSqlSessionFactory;
import org.apache.ibatis.copyright.session.SqlSession;
import org.apache.ibatis.copyright.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author stanley
 * @description:
 * @datetime 2025-06-23 17:07
 * @version: 1.0
 */

public class ApiTest {

    private final Logger logger = LoggerFactory.getLogger(ApiTest.class);


    @Test
    public void test_proxy_factory() {

//        MapperProxyFactory<IUserDao> factory = new MapperProxyFactory<>(IUserDao.class);
//        Map<String, String> sqlSession = Map.of("org.apache.ibatis.copyright.dao.IUserDao.queryUserInfoById",
//                "simulate the result from database, it's a mapper file that query the data from its identify.");
//
//        IUserDao userDao = factory.newInstance(sqlSession);
        MapperRegistry registry = new MapperRegistry();
        registry.addMapper("org.apache.ibatis.copyright.dao");

        SqlSessionFactory factory = new DefaultSqlSessionFactory(registry);
        SqlSession sqlSession = factory.openSession();

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        String result = userDao.queryUserInfoById(1L);
        logger.info("test_proxy_factory 测试结果：{}", result);
    }


    @Test
    public void test_proxy_instance(){
        IUserDao userDao = (IUserDao) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{IUserDao.class},
                ((proxy, method, args) ->
                        String.format("the method %s is proxied.", method.getName())

                ));
        String result = userDao.queryUserInfoById(1L);
        logger.info("测试结果：{}", result);
    }
}
