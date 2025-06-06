package com.coderdream.util.bbc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtitleGenerator {

    private static final int MAX_LINE_LENGTH = 70;
    private static final Pattern SPEAKER_PATTERN = Pattern.compile("^(Neil|Catherine|Phil Hall)\\b.*");
    private static final Pattern SENTENCE_END_PATTERN = Pattern.compile("(?<=[.?!])\\s+");
    private static final Pattern COMMA_PATTERN = Pattern.compile("(?<=[,])\\s+");
    private static final String TRANSCRIPT_MARKER = "This is not a word-for-word transcript";
    private static final String VOCABULARY_MARKER = "Vocabulary";


    public static List<String> generateSubtitles(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        String[] rawLines = text.split("\\n");
        boolean startProcessing = false;
        StringBuilder speakerBuffer = new StringBuilder(); // 用于存储同一个说话人的内容
        String currentSpeaker = null; // 当前说话人

        for (String rawLine : rawLines) {
            String line = rawLine.trim();

            // 找到 "This is not a word-for-word transcript" 这行，之后的内容才开始处理
            if (!startProcessing) {
                if (line.equals(TRANSCRIPT_MARKER)) {
                    startProcessing = true;
                }
                continue; // 跳过此行之前的
            }

             // 如果遇到 "Vocabulary"，则停止处理
            if (line.equals(VOCABULARY_MARKER)) {
                if(speakerBuffer.length() > 0) {
                     splitAndAddLines(speakerBuffer.toString(), lines);
                }
                break;
            }

            // 跳过空行
            if (line.isEmpty()) {
                continue;
            }


            Matcher speakerMatcher = SPEAKER_PATTERN.matcher(line);
            if (speakerMatcher.matches()) {
                 // 提取说话人
                 String speaker = speakerMatcher.group(1);

                 // 如果有新的说话人或者当前有缓存内容
                if(!speaker.equals(currentSpeaker) && speakerBuffer.length() > 0) {
                     // 处理之前的说话人内容
                    splitAndAddLines(speakerBuffer.toString(),lines);
                    speakerBuffer.setLength(0);
                }
                //设置新的说话人
                currentSpeaker = speaker;
                // 追加说话内容到缓存
                speakerBuffer.append(line.substring(speakerMatcher.end()).trim()).append(" ");

            } else {
                //处理非说话人内容
                 if(speakerBuffer.length() > 0) {
                    splitAndAddLines(speakerBuffer.toString(), lines);
                    speakerBuffer.setLength(0);
                }

                // 使用句号、问号、感叹号分割句子
                String[] sentences = SENTENCE_END_PATTERN.split(line);
                for (String sentence : sentences) {
                    sentence = sentence.trim();
                    if (sentence.isEmpty()) {
                        continue;
                    }
                    // 处理过长的句子
                    splitLongSentence(sentence, lines);
                }

                currentSpeaker = null;
            }
        }

        // 处理最后缓存的内容
        if(speakerBuffer.length() > 0) {
             splitAndAddLines(speakerBuffer.toString(), lines);
        }
        return lines;
    }


    private static void splitAndAddLines(String text, List<String> lines){
        String[] sentences = SENTENCE_END_PATTERN.split(text);
        for (String sentence : sentences) {
            sentence = sentence.trim();
             if(sentence.isEmpty()){
                 continue;
             }
            splitLongSentence(sentence,lines);
        }
    }

    private static void splitLongSentence(String sentence, List<String> lines) {
        if (sentence.length() <= MAX_LINE_LENGTH) {
            lines.add(sentence);
        } else {
            String[] parts = COMMA_PATTERN.split(sentence);
            StringBuilder currentLine = new StringBuilder();
            for (String part : parts) {
                part = part.trim();
                 if(part.isEmpty()){
                     continue;
                 }
                if (currentLine.length() == 0) {
                    currentLine.append(part);
                } else if (currentLine.length() + part.length() + 1 <= MAX_LINE_LENGTH) {
                    currentLine.append(" ").append(part);
                } else {
                    lines.add(currentLine.toString());
                    currentLine.setLength(0);
                    currentLine.append(part);
                }
            }
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }
    }


    public static void main(String[] args) {
        String text = """
                BBC LEARNING ENGLISH
                6 Minute English
                Are smartphones killing cameras?

                This is not a word-for-word transcript

                Neil
                Hello and welcome to 6 Minute English. I'm Neil.

                Catherine
                And I'm Catherine. Hello!

                Neil
                Now, Catherine, say cheese.

                Catherine
                Cheeeese.

                Neil
                Thank you, a little souvenir of our time together.

                Catherine
                Let's have a look… Hang on a minute. You just took a selfie, I wasn't even in the picture.

                Neil
                Ah, well, that's the magic of the smartphone, two cameras! You know, that's not something
                you can do with a traditional camera. I mean, do you even have a separate camera these
                days?

                Catherine
                I do actually. It's in a cupboard somewhere at home.

                Neil
                Well, that is the topic of this programme. Have traditional cameras been completely
                replaced by smartphones, or to put it another way, have cameras been made obsolete by
                the smartphone?

                Catherine
                Interesting question. But before we get into this topic, how about a question for our
                listeners?

                Neil
                Of course. We are certainly in the digital age of photography but when was the first digital
                camera phone released? Was it:
                a) 2000
                b) 2004 or
                c) 2007?
                What do you think?

                Catherine
                Well, I actually know this one, so I'm going to be fair and keep it to myself.

                Neil
                OK, well, listen out for the answer at the end of the programme. There are different kinds
                of cameras available today. There are compact cameras, which are small and mostly
                automatic and usually come with a fixed lens.

                Catherine
                That's right. And then there are SLRs and DSLRs which are bigger, and you can change the
                lenses on these cameras and they allow for a lot of manual control.

                Neil
                And there are also mirrorless cameras, which are a cross between compact cameras and
                DSLRs. They are small like a compact camera but you can also use the same lenses on them
                that you can use on DSLRs.

                Catherine
                And of course, there are the cameras on smartphones, and these are convenient and
                they're becoming increasingly sophisticated.

                Neil
                Phil Hall is the editor of Tech Radar magazine. He was asked on the BBC programme You
                and Yours if he thought smartphones would make other cameras obsolete. What is his
                opinion?

                Phil Hall
                I don't think so. I think while compact camera sales have really sort of dropped off a cliff,
                it's the lower end, cheap compacts where people have opted for a smartphone and I think
                manufacturers are looking at the more higher end premium cameras, high-end compacts,
                DSLRs, which are the ones you can attach lenses to, mirrorless cameras. So, the market's
                changing. And I don't think there'll be a time soon, yet, that… the smartphone will take
                over the camera completely.

                Neil
                So does Phil think smartphones will kill the camera?

                Catherine
                In a word, no. He does say that sales of cheap compact cameras have dropped off a cliff.
                This rather dramatic expression describes a very big fall in sales.

                Neil
                This is because the kind of consumers who would choose a compact camera are now
                opting for the camera on their smartphone. When you opt for something you choose it
                rather than something else.

                Catherine
                For people who want a quick, easy to use and convenient way to take reasonable quality
                photos, compact cameras used to be the best choice – but now it's the smartphone.

                Neil
                So camera makers are now moving to the more high-end market, the DSLRs and mirrorless
                cameras. So who is still buying these more expensive cameras? Here's Phil Hall again.

                Phil Hall
                I think it's… some of it is people who are picking up a smartphone and sort of getting into
                photography that way and that's a really great first step into photography and I think people
                are probably, sometimes, getting a bit frustrated with the quality once they sort of start
                pushing their creative skills and then looking to see what's the next rung up so it's people
                wanting to broaden their creative skills a bit.

                Neil
                Who does he say might be buying cameras?

                Catherine
                He says that people who are getting into photography might get frustrated with the
                quality of smartphones.

                Neil
                Getting into something means becoming very interested in it.

                Catherine
                And if you are frustrated with something it means you are disappointed with it. You are
                not happy with it.

                Neil
                So people who have got into photography with a smartphone but are frustrated with its
                limitations and want to be more creative are going to the next level. They are moving up,
                they are, as Phil said taking 'the next rung up'.

                Catherine
                Now, a rung is the horizontal step of a ladder, so the expression taking the next rung
                up is a way to describe doing something at a higher level.

                Neil
                Now, talking of higher levels, did you get this week's quiz question right? The question was:
                When was the first phone with a digital camera released? Was it 2000, 2004 or 2007?
                The first phone with a digital camera was released in 2000. Now, to take us up to the end
                of the programme, let's look at the vocabulary again.

                Catherine
                First we had the adjective obsolete which describes something that has been replaced and
                is no longer the first choice.

                Neil
                When the expression to drop off a cliff is used about, for example, sales numbers, it
                means sales have fallen significantly over a short period of time.

                Catherine
                To opt for something means to choose something and when you become very interested
                in an activity you can say that you get into it.

                Neil
                If you are trying to do something and you can't do it because you don't have the skill or the
                equipment you are using is not right or not good enough, you can become frustrated.

                Catherine
                And developing your skills to a higher level can be described as taking the next rung up.

                Neil
                Right, that's all from us from us in this programme. Do join us again next time and don't
                forget that in the meantime you can find us on Instagram, Facebook, Twitter, YouTube and
                of course our website bbclearningenglish.com. See you soon. Goodbye.

                Catherine
                Bye!


                Vocabulary

                obsolete
                something that has been replaced and is no longer the first choice

                to drop off a cliff
                (metaphor) used about, for example, sales numbers, it means sales have fallen significantly over a short period of time

                opt for something
                choose something

                get into something
                become very interested in an activity

                frustrated with something
                disappointed with something

                take the next rung up
                do something at a higher level
                """;


        List<String> subtitles = generateSubtitles(text);
        for (String subtitle : subtitles) {
            System.out.println(subtitle);
        }
    }
}
