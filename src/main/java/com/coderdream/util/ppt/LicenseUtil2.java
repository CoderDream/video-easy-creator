package com.coderdream.util.ppt;

import cn.hutool.core.io.resource.ClassPathResource;
import com.aspose.slides.License;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LicenseUtil2 {

  public static void main(String[] args) {
    System.out.println("开始加载PPTX转换其他格式的license");
//    loadLicense(MicrosoftConstants.PPTX_TO_OTHER);

    ClassPathResource classPathResource = new ClassPathResource("license.xml");
    InputStream fis = classPathResource.getStream();
    License license = new License();
    license.setLicense(fis);
  }

//  public static void loadLicense(String who) {
//    InputStream fis = null;// ResourceUtil.getStream("license.xml");
//    try {
//      fis = new FileInputStream("src/main/resources/license.xml");
//    } catch (FileNotFoundException e) {
//      System.out.println(who + "找不到license.xml");
//      throw new RuntimeException(e);
//    }
//    if (MicrosoftConstants.PPTX_TO_OTHER.equals(who)) {
//      com.aspose.slides.License license = new com.aspose.slides.License();
//      license.setLicense(fis);
//    }
//  }
}
