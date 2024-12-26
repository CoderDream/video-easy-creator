package com.coderdream.util;

/**
 * @author CoderDream
 */
public class CdConstants {

  public static String MIDDLE_POINT = "•";

  public static String URL_US_BASE = "https://apps.apple.com/us/app/";

  public static String URL_CN_BASE = "https://apps.apple.com/cn/app/";

  public static String URL_PLATFORM_IPHONE = "?platform=iphone";

  public static String SNAPSHOT_JPG_SUFFIX = "jpg 600w";
  public static String SNAPSHOT_JPG_2_SUFFIX = "jpg 643w";

  public static String SNAPSHOT_PNG_SUFFIX = "png 600w";

  public static String SNAPSHOT_PNG_2_SUFFIX = "png 643w";

  public static String APP_ICON_JPG_SUFFIX = "jpg 492w";
  public static String APP_ICON_JPG_2_SUFFIX = "jpg 460w";

  public static String APP_ICON_PNG_SUFFIX = "png 492w";
  public static String APP_ICON_2_PNG_SUFFIX = "png 460w";

  public static int BATCH_INSERT_UPDATE_ROWS = 25;
  public static int BATCH_UPDATE_ROWS = 1000;

  public static int BATCH_SNAPSHOT_ROWS = 100;

  public static String PPT_TEMPLATE_FILE_NAME = "20230430.pptx";

  public static String MIDDLE_DOT = "·";
  public static String MIDDLE_MINUS = "-";
  public static Integer SALARY_MONTH = 12;

//            switch (dictType) {
//        case "cambridge":
//            mdxFile = "E:\\BaiduPan\\0002_词典共享\\剑桥在线英汉双解词典完美版\\cdepe.mdx"; // 剑桥在线英汉双解词典完美版 400MB
//            break;
//        case "oaldpe":
//            mdxFile = "E:\\BaiduPan\\0002_词典共享\\牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx"; // 74MB
//            break;
//        case "maldpe":
//            mdxFile = "E:\\BaiduPan\\0002_词典共享\\韦氏高阶英汉双解词典2019完美版\\maldpe.mdx"; // 28MB
//            break;
//
//        case "c8":
//            mdxFile = "E:\\BaiduPan\\好用的词典~\\牛津高阶8简体spx\\牛津高阶8简体.mdx"; // 28MB
//            break;   //
//        case "collins":
//            mdxFile = "D:\\Download\\柯林斯COBUILD高阶英汉双解学习词典.mdx";
//            break;   //
//        default:
//            mdxFile = "D:\\Download\\柯林斯COBUILD高阶英汉双解学习词典.mdx";

  /**
   * 剑桥在线英汉双解词典完美版\\cdepe.mdx
   */
  public static String CAMBRIDGE = "cambridge";


  /**
   * 牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx
   */
  public static String OALDPE = "oaldpe";


  public static String SPEECH_KEY = System.getenv("SPEECH_KEY");

  /**
   * 美国西部
   */
  public static String SPEECH_REGION_EASTUS = "eastus";

  // Azure 配置信息
  /**
   * 美国西部
   */
  public static String SPEECH_KEY_EAST_US = System.getenv("SPEECH_KEY_EAST_US");

  /**
   * 东亚 eastasia
   */
  public static String SPEECH_REGION_EASTASIA = "eastasia";

  public static final String IMAGE_PATH = "src/main/resources/pic";
  public static final String AUDIO_CN_PATH = "src/main/resources/wav/cn";
  public static final String AUDIO_EN_PATH = "src/main/resources/wav/en";
//  public static final String OUTPUT_PATH = "src/main/resources/output_video/";
//  public static final String VIDEO_PATH = "src/main/resources/video/";
//  public static final String VIDEO_CN_PATH = "src/main/resources/video/cn/";
//  public static final String VIDEO_EN_PATH = "src/main/resources/video/en/";

  /**
   * 音频文件夹
   */
  public static final String AUDIO_FOLDER = "audio";
  /**
   * 视频文件夹
   */
  public static final String VIDEO_FOLDER = "video";

  public static final String LANG_CN = "cn";
  public static final String LANG_EN = "en";

  public static final String AUDIO_TYPE_WAV = "wav";
  public static final String AUDIO_TYPE_MP3 = "mp3";

  public static final String RESOURCES_BASE_PATH = "src/main/resources/";
  // 设置路径
  public static final String BACKGROUND_IMAGE_FILENAME = "background.png"; // 背景图片
  public static final String PIC_FOLDER = "pic"; // 输出目录

  public static final String GOOGLE_API_KEY = System.getenv("GOOGLE_API_KEY");


  public static final String GEMINI_API_KEY =  System.getenv("GEMINI_API_KEY");

  // proxy-host 127.0.0.1
  public static final String PROXY_HOST = "127.0.0.1";

  // proxy-port 7890
  public static final int PROXY_PORT = 7890;

  /**
   * "帮我实现如下功能：输入是是6组英文词汇，6行为1组；第1行英文单词或词组，第2行是英文释义，第1行和第2行不要处理，按原始文本返回，第3行是对第1行的中文简明翻译，翻译结果尽量少于20个字符串，第4行是对第2行的中文翻译，第5行是用第1行进行英文造句，第6行时对第5行进行中文翻译；请根据规则补齐空行，按文本文件格式返回给我，不要任何标记，移除空行，后面的5组也是按一样方式处理；以下是6组词汇：cephalopod\nthe group of animals to which the octopus belongs\npublicity stunt\nsomething a company might do to grab your attention and promote its products\na common ancestor\na distant relative from which two different species evolved\ncomparable to\nsimilar to\nvertebrates\nanimals that have a spine\nprotean\n(adjective) adaptable and changeable";
   */
  public static final String VOC_CN_PREFIX = "帮我实现如下功能：输入是是6组英文词汇，6行为1组；第1行英文单词或词组，第2行是英文释义，第1行和第2行不要处理，按原始文本返回，第3行是对第1行的中文简明翻译，翻译结果尽量少于20个字符串，第4行是对第2行的中文翻译，第5行是用第1行进行英文造句，我是要考雅思的考生，请优先选用《柯林斯英汉双解大词典》或《牛津双语词典》中的较难的例句，第6行时对第5行进行中文翻译；请根据规则补齐空行，按文本文件格式返回给我，不要任何标记，移除空行，后面的5组也是按一样方式处理；以下是6组词汇：";
}
