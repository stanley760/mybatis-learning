package org.apache.ibatis.copyright.transaction.jdbc;

import org.apache.ibatis.copyright.session.TransactionIsolationLevel;
import org.apache.ibatis.copyright.transaction.Transaction;
import org.apache.ibatis.copyright.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-30 15:07
 * @version: 1.0
 */
public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
