package org.apache.ibatis.parsing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;

/**
 * @author ywb
 * @datetime 2024-08-14 15:13
 * @version: 1.0
 */
class OriginalXmlXPathParseTest {

  @Test
  void testXPathParse() throws Exception {
    // 创建一个DocumentBuilderFactory实例
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    // 创建一个DocumentBuilder实例,该对象用于将文件加载到内存中以解析XML数据。
    DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
    File file = new File("src/test/resources/org/apache/ibatis/parsing/input.txt");
    // 创建DOMParser对象，并将xml文件解析为Document
    Document doc = documentBuilder.parse(file);
    // 解析DOM树形结构（将所有节点放入内存中）
    doc.getDocumentElement().normalize();
    // 创建XPath工厂并获取XPath实例。
    XPath xPath = XPathFactory.newInstance().newXPath();
    // 编写XPath表达式（查询语句）
    String expression = "/class/student";
    // 编译XPath表达式并获取结果。
    NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
    Assertions.assertNotNull(nodeList);
    // 循环遍历nodeList。
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      System.out.println("\nCurrent Element :" + node.getNodeName());
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element eElement = (Element) node;
        System.out.println("Student roll no : " + eElement.getAttribute("rollno"));
        System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
        System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
        System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
        System.out.println("Marks : " + eElement.getElementsByTagName("marks").item(0).getTextContent());
      }
    }
  }
}
