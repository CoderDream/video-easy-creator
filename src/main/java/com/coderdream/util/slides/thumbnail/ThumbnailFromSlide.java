package com.coderdream.util.slides.thumbnail;

import com.aspose.slides.IImage;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.coderdream.util.ppt.LicenseUtil;
import com.coderdream.util.ppt.MicrosoftConstants;
import com.coderdream.util.slides.RunExamples;


public class ThumbnailFromSlide {

  public static void main(String[] args) {

    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
    //ExStart:ThumbnailFromSlide
    // The path to the documents directory.
    String dataDir = RunExamples.getDataDir_Slides_Presentations_Thumbnail();
    String outputDir = RunExamples.getOutPath();

    // Instantiate a Presentation class that represents the presentation file
    Presentation pres = new Presentation(dataDir + "181213.pptx");

    try {
      int number = 0;
      for (int i = 0; i < pres.getSlides().size(); i++) {
        // Access the first slide
        ISlide sld = pres.getSlides().get_Item(i);

        // Create a full scale image
        IImage img = sld.getImage(1f, 1f);
        number = i + 1;
        // Save the image to disk in JPEG format
        img.save(outputDir + "Thumbnail_out" + number + ".jpg");
      }

    } finally {
      if (pres != null) {
        pres.dispose();
      }
    }
    //ExEnd:ThumbnailFromSlide
  }
}
