package com.coderdream.util.ppt;

import cn.hutool.core.util.StrUtil;
import com.aspose.slides.*;
import com.coderdream.entity.VocInfo;
import com.coderdream.entity.WordInfo;
import com.coderdream.entity.WordInfoEntity;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.excel.CdExcelUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * PPT 处理工具类，用于替换文本和图片，并设置图片样式
 */
@Slf4j
public class PptUtil {

    /**
     * 处理 PPT，替换占位符文本和图片，并设置图片样式
     *
     * @param folderName 文件夹名称，用于查找 PPT 模板文件
     * @return String  处理耗时
     */
    public static String processPpt(String folderName) {
        Instant startTime = Instant.now();
        log.info("开始处理PPT，文件夹名称为：{}", folderName);
        // 加载许可证
        LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
        String presentationName = CdConstants.PPT_TEMPLATE_FILE_NAME;
        Presentation pres = null;
        try {
            pres = new Presentation(presentationName);
            // 创建属性映射，用于存储要替换的文本
            Map<String, String> props = createProps(folderName);
            // 遍历幻灯片，替换文本和图片
            processSlides(pres, folderName, props);
            // 填充高级词汇表格
            fillAdvancedWordTables(pres, folderName);
            // 保存修改后的 PPT
            saveProcessedPpt(pres, folderName);
        } catch (Exception e) {
            log.error("处理 PPT 时发生异常：", e);
            throw new RuntimeException("处理 PPT 异常", e);
        } finally {
            if (pres != null) {
                pres.dispose();
            }
        }
        // 计算方法执行耗时
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        long seconds = duration.getSeconds();
        long milliseconds = duration.toMillisPart();
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        String timeTaken = String.format("%d分%d秒%d毫秒", minutes, remainingSeconds, milliseconds);
        log.info("PPT处理完成，耗时: {}", timeTaken);
        return timeTaken;
    }

    /**
     * 创建属性映射，用于存储要替换的文本
     *
     * @param folderName 文件夹名称
     * @return Map 属性映射
     */
    private static Map<String, String> createProps(String folderName) {
        Map<String, String> props = new HashMap<>();
        log.info("开始创建属性映射，文件夹名称为：{}", folderName);
        props.put("folderName", folderName);
        props.put("title", CdFileUtil.getArticleTitle(folderName));
        List<VocInfo> vocInfoList = DictUtils.getVocInfoList(folderName);
        int i = 1;
        for (VocInfo vocInfo : vocInfoList) {
            props.put("word" + i, vocInfo.getWord());
            props.put("wordEn" + i, vocInfo.getWordExplainEn());
            props.put("wordCn" + i, vocInfo.getWordCn());
            props.put("wordCnEx" + i, vocInfo.getWordExplainCn());
            String sampleSentenceEn = vocInfo.getSampleSentenceEn();
            String sampleSentenceCn = vocInfo.getSampleSentenceCn();
           log.debug("示例英文句子：{} , 长度为：{}",sampleSentenceEn, sampleSentenceEn == null ? 0: sampleSentenceEn.length());
            String mid = StrUtil.isNotEmpty(sampleSentenceEn) && sampleSentenceEn.length() < 50 ? "\n" : "";
            props.put("wordSen" + i, sampleSentenceEn + mid + sampleSentenceCn);
            props.put("wordSenEn" + i, sampleSentenceEn);
            props.put("wordSenCn" + i, sampleSentenceCn);
            String midStr = sampleSentenceEn.length() < 50 && sampleSentenceCn.length() < 50 ? "\n" : " ";
            props.put("wordSenEn" + i + "MixwordSenCn" + i, sampleSentenceEn + midStr + sampleSentenceCn);
            i++;
        }
        log.info("属性映射创建完成，属性数量：{}",props.size());
        return props;
    }

