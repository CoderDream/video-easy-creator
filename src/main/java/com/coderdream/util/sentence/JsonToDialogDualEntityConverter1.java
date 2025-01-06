package com.coderdream.util.sentence;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

public class JsonToDialogDualEntityConverter1 {

  @Data
  public static class DialogDualEntity {

    /**
     * 主持人A英文名
     */
    private String hostAEn;
    /**
     * 主持人A中文名
     */
    private String hostACn;
    /**
     * 脚本A英文
     */
    private String contentAEn;
    /**
     * 脚本A中文
     */
    private String contentACn;

    /**
     * 主持人B英文名
     */
    private String hostBEn;
    /**
     * 主持人B中文名
     */
    private String hostBCn;
    /**
     * 脚本B英文
     */
    private String contentBEn;
    /**
     * 脚本B英文
     */
    private String contentBCn;
  }


  public static void main(String[] args) {
    String jsonString =
      "[\n"
        + "	{\n"
        + "		\"speaker\": \"A\",\n"
        + "		\"sentences\": [\n"
        + "			\"I'm looking for a job which offers lodging.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"B\",\n"
        + "		\"sentences\": [\n"
        + "			\"It's hard.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"A\",\n"
        + "		\"sentences\": [\n"
        + "			\"Is this vacancy still available?\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"B\",\n"
        + "		\"sentences\": [\n"
        + "			\"Yes, we still need satisfying people.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"A\",\n"
        + "		\"sentences\": [\n"
        + "			\"What are the requirements to apply for the position?\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"B\",\n"
        + "		\"sentences\": [\n"
        + "			\"You must have over two years' experience first.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"A\",\n"
        + "		\"sentences\": [\n"
        + "			\"我正在找一份能提供住宿的工作。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"B\",\n"
        + "		\"sentences\": [\n"
        + "			\"这挺难的。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"A\",\n"
        + "		\"sentences\": [\n"
        + "			\"这个工作还招人吗？\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"B\",\n"
        + "		\"sentences\": [\n"
        + "			\"是的，我们一直需要合适的人选。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"A\",\n"
        + "		\"sentences\": [\n"
        + "			\"应聘这个职务的条件是什么？\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"B\",\n"
        + "		\"sentences\": [\n"
        + "			\"首先你必须有两年以上的经验。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Mary\",\n"
        + "		\"sentences\": [\n"
        + "			\"Hey Tom, I saw this ad in the paper.\",\n"
        + "			\"You should take a look.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Tom\",\n"
        + "		\"sentences\": [\n"
        + "			\"What is it?\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Mary\",\n"
        + "		\"sentences\": [\n"
        + "			\"It's for a job.\",\n"
        + "			\"It looks perfect for you.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Tom\",\n"
        + "		\"sentences\": [\n"
        + "			\"Let me see.\",\n"
        + "			\"\\\"Wanted：manager for upand-coming firm.\",\n"
        + "			\"Must have good organizational skills.\",\n"
        + "			\"Experience a plus.\",\n"
        + "			\"Please contact Susan Lee.\\\"\",\n"
        + "			\"Oh, I don't know...\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Mary\",\n"
        + "		\"sentences\": [\n"
        + "			\"Come on, what have you got to lose?\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Tom\",\n"
        + "		\"sentences\": [\n"
        + "			\"What about my resume?\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Mary\",\n"
        + "		\"sentences\": [\n"
        + "			\"Here, I'll help you type one up.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Tom\",\n"
        + "		\"sentences\": [\n"
        + "			\"Thanks, Mary.\",\n"
        + "			\"You're a real pal.\",\n"
        + "			\"I'll call now to set up an interview .\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"玛丽\",\n"
        + "		\"sentences\": [\n"
        + "			\"嘿，汤姆，我看到报纸上的这篇广告。\",\n"
        + "			\"你该看一看。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"汤姆\",\n"
        + "		\"sentences\": [\n"
        + "			\"是什么广告？\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"玛丽\",\n"
        + "		\"sentences\": [\n"
        + "			\"招聘广告。\",\n"
        + "			\"看上去很适合你。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"汤姆\",\n"
        + "		\"sentences\": [\n"
        + "			\"让我瞧瞧。\",\n"
        + "			\"”招聘：极富发展潜力的公司招聘经理。\",\n"
        + "			\"需良好的组织才能。\",\n"
        + "			\"需有经验。\",\n"
        + "			\"有意者请与苏珊·李联系“。\",\n"
        + "			\"哦，我不知道......\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"玛丽\",\n"
        + "		\"sentences\": [\n"
        + "			\"别这样，你会有什么损失吗？\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"汤姆\",\n"
        + "		\"sentences\": [\n"
        + "			\"可是我的履历表怎么办呢？\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"玛丽\",\n"
        + "		\"sentences\": [\n"
        + "			\"关于这个，我会帮你打印一份的。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"汤姆\",\n"
        + "		\"sentences\": [\n"
        + "			\"谢谢你，玛丽。\",\n"
        + "			\"你真够朋友。\",\n"
        + "			\"我现在就打电话约定面试。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"I have a resume here.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Shirley\",\n"
        + "		\"sentences\": [\n"
        + "			\"What's your name, please?\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"David Chou.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Shirley\",\n"
        + "		\"sentences\": [\n"
        + "			\"Oh, yes, Mr. Chou.\",\n"
        + "			\"We have been looking forward to this.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"These are all my personal documents about my education and working experience you ask for.\",\n"
        + "			\"And I have to be off for an important meeting now.\",\n"
        + "			\"If you think I'm right for the job, please keep me informed.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Shirley\",\n"
        + "		\"sentences\": [\n"
        + "			\"OK.\",\n"
        + "			\"I will call you if you give me your name card.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"I am sorry but I don't have one with me right now.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Shirley\",\n"
        + "		\"sentences\": [\n"
        + "			\"In that case, just tell me your phone number.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"It's 687-3452.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"大卫\",\n"
        + "		\"sentences\": [\n"
        + "			\"我这儿有一份简历。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"雪莉\",\n"
        + "		\"sentences\": [\n"
        + "			\"请问您的姓名?\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"大卫\",\n"
        + "		\"sentences\": [\n"
        + "			\"周大卫。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"雪莉\",\n"
        + "		\"sentences\": [\n"
        + "			\"啊，是的，周先生。\",\n"
        + "			\"我们一直在等着您。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"大卫\",\n"
        + "		\"sentences\": [\n"
        + "			\"这是你们要的我的全部资料，包括我的教育背景和工作经历。\",\n"
        + "			\"我现在有一个重要会议要参加先告辞了。\",\n"
        + "			\"如果你们认为我合适这个岗位的话，请通知我一声。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"雪莉\",\n"
        + "		\"sentences\": [\n"
        + "			\"没问题。\",\n"
        + "			\"给我一张名片吧，我会打电话给您。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"大卫\",\n"
        + "		\"sentences\": [\n"
        + "			\"真抱歉，我现在身上没带。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"雪莉\",\n"
        + "		\"sentences\": [\n"
        + "			\"如果那样的话，那告诉我您的电话号码好了。\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"大卫\",\n"
        + "		\"sentences\": [\n"
        + "			\"687-3452。\"\n"
        + "		]\n"
        + "	},\n"
        + " {\n"
        + "		\"speaker\": \"Rose\",\n"
        + "		\"sentences\": [\n"
        + "			\"So, how was your interview?\"\n"
        + "		]\n"
        + "	},\n"
        + " {\n"
        + "		\"speaker\": \"Tom\",\n"
        + "		\"sentences\": [\n"
        + "			\"I haven't gone to the interview yet.\",\n"
        + "			\"It's tomorrow.\",\n"
        + "			\"I'm so nervous.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Rose\",\n"
        + "		\"sentences\": [\n"
        + "			\"Don't worry.\",\n"
        + "		  \"You should do fine.\",\n"
        + "    \"You have the experience.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Tom\",\n"
        + "		\"sentences\": [\n"
        + "			\"I hope so.\"\n"
        + "		]\n"
        + "	},\n"
        + "  {\n"
        + "		\"speaker\": \"Rose\",\n"
        + "		\"sentences\": [\n"
        + "			\"Remember, they want someone who works well with people.\",\n"
        + "			\"You've got to show them how easy-going and personable you are!\"\n"
        + "		]\n"
        + "	},\n"
        + "  {\n"
        + "		\"speaker\": \"Tom\",\n"
        + "		\"sentences\": [\n"
        + "			\"Thanks.\",\n"
        + "			\"I'll keep that in mind .\"\n"
        + "		]\n"
        + "	},\n"
        + " {\n"
        + "		\"speaker\": \"罗丝\",\n"
        + "		\"sentences\": [\n"
        + "			\"那么你的面试怎么样了？\"\n"
        + "		]\n"
        + "	},\n"
        + " {\n"
        + "		\"speaker\": \"汤姆\",\n"
        + "		\"sentences\": [\n"
        + "			\"我还没去面试呢。\",\n"
        + "			\"明天面试。\",\n"
        + "			\"我好紧张。\"\n"
        + "		]\n"
        + "	},\n"
        + " {\n"
        + "		\"speaker\": \"罗丝\",\n"
        + "		\"sentences\": [\n"
        + "     \"别担心。\",\n"
        + "    \"你会做得很好的。\",\n"
        + "    \"你有经验。\"\n"
        + "		]\n"
        + "	},\n"
        + " {\n"
        + "		\"speaker\": \"汤姆\",\n"
        + "		\"sentences\": [\n"
        + "			\"希望如此。\"\n"
        + "		]\n"
        + "	},\n"
        + "{\n"
        + "		\"speaker\": \"罗丝\",\n"
        + "		\"sentences\": [\n"
        + "			\"记住，他们想找可以和别人共同工作的人。\",\n"
        + "		  \"你要向他们展示你是多么得好相处，多么地有个人魅力！\"\n"
        + "		]\n"
        + "	},\n"
        + "{\n"
        + "		\"speaker\": \"汤姆\",\n"
        + "		\"sentences\": [\n"
        + "			\"谢谢。\",\n"
        + "			\"我会牢记的。\"\n"
        + "		]\n"
        + "	},\n"
        + "  {\n"
        + "		\"speaker\": \"Ann\",\n"
        + "		\"sentences\": [\n"
        + "			\"David, I am going for an interview tomorrow.\",\n"
        + "			\"It's an American company.\",\n"
        + "			\"Can you give me an idea of what the interviewer will ask?\"\n"
        + "		]\n"
        + "	},\n"
        + "{\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"Well, they may ask you to tell them more about your educational background and your working background.\"\n"
        + "		]\n"
        + "	},\n"
        + "  {\n"
        + "		\"speaker\": \"Ann\",\n"
        + "		\"sentences\": [\n"
        + "			\"But they can see my resume.\",\n"
        + "			\"It's all in there.\"\n"
        + "		]\n"
        + "	},\n"
        + "{\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"I know, but sometimes, interviewers just want to hear you say it.\",\n"
        + "			\"Then, you may be asked for your viewpoint on why you feel you're qualified for the job, give them all your qualifications and how you think they'll fit with the position you are applying for.\",\n"
        + "			\"Questions like \\\"What sort of experience do you have and what are your goals?\\\" are closely related to this.\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"Ann\",\n"
        + "		\"sentences\": [\n"
        + "			\"OK, what else?\"\n"
        + "		]\n"
        + "	},\n"
        + "	{\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"They may want to know how the company will benefit and why they should hire you.\"\n"
        + "		]\n"
        + "	},\n"
        + "  {\n"
        + "		\"speaker\": \"Ann\",\n"
        + "		\"sentences\": [\n"
        + "			\"I see, what about the salary?\"\n"
        + "		]\n"
        + "	},\n"
        + "  {\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"Umm, they may ask you what kind of salary you have in mind, or simply tell you what the company is offering.\",\n"
        + "			\"I think the latter is more likely for a starting position, especially if you have little experience.\",\n"
        + "			\"And if they are seriously considering hiring you, they will probably want to know when you would be able to start working.\",\n"
        + "			\"And basically , that's about it.\"\n"
        + "		]\n"
        + "	},\n"
        + "  {\n"
        + "		\"speaker\": \"Ann\",\n"
        + "		\"sentences\": [\n"
        + "			\"Oh, thanks, David.\",\n"
        + "			\"You've helped me a lot.\"\n"
        + "		]\n"
        + "	},\n"
        + "{\n"
        + "		\"speaker\": \"David\",\n"
        + "		\"sentences\": [\n"
        + "			\"Anytime.\",\n"
        + "			\"Let me know how it works out.\"\n"
        + "		]\n"
        + "	}\n"
        + "]";

    List<DialogDualEntity> dialogDualEntities = convertJsonToDialogDualEntities(
      jsonString);

    if (dialogDualEntities != null) {
      dialogDualEntities.forEach(item -> {
        System.out.println("Host A En: " + item.getHostAEn());
        System.out.println("Host A Cn: " + item.getHostACn());
        System.out.println("Content A En: " + item.getContentAEn());
        System.out.println("Content A Cn: " + item.getContentACn());
        System.out.println("Host B En: " + item.getHostBEn());
        System.out.println("Host B Cn: " + item.getHostBCn());
        System.out.println("Content B En: " + item.getContentBEn());
        System.out.println("Content B Cn: " + item.getContentBCn());
        System.out.println("--------------------");
      });
    } else {
      System.out.println("Failed to convert JSON to List<DialogDualEntity>.");
    }

  }

