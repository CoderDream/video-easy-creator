package com.coderdream.util;

import java.io.File;

/**
 * @author CoderDream
 */
public class CommonUtil {

    public static String getFullPathFileName(String folderName, String fileName, String extensionName) {
//        System.out.println("###########folderName#######: " + folderName);
        String year = "20" + folderName.substring(0, 2);
        return BbcConstants.ROOT_FOLDER_NAME + year + File.separator + folderName + File.separator + fileName
            + extensionName;
    }

    public static String getFullPath(String folderName) {
//        System.out.println("###########folderName#######: " + folderName);
        String year = "20" + folderName.substring(0, 2);
        return BbcConstants.ROOT_FOLDER_NAME + year + File.separator + folderName + File.separator;
    }

}
