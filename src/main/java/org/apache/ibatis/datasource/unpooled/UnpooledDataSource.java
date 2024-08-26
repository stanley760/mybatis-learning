/*
 *    Copyright 2009-2024 the original author or authors.
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
package org.apache.ibatis.datasource.unpooled;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.util.MapUtil;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class UnpooledDataSource implements DataSource {
    // Driver 类加载器
    private ClassLoader driverClassLoader;
    // Driveer 属性
    private Properties driverProperties;
    // 已注册的 Driver 映射
    private static final Map<String, Driver> registeredDrivers = new ConcurrentHashMap<>();
    // Driver 类名
    private String driver;
    // JDBC 连接 URL
    private String url;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 是否自动提交事务
    private Boolean autoCommit;
    // 默认事务隔离级别
    private Integer defaultTransactionIsolationLevel;
    // 网络超时时间，以毫秒为单位
    private Integer defaultNetworkTimeout;

    static {
        // 注册 Driver 到 DriverManager
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        // 初始化 DriverManager 的驱动管理器列表
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            registeredDrivers.put(driver.getClass().getName(), driver);
        }
    }

    public UnpooledDataSource() {
    }

    public UnpooledDataSource(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UnpooledDataSource(String driver, String url, Properties driverProperties) {
        this.driver = driver;
        this.url = url;
        this.driverProperties = driverProperties;
    }

    public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username,
                              String password) {
        this.driverClassLoader = driverClassLoader;
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
        this.driverClassLoader = driverClassLoader;
        this.driver = driver;
        this.url = url;
        this.driverProperties = driverProperties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public void setLoginTimeout(int loginTimeout) {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) {
        DriverManager.setLogWriter(logWriter);
    }

    @Override
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    public ClassLoader getDriverClassLoader() {
        return driverClassLoader;
    }

    public void setDriverClassLoader(ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }

    public Properties getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public Integer getDefaultTransactionIsolationLevel() {
        return defaultTransactionIsolationLevel;
    }

    public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
        this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
    }

    /**
     * Gets the default network timeout.
     *
     * @return the default network timeout
     *
     * @since 3.5.2
     */
    public Integer getDefaultNetworkTimeout() {
        return defaultNetworkTimeout;
    }

    /**
     * Sets the default network timeout value to wait for the database operation to complete. See
     * {@link Connection#setNetworkTimeout(java.util.concurrent.Executor, int)}
     *
     * @param defaultNetworkTimeout The time in milliseconds to wait for the database operation to complete.
     * @since 3.5.2
     */
    public void setDefaultNetworkTimeout(Integer defaultNetworkTimeout) {
        this.defaultNetworkTimeout = defaultNetworkTimeout;
    }

    private Connection doGetConnection(String username, String password) throws SQLException {
        //初始化Properties
        Properties props = new Properties();
        // 设置 driverProperties 到 props中
        if (driverProperties != null) {
            props.putAll(driverProperties);
        }
        // 添加用户名和密码
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        // 执行获取连接操作
        return doGetConnection(props);
    }

    private Connection doGetConnection(Properties properties) throws SQLException {
        // 加载 Driver
        initializeDriver();
        // 获取 Connection
        Connection connection = DriverManager.getConnection(url, properties);
        // 配置 Connection
        configureConnection(connection);
        return connection;
    }

    private void initializeDriver() throws SQLException {
        // 判断 registeredDrivers 是否已经存在该 driver ，若不存在，进行初始化
        try {
            MapUtil.computeIfAbsent(registeredDrivers, driver, x -> {
                Class<?> driverType;
                try {
                    // 获取 driver 类型，可能是通过驱动类的全路径名来加载的，或者通过 ClassLoader 来加载
                    if (driverClassLoader != null) {
                        driverType = Class.forName(x, true, driverClassLoader);
                    } else {
                        driverType = Resources.classForName(x);
                    }
                    // 创建 Driver 实例
                    Driver driverInstance = (Driver) driverType.getDeclaredConstructor().newInstance();
                    // 创建DriverProxy对象并注册到 DriverManager
                    DriverManager.registerDriver(new DriverProxy(driverInstance));
                    return driverInstance;
                } catch (Exception e) {
                    throw new RuntimeException("Error setting driver on UnpooledDataSource.", e);
                }
            });
        } catch (RuntimeException re) {
            throw new SQLException("Error setting driver on UnpooledDataSource.", re.getCause());
        }
    }

    private void configureConnection(Connection conn) throws SQLException {
        if (defaultNetworkTimeout != null) {
            conn.setNetworkTimeout(Executors.newSingleThreadExecutor(), defaultNetworkTimeout);
        }
        if (autoCommit != null && autoCommit != conn.getAutoCommit()) {
            conn.setAutoCommit(autoCommit);
        }
        if (defaultTransactionIsolationLevel != null) {
            conn.setTransactionIsolation(defaultTransactionIsolationLevel);
        }
    }

    private static class DriverProxy implements Driver {
        private final Driver driver;

        DriverProxy(Driver d) {
            this.driver = d;
        }

        @Override
        public boolean acceptsURL(String u) throws SQLException {
            return this.driver.acceptsURL(u);
        }

        @Override
        public Connection connect(String u, Properties p) throws SQLException {
            return this.driver.connect(u, p);
        }

        @Override
        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
            return this.driver.getPropertyInfo(u, p);
        }

        @Override
        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public Logger getParentLogger() {
        // requires JDK version 1.6
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

}