    /**
     * 遍历幻灯片，替换文本和图片
     *
     * @param pres       演示文稿对象
     * @param folderName 文件夹名称
     * @param props      属性映射
     */
    private static void processSlides(Presentation pres, String folderName, Map<String, String> props) {
        log.info("开始处理幻灯片，文件夹名称为：{}", folderName);
        int sizePpt = pres.getSlides().size();
        for (int j = 0; j < sizePpt; j++) {
           log.debug("开始处理幻灯片：{}，形状数量：{}", j,pres.getSlides().get_Item(j).getShapes().size());
            ISlide sld = pres.getSlides().get_Item(j);
            // 用于存储需要删除的形状的列表
            List<IShape> shapesToRemove = new ArrayList<>();
            // 创建 Shapes 集合的副本
            List<IShape> shapesCopy = new ArrayList<IShape>();
            for (IShape shape : sld.getShapes()) {
                shapesCopy.add(shape);
            }
            // 遍历形状以查找占位符
            for (IShape shp : shapesCopy) {
                if (shp.getPlaceholder() != null) {
                    IPlaceholder placeholder = shp.getPlaceholder();
                    int type = placeholder.getType();
                    log.debug("当前形状类型为：{}，占位符类型为：{} ", shp.getClass().getSimpleName(), placeholder.getType());
                    switch (type) {
                        case 1:
                             log.debug("处理文本占位符, 内容为：{} ", ((IAutoShape) shp).getTextFrame().getText());
                            replaceTextPlaceholder((IAutoShape) shp, props);
                            break;
                        case 15:
                             log.debug("处理图片占位符,");
                            replacePicturePlaceholder(pres, sld, shp, folderName, shapesToRemove);
                            break;
                        default:
                            log.debug("忽略占位符类型：{}", type);
                    }
                }
            }
            //  统一删除需要删除的形状
            for (IShape shapeToRemove : shapesToRemove) {
                sld.getShapes().remove(shapeToRemove);
            }
           log.debug("幻灯片：{} 处理完成。",j);
        }
         log.info("幻灯片处理完成，文件夹名称为：{}", folderName);
    }

    /**
     * 替换文本占位符中的文本
     *
     * @param shape 文本占位符形状
     * @param props 属性映射
     */
    private static void replaceTextPlaceholder(IAutoShape shape, Map<String, String> props) {
        String text = shape.getTextFrame().getText();
        text = text.replaceAll("\\{", "").replaceAll("\\}", "");
        String newText = props.get(text);
        log.debug("替换占位符文本：{} 为：{}", text, newText);
        if (StrUtil.isNotEmpty(newText)) {
            shape.getTextFrame().setText(newText);
        }

    }

    /**
     * 替换图片占位符中的图片，并设置图片样式
     *
     * @param pres           演示文稿对象
     * @param sld            当前幻灯片
     * @param shp            图片占位符形状
     * @param folderName     文件夹名称
     * @param shapesToRemove 要删除的形状列表
     */
    private static void replacePicturePlaceholder(Presentation pres, ISlide sld, IShape shp, String folderName, List<IShape> shapesToRemove) {
         // 加载替换图片，将 BufferedImage 转为 InputStream
        BufferedImage bufferedImage = null;
        try {
            String picFilePath = CommonUtil.getFullPathFileName(folderName, folderName, ".jpg");
            bufferedImage = ImageIO.read(new File(picFilePath));
            if (bufferedImage == null) {
                log.warn("加载替换图片失败：{}", picFilePath);
                return;
            }
        } catch (IOException e) {
            log.error("加载替换图片发生异常", e);
            throw  new RuntimeException("加载替换图片发生异常",e);
        }

      try (ByteArrayOutputStream os = new ByteArrayOutputStream();
           InputStream is = new ByteArrayInputStream(os.toByteArray())) {
            ImageIO.write(bufferedImage, "png", os);
            // 使用推荐方法 addImage(InputStream)
           IPPImage pptxImage = pres.getImages().addImage(is);
            // 获取占位符的位置和大小
            float x = shp.getX();
            float y = shp.getY();
            float width = shp.getWidth();
            float height = shp.getHeight();
            // 创建圆角矩形形状
            IAutoShape shape = sld.getShapes().addAutoShape(ShapeType.RoundCornerRectangle, x, y, width, height);
            // 设置图片填充
            shape.getFillFormat().setFillType(FillType.Picture);
            IPictureFillFormat pictureFillFormat = shape.getFillFormat().getPictureFillFormat();
            pictureFillFormat.getPicture().setImage(pptxImage);
            // 设置填充模式为拉伸
            pictureFillFormat.setPictureFillMode(PictureFillMode.Stretch);

            // 将原始占位符添加到待删除列表
             shapesToRemove.add(shp);
        } catch (Exception e) {
            log.error("处理图片占位符时发生异常：", e);
            throw new RuntimeException("处理图片占位符异常", e);
        }

    }

