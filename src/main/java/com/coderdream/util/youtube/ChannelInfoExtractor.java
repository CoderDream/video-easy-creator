package com.coderdream.util.youtube;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class ChannelInfoExtractor {
    public static void main(String[] args) {
        try {
            // 读取JSON文件内容
            String jsonContent = new String(Files.readAllBytes(Paths.get("D:\\0003_油管\\description.json")));

            // 解析JSON
            JSONObject jsonObject = JSONUtil.parseObj(jsonContent);

            // 提取信息
            JSONObject onResponseReceivedEndpoints = jsonObject.getJSONArray("onResponseReceivedEndpoints").getJSONObject(0);
            JSONObject appendContinuationItemsAction = onResponseReceivedEndpoints.getJSONObject("appendContinuationItemsAction");
            JSONObject continuationItems = appendContinuationItemsAction.getJSONArray("continuationItems").getJSONObject(0);
            JSONObject aboutChannelRenderer = continuationItems.getJSONObject("aboutChannelRenderer");
            JSONObject metadata = aboutChannelRenderer.getJSONObject("metadata");
            JSONObject aboutChannelViewModel = metadata.getJSONObject("aboutChannelViewModel");

            String description = aboutChannelViewModel.getStr("description");
            JSONObject channelExternalLinkViewModel = aboutChannelViewModel.getJSONArray("links").getJSONObject(0).getJSONObject("channelExternalLinkViewModel");
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
            ChannelDetails channelDetails = new ChannelDetails(canonicalChannelUrl, subscriberCountText, videoCountText, viewCountText, joinedDateText, country);
            channelInfo.setChannelDetails(channelDetails);

            // 输出信息到控制台
            log.info("简介\t{}", channelInfo.getDescription());
            log.info("链接\t{}", channelInfo.getLink());
            log.info("频道详情\t{}", channelInfo.getChannelDetails());

            // 将信息写入文本文件
            writeToFile(channelInfo);
        } catch (Exception e) {
            log.error("解析JSON文件出错", e);
        }
    }

    private static void writeToFile(ChannelInfo channelInfo) throws IOException {
        File file = new File("D:\\0003_油管\\description_log.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file, true); // 设置为追加模式
//        writer.write("简介\t" + channelInfo.getDescription() + "\n");
//        writer.write("链接\t" + channelInfo.getLink() + "\n");
        writer.write("频道详情\t" + channelInfo.getChannelDetails() + "\n");
        writer.close();
    }

    @Data
    static class ChannelInfo {
        private String description;
        private String link;
        private ChannelDetails channelDetails;
    }

    @Data
    static class ChannelDetails {
        private String canonicalChannelUrl;
        private String subscriberCountText;
        private String videoCountText;
        private String viewCountText;
        private String joinedDateText;
        private String country;

        public ChannelDetails(String canonicalChannelUrl, String subscriberCountText, String videoCountText, String viewCountText, String joinedDateText, String country) {
            this.canonicalChannelUrl = canonicalChannelUrl;
            this.subscriberCountText = subscriberCountText;
            this.videoCountText = videoCountText;
            this.viewCountText = viewCountText;
            this.joinedDateText = joinedDateText;
            this.country = country;
        }
    }
}
