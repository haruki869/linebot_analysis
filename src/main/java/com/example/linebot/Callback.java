package com.example.linebot;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ResponseCache;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Haruki-Ueno
 *         Created by haruki on 2018/06/02.
 * @since 2018/06/02
 */
@LineMessageHandler
public class Callback {

    private static final Logger log = LoggerFactory.getLogger(Callback.class);

    /**
     * ふぉろーされたとき
     * @param event
     * @return
     */
    @EventMapping
    public TextMessage handleFollow(FollowEvent event) {
        String userId = event.getSource().getUserId();
        return reply("your userId is " + userId);
    }

    /**
     * はなしかけられたとき
     * @param event
     * @return
     */
    @EventMapping
    public Message handleMessage(MessageEvent<TextMessageContent> event) {
        TextMessageContent content = event.getMessage();
        String text = content.getText();
        return replyAnalyzing(text);
        /*
        switch (text) {
            case "やあ":
                return greet();
            case "おみくじ":
                return replyOmikuji();
            default:
                return replyAnalyzing(text);
        }
        */
    }

    @EventMapping
    public Message handlePostBack(PostbackEvent event) {
        String actionLabel = event.getPostbackContent().getData();
        switch (actionLabel) {
            case "CY":
                return reply("いいね");
            case "CN":
                return reply("つらたん");
            default:
                return reply("?");
        }
    }

    private ImageMessage replyOmikuji() {
        int ranNum = new Random().nextInt(3);
        switch (ranNum) {
            case 2:
                return replyImage("https://3.bp.blogspot.com/-vQSPQf-ytsc/T3K7QM3qaQI/AAAAAAAAE-s/6SB2q7ltxwg/s1600/omikuji_daikichi.png");
            case 1:
                return replyImage("https://2.bp.blogspot.com/-27IG0CNV-ZE/VKYfn_1-ycI/AAAAAAAAqXw/fr6Y72lOP9s/s400/omikuji_kichi.png");
            case 0:
            default:
                return replyImage("https://4.bp.blogspot.com/-qCfF4H7YOvE/T3K7R5ZjQVI/AAAAAAAAE-4/Hd1u2tzMG3Q/s1600/omikuji_kyou.png");
        }
    }

    private ImageMessage replyImage(String url) {
        // 本来は、第一引数が実際の画像URL、第二画像がサムネイルのurl
        return new ImageMessage(url, url);
    }

    private TextMessage greet() {
        LocalTime lt = LocalTime.now();
        int hour = lt.getHour();
        if (hour >= 17) {
            return reply("こんばんは！");
        }
        if (hour >= 11) {
            return reply("こんにちは！");
        }
        return reply("おはよう！");
    }

    private TextMessage reply(String message) {
        return new TextMessage(message);
    }

    private TextMessage replyAnalyzing(String message) {
        List<String> ms = analyze(message);
        //List<TextMessage> tms = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        ms.stream().forEach(m -> builder.append(m + "       "));
        //ms.stream().forEach(m -> tms.add(new TextMessage(m)));
        return new TextMessage(builder.toString());
    }

    private List<String> analyze(String message) {
        Tokenizer tokenizer = new Tokenizer();
        List<String> ms = new ArrayList<>();
        List<Token> tokens = tokenizer.tokenize(message);
        tokens
                .stream()
                .map(e -> e.getSurface()+ " "+ e.getAllFeatures())
                .forEach(t -> ms.add(t));
        return ms;
    }

}
