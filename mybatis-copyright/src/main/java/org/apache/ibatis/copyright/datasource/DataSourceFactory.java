package org.apache.ibatis.copyright.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-30 10:34
 * @version: 1.0
 */
public interface DataSourceFactory {

    void setProperties(Properties properties);

    DataSource getDataSource();
}
