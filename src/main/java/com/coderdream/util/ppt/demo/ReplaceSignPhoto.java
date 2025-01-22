//package com.coderdream.util.ppt.demo;
//
//import javax.xml.parsers.DocumentBuilder;
//
///**
// * @Description 签名照片处理
// * @Date 2021/3/30
// **/
//public class ReplaceSignPhoto implements IReplacingCallback {
//
//  private String url;
//  private String name;
//
//  public ReplaceSignPhoto(String url, String name){
//    this.url = url;
//    this.name = name;
//  }
//
//  @Override
//  public int replacing(ReplacingArgs e) throws Exception {
//    //获取当前节点
//    Node currentNode = e.getMatchNode();
//    //节点拆分处理当前匹配字段
//    splitRun(currentNode,e.getMatchOffset());
//    //获取当前文档
//    Document document =  (Document) currentNode.getDocument();
//    DocumentBuilder builder = new DocumentBuilder(document);
//    //将光标移动到指定节点
//    builder.moveTo(currentNode);
//    //插入图片
//    Shape img = builder.insertImage(url);//shape可以设置图片的位置及属性
//    //设置宽高
//    img.setWidth(80);
//    img.setHeight(30);
//    img.setWrapType(WrapType.SQUARE);
//    img.setDistanceLeft(10);
//    img.setHorizontalAlignment(HorizontalAlignment.CENTER);
//    img.setVerticalAlignment(VerticalAlignment.CENTER);
//    img.setName(name);
//    return ReplaceAction.SKIP;
//  }
//  private void splitRun(Node currentNode ,int position){
//    String text =  currentNode.getText();
//    Node newNode = currentNode.deepClone(true);
//    if(text.length() >= position+this.name.length()){
//      ((Run)currentNode).setText (text.substring(position+this.name.length()));
//    }else{
//      int morlength = position+this.name.length() - text.length();
//      ((Run)currentNode).setText ("");
//      Node tmpnode = currentNode;
//      for(int i=0;i<this.name.length();i++){
//        System.out.println(i);
//        tmpnode = tmpnode.getNextSibling();
//        String tmptext= tmpnode.getText();
//        System.out.println(tmptext);
//        System.out.println(morlength);
//        System.out.println("--------"+(tmptext.length() >= morlength));
//
//        if(tmptext.length() >= morlength){
//          ((Run)tmpnode).setText(tmptext.substring(morlength));
//          break;
//        }else{
//          morlength = morlength - tmptext.length();
//          ((Run)tmpnode).setText("");
//        }
//      }
//    }
//    if(position>0){
//      ((Run)newNode).setText(text.substring(0, position));
//      currentNode.getParentNode().insertBefore(newNode, currentNode);
//    }
//  }
//}
