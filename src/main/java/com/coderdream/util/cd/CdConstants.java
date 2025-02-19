package com.coderdream.util.cd;

import com.coderdream.util.proxy.OperatingSystem;

import java.io.File;

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

  public static String PPT_TEMPLATE_FILE_NAME = "D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\ppt\\6min_202501065.pptx";
  public static String PPT_TEMPLATE_FILE_PATH = "D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\ppt\\";

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


  /**
   * 柯林斯COBUILD高阶英汉双解学习词典
   */
  public static String DICT_COLLINS = "collins";

  /**
   * 牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx
   */
  public static String DICT_OALD = "oald";

  /**
   * 牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx
   */
  public static String DICT_OALDPE = "oaldpe";

  /**
   * 牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx
   */
  public static String DICT_CDEPE = "cdepe";

  /**
   * 牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx
   */
  public static String DICT_CDEPE2 = "cdepe";

  /**
   * 牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx
   */
  public static String DICT_CDEPE4 = "cdepe";

  /**
   * 牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx
   */
  public static String DICT_OALDPE3 = "oaldpe";

//  public static String SPEECH_KEY = System.getenv("SPEECH_KEY");

  /**
   * 美国西部
   */
  public static String SPEECH_REGION_EASTUS = "eastus";

  // Azure 配置信息
  /**
   * 美国西部
   */
  public static String SPEECH_KEY_EAST_US = System.getenv("SPEECH_KEY_EAST_US");


  public static String SPEECH_KEY_EASTASIA = System.getenv(
    "SPEECH_KEY_EASTASIA");

  /**
   * 东亚 eastasia
   */
  public static String SPEECH_REGION_EASTASIA = "eastasia";

  public static String SPEECH_VOICE_ZH_CN_XIAOCHEN = "zh-CN-XiaochenNeural";

  public static final String IMAGE_PATH = "src/main/resources/pic";
  public static final String AUDIO_CN_PATH = "src/main/resources/wav/cn";
  public static final String AUDIO_EN_PATH = "src/main/resources/wav/en";
//  public static final String OUTPUT_PATH = "src/main/resources/output_video/";
//  public static final String VIDEO_PATH = "src/main/resources/video/";
//  public static final String VIDEO_CN_PATH = "src/main/resources/video/cn/";
//  public static final String VIDEO_EN_PATH = "src/main/resources/video/en/";

  // 百词斩标准起始时间字符串
  public static final String BAI_CI_ZAN_START_TIME = "2025-01-01 00:00:00";

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


  public static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");

  // proxy-host 127.0.0.1
  public static final String PROXY_HOST = "127.0.0.1";

  // proxy-port 7890
//  public static final int PROXY_PORT = 7890;

  /**
   * "帮我实现如下功能：输入是是6组英文词汇，6行为1组；第1行英文单词或词组，第2行是英文释义，第1行和第2行不要处理，按原始文本返回，第3行是对第1行的中文简明翻译，翻译结果尽量少于20个字符串，第4行是对第2行的中文翻译，第5行是用第1行进行英文造句，第6行时对第5行进行中文翻译；请根据规则补齐空行，按文本文件格式返回给我，不要任何标记，移除空行，后面的5组也是按一样方式处理；以下是6组词汇：cephalopod\nthe
   * group of animals to which the octopus belongs\npublicity stunt\nsomething a
   * company might do to grab your attention and promote its products\na common
   * ancestor\na distant relative from which two different species
   * evolved\ncomparable to\nsimilar to\nvertebrates\nanimals that have a
   * spine\nprotean\n(adjective) adaptable and changeable";
   */
  public static final String VOC_CN_PREFIX = "帮我实现如下功能：输入是是6组英文词汇，6行为1组；"
    + "第1行英文单词或词组，第2行是英文释义，第1行和第2行不要处理，按原始文本返回，"
    + "第3行是对第1行的中文简明翻译，翻译结果尽量少于20个字符串，"
    + "第4行是对第2行的中文翻译，第4行很重要，请严格按照柯林斯英汉双解大词典或牛津双语词典的释义进行翻译，"
    + "第5行是用第1行进行英文造句，我是要考雅思的考生，请优先选用《柯林斯英汉双解大词典》或《牛津双语词典》中的较难的例句，"
    + "第6行是对第5行进行中文翻译；"
    + "再次强调，第2行按原文输出，不要修改；"
    + "请根据规则补齐空行，按文本文件格式返回给我，不要任何标记，移除空行；"
    + "后面的5组也是按一样方式处理；以下是6组词汇：";

  public static final String SRC_TRANSLATE_PREFIX = "帮我把英文字幕翻译成中文字幕，返回英中文双语字幕；"
    + "尽量通顺，前后文一致；"
    + "你返回内容一定要和我给你的匹配，给你100行，返回200行，英文放在上面，中文放在下面，一行英文，一行中文；"
    + "以下是英文字幕：";


  public static final String VOC_EN_PREFIX = "帮我实现如下功能：输入是是6组英文词汇，6行为1组；第1行英文单词或词组，第2行是英文释义，第1行和第2行不要处理，按原始文本返回，第3行是对第1行的中文简明翻译，翻译结果尽量少于20个字符串，第4行是对第2行的中文翻译，第5行是用第1行进行英文造句，我是要考雅思的考生，请优先选用《柯林斯英汉双解大词典》或《牛津双语词典》中的较难的例句，第6行时对第5行进行中文翻译；请根据规则补齐空行，按文本文件格式返回给我，不要任何标记，移除空行，后面的5组也是按一样方式处理；以下是6组词汇：";

  // pdf扩展名
  public static final String PDF_EXTENSION = ".pdf";
  // txt扩展名
  public static final String TXT_EXTENSION = ".txt";
  // mp3扩展名
  public static final String MP3_EXTENSION = ".mp3";
  // srt扩展名
  public static final String SRT_EXTENSION = ".srt";

  /**
   * A:联想；B、小米；C、戴尔；D、三星
   */
  public static final String TEMPLATE_FLAG = "D";

  public static Integer SINGLE_SCRIPT_LENGTH = 65;

  public static String GEN_PHONETICS_TEXT = "解析下面的文本，给每行英文句子加上音标，"
    + "放到下一行，给的n行英文句子，返回2*n行数据给我，"
    + "以Scene开头的句子也都要加上音标，放到下一行，"
    + "句子的音标只在句首句尾加斜线，中间不要有斜线。"
    + "待添加音标的句子文本如下：";


  public static final String OS_WINDOWS = "Windows";
  public static final String OS_MAC = "Mac";
  public static final String OS_LINUX = "Linux";
  public static final String OS_UNKNOWN = "Unknown";

  public static final String KEYWORD_WINDOWS = "windows";
  public static final String KEYWORD_MAC1 = "mac";
  public static final String KEYWORD_MAC2 = "darwin";
  public static final String KEYWORD_LINUX1 = "linux";
  public static final String KEYWORD_LINUX2 = "unix";

  public static final String POSTS_FOLDER =
    OperatingSystem.getHexoFolder() + "source" + File.separator + "_posts";
}
