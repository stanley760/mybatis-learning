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
package org.apache.ibatis.parsing;

/**
 * @author Clinton Begin
 */
public interface TokenHandler {

    /**
     * 处理 Token （对应有四个不同的子类实现）
     * 1. BindingTokenHandler  使用OGNL表达式，完成对content内容的解析
     * 2. VariableTokenHandler 占位符${}格式的处理
     * 3. DynamicCheckerTokenParser 解析XML的Sql语句时，查看是否Sql里是否包含${}，如果包含就叫动态SQl
     * 4. ParameterMappingTokenHandler 使用?进行替换,并且将#{}里的内容转变成ParameterMapping对象。
     *
     * @param content Token 字符串
     * @return 处理后的结果
     */
    String handleToken(String content);
}
