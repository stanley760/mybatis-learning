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
public class GenericTokenParser {

    /**
     * 起始的token标识符
     */
    private final String openToken;

    /**
     * 结束的token标识符
     */
    private final String closeToken;

    /**
     * 处理开始和结束token的Handler接口实现实例
     */
    private final TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    /**
     * openToken和endToken间的字符串取出来用handler处理下，然后再拼接到一块
     *
     * @param text 原始字符串
     * @return {@link java.lang.String}
     */
    public String parse(String text) {
        // 判断传入的字符串不为空
        if (text == null || text.isEmpty()) {
            return "";
        }
        // 获取起始标识符的位置索引
        // ⭐️: indexOf函数的返回值-1表示不存在，0表示在在开头的位置
        int start = text.indexOf(openToken);
        if (start == -1) {
            return text;
        }
        // 字符串转为字符数组
        char[] src = text.toCharArray();
        int offset = 0;
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        do {
            // 搜索开始token的前一个字符，如果前一个字符是反斜杠，则说明这个token是escaped的，需要去掉反斜杠，继续处理下一个token
            if (start > 0 && src[start - 1] == '\\') {
                //如果text中在openToken前存在转义符就将转义符去掉。如果openToken前存在转义符，start的值必然大于0，最小也为1
                //因为此时openToken是不需要进行处理的，所以也不需要处理endToken。接着查找下一个openToken
                builder.append(src, offset, start - offset - 1).append(openToken);
                //重设offset
                offset = start + openToken.length();
            } else {
                // 搜索结束token 逻辑
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                // 添加 offset 和 openToken 之间的内容，添加到 builder 中
                builder.append(src, offset, start - offset);
                // 重设offset
                offset = start + openToken.length();
                // 获取closeToken的位置
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if ((end <= offset) || (src[end - 1] != '\\')) {
                        expression.append(src, offset, end - offset);
                        break;
                    }
                    // 因为 closeToken 前面一个位置是 \ 转义字符，所以忽略 \
                    // 添加 [offset, end - offset - 1] 和 closeToken 的内容，添加到 builder 中
                    expression.append(src, offset, end - offset - 1).append(closeToken);
                    //重设offset
                    offset = end + closeToken.length();
                    // 继续，寻找结束的 closeToken 的位置
                    end = text.indexOf(closeToken, offset);
                }
                if (end == -1) {
                    //如果不存在openToken，则直接将offset位置后的字符添加到builder中
                    builder.append(src, start, src.length - start);
                    //重设offset
                    offset = src.length;
                } else {
                    //⭐️: 调用handler进行处理
                    builder.append(handler.handleToken(expression.toString()));
                    //重设offset
                    offset = end + closeToken.length();
                }
            }
            // 开始查找下一个openToken的索引位置。如果是-1就退出循环。
            start = text.indexOf(openToken, offset);
        } while (start > -1);
        // 只有输入原始text中不存在openToken且length > 0 时才执行下面的操作
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }
}
