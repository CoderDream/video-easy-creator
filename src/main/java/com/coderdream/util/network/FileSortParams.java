package com.coderdream.util.network;

import lombok.Data;

/**
 * 文件排序参数对象
 */
@Data
public class FileSortParams {

  private String pr = "ucpro";
  private String fr = "pc";
  private String uc_param_str = "";
  private String pdir_fid = "df5fa55ae8c34cd08e50e76cc57da28a";
  private int _page = 1;
  private int _size = 50;
  private int _fetch_total = 1;
  private int _fetch_sub_dirs = 0;
  private String _sort = "file_type:asc,file_name:asc";
}
