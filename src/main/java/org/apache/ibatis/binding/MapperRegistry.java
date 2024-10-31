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
package org.apache.ibatis.binding;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 * @author Lasse Voss
 */
public class MapperRegistry {
  /**
   * MyBatis Configuration 对象
   */
  private final Configuration config;

  /**
   * MapperProxyFactory 的映射
   *
   * KEY：Mapper 接口
   */
  private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new ConcurrentHashMap<>();

  public MapperRegistry(Configuration config) {
    this.config = config;
  }

  /**
   *  ⭐️⭐️⭐️万里长征第四步:获得 Mapper Proxy 对象
   * <p>
   * 从knowMappers(Map<Class<?>, MapperProxyFactory<?>> knowMappers = new HashMap<>())缓存中获取代理工厂MapperProxyFactory
   *
   * </p>
   *
   * @param type Mapper的接口类型的class对象
   * @param sqlSession SQL会话对象
   * @return Mapper Proxy 对象的实例
   * @param <T> Mapper的类型参数
   */
  @SuppressWarnings("unchecked")
  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    // 1.获得 MapperProxyFactory 对象(问题：knowMappers 在那里初始化的？)
    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
    // 2.不存在，则抛出 BindingException 异常
    if (mapperProxyFactory == null) {
      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    }
    // 创建 Mapper Proxy 对象
    try {
      return mapperProxyFactory.newInstance(sqlSession);
    } catch (Exception e) {
      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
  }

  /**
   * 获得指定包下，符合条件的类
   * @param type 类class
   * @return {@link java.lang.Boolean}
   * @param <T> Mapper的类型参数
   */
  public <T> boolean hasMapper(Class<T> type) {
    return knownMappers.containsKey(type);
  }

  /**
   * 添加到 knownMappers 中
   * @param type Mapper接口的class对象
   * @param <T> Mapper的类型参数
   */
  public <T> void addMapper(Class<T> type) {
    // 1. 判断是否是接口
    if (type.isInterface()) {
      // 2. 已经添加到knowMappers，则抛出 BindingException 异常
      if (hasMapper(type)) {
        throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
      }
      boolean loadCompleted = false;
      try {
        // 3. 添加mapper类型到knowMappers中
        knownMappers.put(type, new MapperProxyFactory<>(type));
        // It's important that the type is added before the parser is run
        // otherwise the binding may automatically be attempted by the
        // mapper parser. If the type is already known, it won't try.
        // 4. 解析 Mapper 的注解配置
        MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
        parser.parse();
        // 5. 设置加载完成的标志位为 true
        loadCompleted = true;
      } finally {
        // 6. 如果加载失败，从 knownMappers 中移除该 Mapper 接口类型的配置
        if (!loadCompleted) {
          knownMappers.remove(type);
        }
      }
    }
  }

  /**
   * Gets the mappers.
   *
   * @return the mappers
   *
   * @since 3.2.2
   */
  public Collection<Class<?>> getMappers() {
    return Collections.unmodifiableCollection(knownMappers.keySet());
  }

  /**
   * Adds the mappers.
   * 扫描指定包，并将符合的类，添加到 knownMappers 中
   * @param packageName
   *          the package name
   * @param superType
   *          the super type
   *
   * @since 3.2.2
   */
  public void addMappers(String packageName, Class<?> superType) {
    ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
    // 1.扫描指定包下的指定类
    resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
    Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
    // 2.遍历，添加到 knownMappers 中
    for (Class<?> mapperClass : mapperSet) {
      addMapper(mapperClass);
    }
  }

  /**
   * Adds the mappers.
   *
   * @param packageName
   *          the package name
   *
   * @since 3.2.2
   */
  public void addMappers(String packageName) {
    addMappers(packageName, Object.class);
  }

}
