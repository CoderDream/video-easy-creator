package com.coderdream.util.ppt;

import java.io.File;

/**
 * @author Lzm
 * @description 文档类型常量.参考https://learn.microsoft.com/zh-cn/deployoffice/compat/office-file-format-reference
 * @date 2022/11/12 15:54
 */
public class MicrosoftConstants {
    public static final String TO_BYTE_TEMP_PATH= File.separator+"home"+File.separator+"project"+File.separator+"temp"+File.separator;
    public static final String WORDS_TO_OTHER="1";
    public static final String PDF_TO_OTHER="2";
    public static final String EXCEL_TO_OTHER="3";
    public static final String PPTX_TO_OTHER="4";
    public static final String CAD_TO_OTHER="5";
    public static final String IMAGING_TO_OTHER="6";
    public static final String MarkDown="md";
    public static final String Pdf="pdf";
    public static final String PdfXml = "pdfxml";//Adobe PDFXML文档, 页面布局文件
    public static final String Doc = "doc";//Word 97 – 2003中的文档
    public static final String Docx="docx";//2003以后的Word文档
    public static final String Xps = "xps";// XPS文档。XML 纸张规范
    public static final String Html = "html";//网页，通常另存为文件夹
//    public static final String Html5 = "html";//升级版的html，支持视频标签。网页，通常另存为文件夹
    public static final String Xml = "xml";//PowerPoint XML演示文稿
    public static final String MobiXml = "mobi";//电子书文件，国外主流.外文电子书或者外网上的中文电子书大多是以mobi格式保存
    public static final String Xlsx = "xlsx";//2003年之后所有版本的Excel的默认Excel文件
    public static final String Xls = "xls";//97—2003文档格式
    /**text文本：tsv、csv*/
    public static final String Tsv="tsv";
    public static final String Csv = "csv";//以逗号分隔的文件，仅保存活动工作表。
    public static final String Xlsm = "xlsm";//适用于2003年之后所有版本的Excel的启用宏的文件
    public static final String Xltx = "xltx";//2003年之后所有版本的Excel的默认Excel模板
    public static final String Xltm = "xltm";//用于2003年之后版本的Excel的启用宏的Excel模板
    public static final String Xlam = "xlam";//2003年之后所有版本的Excel加载项文件
    public static final String Ods = "ods";//一个OpenDocument电子表格，可以在与OpenDocument文件类型兼容的其他电子表格程序中使用
    public static final String Ots = "ots";
    public static final String Xlsb = "xlsb";//2003年之后所有版本的Excel二进制文件,xlsb的文件更小，对保存大Excel文件很有用。打开和保存的速度更快
    public static final String Dif = "dif";//数据交换格式文件，仅保存活动工作表



    public static final String Epub = "epub";//电子书文件，国内主流
    public static final String Plugin = "plugin";//作为一种 Mac OS X插件使用
    public static final String Aps = "aps";//卡Studio项目文件, 光栅图像文件

    public static final String Pptx="pptx";//2003年之后的PowerPoint版本的默认PowerPoint演示文稿


    public static final String Otp="otp";//基于 OpenDocument 标准，该标准支持将文档表示为单个 XML 文档以及以 ZIP 格式在单个压缩包中收集多个子文档
    /**图形相关TIFF， JPEG， PNG， BMP， SVG， EMF， GIF*/
    public static final String Png = "png";//可移植网络图形格式文件
    public static final String Emf = "emf";//增强的Windows图元文件是保存为32位图形的幻灯
    public static final String Wmf = "wmf";//
    public static final String Svg = "svg";//保存游戏文件
    public static final String Svm = "svm";//保存游戏文件
    public static final String Bmp = "bmp";//独立于设备的位图是一张幻灯片，另存为图形以用于网页
    public static final String Jpeg="jpeg";
    public static final String Tiff="tiff";//网页图形文档
    public static final String Gif="gif";

    public static final String Swf="swf";//一种flash动画文件，很多网页视频都使用的这种格式

    public static final String Ppt="ppt";
    public static final String Pps="pps";
    public static final String Pot="pot";
    public static final String Ppsx="ppsx";

    public static final String Pptm="pptm";
    public static final String Ppsm="ppsm";
    public static final String Potx="potx";
    public static final String Potm="potm";
    public static final String Odp="odp";
    public static final String Dot = "dot";// Word 97 – 2003的模板
    public static final String Docm = "docm";//启用Word宏的文档
    public static final String Dotx = "dotx";// 2003年之后的所有内容的Word模板
    public static final String Dotm = "dotm";//启用Word宏的模板
    public static final String Rtf = "rtf";//控制文档在屏幕上以及打印时的外观和格式
    public static final String OPEN_XPS = "oxps";//开放式XML文件规范，是在Windows 8 M3 Build 8102预览版中新引入的一种文件格式
    public static final String Ps = "ps";//PostScript文件, 矢量图形文件
    public static final String Pcl = "pcl";//打印机命令语言文档
    public static final String Mhtml = "mhtml";//网页，另存为单个文件
    public static final String Odt = "odt";//OpenDocument文本，与其他与OpenDocument兼容的文字处理器兼容
    public static final String Ott = "ott";// 文本文件,作为一种 OpenDocument格式文档模板使用
    public static final String Text = "txt";//没有格式的纯文本文件。用户将文档另存为 .txt 文件时，文档会丢失所有格式。
    public static final String Tex="tex";
    public static final String Fods="fods";
    public static final String Fodp="fodp";
    public static final String Sxc="sxc";
    public static final String Json="json";
    public static final String Sql_Script="sql";//sql脚本
}
