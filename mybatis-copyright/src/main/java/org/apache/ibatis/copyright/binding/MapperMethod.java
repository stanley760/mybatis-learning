package org.apache.ibatis.copyright.binding;

import org.apache.ibatis.copyright.mapping.MappedStatement;
import org.apache.ibatis.copyright.mapping.SqlCommandType;
import org.apache.ibatis.copyright.session.Configuration;
import org.apache.ibatis.copyright.session.SqlSession;

import java.lang.reflect.Method;

import static org.apache.ibatis.copyright.mapping.SqlCommandType.*;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-25 15:52
 * @version: 1.0
 */
public class MapperMethod {
    private final SqlCommand command;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.command = new SqlCommand(configuration, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) throws Throwable {
        Object result = null;
        switch (command.getType()) {
            case SELECT:
                result = sqlSession.selectOne("select", args);
                break;
            case INSERT:
                break;
            case UPDATE:
                break;
            case DELETE:
                break;
            default:
                throw new IllegalArgumentException("unknown execution method that  is: " + command.getName());
        }
        return result;
    }

    public static class SqlCommand {

        private final String name;
        // sql操作类型的枚举
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            // 获取方法名称
            final String methodName = method.getName();
            // 方法所声明的类名
            final Class<?> declaringClass = method.getDeclaringClass();

            MappedStatement ms = resolveMappedStatement(mapperInterface, methodName, declaringClass, configuration);

            name = ms.getId();
            type = ms.getSqlCommandType();
            if (type == SqlCommandType.UNKNOWN) {
                throw new RuntimeException("Unknown execution method for: " + name);
            }

        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }

        private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName, Class<?> declaringClass, Configuration configuration) {
            // 获取mapper的全限定类名+方法名为statementId
            String statementId = mapperInterface.getName() + "." + methodName;
            // 如何configuration对象中包含该statementId则直接返回configuration中的MapperStatement对象
            if (configuration.hasStatement(statementId)) {
                return configuration.getMappedStatement(statementId);
            }
            if (mapperInterface.equals(declaringClass)) {
                return null;
            }
            for (Class<?> superInterface : mapperInterface.getInterfaces()) {
                if (declaringClass.isAssignableFrom(superInterface)) {
                    MappedStatement ms = resolveMappedStatement(superInterface, methodName, declaringClass, configuration);
                    if (ms != null) {
                        return ms;
                    }
                }
            }
            return null;
        }
    }
}
