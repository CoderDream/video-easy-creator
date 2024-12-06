package com.coderdream.enums;

/**
 * 雅思等级枚举
 */
public enum IeltsLevelEnum {
    A1(1, "A1"),
    A2(2, "A2"),
    B1(3, "B1"),
    B2(4, "B2"),
    C1(5, "C1"),
    C2(6, "C2");

    private final Integer key; // 雅思等级的编号（1, 2, 3, ..., 6）
    private final String value; // 对应的雅思等级名称（A1, A2, ...）

    private IeltsLevelEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    // 根据value查找对应的枚举值
    public static IeltsLevelEnum match(String value) {
        for (IeltsLevelEnum item : IeltsLevelEnum.values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }

    // 根据key查找对应的枚举值
    public static IeltsLevelEnum init(Integer key) {
        for (IeltsLevelEnum item : IeltsLevelEnum.values()) {
            if (item.key.equals(key)) {
                return item;
            }
        }
        return null;
    }
}
