package org.apache.ibatis.copyright.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.copyright.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-30 10:34
 * @version: 1.0
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private Properties properties;


    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {

        DruidDataSource datasource = new DruidDataSource();
        datasource.setDriverClassName(properties.getProperty("driverClassName"));
        datasource.setUrl(properties.getProperty("url"));
        datasource.setUsername(properties.getProperty("username"));
        datasource.setPassword(properties.getProperty("password"));
        return datasource;
    }
}
