package com.coderdream.util.youtube;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ChannelInfoExtractor3 {
    public static void main(String[] args) {
        try {
            // 读取JSON文件内容
            String jsonContent = new String(Files.readAllBytes(Paths.get("D:\\0003_油管\\description.json")));

            // 解析JSON
            JSONObject jsonObject = JSONUtil.parseObj(jsonContent);

            // 提取信息
            JSONObject onResponseReceivedEndpoints = jsonObject.getJSONObject("onResponseReceivedEndpoints");
            if (onResponseReceivedEndpoints!= null) {
                JSONArray appendContinuationItemsActionArray = onResponseReceivedEndpoints.getJSONArray("appendContinuationItemsAction");
                if (appendContinuationItemsActionArray!= null && appendContinuationItemsActionArray.size() > 0) {
                    JSONObject actionItem = appendContinuationItemsActionArray.getJSONObject(0);
                    if (actionItem!= null) {
                        JSONObject aboutChannelRenderer = actionItem.getJSONObject("aboutChannelRenderer");
                        if (aboutChannelRenderer!= null) {
                            JSONObject metadata = aboutChannelRenderer.getJSONObject("metadata");
                            if (metadata!= null) {
                                JSONObject aboutChannelViewModel = metadata.getJSONObject("aboutChannelViewModel");
                                if (aboutChannelViewModel!= null) {
                                    String description = aboutChannelViewModel.getStr("description");
                                    JSONArray linksArray = aboutChannelViewModel.getJSONArray("links");
                                    if (linksArray!= null && linksArray.size() > 0) {
                                        JSONObject channelExternalLinkViewModel = linksArray.getJSONObject(0).getJSONObject("channelExternalLinkViewModel");
                                        if (channelExternalLinkViewModel!= null) {
                                            String linkTitle = channelExternalLinkViewModel.getJSONObject("title").getStr("content");
                                            String linkUrl = channelExternalLinkViewModel.getJSONObject("link").getStr("content");
                                            if (linkTitle!= null && linkUrl!= null) {
                                                // 封装信息
                                                ChannelInfo channelInfo = new ChannelInfo();
                                                channelInfo.setDescription(description);
                                                channelInfo.setLink(linkTitle + " " + linkUrl);
                                                channelInfo.setChannelDetails(getChannelDetails(aboutChannelViewModel));

                                                // 输出信息
                                                System.out.println("简介\t" + channelInfo.getDescription());
                                                System.out.println("链接\t" + channelInfo.getLink());
                                                System.out.println("频道详情\t" + channelInfo.getChannelDetails());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getChannelDetails(JSONObject aboutChannelViewModel) {
        StringBuilder detailsBuilder = new StringBuilder();
        if (aboutChannelViewModel!= null) {
            detailsBuilder.append(aboutChannelViewModel.getStr("canonicalChannelUrl")).append("\n");
            detailsBuilder.append(aboutChannelViewModel.getStr("subscriberCountText")).append("\n");
            detailsBuilder.append(aboutChannelViewModel.getStr("videoCountText")).append("\n");
            detailsBuilder.append(aboutChannelViewModel.getStr("viewCountText")).append("\n");
            detailsBuilder.append(aboutChannelViewModel.getJSONObject("joinedDateText").getStr("content")).append("\n");
            detailsBuilder.append(aboutChannelViewModel.getStr("country"));
        }
        return detailsBuilder.toString();
    }

    static class ChannelInfo {
        private String description;
        private String link;
        private String channelDetails;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getChannelDetails() {
            return channelDetails;
        }

        public void setChannelDetails(String channelDetails) {
            this.channelDetails = channelDetails;
        }
    }
}
