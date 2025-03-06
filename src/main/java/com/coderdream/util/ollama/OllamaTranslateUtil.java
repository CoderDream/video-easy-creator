package com.coderdream.util.ollama;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.bbc.TranslateUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * OllamaApiUtil 类：封装了与 Ollama API 交互的工具方法，包含重试机制
 */
@Slf4j
public class OllamaTranslateUtil {

    public List<String> translateList(List<String> sourceList) {
        String prompt = "你是一个精通中文和英文的翻译大师。如果我给你英文就翻译成中文，给你中文就翻译成英文。";  // prompt：描述模型的任务
        String message = "Ollama now supports tool calling with popular models such as Llama 3.1."; // 消息内容：要翻译的文本

        // 英文字幕内容
//        List<String> stringList = new ArrayList<String>();
//
//        stringList.add("hello everybody");
//        stringList.add("hello how are we great ");
//        stringList.add("I apologize for my tardiness ");
//        stringList.add("it's quite the newsy day ");
//        stringList.add("and I was with the president talking about that news ");
//        stringList.add("so I look forward to taking your questions on it ");
//        stringList.add("but first ");
//        stringList.add("I want to talk about President Trump's historic ");
//        stringList.add("and incredible speech ");
//        stringList.add("last night the American people and the entire world ");
//        stringList.add("watch President Trump powerfully lay out ");
//        stringList.add("how he's renewing the American dream ");
//        stringList.add("in a record breaking joint address to Congress ");
//        stringList.add("and Americans loved what they heard ");
//        stringList.add("according to a CBS Yugov survey ");
//        stringList.add("an overwhelming 76 percent of those watching approved ");
//        stringList.add("of President Trump's speech last night ");
//        stringList.add("the president spoke about how he's taken more than ");
//        stringList.add("400 executive actions on his key promises ");
//        stringList.add("the expectations were high ");
//        stringList.add("and President Trump is exceeding them ");
//        stringList.add("according to brand new polling from the Daily Mail ");
//        stringList.add("President Trump has never been more popular ");
//        stringList.add("as his approval ratings are reaching ");
//        stringList.add("historic highs ");
//        stringList.add("more Americans believe ");
//        stringList.add("America is headed in the right direction ");
//        stringList.add("than the wrong direction ");
//        stringList.add("everyday Americans love this president because he ");
//        stringList.add("tells it like it is ");
//        stringList.add("no matter what ");
//        stringList.add("and he did that last night ");
//        stringList.add("President Trump level set ");
//        stringList.add("with the American people on the economy ");
//        stringList.add("and exposed how badly Joe Biden screwed it up ");
//        stringList.add("by causing the worst inflation crisis in four decades \n");
//        stringList.add("President Trump was honest about where we are ");
//        stringList.add("while making clear that help is on the way ");
//        stringList.add("as the president declared last night ");
//        stringList.add("he will make America affordable again ");
//        stringList.add("last night you also saw ");
//        stringList.add("who motivates the president to work so hard ");
//        stringList.add("everyday Americans ");
//        stringList.add("who President Trump shined a spotlight on last night ");
//        stringList.add("in his speech ");
//        stringList.add("from Mark Fogel ");
//        stringList.add("who President Trump was finally able to reunite with ");
//        stringList.add("his family and his beautiful 95 year old mother ");
//        stringList.add("after being detained in Russia ");
//        stringList.add("to Payton McNab ");
//        stringList.add("whose heart wrenching story ");
//        stringList.add("motivated President Trump to ");
//        stringList.add("end men and women's sports ");
//        stringList.add("and to Allison and Lauren Phillips ");
//        stringList.add("the mother and daughter and sister of Lakin Riley ");
//        stringList.add("who President Trump honored ");
//        stringList.add("by signing the Lakin Riley Act ");
//        stringList.add("to ensure her name will live on forever ");
//        stringList.add("in other amazing and surprised moments ");
//        stringList.add("President Trump honored the life of Jocelyn Nungesser ");
//        stringList.add("who was brutally murdered ");
//        stringList.add("by illegal alien gang members ");
//        stringList.add("he ensured Jocelyn will never be forgotten ");
//        stringList.add("by renaming a national wildlife refuge ");
//        stringList.add("in her home state of Texas ");
//        stringList.add("to honor her life ");
//        stringList.add("and in one of the greatest ");
//        stringList.add("surprise moments of the night ");
//        stringList.add("DJ Daniel an incredible 13 year old boy ");
//        stringList.add("who is beating brain cancer ");
//        stringList.add("saw his dreams fulfilled by President Trump ");
//        stringList.add("when he was made an honorary Secret Service agent ");
//        stringList.add("and finally after nearly four years ");
//        stringList.add("President Trump delivered justice ");
//        stringList.add("for the families of the 13 American heroes ");
//        stringList.add("who were killed at Abbey Gate ");
//        stringList.add("in the Biden botched Afghanistan withdrawal ");
//        stringList.add("which was one of the worst humiliations in the history ");
//        stringList.add("of our country ");
//        stringList.add("President Trump announced that we have detained ");
//        stringList.add("Muhammad Shirifullah ");
//        stringList.add("the monster ");
//        stringList.add("who was responsible for that horrific attack ");
//        stringList.add("and he was delivered to Dulles Airfield earlier ");
//        stringList.add("this morning ");
//        stringList.add("on his first day in office ");
//        stringList.add("President Trump's national security team ");
//        stringList.add("across the federal government ");
//        stringList.add("prioritized intelligence gathering to locate ");
//        stringList.add("this evil individual ");
//        stringList.add("President Trump's team ");
//        stringList.add("shared intelligence with regional partners ");
//        stringList.add("such as Pakistan ");
//        stringList.add("who helped identify this monster ");
//        stringList.add("in the borderland area ");
//        stringList.add("late last month ");
//        stringList.add("Mohammed confessed to his crimes related to Abu Gheit ");
//        stringList.add("and other attacks in Russia ");
//        stringList.add("and Iran as well to the Pakistanis ");
//        stringList.add("and U S");

        // 英文字幕内容
        String englishSubtitles = "hello everybody \n" +
                "hello how are we great \n" +
                "I apologize for my tardiness \n" +
                "it's quite the newsy day \n" +
                "and I was with the president talking about that news \n" +
                "so I look forward to taking your questions on it \n" +
                "but first \n" +
                "I want to talk about President Trump's historic \n" +
                "and incredible speech \n" +
                "last night the American people and the entire world \n" +
                "watch President Trump powerfully lay out \n" +
                "how he's renewing the American dream \n" +
                "in a record breaking joint address to Congress \n" +
                "and Americans loved what they heard \n" +
                "according to a CBS Yugov survey \n" +
                "an overwhelming 76 percent of those watching approved \n" +
                "of President Trump's speech last night \n" +
                "the president spoke about how he's taken more than \n" +
                "400 executive actions on his key promises \n" +
                "the expectations were high \n" +
                "and President Trump is exceeding them \n" +
                "according to brand new polling from the Daily Mail \n" +
                "President Trump has never been more popular \n" +
                "as his approval ratings are reaching \n" +
                "historic highs \n" +
                "more Americans believe \n" +
                "America is headed in the right direction \n" +
                "than the wrong direction \n" +
                "everyday Americans love this president because he \n" +
                "tells it like it is \n" +
                "no matter what \n" +
                "and he did that last night \n" +
                "President Trump level set \n" +
                "with the American people on the economy \n" +
                "and exposed how badly Joe Biden screwed it up \n" +
                "by causing the worst inflation crisis in four decades \n" +
                "President Trump was honest about where we are \n" +
                "while making clear that help is on the way \n" +
                "as the president declared last night \n" +
                "he will make America affordable again \n" +
                "last night you also saw \n" +
                "who motivates the president to work so hard \n" +
                "everyday Americans \n" +
                "who President Trump shined a spotlight on last night \n" +
                "in his speech \n" +
                "from Mark Fogel \n" +
                "who President Trump was finally able to reunite with \n" +
                "his family and his beautiful 95 year old mother \n" +
                "after being detained in Russia \n" +
                "to Payton McNab \n" +
                "whose heart wrenching story \n" +
                "motivated President Trump to \n" +
                "end men and women's sports \n" +
                "and to Allison and Lauren Phillips \n" +
                "the mother and daughter and sister of Lakin Riley \n" +
                "who President Trump honored \n" +
                "by signing the Lakin Riley Act \n" +
                "to ensure her name will live on forever \n" +
                "in other amazing and surprised moments \n" +
                "President Trump honored the life of Jocelyn Nungesser \n" +
                "who was brutally murdered \n" +
                "by illegal alien gang members \n" +
                "he ensured Jocelyn will never be forgotten \n" +
                "by renaming a national wildlife refuge \n" +
                "in her home state of Texas \n" +
                "to honor her life \n" +
                "and in one of the greatest \n" +
                "surprise moments of the night \n" +
                "DJ Daniel an incredible 13 year old boy \n" +
                "who is beating brain cancer \n" +
                "saw his dreams fulfilled by President Trump \n" +
                "when he was made an honorary Secret Service agent \n" +
                "and finally after nearly four years \n" +
                "President Trump delivered justice \n" +
                "for the families of the 13 American heroes \n" +
                "who were killed at Abbey Gate \n" +
                "in the Biden botched Afghanistan withdrawal \n" +
                "which was one of the worst humiliations in the history \n" +
                "of our country \n" +
                "President Trump announced that we have detained \n" +
                "Muhammad Shirifullah \n" +
                "the monster \n" +
                "who was responsible for that horrific attack \n" +
                "and he was delivered to Dulles Airfield earlier \n" +
                "this morning \n" +
                "on his first day in office \n" +
                "President Trump's national security team \n" +
                "across the federal government \n" +
                "prioritized intelligence gathering to locate \n" +
                "this evil individual \n" +
                "President Trump's team \n" +
                "shared intelligence with regional partners \n" +
                "such as Pakistan \n" +
                "who helped identify this monster \n" +
                "in the borderland area \n" +
                "late last month \n" +
                "Mohammed confessed to his crimes related to Abu Gheit \n" +
                "and other attacks in Russia \n" +
                "and Iran as well to the Pakistanis \n" +
                "and U S";

//        List<String> subtitleList = new ArrayList<>();
//        String[] lines = englishSubtitles.split("\n"); // Split the string by newline character
//        int index = 1;
//        for (String line : lines) {
//            String indexStr = String.format("%03d", index++);
//            subtitleList.add(indexStr + ": " + line.trim());
//        }
//
//        //按每100个一组分割
//        List<List<String>> parts = ListUtil.partition(subtitleList, 30);
//        for (List<String> part : parts) {
//            String text = String.join(" \n", part);
//            System.out.println("待翻译的英文字幕列表： \n" + text);
//            message = text;
//
//            OllamaApiUtil.OllamaRequest ollamaRequest = new OllamaApiUtil.OllamaRequest();
//            ollamaRequest.setPrompt(prompt);
//            ollamaRequest.setMessage(message);
//
//            OllamaApiUtil.OllamaResponse ollamaResponse = OllamaApiUtil.generate(ollamaRequest);
//
//            log.info("翻译后的文本: {}", ollamaResponse.getResponse());
//            log.info("API 调用耗时: {}", ollamaResponse.getDuration());
//        }
        //  通过微软服务翻译
//        int retryTime = 0;
//        while ((CdFileUtil.isFileEmpty(srcFileNameZhCn) || CdFileUtil.isFileEmpty(
//                srcFileNameZhCn)) && retryTime < 10) {
//            if (retryTime > 0) {
//                log.info(CdConstants.TRANSLATE_PLATFORM_MSTTS + " 重试次数: {}",
//                        retryTime);
//            }
//            TranslateUtil.translateSrcWithPlatform(srcFileNameEn, srcFileNameZhCn,
//                    srcFileNameZhTw,
//                    CdConstants.TRANSLATE_PLATFORM_MSTTS);
//            retryTime++;
//            ThreadUtil.sleep(3000L);
//        }

        return null;
    }

