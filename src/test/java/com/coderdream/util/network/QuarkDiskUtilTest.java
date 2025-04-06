package com.coderdream.util.network;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class QuarkDiskUtilTest {

  @Test
  void process() {

    //    String year = "2023";
//    List<String> years = List.of("2018", "2019", "2020", "2021", "2022", "2023");
    List<String> years = List.of("2018", "2025");
    for (String year : years) {
      QuarkDiskUtil.process(year);
    }
  }
}
