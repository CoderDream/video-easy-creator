//package com.coderdream.util.itext5.sample;
//
//import com.itextpdf.text.Anchor;
//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Chunk;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfWriter;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//public class Demo01 {
//
//  public static void main(String[] args) {
//    //new一个Document对象,设置纸张大小
//    Document document = new Document(PageSize.A4);
////解析器
//    PdfWriter writer = PdfWriter.getInstance(document,
//      new FileOutputStream("D://pdfText.pdf"));
////添加PDF属性
///*document.addAuthor("作者");
//document.addTitle("标题");
//document.addSubject("主题");
//document.addKeywords("关键字");*/
//    /*页边距*/
//    document.setMargins(10, 20, 30, 40);
//
////打开document
//    document.open();
//
///***添加新段落***/
//    document.add(new Paragraph("Hello world"));  //英文段落添加成功
////字体的设置，如颜色，字体，大小等
//    Font titlefontChinese = new Font(bfChinese, 22, Font.BOLD, BaseColor.BLACK);
//    Paragraph title_hmc = new Paragraph("添加中文段落", titlefontChinese);
//    title_hmc.setAlignment(Element.ALIGN_CENTER);
//    title_hmc.setSpacingBefore(30f);
//    title_hmc.setSpacingAfter(20f);
//    document.add(title_hmc);  //中文段落添加成功
//
////添加新的空白页
//    document.newPage();
//    writer.setPageEmpty(true); //显示空白页
//
//    /*Chunk:块对象,可配置字符间距、字符+背景色、背景色的矩形大小可任意调整*/
//    document.newPage();
//    document.add(new Chunk("中文输出： ", getChineseFont()));
//    Chunk tChunk2 = new Chunk("输出的内容", getChineseFont());
//    tChunk2.setBackground(BaseColor.CYAN, 1f, 0.5f, 1f, 1.5f); // 设置背景色
//    tChunk2.setTextRise(6); // 上浮
//    tChunk2.setUnderline(0.2f, -2f); // 下划线
//    document.add(tChunk2);
//    document.add(Chunk.NEWLINE); // 新建一行
//
//    /*Phrase:短语可配置行间距、不会另起一行*/
//    document.newPage();
//    document.add(new Phrase("Phrase: "));
//    Phrase tPhrase = new Phrase();
//    Chunk name = new Chunk("Chunk1");
//    name.setUnderline(0.2f, -2f);
//    tPhrase.add(name);
//    tPhrase.add(Chunk.NEWLINE);// 放在容器中好用
//    tPhrase.add(new Chunk("换行Chunk: ", getChineseFont()));
//    tPhrase.add(new Chunk("Chunk2"));
//    tPhrase.setLeading(14f);// 行间距
//    document.add(tPhrase);
//
//    /*Paragraph:段落*/
//    document.newPage();
//    Paragraph info = new Paragraph("New page");
//    info.setLeading(20f); //设置行距
//    info.setSpacingBefore(5f);// 设置段落上空白
//    info.setSpacingAfter(10f);// 设置段落下空白
//    info.setFirstLineIndent(24);// 首行缩进
//    info.setIndentationLeft(12);// 左缩进
//    info.setIndentationRight(12);// 右缩进
//    info.setAlignment(Element.ALIGN_CENTER);  //设置居中
//    document.add(info);
//
//    /*Image:图片,继承自Rectangle,scaleToFit设置大小,setAbsolutePosition设置绝对位置*/
//    document.newPage();
//    Image img = Image.getInstance(
//      "C:\\Users\\Administrator\\Pictures\\img10B6-36.jpg");
//    img.setAlignment(Image.LEFT);  //对齐方式
//    img.setBorder(Image.BOX);  //边框
//    img.setBorderWidth(10);  //边框宽度
//    img.setBorderColor(BaseColor.WHITE);  //边框颜色
//    img.scaleToFit(800, 600);// 大小
//    img.setRotationDegrees(-3);// 旋转
//    document.add(img);
//
//    /*Anchor:超链接和锚点对象*/
//    document.newPage();
//    Paragraph paragraph = new Paragraph();
//    Anchor anchor = new Anchor("锚点文字", getChineseFont());
//    anchor.setName("achorName"); // 设置锚点的名字
//    anchor.setReference("http://www.baidu.com");// 连接
//    paragraph.add(anchor);
//    paragraph.add(String.format(": %d sites", 10000));
//    document.add(paragraph);
//
////关闭document
//    document.close();
//  }
//
//
//  /**
//   * 获取中国字体
//   * @return
//   */
//  public static Font getChineseFont() {
//    BaseFont bfChinese;  //确认支持中文
//    Font fontChinese = null;  //字体的设置，如颜色，字体，大小等
//    try {
//      bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
//      // fontChinese = new Font(bfChinese, 12, Font.NORMAL);
//      fontChinese = new Font(bfChinese, 12, Font.NORMAL, BaseColor.BLACK);
//    } catch (DocumentException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return fontChinese;
//  }
//}
