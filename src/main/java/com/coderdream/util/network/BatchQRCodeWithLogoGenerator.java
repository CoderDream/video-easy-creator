package com.coderdream.util.network;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BatchQRCodeWithLogoGenerator {

    private static final int QR_CODE_WIDTH = 300; // 二维码的宽度和高度
    private static final int QR_CODE_HEIGHT = 300;
    private static final String OUTPUT_DIR = "D:\\input\\output"; // 输出目录
    private static final String INPUT_FILE_PATH = "D:\\input\\quark_share_2017.txt"; // 输入文件路径
    private static final String LOGO_PATH = "D:\\input\\logo.png"; // logo 文件路径
    /**
     * 生成带logo的二维码
     *
     * @param content         二维码内容 (URL)
     * @param logoPath        logo文件路径
     * @param qrCodeImagePath  二维码图片保存路径（包含文件名，如：170105.png）
     * @param width 二维码宽度
     * @param height 二维码高度
     * @throws Exception
     */
    public static void generateQRCodeWithLogo(String content, String logoPath, String qrCodeImagePath, int width, int height) throws Exception {

        // 1. 设置二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 字符编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 容错等级，L=7% M=15% Q=25% H=30%
        hints.put(EncodeHintType.MARGIN, 1); // 边距

        // 2. 生成二维码矩阵
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        // 3. 创建BufferedImage对象
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 4. 填充二维码颜色
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF); // 黑色和白色
            }
        }

        // 5. 添加logo
        insertLogo(bufferedImage, logoPath);

        // 6. 保存二维码图片
        File outputFile = new File(qrCodeImagePath);
        ImageIO.write(bufferedImage, "png", outputFile);

        System.out.println("二维码生成成功！保存路径：" + qrCodeImagePath);

    }

    /**
     * 插入logo到二维码中间
     *
     * @param source  二维码图片
     * @param logoPath logo图片路径
     * @throws IOException
     */
    private static void insertLogo(BufferedImage source, String logoPath) throws IOException {
        File logoFile = new File(logoPath);
        if (!logoFile.exists()) {
            System.err.println("logo文件不存在：" + logoPath);
            return;
        }

        BufferedImage logo = ImageIO.read(logoFile);
        int width = source.getWidth();
        int height = source.getHeight();

        Graphics2D graph = source.createGraphics();

        // logo的尺寸，建议设为二维码的1/5
        int logoWidth = width / 5;
        int logoHeight = height / 5;
        int x = (width - logoWidth) / 2;
        int y = (height - logoHeight) / 2;

        // 画logo
        graph.drawImage(logo, x, y, logoWidth, logoHeight, null);

        // logo边框设置
        Shape shape = new RoundRectangle2D.Float(x, y, logoWidth, logoHeight, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);

        graph.dispose();
    }



    public static void main(String[] args) {
        try {
            // 创建输出目录
            Path outputDirPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDirPath)) {
                Files.createDirectories(outputDirPath);
            }

            // 读取输入文件
            Path inputFilePath = Paths.get(INPUT_FILE_PATH);
            if (!Files.exists(inputFilePath)) {
                System.err.println("输入文件不存在：" + INPUT_FILE_PATH);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 移除行尾的空格 (实际上应该是不需要的了，因为现在文本文件里没有逗号)
                    line = line.trim();


                    // 解析每行数据
                    String[] parts = line.split(" : ");
                    if (parts.length == 2) {
                        String titlePart = parts[0].trim();
                        String url = parts[1].trim();

                        // 提取文件名
                        String[] titleParts = titlePart.split(" ");
                        String fileName = titleParts[titleParts.length - 1].trim();
                        if (fileName.isEmpty()) {
                            System.err.println("无法提取文件名，跳过该行：" + line);
                            continue;
                        }

                        String qrCodeImagePath = Paths.get(OUTPUT_DIR, fileName + ".png").toString();

                        // 生成二维码
                        generateQRCodeWithLogo(url, LOGO_PATH, qrCodeImagePath, QR_CODE_WIDTH, QR_CODE_HEIGHT);
                    } else {
                        System.err.println("格式错误，跳过该行：" + line);
                    }
                }
            }

            System.out.println("所有二维码生成完成！");

        } catch (Exception e) {
            System.err.println("生成二维码失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *  解码二维码，返回内容
     * @param qrCodeImagePath  二维码图片路径
     * @return
     */
    public static String decodeQRCode(String qrCodeImagePath) throws IOException, NotFoundException {
        File qrCodeFile = new File(qrCodeImagePath);
        BufferedImage bufferedImage = ImageIO.read(qrCodeFile);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }
}
