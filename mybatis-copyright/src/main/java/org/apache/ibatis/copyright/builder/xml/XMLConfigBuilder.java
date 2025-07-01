package org.apache.ibatis.copyright.builder.xml;

import org.apache.ibatis.copyright.builder.BaseBuilder;
import org.apache.ibatis.copyright.datasource.DataSourceFactory;
import org.apache.ibatis.copyright.io.Resources;
import org.apache.ibatis.copyright.mapping.BoundSql;
import org.apache.ibatis.copyright.mapping.Environment;
import org.apache.ibatis.copyright.mapping.MappedStatement;
import org.apache.ibatis.copyright.mapping.SqlCommandType;
import org.apache.ibatis.copyright.session.Configuration;
import org.apache.ibatis.copyright.transaction.TransactionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-25 9:07
 * @version: 1.0
 */
public class XMLConfigBuilder extends BaseBuilder {

    private Element root;

    public XMLConfigBuilder(Reader reader) {
        // 1. 调用父类初始化Configuration
        super(new Configuration());
        // 2. dom4j 处理 xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new InputSource(reader));
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public Configuration parse() {
        try {
            // 解析environment
            environmentsElement(root.element("environments"));
            // 解析映射器
            mapperElement(root.element("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return configuration;
    }

    private void environmentsElement(Element context) throws Exception {
        String environment = context.attributeValue("default");

        List<Element> environments = context.elements("environment");
        for (Element el : environments) {
            String id = el.attributeValue("id");
            if (environment.equals(id)) {
                TransactionFactory txFactory = (TransactionFactory) typeAliasRegistry.resolveAlias(el.element("transactionManager").attributeValue("type")).getDeclaredConstructor().newInstance();

                Element dataSourceElement = el.element("dataSource");
                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceElement.attributeValue("type")).getDeclaredConstructor().newInstance();

                List<Element> properties = dataSourceElement.elements("property");

                Properties props = properties.stream()
                        .collect(Properties::new, (p, element) ->
                                p.setProperty(element.attributeValue("name"),
                                        element.attributeValue("value")), Properties::putAll);
                dataSourceFactory.setProperties(props);
                DataSource dataSource = dataSourceFactory.getDataSource();
                // 构建环境
                Environment.Builder environmentBuilder = new Environment.Builder(id)
                        .transactionFactory(txFactory)
                        .dataSource(dataSource);
                configuration.setEnvironment(environmentBuilder.build());
            }
        }
    }

    private void mapperElement(Element mappers) throws Exception {
        List<Element> mapperList = mappers.elements("mapper");
        for (Element child : mapperList) {
            // 解析处理，具体参照源码
            String resource = child.attributeValue("resource");
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            Element root = document.getRootElement();
            String namespace = root.attributeValue("namespace");

            List<Element> selectNodes = root.elements("select");
            for (Element selectNode : selectNodes) {
                String id = selectNode.attributeValue("id");
                String parameterType = selectNode.attributeValue("parameterType");
                String resultType = selectNode.attributeValue("resultType");
                String sql = selectNode.getText();

                Map<Integer, String> parameter = new HashMap<>();
                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                Matcher matcher = pattern.matcher(sql);
                for (int i = 1; matcher.find(); i++) {
                    String g1 = matcher.group(1);
                    String g2 = matcher.group(2);
                    parameter.put(i, g2);
                    sql = sql.replace(g1, "?");
                }
                String msId = namespace + "." + id;
                String nodeName = selectNode.getName();
                SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType,
                        new BoundSql(sql, parameter, parameterType, resultType), parameterType, resultType).build();
                // 添加解析 SQL
                configuration.addMappedStatement(mappedStatement);
            }
            // 注册Mapper映射器
            configuration.addMapper(Resources.classForName(namespace));
        }
    }
}
