package com.coderdream.util.pic;

import java.awt.*;

public class FontList {
    public static void main(String[] args) {
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        for (String fontName : fontNames) {
            System.out.println(fontName);
        }
    }
}
