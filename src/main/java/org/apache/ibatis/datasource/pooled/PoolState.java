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
package org.apache.ibatis.datasource.pooled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Clinton Begin
 */
public class PoolState {

  // This lock does not guarantee consistency.
  // Field values can be modified in PooledDataSource
  // after the instance is returned from
  // PooledDataSource#getPoolState().
  // A possible fix is to create and return a 'snapshot'.
  private final ReentrantLock lock = new ReentrantLock();
  // 所属的 PooledDataSource 对象
  protected PooledDataSource dataSource;
  // 空闲的连接池资源集合
  protected final List<PooledConnection> idleConnections = new ArrayList<>();
  // 活跃的连接池资源集合
  protected final List<PooledConnection> activeConnections = new ArrayList<>();
  // 请求次数
  protected long requestCount;
  // 累计的请求连接的时间
  protected long accumulatedRequestTime;
  // 累计的使用连接的时间。从连接取出到归还，算一次使用的时间；
  protected long accumulatedCheckoutTime;
  // 使用连接超时的次数
  protected long claimedOverdueConnectionCount;
  // 累计超时时间
  protected long accumulatedCheckoutTimeOfOverdueConnections;
  // 累计等待时间
  protected long accumulatedWaitTime;
  // 等待次数
  protected long hadToWaitCount;
  // 无效的连接次数
  protected long badConnectionCount;

  public PoolState(PooledDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public long getRequestCount() {
    lock.lock();
    try {
      return requestCount;
    } finally {
      lock.unlock();
    }
  }

  public long getAverageRequestTime() {
    lock.lock();
    try {
      return requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
    } finally {
      lock.unlock();
    }
  }

  public long getAverageWaitTime() {
    lock.lock();
    try {
      return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;
    } finally {
      lock.unlock();
    }
  }

  public long getHadToWaitCount() {
    lock.lock();
    try {
      return hadToWaitCount;
    } finally {
      lock.unlock();
    }
  }

  public long getBadConnectionCount() {
    lock.lock();
    try {
      return badConnectionCount;
    } finally {
      lock.unlock();
    }
  }

  public long getClaimedOverdueConnectionCount() {
    lock.lock();
    try {
      return claimedOverdueConnectionCount;
    } finally {
      lock.unlock();
    }
  }

  public long getAverageOverdueCheckoutTime() {
    lock.lock();
    try {
      return claimedOverdueConnectionCount == 0 ? 0
          : accumulatedCheckoutTimeOfOverdueConnections / claimedOverdueConnectionCount;
    } finally {
      lock.unlock();
    }
  }

  public long getAverageCheckoutTime() {
    lock.lock();
    try {
      return requestCount == 0 ? 0 : accumulatedCheckoutTime / requestCount;
    } finally {
      lock.unlock();
    }
  }

  public int getIdleConnectionCount() {
    lock.lock();
    try {
      return idleConnections.size();
    } finally {
      lock.unlock();
    }
  }

  public int getActiveConnectionCount() {
    lock.lock();
    try {
      return activeConnections.size();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public String toString() {
    lock.lock();
    try {
      StringBuilder builder = new StringBuilder();
      builder.append("\n===CONFIGURATION==============================================");
      builder.append("\n jdbcDriver                     ").append(dataSource.getDriver());
      builder.append("\n jdbcUrl                        ").append(dataSource.getUrl());
      builder.append("\n jdbcUsername                   ").append(dataSource.getUsername());
      builder.append("\n jdbcPassword                   ")
          .append(dataSource.getPassword() == null ? "NULL" : "************");
      builder.append("\n poolMaxActiveConnections       ").append(dataSource.poolMaximumActiveConnections);
      builder.append("\n poolMaxIdleConnections         ").append(dataSource.poolMaximumIdleConnections);
      builder.append("\n poolMaxCheckoutTime            ").append(dataSource.poolMaximumCheckoutTime);
      builder.append("\n poolTimeToWait                 ").append(dataSource.poolTimeToWait);
      builder.append("\n poolPingEnabled                ").append(dataSource.poolPingEnabled);
      builder.append("\n poolPingQuery                  ").append(dataSource.poolPingQuery);
      builder.append("\n poolPingConnectionsNotUsedFor  ").append(dataSource.poolPingConnectionsNotUsedFor);
      builder.append("\n ---STATUS-----------------------------------------------------");
      builder.append("\n activeConnections              ").append(getActiveConnectionCount());
      builder.append("\n idleConnections                ").append(getIdleConnectionCount());
      builder.append("\n requestCount                   ").append(getRequestCount());
      builder.append("\n averageRequestTime             ").append(getAverageRequestTime());
      builder.append("\n averageCheckoutTime            ").append(getAverageCheckoutTime());
      builder.append("\n claimedOverdue                 ").append(getClaimedOverdueConnectionCount());
      builder.append("\n averageOverdueCheckoutTime     ").append(getAverageOverdueCheckoutTime());
      builder.append("\n hadToWait                      ").append(getHadToWaitCount());
      builder.append("\n averageWaitTime                ").append(getAverageWaitTime());
      builder.append("\n badConnectionCount             ").append(getBadConnectionCount());
      builder.append("\n===============================================================");
      return builder.toString();
    } finally {
      lock.unlock();
    }
  }

}
