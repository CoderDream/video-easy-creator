package com.coderdream.util.network;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ShareResponse {
    private int status;
    private int code;
    private String message;
    private long timestamp;
    private ShareData data;

    @Data
    public static class ShareData {
        private String title;
        private String sub_title;
        private Integer share_type;
        private String pwd_id;
        private String share_url;
        private Integer url_type;
        private Integer expired_type;
        private Integer file_num;
        private Long expired_at;
        private FirstFile first_file;
        private String path_info;
        private Boolean partial_violation;
        private List<Integer> first_layer_file_categories;
        private Boolean download_pvlimited;
    }

    @Data
    public static class FirstFile {
        private String fid;
        private Integer category;
        private Integer file_type;
        private String format_type;
        private Integer name_space;
        private Boolean series_dir;
        private Boolean album_dir;
        private Boolean more_than_one_layer;
        private Boolean upload_camera_root_dir;
        private Double fps;
        private Integer like;
        private Integer risk_type;
        private Integer file_name_hl_start;
        private Integer file_name_hl_end;
        private Integer duration;
        private Integer scrape_status;
        private Boolean ban;
        private Integer cur_version_or_default;
        private Boolean save_as_source;
        private Boolean backup_source;
        private Boolean offline_source;
        private Integer owner_drive_type_or_default;
        private Boolean dir;
        private Boolean file;
        private Map<String, Object> _extra;
    }
}
