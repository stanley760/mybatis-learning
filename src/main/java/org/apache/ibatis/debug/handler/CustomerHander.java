package org.apache.ibatis.debug.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ywb
 * @description:
 * @datetime 2024-08-19 10:35
 * @version: 1.0
 */
@MappedTypes({String.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CustomerHander implements TypeHandler<String>  {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void setParameter(PreparedStatement ps, int index, String value, JdbcType arg3) throws SQLException {
        log.info("************************使用自定义TypeHandler");
        ps.setString(index, value);
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        log.info("************************使用自定义TypeHandler,ResultSet列名获取字符串");
        return rs.getString(columnName);
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        log.info("************************使用自定义TypeHandler,ResultSet下标获取字符串");
        return rs.getString(columnIndex);
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        log.info("************************使用自定义TypeHandler,CallableStatement下标获取字符串");
        return cs.getString(columnIndex);
    }
}
