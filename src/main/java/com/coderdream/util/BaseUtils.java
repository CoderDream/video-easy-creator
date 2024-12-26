package com.coderdream.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.util.CollectionUtils;

/**
 * @author CoderDream
 */
public class BaseUtils {

    public static void main(String[] args) {
//        int a = 10, b = 3;
//        System.out.println(a % b);
//        List<String> urlList = new ArrayList<>(Arrays.asList("https://apps.apple.com/cn/app/id1592844577",
//                "https://apps.apple.com/cn/app/id1578843767",
//                "https://apps.apple.com/cn/app/id1536585848",
//                "https://apps.apple.com/cn/app/id425893570",
//                "https://apps.apple.com/cn/app/id1510078277",
//                "https://apps.apple.com/cn/app/id1255192598",
//                "https://apps.apple.com/cn/app/id598710611",
//                "https://apps.apple.com/cn/app/id6443737201"));
//        List<Object> objectList = new ArrayList<>();
//        objectList.addAll(urlList);
//        splitObjectList(3, objectList);


//        List<AppBrief> appBriefList = BaseUtils.genBrief();


//        BaseUtils.genBriefByWechat();
//        BaseUtils.splitAppBriefList(3, appBriefList);
    }

    public static void splitObjectList(int subListSize, List<Object> list) {
        List<List<Object>> lists = new ArrayList<>();


        List<Object> tempList = new ArrayList<>();
        int tempIndex = 0;
        for (Object object : list) {

            if (object instanceof String) {
                String str = (String) object;
                System.out.println(str);
                tempList.add(str);
                tempIndex++;
                if (tempIndex % subListSize == 0) {
                    lists.add(tempList);
                    tempList = new ArrayList<>();
                }
            }
        }

        if (!CollectionUtils.isEmpty(tempList)) {
            lists.add(tempList);
        }
        System.out.println("####");
    }

    /**
     * 返回
     * @return
     */
    public static String getPath() {
        String monthStr = new SimpleDateFormat("yyyyMM").format(new Date());
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String path = "D:" + File.separator + "12_iOS_Android" + File.separator + monthStr + File.separator + dateStr;
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
        return path;
    }


}

