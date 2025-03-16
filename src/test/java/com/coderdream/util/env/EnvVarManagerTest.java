package com.coderdream.util.env;


import cn.hutool.core.io.FileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

@Slf4j
class EnvVarManagerTest {

  @Test
  void readEnvVar() {
    String string = EnvVarManager.readEnvVar("JAVA_HOME");
    log.info("JAVA_HOME: {}", string); // MY_GLOBAL_VAR
  }

  @Test
  void readEnvVar02() {
    String string = EnvVarManager.readEnvVar("MY_GLOBAL_VAR");
    log.info("MY_GLOBAL_VAR: {}", string); //
  }

  //
  @Test
  void readEnvVar03() {
    String string = EnvVarManager.readEnvVar("MY_PERSISTENT_PREF");
    log.info("MY_PERSISTENT_PREF: {}", string); //
  }

  @Test
  void readEnvVar04() {
    String envFileName = OperatingSystem.getBaseFolder() + File.separator + "env.txt";
    List<String> envVars = FileUtil.readLines(envFileName, StandardCharsets.UTF_8);
    for (String envVar : envVars) {
      String[] split = envVar.split("\t");
      if (split.length == 2) {
        String string = EnvVarManager.readEnvVar(split[0]);
        log.info("{} : {}",split[0], string); //
      } else {
        log.warn("env var format error: {}", envVar);
      }
    }
  }

  @Test
  void setEnvVar() {
    String envFileName = OperatingSystem.getBaseFolder() + File.separator + "env.txt";
    List<String> envVars = FileUtil.readLines(envFileName, StandardCharsets.UTF_8);
    for (String envVar : envVars) {
      String[] split = envVar.split("\t");
      if (split.length == 2) {
        EnvVarManager.setEnvVar(split[0], split[1]);
      } else {
        log.warn("env var format error: {}", envVar);
      }
    }
  }
}