    /**
     * 填充高级词汇表格
     *
     * @param pres       演示文稿对象
     * @param folderName 文件夹名称
     */
    private static void fillAdvancedWordTables(Presentation pres, String folderName) {
         log.info("开始填充高级词汇表格，文件夹名称：{}", folderName);
        List<WordInfoEntity> wordInfoEntityList = CdExcelUtil.getAdvancedWordList(folderName);
        List<WordInfo> wordInfoList = new ArrayList<>();
        WordInfo wordInfo;
        int no = 0;
        for (WordInfoEntity wordInfoEntity : wordInfoEntityList) {
            wordInfo = new WordInfo();
            BeanUtils.copyProperties(wordInfoEntity, wordInfo);
            ++no;
            wordInfo.setNo(no);
            wordInfo.setCn(wordInfoEntity.getComment());
            wordInfoList.add(wordInfo);
        }
        // 将列表分割成10个对象为一组，每组包含10个对象
        List<List<WordInfo>> wordInfoListGroups = new ArrayList<>();
        for (int j = 0; j < wordInfoList.size(); j += 10) {
            wordInfoListGroups.add(wordInfoList.subList(j, Math.min(j + 10, wordInfoList.size())));
        }
        int tableIndex = 10;
         // 遍历每一页幻灯片
        if (wordInfoListGroups.size() > 0) {
            int groupSize = wordInfoListGroups.size();
            switch (groupSize) {
                case 1:
                    fillTable(pres.getSlides().get_Item(tableIndex), wordInfoListGroups.get(0));
                    break;
                case 2:
                    fillTable(pres.getSlides().get_Item(tableIndex), wordInfoListGroups.get(0));
                    fillTable(pres.getSlides().get_Item(tableIndex + 1), wordInfoListGroups.get(1));
                    break;
                case 3:
                    fillTable(pres.getSlides().get_Item(tableIndex), wordInfoListGroups.get(0));
                    fillTable(pres.getSlides().get_Item(tableIndex + 1), wordInfoListGroups.get(1));
                    fillTable(pres.getSlides().get_Item(tableIndex + 2), wordInfoListGroups.get(2));
                    break;
                default:
                    log.warn("高级词汇表格数量为：{}，超出预期，未处理", groupSize);
            }

        }
         log.info("高级词汇表格填充完成，文件夹名称：{}", folderName);
    }

    /**
     * 填充表格
     *
     * @param sld          幻灯片对象
     * @param wordInfoList 要填充的数据
     */
    private static void fillTable(ISlide sld, List<WordInfo> wordInfoList) {
         ITable tbl = null;
        for (IShape shp : sld.getShapes()) {
            if (shp instanceof ITable) {
                tbl = (ITable) shp;
                log.debug("开始填充表格，表格位置：{}, 数据量: {}",shp.getFrame(), wordInfoList.size());
                int y = 0;
                for (WordInfo wordInfo2 : wordInfoList) {
                    y++;
                    tbl.get_Item(0, y).getTextFrame().setText(wordInfo2.getNo() + "");
                    tbl.get_Item(1, y).getTextFrame().setText(wordInfo2.getWord());
                    tbl.get_Item(2, y).getTextFrame().setText(wordInfo2.getUk());
                    tbl.get_Item(3, y).getTextFrame().setText(wordInfo2.getCn());
                    tbl.get_Item(4, y).getTextFrame().setText(wordInfo2.getLevelStr());
                    tbl.get_Item(5, y).getTextFrame().setText(wordInfo2.getTimes() + "");
                }
                log.debug("表格填充完成");
            }
        }
    }


    /**
     * 保存处理后的 PPT
     *
     * @param pres       演示文稿对象
     * @param folderName 文件夹名称
     */
    private static void saveProcessedPpt(Presentation pres, String folderName) {
         log.info("开始保存处理后的PPT，文件夹名称为：{}", folderName);
         String outputFileName = CommonUtil.getFullPathFileName(folderName, folderName, ".pptx");
        pres.save(outputFileName, SaveFormat.Pptx);
        log.info("PPT保存完成，输出路径：{}", outputFileName);
    }
}
