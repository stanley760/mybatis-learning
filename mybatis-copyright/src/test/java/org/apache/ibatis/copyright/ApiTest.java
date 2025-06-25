package org.apache.ibatis.copyright;

import org.apache.ibatis.copyright.binding.MapperRegistry;
import org.apache.ibatis.copyright.dao.IUserDao;
import org.apache.ibatis.copyright.io.Resources;
import org.apache.ibatis.copyright.session.SqlSession;
import org.apache.ibatis.copyright.session.SqlSessionFactory;
import org.apache.ibatis.copyright.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.copyright.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.lang.reflect.Proxy;

/**
 * @author stanley
 * @description:
 * @datetime 2025-06-23 17:07
 * @version: 1.0
 */

public class ApiTest {

    private final Logger logger = LoggerFactory.getLogger(ApiTest.class);


    @Test
    public void test_proxy_factory() throws Exception {

        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();

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
