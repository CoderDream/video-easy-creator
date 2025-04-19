package com.coderdream.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代表单个已发布的隧道端点信息
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublishTunnel {
    @JsonProperty("name")
    private String name;

    @JsonProperty("public_url")
    private String publicUrl;

    @JsonProperty("proto")
    private String proto;

    @JsonProperty("addr")
    private String addr;

    @JsonProperty("type")
    private String type;

    // 日期时间可以保持为 String，或者引入 jackson-datatype-jsr310 模块并使用 Instant 或 ZonedDateTime 类型
    @JsonProperty("create_datetime")
    private String createDatetime;
}
