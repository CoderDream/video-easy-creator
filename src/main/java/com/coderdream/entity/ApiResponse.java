// 包名请替换为你项目的实际包名
package com.coderdream.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代表整体 API 响应结构
 */
@Data // Lombok 注解：自动生成 getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok 注解：生成无参构造函数
@JsonIgnoreProperties(ignoreUnknown = true) // Jackson 注解：忽略未知属性
public class ApiResponse {
    @JsonProperty("data") // Jackson 注解：映射 JSON 中的 "data" 字段
    private TunnelData data;

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;
}
