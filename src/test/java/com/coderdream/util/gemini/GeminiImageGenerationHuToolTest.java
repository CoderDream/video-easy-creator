package com.coderdream.util.gemini;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.gemini.demo02.GeminiImageGenerationHuTool;
 import com.coderdream.util.proxy.OperatingSystem;
 import lombok.extern.slf4j.Slf4j;
 import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
 import org.junit.jupiter.params.ParameterizedTest;
 import org.junit.jupiter.params.provider.ValueSource;

 import java.io.File;
import java.nio.file.Files;
 import java.nio.file.Paths;
 import java.util.Arrays;
 import java.util.List;
 import java.util.concurrent.TimeUnit;

 import static org.junit.jupiter.api.Assertions.assertTrue;
 import static org.junit.jupiter.api.Assertions.fail;

 @Slf4j
 public class GeminiImageGenerationHuToolTest {

   private static final String IMAGE_SAVE_PATH_PREFIX = "gemini-native-image-";
   private static final String IMAGE_SAVE_PATH_SUFFIX = ".png";
   private static final int TARGET_WIDTH = 1920;
   private static final int TARGET_HEIGHT = 1080;
   private static final int MAX_RETRIES = 10;
   private static final long RETRY_SLEEP_SECONDS = 5;

   // 爱丽丝梦游仙境第一章的提示词列表
//   private static final String ALICE_STYLE = "风格统一，图像分辨率为 1080p (1920x1080)：使用迪士尼动画风格，爱丽丝穿着标志性的蓝色连衣裙，金发碧眼，面容天真烂漫。所有场景都具有鲜艳的色彩、梦幻般的氛围和柔和的光照。";
//
//   private static final List<String> alicePrompts = Arrays.asList(
//     ALICE_STYLE + "爱丽丝和她的姐姐坐在河岸上，姐姐在看一本没有插图的书，阳光明媚，爱丽丝显得无聊。",
//     ALICE_STYLE + "白兔穿着红色马甲，神色匆忙地跑过，手里拿着怀表，背景是绿色的草地。",
//     ALICE_STYLE + "爱丽丝惊讶地掉进黑暗的兔子洞，四肢伸展开来，周围是泥土和树根，洞口的光线逐渐消失。",
//     ALICE_STYLE + "爱丽丝慢慢地掉进深井里，周围是书架、柜子和地图，书籍摆放整齐，透着奇异的光芒。",
//     ALICE_STYLE + "一个空橘子酱罐头从书架上掉下来，爱丽丝试图抓住它，周围是各种奇怪的物品，井壁色彩斑斓。",
//     ALICE_STYLE + "爱丽丝在井里思考，表情迷茫，自言自语：'这次摔下去之后，以后再也不会害怕下楼梯了！'",
//     ALICE_STYLE + "爱丽丝在下落时思考，周围是黑暗，她好奇地想自己已经掉了多少英里，周围漂浮着书本。",
//     ALICE_STYLE + "爱丽丝想象着倒立行走的人们，她感到既好奇又有点害怕，人物形象滑稽可爱。",
//     ALICE_STYLE + "爱丽丝思念着她的猫咪丁娜，希望她也能在这里和自己作伴，脑海中浮现出丁娜的可爱形象。",
//     ALICE_STYLE + "爱丽丝重重地摔在一堆树枝和落叶上，但毫发无伤，她好奇地站起来，准备探索，阳光从上方射入。",
//     ALICE_STYLE + "爱丽丝站在一个狭长的大厅里，周围挂满了灯，她感到迷茫和不知所措，大厅的尽头隐约可见。",
//     ALICE_STYLE + "爱丽丝发现一张小玻璃桌子，上面放着一把金钥匙，她充满了希望，眼中闪烁着光芒。",
//     ALICE_STYLE + "金钥匙闪闪发光，细节精致，它象征着通往新世界的大门，钥匙的周围环绕着光晕。",
//     ALICE_STYLE + "爱丽丝跪在地上，透过小门向外看，一个美丽的花园在等待着她，花园里鲜花盛开，喷泉流淌。",
//     ALICE_STYLE + "爱丽丝渴望进入花园，她的眼神充满了向往和期待，小门显得格外渺小。",
//     ALICE_STYLE + "爱丽丝沮丧地想，如果能像望远镜一样折叠起来就好了，她的身体开始变得柔软。",
//     ALICE_STYLE + "瓶子上贴着标签，喝我，瓶子的颜色鲜艳而神秘，引人注目。",
//     ALICE_STYLE + "爱丽丝小心翼翼地品尝瓶子里的饮料，表情既好奇又有点害怕，饮料的颜色如彩虹般绚丽。",
//     ALICE_STYLE + "爱丽丝迅速缩小，她的裙子变得巨大，她感到有些惊慌，周围的家具也变得巨大无比。",
//     ALICE_STYLE + "蛋糕上用葡萄干写着，吃我，它散发着诱人的香气，蛋糕的造型精美可爱。"
//   );

   private static final String ALICE_STYLE = "Consistent style, image resolution of 1080p (1920x1080): Use a Disney animation style, Alice wearing her iconic blue dress, blonde hair and blue eyes, with an innocent and charming face. All scenes should have vibrant colors, a dreamlike atmosphere, and soft lighting.";

   private static final List<String> alicePrompts = Arrays.asList(
     ALICE_STYLE + "Alice and her sister are sitting on a riverbank, her sister is reading a book without illustrations, the sun is shining brightly, and Alice looks bored.",
     ALICE_STYLE + "The White Rabbit, wearing a red waistcoat, runs by in a hurry, holding a pocket watch, with a green grassy background.",
     ALICE_STYLE + "Alice falls surprisingly into a dark rabbit hole, her limbs outstretched, surrounded by dirt and roots, the light from the hole gradually disappearing.",
     ALICE_STYLE + "Alice slowly falls into a deep well, surrounded by bookshelves, cabinets, and maps, the books neatly arranged and emitting a strange glow.",
     ALICE_STYLE + "An empty marmalade jar falls from a bookshelf, Alice tries to catch it, surrounded by various strange objects, the well walls are colorfully decorated.",
     ALICE_STYLE + "Alice thinks in the well, with a confused expression, muttering to herself: 'After this fall, I'll never be afraid of going downstairs again!'",
     ALICE_STYLE + "Alice thinks while falling, surrounded by darkness, she wonders curiously how many miles she has already fallen, with books floating around.",
     ALICE_STYLE + "Alice imagines people walking upside down, she feels both curious and a little scared, the figures are comically cute.",
     ALICE_STYLE + "Alice misses her cat Dinah and wishes she could be here with her, the cute image of Dinah appears in her mind.",
     ALICE_STYLE + "Alice falls heavily onto a pile of branches and fallen leaves, but is unharmed, she stands up curiously, ready to explore, sunlight streaming in from above.",
     ALICE_STYLE + "Alice stands in a long, narrow hall, surrounded by hanging lamps, she feels lost and overwhelmed, the end of the hall is dimly visible.",
     ALICE_STYLE + "Alice finds a small glass table with a golden key on it, she is filled with hope, her eyes sparkling.",
     ALICE_STYLE + "The golden key shines brightly, with delicate details, it symbolizes the gateway to a new world, a halo of light surrounds the key.",
     ALICE_STYLE + "Alice kneels on the ground and looks out through a small door, a beautiful garden awaits her, with blooming flowers and flowing fountains.",
     ALICE_STYLE + "Alice yearns to enter the garden, her eyes are full of longing and anticipation, the small door looks particularly tiny.",
     ALICE_STYLE + "Alice thinks dejectedly that it would be nice if she could fold up like a telescope, her body begins to feel soft.",
     ALICE_STYLE + "There is a label on the bottle, reading 'DRINK ME', the color of the bottle is bright and mysterious, attracting attention.",
     ALICE_STYLE + "Alice carefully sips the drink from the bottle, her expression is both curious and a little scared, the color of the drink is as brilliant as a rainbow.",
     ALICE_STYLE + "Alice shrinks rapidly, her dress becomes huge, she feels a little panicked, and the surrounding furniture becomes enormous.",
     ALICE_STYLE + "The cake has 'EAT ME' written on it with currants, it emits an enticing aroma, the cake's shape is exquisite and lovely."
   );

   @ParameterizedTest
   @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
     17, 18, 19})
   @EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
   public void testGenerateAliceIllustrations(int promptIndex) {
     String prompt = alicePrompts.get(promptIndex);
     String bookName = "EnBook010";
     String folderPath = OperatingSystem.getFolderPath(bookName);
     String subFolder = "Chapter001";
     String imageSavePath =
       folderPath + File.separator + subFolder + File.separator
         + IMAGE_SAVE_PATH_PREFIX + String.format("%03d", promptIndex)
         + IMAGE_SAVE_PATH_SUFFIX;


     for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
       try {
         log.info("Generating image for prompt: \"{}\", attempt {}/{}", prompt, attempt, MAX_RETRIES);
         if(CdFileUtil.isFileEmpty(imageSavePath)){
           log.warn("Image already exists for prompt index {}, skipping generation", promptIndex);
           GeminiImageGenerationHuTool.generateContent(prompt, imageSavePath);
//           return; // 如果文件已存在，则跳过生成步骤
         } else {
           // 跳出循环，因为文件已存在
           break;
         }

         // 检查文件是否创建
         File generatedImage = new File(imageSavePath);
         assertTrue(generatedImage.exists(),
           "Image file should be created for prompt index " + promptIndex);

         // 检查文件是否不为空
         assertTrue(generatedImage.length() > 0,
           "Image file should not be empty for prompt index " + promptIndex);

         // 检查图像大小
         if (CdImageUtil.isImageSizeCorrect(imageSavePath, TARGET_WIDTH, TARGET_HEIGHT)) {
           log.info("Image generated successfully for prompt index {}, saved to {}, width = {}, height = {}",
             promptIndex, imageSavePath, TARGET_WIDTH, TARGET_HEIGHT);
           return; // 成功生成并校验后，结束重试
         } else {
           log.warn("Image size is incorrect for prompt index {}. Retrying (attempt {}/{})", promptIndex, attempt, MAX_RETRIES);
           // 删除不符合要求的图片
           Files.deleteIfExists(Paths.get(imageSavePath));
         }
       } catch (Exception e) {
         log.error("Error generating image for prompt index {}: {}", promptIndex, e.getMessage(), e);
         // 如果不是最后一次重试，则记录并继续
         if (attempt < MAX_RETRIES) {
           log.warn("Retrying after error for prompt index {}: {}", promptIndex, e.getMessage());
         } else {
           fail("Test failed for prompt index " + promptIndex + " after " + MAX_RETRIES + " retries: " + e.getMessage());
         }
       }

       // 等待一段时间后重试
       if (attempt < MAX_RETRIES) {
         try {
           TimeUnit.SECONDS.sleep(RETRY_SLEEP_SECONDS);
         } catch (InterruptedException ex) {
           Thread.currentThread().interrupt();
           log.warn("Sleep interrupted", ex);
         }
       }
     }

     fail("Failed to generate image with correct size for prompt index " + promptIndex + " after " + MAX_RETRIES + " retries");
   }
 }
