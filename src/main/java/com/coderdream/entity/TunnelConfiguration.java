package com.coderdream.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代表隧道的 'configuration' 详细信息
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TunnelConfiguration {
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private String id;
    @JsonProperty("subdomain")
    private String subdomain;
    @JsonProperty("hostname")
    private String hostname;
    @JsonProperty("proto")
    private String proto;
    @JsonProperty("auth")
    private String auth;
    @JsonProperty("addr")
    private String addr;
    // 注意：JSON 中的 "false" 是字符串，这里用 String 类型接收更安全
    @JsonProperty("inspect")
    private String inspect;
    @JsonProperty("host_header")
    private String hostHeader;
    @JsonProperty("bind_tls")
    private String bindTls;
    @JsonProperty("crt")
    private String crt;
    @JsonProperty("key")
    private String key;
    @JsonProperty("client_cas")
    private String clientCas;
    @JsonProperty("remote_addr")
    private String remoteAddr;
    @JsonProperty("region")
    private String region;
    @JsonProperty("disable_keep_alives")
    private String disableKeepAlives; // 同样是字符串 "false"
    @JsonProperty("redirect_https")
    private String redirectHttps; // 同样是字符串 "false"
    @JsonProperty("start_type")
    private String startType;
    @JsonProperty("permanent")
    private boolean permanent; // 这个是布尔值 true
}
