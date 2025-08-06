package com.coderdream.util.daily;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cmd.CommandUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
class DailyUtilDemo03 {

  public static void main(String[] args) {

    List<String> years = Arrays.asList("2016", "2017", "2018", "2019", "2020", "2021",
      "2022", "2023", "2024", "2025");
    for (String year : years) {
      DailyUtil.syncFilesToQuark(year);
    }
  }


}
