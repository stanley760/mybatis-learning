package org.apache.ibatis.copyright.mapping;

import org.apache.ibatis.copyright.session.Configuration;

import java.util.Map;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-25 9:49
 * @version: 1.0
 */
public class MappedStatement {

    private Configuration configuration;
    // 命名空间 + 标签ID eg：org.apache.ibatis.debug.mapper.UserMapper.getUserById
    private String id;

    private SqlCommandType sqlCommandType;

    private String sql;
    private String parameterType;
    private String resultType;
    private Map<Integer, String> parameterMap;

    MappedStatement() {
    }

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, String sql,
                       String parameterType, String resultType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.sql = sql;
            mappedStatement.parameterType = parameterType;
            mappedStatement.resultType = resultType;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }
    }

    public String getId() {
        return id;
    }

    public String getSql() {
        return sql;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }
}
