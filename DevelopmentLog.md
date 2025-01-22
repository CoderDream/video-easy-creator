# 开发日志



## TODO
- [ ] 生成百词斩日志封面图片
- [ ] 生成多邻国日志封面图片
- [ ] Java通过命令wmv视频转mp4
- [ ] 生成听力视频（开始提示音，两遍男声两遍无字幕，女声一遍有字幕，男声一遍有字幕）
- [ ] 生成中文音频
- [ ] 生成英文音频
- [ ] 收集提示音音频
- [ ] 生成中文字幕
- [ ] 生成英文字母
- [ ] 生成音标字母
- [ ] 生成ass字幕



## 20250116

- [x] 生成章节文本
  - TextFileUtil.filterTextFile(sourcePath, targetPath);
- [x] 全流程生成视频
  - GenVideoUtil.process()
- [x] 发布前准备（生成字幕及视频简介）
  - PreparePublishUtil.process(folderPath, subFolder)



## 20250115

- [x] 生成字幕
  - executeCommand_04
- [x] 生成油管描述
  - testGenerateContent_4010


## 20250110

- [x] 批量生成图片
  - HighResImageVideoUtil.generateImages(backgroundImageName,   filePath, contentFileName);
- [x] 批量生成视频
  - SingleCreateVideoUtil.batchCreateSingleVideo(imagePath, audioPath,      videoPath)
  - SingleCreateVideoUtilTest.batchCreateSingleVideo_01()

## 20250109

- [x] 获取音频文件夹中的音频时长，将时长写入时长文本中
  - AudioFolderDurationUtil.createAudioDurationFileList(folderName,durationFileName)
- [x] 优化合并文件夹中的音频，可以设置是否加头，是否加翻页声
  - AudioMergerUtil.mergeWavFiles(inputDir, outputFilePath, i)

## 20250108

- [x] 根据内容和背景图片批量生成图片到文件夹
  - HighResImageVideoUtil.generateImages(backgroundImageName,  filePath, contentFileName)

- [x] 读取文本内容生成对话对象，含中英文及音标
  - SentenceParser.parseSentencesFromFile

## 20250107

- [x] 生成单个的音频文件，适配中英文
  - AzureSpeechService
  
- [x] 批量生成中英文语音
  - SpeechUtil.genDialog2Audio900
  
- [x] 按模式生成单个文件（合并中文音频、英文音频）
  - AudioMergerSingleBatch

- [x] 合并指定文件夹中的音频文件到一个音频文件
  - AudioMerger

- [x] 根据音频文件和字幕脚本生成字幕文件
  - SubtitleUtil.genSubtitleRaw()

- [x] 音标和字幕脚本生成多个字幕文件
  - SubtitleUtil.genSubtitleRaw()

- [x] 创建单个视频文件
  - SingleCreateVideoUtil.singleCreateVideo(imagePath,  audioFileName, videoFileName)


## 20250106

- [x] 优化解析《900》对话解析成句子（英中），分割成两部分，生成3份文件
  - DialogSingleEntityUtil


## 20250105

- [x] 解析《900》对话解析成句子（英中）
  - TextUtil.writeSentenceToFile(filePath, fileName);
- [x] Epub转markdown、txt文件
  - EpubToMarkdownUtil.processEpubToZip(fileName)


## 20250104

- [x] 完成GeminiApiUtil工具类初始版
  - GeminiApiUtil.generateContent()
- [x] Epub转markdown、txt文件
  - EpubToMarkdownUtil.processEpubToZip(fileName)

## 20250102

- [x] 【六分钟英语】生成文章描述
  - TranslationUtil.genDescription(fileName);

## 20250101

- [x] 给MarkDownload文件的单词补上音标
  - MarkdownWordUtil.fillWordPhonetics(markdownFilePath)

- [x] 优化根据百词斩完整文档生成每日《考研词汇精选》，补上音标
  - MarkdownSplitterAdvanced

## 20241231

- [x] （1）通过字典查询单词 https://github.com/Grinner2436/mdict-java
  - NormalDictUtil.query("hello");【牛津高阶8简体】
  - SimpleDictUtil.query("hello");【Cambridge English-Chinese (Simplified) Dictionary】
- [ ] （2）通过其他字典查询单词
  - HtmlCollinsParser.query()
  - HtmlOaldpeParser.query();
  - HtmlOaldParser.query();


## 20241230

- [x] 将对话脚本文件分割成短句
  - TextUtil.writeSentenceToFile(filePath, fileName);
- [x] 英中文对话句子中间增加音标
  - TranslationUtil.genPhonetics(fileName)

## 20241229

- [x] 分割长图片
  - ImageSplitterUtil
- [x] PDF保存为图片，可以设置清晰度ppi
  - PdfToImageConverterUtil


## 20241228

- [x] 优雅的执行Windows控制台的命令
  - CommandUtil.executeCommand(command);
  - CommandExecutorTest.executeCommandDeployHexo() 打包发布Hexo
- [x] 根据原始文本生成字幕文本
  - SubtitleParser


## 20241227

- [x] 优化根据百词斩完整文档生成每日《考研词汇精选》
  - MarkdownSplitterAdvanced
- [x] 【六分钟英语】优化根据Excel生成高级词汇表图片
  - HighResImageGenUtil2
  
## 20241225


- [x] 根据百词斩完整文档生成每日《考研词汇精选》
  - MarkdownSplitterAdvanced
- [x] 根据图片生成公众号《六分钟英语》
  - MarkdownFileGenerator
- [x] Java通过调用cmd命令打包发布hexo
  - CmdUtil3

## 20241224

- [x]  通过Google Gemini API查询，翻译单词和句子，填充voc_cn.txt
  - DictUtil.processVocWithGemini()
- [x] 通过调用Google Youtube API，查看油管频道简介
  - YouTubeApiUtil.processUsernames(usernames)

## 20241223

![image-20241217102927661](DevelopmentLog/image-20241217102927661.png)



#### 测试类`CdDictionaryUtilTest`

### 返回

* 词性1个
* 发音
* 定义3个
* 句子5个

![image-20241217110644537](DevelopmentLog/image-20241217110644537.png)



### 缺陷修复与代码优化

#### 无法取消引用void

报错信息 **“java: 无法取消引用void”** 的根本原因是 `lombok` 的 `@Data` 注解会自动为你的类生成 **setter** 方法，但这些生成的 `setter` 方法返回类型是 `void`。因此，你不能在这些 `setter` 方法上进行链式调用（方法调用后返回的是 `void`）。

例如，`setSource()` 返回类型为 `void`，所以 `new DictionaryEntity().setSource(source)` 会引发“无法取消引用 `void`”的错误。

**使用 Lombok 的 `@Accessors` 注解**

Lombok 提供了一个 `@Accessors` 注解，可以为你的类自动生成支持链式调用的 `setter` 方法。

**解释**：

- `@Accessors(chain = true)` 告诉 Lombok 生成支持链式调用的 `setter` 方法。
- 例如，`setSource()` 的返回类型会变为 `DictionaryEntity` 而不是 `void`。

### v0.0.1-20241214

* 实现单词从`youdao.com`查询
* 实现单词从字典`牛津高阶英汉双解词典第10版完美版`查询
* 实现单词翻译和英文解释翻译从`ChatGPT`获取
* 实现单词的中英文例句从`ChatGPT`获取

