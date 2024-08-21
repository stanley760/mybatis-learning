package org.apache.ibatis.debug;

import org.apache.ibatis.debug.entity.User;
import org.apache.ibatis.debug.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author ywb
 * @description: Debug入口
 * @datetime 2024-08-19 9:53
 * @version: 1.0
 */
public class DebuggerMain {

    private static final Logger log = LoggerFactory.getLogger(DebuggerMain.class);

    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";

        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.getUserById(1L);
        log.info("id: {} name:{} age:{}", user.getId(), user.getUserName(), user.getAge());
        sqlSession.commit();
        sqlSession.close();
    }
}
