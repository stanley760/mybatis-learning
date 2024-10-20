/*
 *    Copyright 2009-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 可重用执行器
 * <p>
 * 重用的是Statement对象，第一次执行一条sql，会将这条sql的Statement对象缓存在statementMap中。
 * 下一次执行，就可以从缓存中取出Statement对象，减少了重复编译的次数，从而提高了性能。
 * 每个SqlSession对象都有一个Executor对象，因此这个缓存是SqlSession级别的，当SqlSession销毁时，缓存也会销毁。
 * </p>
 *
 * @author Clinton Begin
 */
public class ReuseExecutor extends BaseExecutor {

    private final Map<String, Statement> statementMap = new HashMap<>();

    public ReuseExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
        Configuration configuration = ms.getConfiguration();
        StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
        // 获取statement对象
        Statement stmt = prepareStatement(handler, ms.getStatementLog());
        return handler.update(stmt);
    }

    @Override
    public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler,
                               BoundSql boundSql) throws SQLException {
        Configuration configuration = ms.getConfiguration();
        StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler,
                boundSql);
        // 获取statement对象
        Statement stmt = prepareStatement(handler, ms.getStatementLog());
        return handler.query(stmt, resultHandler);
    }

    @Override
    protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql)
            throws SQLException {
        Configuration configuration = ms.getConfiguration();
        StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, null, boundSql);
        // 获取statement对象
        Statement stmt = prepareStatement(handler, ms.getStatementLog());
        return handler.queryCursor(stmt);
    }

    @Override
    public List<BatchResult> doFlushStatements(boolean isRollback) {
        for (Statement stmt : statementMap.values()) {
            closeStatement(stmt);
        }
        statementMap.clear();
        return Collections.emptyList();
    }

    /**
     * ⭐️⭐️⭐️ 获取Statement对象
     * 主要功能：
     * 如果缓存命中，直接从缓存中获取Statement对象，
     * 否则构建新的Statement对象，并将statement放入缓存中
     *
     * @param handler      处理Statement的对象
     * @param statementLog jdbc日志
     * @return statement对象
     *
     * @throws SQLException 异常信息
     */
    private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
        Statement stmt;
        BoundSql boundSql = handler.getBoundSql();
        String sql = boundSql.getSql();
        // 1. sql中是否存在缓存中的statement
        if (hasStatementFor(sql)) {
            // 2.命中缓存直接从缓存中获取到Statement对象
            stmt = getStatement(sql);
            applyTransactionTimeout(stmt);
        } else {
            Connection connection = getConnection(statementLog);
            // 3. 构建新的Statement对象，并将statement放到缓存中
            stmt = handler.prepare(connection, transaction.getTimeout());
            // 4. 将statement放入缓存中
            putStatement(sql, stmt);
        }
        handler.parameterize(stmt);
        return stmt;
    }

    /**
     * 根据statementMap获取statement对象，如果连接关闭则移除
     * @param sql sql语句
     * @return {@link java.lang.Boolean}
     */
    private boolean hasStatementFor(String sql) {
        try {
            Statement statement = statementMap.get(sql);
            return statement != null && !statement.getConnection().isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private Statement getStatement(String s) {
        return statementMap.get(s);
    }

    private void putStatement(String sql, Statement stmt) {
        statementMap.put(sql, stmt);
    }

}
