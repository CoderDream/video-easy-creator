package com.coderdream.util.youtube;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ChannelInfoExtractor2 {
    public static void main(String[] args) {
        try {
            // 读取JSON文件内容
            String jsonContent = new String(Files.readAllBytes(Paths.get("D:\\0003_油管\\description.json")));

            // 解析JSON
            JSONObject jsonObject = JSONUtil.parseObj(jsonContent);

            // 提取信息
            JSONObject aboutChannelViewModel = jsonObject.getJSONObject("onResponseReceivedEndpoints")
                   .getJSONArray("appendContinuationItemsAction")
                   .getJSONObject(0)
                   .getJSONObject("aboutChannelRenderer")
                   .getJSONObject("metadata")
                   .getJSONObject("aboutChannelViewModel");

            String description = aboutChannelViewModel.getStr("description");
            JSONObject channelExternalLinkViewModel = aboutChannelViewModel.getJSONArray("links")
                   .getJSONObject(0)
                   .getJSONObject("channelExternalLinkViewModel");
            String linkTitle = channelExternalLinkViewModel.getJSONObject("title").getStr("content");
            String linkUrl = channelExternalLinkViewModel.getJSONObject("link").getStr("content");
            String canonicalChannelUrl = aboutChannelViewModel.getStr("canonicalChannelUrl");
            String subscriberCountText = aboutChannelViewModel.getStr("subscriberCountText");
            String videoCountText = aboutChannelViewModel.getStr("videoCountText");
            String viewCountText = aboutChannelViewModel.getStr("viewCountText");
            String joinedDateText = aboutChannelViewModel.getJSONObject("joinedDateText").getStr("content");
            String country = aboutChannelViewModel.getStr("country");

            // 封装信息
            ChannelInfo channelInfo = new ChannelInfo();
            channelInfo.setDescription(description);
            channelInfo.setLink(linkTitle + " " + linkUrl);
            channelInfo.setChannelDetails(canonicalChannelUrl + "\n" + subscriberCountText + "\n" + videoCountText + "\n" + viewCountText + "\n" + joinedDateText + "\n" + country);

            // 输出信息
            System.out.println("简介\t" + channelInfo.getDescription());
            System.out.println("链接\t" + channelInfo.getLink());
            System.out.println("频道详情\t" + channelInfo.getChannelDetails());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
