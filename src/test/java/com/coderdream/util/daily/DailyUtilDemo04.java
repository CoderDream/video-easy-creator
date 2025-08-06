package com.coderdream.util.daily;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
class DailyUtilDemo04 {

  public static void main(String[] args) {

    List<String> years = Arrays.asList("2016", "2017", "2018", "2019", "2020", "2021",
      "2022", "2023", "2024", "2025");
    for (String year : years) {
      DailyUtil.syncFilesToQuark(year);
    }
  }


}
