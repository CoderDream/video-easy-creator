package com.coderdream.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代表 'items' 列表中的单个隧道项
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TunnelItem {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("configuration")
    private TunnelConfiguration configuration;

    @JsonProperty("status")
    private String status;

    @JsonProperty("public_url")
    private String publicUrl;

    @JsonProperty("publish_tunnels")
    private List<PublishTunnel> publishTunnels;
}