  public static List<DialogDualEntity> convertJsonToDialogDualEntities(
    String jsonString) {
    if (jsonString == null || jsonString.trim().isEmpty()) {
      return null;
    }
    JSONArray jsonArray = JSONUtil.parseArray(jsonString);
    List<DialogDualEntity> dialogDualEntities = new ArrayList<>();
    DialogDualEntity currentEntity = new DialogDualEntity();
    for (int i = 0; i < jsonArray.size(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      String speaker = jsonObject.getStr("speaker");
      List<String> sentences = jsonObject.getBeanList("sentences",
        String.class);
      String content = String.join(" ", sentences);
      if (speaker.equalsIgnoreCase("A") || speaker.equalsIgnoreCase("Mary")
        || speaker.equalsIgnoreCase("Ann")) {
        if (currentEntity.getHostAEn() == null) {
          currentEntity.setHostAEn(speaker);
        } else if (currentEntity.getHostACn() == null) {
          currentEntity.setHostACn(speaker);
        }
        if (currentEntity.getContentAEn() == null
          || currentEntity.getContentAEn().isEmpty()) {
          currentEntity.setContentAEn(content);
        } else {
          currentEntity.setContentAEn(
            currentEntity.getContentAEn() + " " + content);
        }

      } else if (speaker.equalsIgnoreCase("B") || speaker.equalsIgnoreCase(
        "Tom") || speaker.equalsIgnoreCase("Shirley")
        || speaker.equalsIgnoreCase("Rose") || speaker.equalsIgnoreCase("David")
        || speaker.equalsIgnoreCase("雪莉") || speaker.equalsIgnoreCase("汤姆")
        || speaker.equalsIgnoreCase("罗丝") || speaker.equalsIgnoreCase("大卫")
        || speaker.equalsIgnoreCase("安")) {
        if (currentEntity.getHostBEn() == null) {
          currentEntity.setHostBEn(speaker);
        } else if (currentEntity.getHostBCn() == null) {
          currentEntity.setHostBCn(speaker);
        }
        if (currentEntity.getContentBEn() == null
          || currentEntity.getContentBEn().isEmpty()) {
          currentEntity.setContentBEn(content);
        } else {
          currentEntity.setContentBEn(
            currentEntity.getContentBEn() + " " + content);
        }
      }
      if (i == jsonArray.size() - 1 || (i + 1 < jsonArray.size()
        && !jsonArray.getJSONObject(i + 1).getStr("speaker")
        .equalsIgnoreCase(speaker))) {
        dialogDualEntities.add(currentEntity);
        currentEntity = new DialogDualEntity();
      }
    }
    return dialogDualEntities;
  }
}
