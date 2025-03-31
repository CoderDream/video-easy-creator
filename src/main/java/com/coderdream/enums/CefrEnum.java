package com.coderdream.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CefrEnum {
  A1(1),
  A2(2),
  B1(3),
  B2(4),
  C1(5),
  C2(6),
  UNKNOWN(99);

//  private final int level;

//  CefrEnum(int level) {
//    this.level = level;
//  }


  private final int level;

  private static final Map<String, CefrEnum> lookup = Arrays.stream(values())
    .collect(Collectors.toMap(CefrEnum::name, Function.identity()));

  private static final Map<Integer, CefrEnum> levelLookup = Arrays.stream(values())
    .collect(Collectors.toMap(CefrEnum::getLevel, Function.identity()));


  CefrEnum(int level) {
    this.level = level;
  }

//  public static CefrEnum fromString(String cefrString) {
//    if (cefrString == null || cefrString.isEmpty()) {
//      throw new IllegalArgumentException("cefrString cannot be null or empty");
//    }
//
//    CefrEnum result = lookup.get(cefrString.toUpperCase());
//    if (result == null) {
//      throw new IllegalArgumentException("Invalid cefrString: " + cefrString);
//    }
//    return result;
//  }

  public static CefrEnum fromLevel(int level) {
    return levelLookup.get(level);
  }

  public static CefrEnum fromString(String cefrString) {
    if (cefrString != null) {
      cefrString = cefrString.toUpperCase(); // 转换为大写以增强鲁棒性
      return switch (cefrString) {
        case "A1" -> A1;
        case "A2" -> A2;
        case "B1" -> B1;
        case "B2" -> B2;
        case "C1" -> C1;
        case "C2" -> C2;
        case "UNKNOWN" -> UNKNOWN;
        default -> null; // 或者抛出异常，根据你的需求
      };
    }
    return null; // 或者抛出异常
  }

}
