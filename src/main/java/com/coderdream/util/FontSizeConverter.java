package com.coderdream.util;

/**
 * 工具类：用于在 HTML 像素大小（font-size）和 Java AWT 字体大小（Font size）之间进行转换。
 */
public class FontSizeConverter {

    /**
     * 默认屏幕分辨率 DPI（Dots Per Inch）。
     * 常见屏幕分辨率为 96 DPI。
     */
    private static final int DEFAULT_SCREEN_DPI = 96;

    /**
     * 将 HTML 的像素字体大小（font-size）转换为 Java AWT 的字体大小（point size）。
     * @param pixelSize HTML 字体的像素大小，例如 18px。
     * @return 对应的 Java AWT 字体大小（point size）。
     */
    public static int pixelToPoint(int pixelSize) {
        return Math.round((float) pixelSize * 72 / DEFAULT_SCREEN_DPI);
    }

    /**
     * 将 Java AWT 的字体大小（point size）转换为 HTML 的像素字体大小（font-size）。
     * @param pointSize Java AWT 字体大小，例如 14。
     * @return 对应的 HTML 字体的像素大小（font-size）。
     */
    public static int pointToPixel(int pointSize) {
        return Math.round((float) pointSize * DEFAULT_SCREEN_DPI / 72);
    }

    /**
     * 主方法：测试像素和点之间的转换。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        int pixelSize = 18;
        int pointSize = FontSizeConverter.pixelToPoint(pixelSize);
        System.out.println("HTML 像素大小: " + pixelSize + "px 对应的 Java AWT 字体大小: " + pointSize + "pt");

        int backToPixelSize = pointToPixel(pointSize);
        System.out.println("Java AWT 字体大小: " + pointSize + "pt 对应的 HTML 像素大小: " + backToPixelSize + "px");
    }
}
