package com.coderdream.util.network;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * 响应数据对象 - 简化示例，请根据实际情况调整
 */
@Data
public class QuarkDiskResponse {

  private int status;
  private int code;
  private String message;
  private long timestamp;
  private Data data;

  @lombok.Data
  public static class Data {
    private List<FileList> list;
  }

  @lombok.Data
  public static class FileList {
    private String fid;
    private String file_name;
    private String pdir_fid;
    private Integer category;
    private Integer file_type;
    private Integer size;
    private String format_type;
    private Integer status;
    private String tags;
    private Long l_created_at;
    private Long l_updated_at;
    private String source;
    private String file_source;
    private Integer name_space;
    private Long l_shot_at;
    private String source_display;
    private Integer include_items;
    private Boolean series_dir;
    private Boolean album_dir;
    private Boolean more_than_one_layer;
    private Boolean upload_camera_root_dir;
    private Double fps;
    private Integer like;
    private Long operated_at;
    private String sort_type;
    private String sort_range;
    private Integer risk_type;
    private Integer backup_sign;
    private Integer file_name_hl_start;
    private Integer file_name_hl_end;
    private FileStruct file_struct;
    private Integer duration;
    private Map<String, Object> event_extra;
    private String file_local_path;
    private String backup_file_local_path;
    private Integer scrape_status;
    private Long update_view_at;
    private Integer owner_drive_type_or_default;
    private Integer raw_name_space;
    private Integer cur_version_or_default;
    private Boolean save_as_source;
    private Boolean backup_source;
    private Boolean offline_source;
    private Boolean dir;
    private Boolean ban;
    private Boolean file;
    private Long created_at;
    private Long updated_at;
    private Map<String, Object> _extra;
  }

  @lombok.Data
  public static class FileStruct {
    private String fir_source;
    private String sec_source;
    private String thi_source;
    private String platform_source;
  }
}
