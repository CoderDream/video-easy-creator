package com.coderdream.util.ppt;

import com.aspose.slides.internal.i9.who;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LicenseUtil {

  public static void main(String[] args) {

    LicenseUtil.loadLicense();
  }

  public static void loadLicense() {

    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
  }

  public static void loadLicense(String who) {
    InputStream fis = null;// ResourceUtil.getStream("license.xml");
    try {
      fis = new FileInputStream("src/main/resources/license.xml");
    } catch (FileNotFoundException e) {
      System.out.println(who + "找不到license.xml");
      throw new RuntimeException(e);
    }
    if (MicrosoftConstants.PPTX_TO_OTHER.equals(who)) {
      com.aspose.slides.License license = new com.aspose.slides.License();
      license.setLicense(fis);
    }
  }
}
