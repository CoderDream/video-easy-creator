//package com.coderdream.util;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;
//import java.io.StringWriter;
//
//public class JAXBExample {
//
//  public static void main(String[] args) {
//    try {
//      // 初始化JAXB上下文
//      JAXBContext context = JAXBContext.newInstance(MyClass.class);
//
//      // 创建Marshaller实例
//      Marshaller marshaller = context.createMarshaller();
//      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//
//      // 序列化对象为XML
//      StringWriter writer = new StringWriter();
//      marshaller.marshal(new MyClass("John Doe", 30), writer);
//
//      String xmlString = writer.toString();
//      System.out.println(xmlString);
//
//    } catch (JAXBException e) {
//      e.printStackTrace();
//    }
//  }
//}
//
//@XmlRootElement
//class MyClass {
//
//  private String name;
//  private int age;
//
//  // 无参构造函数是JAXB所必需的
//  public MyClass() {
//  }
//
//  public MyClass(String name, int age) {
//    this.name = name;
//    this.age = age;
//  }
//
//  @XmlElement
//  public String getName() {
//    return name;
//  }
//
//  public void setName(String name) {
//    this.name = name;
//  }
//
//  @XmlElement
//  public int getAge() {
//    return age;
//  }
//
//  public void setAge(int age) {
//    this.age = age;
//  }
//}