    public static String translateText(String text) {
        String prompt = "你是一个精通中文和英文的翻译大师。如果我给你英文就翻译成中文，给你中文就翻译成英文。";  // prompt：描述模型的任务
        OllamaApiUtil.OllamaRequest ollamaRequest = new OllamaApiUtil.OllamaRequest();
        ollamaRequest.setPrompt(prompt);
        ollamaRequest.setMessage(text);
        OllamaApiUtil.OllamaResponse ollamaResponse = OllamaApiUtil.generate(ollamaRequest);
        log.info("翻译后的文本: {}", ollamaResponse.getResponse());
        log.info("API 调用耗时: {}", ollamaResponse.getDuration());
        return ollamaResponse.getResponse();
    }

//    public static String translateTextRaw(String text) {
//        List<String> subtitleList = Collections.addAll(new ArrayList<>(), text.split("\n")); // Split the string by newline character
//        //  通过微软服务翻译
//        List<String> subtitleList = new ArrayList<>();
//        int retryTime = 0;
//        while ((CdFileUtil.isFileEmpty(srcFileNameZhCn) || CdFileUtil.isFileEmpty(
//                srcFileNameZhCn)) && retryTime < 10) {
//            if (retryTime > 0) {
//                log.info(CdConstants.TRANSLATE_PLATFORM_MSTTS + " 重试次数: {}",
//                        retryTime);
//            }
//            TranslateUtil.translateSrcWithPlatform(srcFileNameEn, srcFileNameZhCn,
//                    srcFileNameZhTw,
//                    CdConstants.TRANSLATE_PLATFORM_MSTTS);
//            retryTime++;
//            ThreadUtil.sleep(3000L);
//        }
//        return ollamaResponse.getResponse();
//    }


}