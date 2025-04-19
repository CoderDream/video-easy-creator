package com.coderdream.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代表 API 响应中的 'data' 部分
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TunnelData {
    @JsonProperty("total")
    private int total;

    @JsonProperty("items")
    private List<TunnelItem> items;
}
