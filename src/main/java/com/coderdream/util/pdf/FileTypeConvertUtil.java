package com.coderdream.util.pdf;

import com.itextpdf.text.pdf.BaseFont;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;

/**
 * 文件格式转换工具类
 *
 * @author lbj
 * <p>
 * 2015-10-8 上午10:52:22
 */
public class FileTypeConvertUtil {

  /**
   * 将HTML转成PD格式的文件。html文件的格式比较严格
   *
   * @param htmlFile
   * @param pdfFile
   * @throws Exception
   */
  // <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd ">
  public static void html2pdf(String htmlFile, String pdfFile)
    throws Exception {
    // step 1
    String url = new File(htmlFile).toURI().toURL().toString();
    System.out.println(url);
    // 修改HTML内容，确保实体引用正确关闭
    String htmlContent = new String(
      Files.readAllBytes(Paths.get(htmlFile))).replace("&More", "&More;");
    // step 2
    OutputStream os = new FileOutputStream(pdfFile);
    ITextRenderer renderer = new ITextRenderer();
//        renderer.setDocumentFromString(getXmlString());
    renderer.setDocumentFromString(htmlContent);
//        renderer.setDocument(url);

    // step 3 解决中文支持
    ITextFontResolver fontResolver = renderer.getFontResolver();
    if ("linux".equals(getCurrentOperatingSystem())) {
      fontResolver.addFont("/usr/share/fonts/chiness/simsun.ttc",
        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    } else {
      fontResolver.addFont("D:\\java_output\\fonts\\SourceHanSansCN-Bold.ttf",
        BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
      fontResolver.addFont("D:\\java_output\\fonts\\DoulosSIL-R.ttf",
        BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    }

    renderer.layout();
    renderer.createPDF(os);
    os.close();

    System.out.println("create pdf done!!");
  }

  public static String getCurrentOperatingSystem() {
    String os = System.getProperty("os.name").toLowerCase();
    System.out.println("---------当前操作系统是-----------" + os);
    return os;
  }

  public static String getXmlString() {
    String xmlString =
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
        +
        "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
        "<head>\n" +
        "    <meta charset=\"UTF-8\"/>\n" +
        "    <title>Hello World</title>\n" +
        "\t<style>\n" +
        "\t  table.table-separate th{\n" +
        "    font-weight:bold;\n" +
        "    font-size:14px;\n" +
        "    border-top:1px solid #F3EDE9 !important;\n" +
        "  }\n" +
        "  table.table-separate td{\n" +
        "    padding: 13px 0;\n" +
        "    font-weight:100;\n" +
        "  }\n" +
        "  .table-separate td.tit{\n" +
        "    background-color: #f4f9fe;\n" +
        "    font-weight:normal;\n" +
        "    padding:22px 0;\n" +
        "    width:15%;\n" +
        "  }\n" +
        "  .table-separate td.cont{\n" +
        "    text-align: left;\n" +
        "    padding:16px 22px;\n" +
        "    width:85%;\n" +
        "    line-height:175%;\n" +
        "  }\n" +
        "  .table-separate.no-border th{\n" +
        "    border:none;\n" +
        "    text-align: left;\n" +
        "  }\n" +
        "  .table-separate.no-border td{\n" +
        "    text-align: left;\n" +
        "    border:none;\n" +
        "  }\n" +
        "\t@page {\n" +
        "\tsize:210mm 297mm;//纸张大小A4\n" +
        "\tmargin: 0.25in;\n" +
        "\t-fs-flow-bottom: \"footer\";\n" +
        "\t-fs-flow-left: \"left\";\n" +
        "\t-fs-flow-right: \"right\";\n" +
        "\tborder: thin solid black;\n" +
        "\tpadding: 1em;\n" +
        "\t}\n" +
        "\t#footer {\n" +
        "\tfont-size: 90%; font-style: italic;\n" +
        "\tposition: absolute; top: 0; left: 0;\n" +
        "\t-fs-move-to-flow: \"footer\";\n" +
        "\t}\n" +
        "\t#pagenumber:before {\n" +
        "\tcontent: counter(page);\n" +
        "\t}\n" +
        "\t#pagecount:before {content: counter(pages);\n" +
        "\t}\n" +
        "\ttable {\n" +
        "\t\t\tborder-collapse: collapse;\n" +
        "\t\t\ttable-layout: fixed;\n" +
        "\t\t\tword-break:break-all;\n" +
        "\t\t\tfont-size: 10px;\n" +
        "\t\t\twidth: 100%;\n" +
        "\t\t\ttext-align: center;\n" +
        "\t}\n" +
        "\ttd {\n" +
        "\t\tword-break:break-all;\n" +
        "\t\tword-wrap : break-word;\n" +
        "\t}\n" +
        "\t</style>\n" +
        "\t</head>\n" +
        "<body style = \"font-family: SimSun;\">\n" +
        "<div id=\"footer\" style=\"\">  Page <span id=\"pagenumber\"/> of <span id=\"pagecount\"/> </div>\n"
        +
        "<div id=\"main\">\n" +
        "    <div style=\"max-width:600px;margin:0 auto;padding:10px;\">\n" +
        "        <div style=\"text-align: center; padding: 5mm 0;\">\n" +
        "            <div style=\"font-weight: bold; font-size: 30px;\"> HI Fudi&More</div>\n"
        +
        "            <div> THANK YOU FOR SHOPPING WITH Fudi&More!</div>\n" +
        "        </div>\n" +
        "        <div style=\"border: 1px solid black; background-color: #f8f8f8; padding: 4mm;\">\n"
        +
        "            <div style=\"font-size: 17px; font-weight: bold; border-bottom: 1px solid black; padding-bottom: 5mm;\"> ORDER DETAILS</div>\n"
        +
        "            <div style=\"padding-top: 10px;\">\n" +
        "                <div><strong>Order: </strong>D-8C2Y Placed on 29/09/2019 10:04</div>\n"
        +
        "                <div><strong>Carrier: </strong>Delivery</div>\n" +
        "                <div><strong>Payment: </strong>Cash Payment</div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div style=\"margin-top: 4mm;\">\n" +
        "            <table class=\"table-separate\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;margin:0 auto;padding:10px;\">\n"
        +
        "                <thead>\n" +
        "                <tr style=\"text-align: center; height: 40px;\">\n" +
        "                    <th style=\"width: 90px; background-color: #f8f8f8; border-top: 1px solid black; border-left: 1px solid black; border-right: 1px solid black;\">\n"
        +
        "                        Reference\n" +
        "                    </th>\n" +
        "                    <th colspan=\"2\" style=\"background-color: #f8f8f8; border-top: 1px solid black; border-right: 1px solid black;\">Product</th>\n"
        +
        "                    <th style=\"width: 110px; background-color: #f8f8f8; border-top: 1px solid black; border-right: 1px solid black;\">Unit price</th>\n"
        +
        "                    <th style=\"width: 80px; background-color: #f8f8f8; border-top: 1px solid black; border-right: 1px solid black;\">Quantity</th>\n"
        +
        "                    <th style=\"width: 90px; background-color: #f8f8f8; border-top: 1px solid black; border-right: 1px solid black;\">Total price</th>\n"
        +
        "                </tr>\n" +
        "                </thead>\n" +
        "                <tbody>\n" +
        "                <tr style=\"text-align: center; \">\n" +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-left: 1px solid black; border-right: 1px solid black;\">\n"
        +
        "                        Main\n" +
        "                    </td>\n" +
        "                    <td colspan=\"2\"\n" +
        "                        style=\"border-top: 1px solid black; border-bottom:1px solid black; border-right: 1px solid black; text-align: left; padding: 010px;\">\n"
        +
        "                        SweetSour Chicken\n" +
        "                    </td>\n" +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-right: 1px solid black;\">€ 7.00</td>\n"
        +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-right: 1px solid black;\">1</td>\n"
        +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-right: 1px solid black;\">€ 7.00</td>\n"
        +
        "                </tr>\n" +
        "                <tr style=\"text-align: center; \">\n" +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-left: 1px solid black; border-right: 1px solid black;\">\n"
        +
        "                        Main\n" +
        "                    </td>\n" +
        "                    <td colspan=\"2\"\n" +
        "                        style=\"border-top: 1px solid black; border-bottom:1px solid black; border-right: 1px solid black; text-align: left; padding: 010px;\">\n"
        +
        "                        Black Bean Stir Fry\n" +
        "                    </td>\n" +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-right: 1px solid black;\">€ 9.00</td>\n"
        +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-right: 1px solid black;\">1</td>\n"
        +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-right: 1px solid black;\">€ 9.00</td>\n"
        +
        "                </tr>\n" +
        "                <tr style=\"text-align: center; \">\n" +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-left: 1px solid black; border-right: 1px solid black;\">\n"
        +
        "                        Pizzas\n" +
        "                    </td>\n" +
        "                    <td colspan=\"2\"\n" +
        "                        style=\"border-top: 1px solid black; border-bottom:1px solid black; border-right: 1px solid black; text-align: left; padding: 010px;\">\n"
        +
        "                        Test Design Your Own 8\" Pizza\n" +
        "                    </td>\n" +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-right: 1px solid black;\">€ 6.00</td>\n"
        +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-right: 1px solid black;\">1</td>\n"
        +
        "                    <td style=\"border-top: 1px solid black; border-bottom: 1px solid black; border-right: 1px solid black;\">€ 6.00</td>\n"
        +
        "                </tr>\n" +
        "                </tbody>\n" +
        "                <tfoot>\n" +
        "                <tr style=\"text-align: center; height: 8mm;\">\n" +
        "                    <td colspan=\"5\"\n" +
        "                        style=\"text-align: right; width: 90px; background-color: #f8f8f8; border-bottom: 1px solid black; border-left: 1px solid black; border-right: 1px solid black; padding: 0 10px;\">\n"
        +
        "                        Item:\n" +
        "                    </td>\n" +
        "                    <td style=\"background-color: #f8f8f8; border-bottom: 1px solid black; border-right: 1px solid black;\">3</td>\n"
        +
        "                </tr>\n" +
        "                <tr style=\"text-align: center; height: 8mm;\">\n" +
        "                    <td colspan=\"5\"\n" +
        "                        style=\"text-align: right; width: 90px; background-color: #f8f8f8; border-bottom: 1px solid black; border-left: 1px solid black; border-right: 1px solid black; padding: 0 10px;\">\n"
        +
        "                        Subtotal:\n" +
        "                    </td>\n" +
        "                    <td style=\"background-color: #f8f8f8; border-bottom: 1px solid black; border-right: 1px solid black;\">€24.00</td>\n"
        +
        "                </tr>\n" +
        "                <tr style=\"text-align: center; height: 8mm;\">\n" +
        "                    <td colspan=\"5\"\n" +
        "                        style=\"text-align: right; width: 90px; background-color: #f8f8f8; border-bottom: 1px solid black; border-left: 1px solid black; border-right: 1px solid black; padding: 0 10px;\">\n"
        +
        "                        Deliver Fee:\n" +
        "                    </td>\n" +
        "                    <td style=\"background-color: #f8f8f8; border-bottom: 1px solid black; border-right: 1px solid black;\">+€2.00</td>\n"
        +
        "                </tr>\n" +
        "                <tr style=\"text-align: center; height: 8mm;\">\n" +
        "                    <td colspan=\"5\"\n" +
        "                        style=\"text-align: right; width: 90px; background-color: #f8f8f8; border-bottom: 1px solid black; border-left: 1px solid black; border-right: 1px solid black; padding: 0 10px;\">\n"
        +
        "                        Discount:\n" +
        "                    </td>\n" +
        "                    <td style=\"background-color: #f8f8f8; border-bottom: 1px solid black; border-right: 1px solid black;\">-€0.00</td>\n"
        +
        "                </tr>\n" +
        "                <tr style=\"text-align: center; height: 8mm;\">\n" +
        "                    <td colspan=\"5\"\n" +
        "                        style=\"text-align: right; width: 90px; background-color: #f8f8f8; border-bottom: 1px solid black; border-left: 1px solid black; border-right: 1px solid black; padding: 0 10px;\">\n"
        +
        "                        Total:\n" +
        "                    </td>\n" +
        "                    <td style=\"background-color: #f8f8f8; border-bottom: 1px solid black; border-right: 1px solid black;\">€24.00</td>\n"
        +
        "                </tr>\n" +
        "                </tfoot>\n" +
        "            </table>\n" +
        "        </div>\n" +
        "        <div>\n" +
        "            <div style=\"border: 1px solid black; background-color: #f8f8f8; padding:5mm; margin-top: 5mm;\">\n"
        +
        "                <div style=\"font-size: 17px; font-weight: bold; border-bottom: 1px solid black; padding-bottom: 15px;\"> DELIVERY ADDRESS</div>\n"
        +
        "                <div style=\"padding-top: 10px;\">\n" +
        "                    <div><strong>guan</strong> ☎ <strong>13656690321</strong></div>\n"
        +
        "                    <div> 1024/ Edenhall,ModelFarmRd,Cork,爱尔兰,A 2048</div>\n"
        +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div style=\"font-size: 13px;\"><p>You can review your order and download your invoice from the \"<a target=\"_blank\"\n"
        +
        "                                                                                                          href=\"http://www.fudiandmore.ie/#/FudiIndex/Order1\">Order\n"
        +
        "            history</a>\"section of your customer account by clicking \"<a target=\"_blank\" href=\"http://www.fudiandmore.ie/#/FudiIndex/Personalcenter1\">My\n"
        +
        "            account</a>\" on ourshop.</p></div>\n" +
        "        <hr style=\"border-width: 5px;\"/>\n" +
        "        <div> Fudi,More powered by <a target=\"_blank\" href=\"http://www.fudiandmore.ie\">A2BLiving</a></div>\n"
        +
        "    </div>\n" +
        "</div>\n" +
        "</body>\n" +
        "</html>";
    StringBuffer stringBuffer = new StringBuffer();

    return xmlString;
  }

  public static void main(String[] args) {
    //        String htmlFile = "/home/lbj/sign.jsp";
    //        String pdfFile = "/home/lbj/sign.pdf";
    String htmlFile = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.html";
    String pdfFile = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001—2.pdf";
    try {
      FileTypeConvertUtil.html2pdf(htmlFile, pdfFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
