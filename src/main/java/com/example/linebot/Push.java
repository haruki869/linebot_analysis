package com.example.linebot;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

/**
 * @author Haruki-Ueno
 *         Created by haruki on 2018/06/02.
 * @since 2018/06/02
 */
@RestController
public class Push {

    private final static Logger log = LoggerFactory.getLogger(Push.class);

    private String userId = "REPLACE THIS MESSAGE FOR YOUR APPLICATION";

    private final LineMessagingClient client;

    @Autowired
    public Push(LineMessagingClient client) {
        this.client = client;
    }

    @GetMapping("timetone")
    //@Scheduled(cron = "0 */1 * * * *", zone = "Asia/Tokyo")
    public String pushTimeTone() {
        String text = DateTimeFormatter.ofPattern("a K:mm").format(LocalDateTime.now());
        try {
            PushMessage pmsg = new PushMessage(userId, new TextMessage(text));
            BotApiResponse res = client.pushMessage(pmsg).get();
            log.info("Sent messages: {}", res);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return text;
    }

    @GetMapping("confirm")
    public String pushConfirm() {
        String text = "I have a question.";
        try {
            Message msg = new TemplateMessage(text,
                                                     new ConfirmTemplate("いいかんじ？",
                                                                                new PostbackAction("おけまる", "CY"),
                                                                                new PostbackAction("やばたん", "CN")));
            PushMessage pmsg = new PushMessage(userId, msg);
            BotApiResponse res = client.pushMessage(pmsg).get();
            log.info("Sent messages: {}", res);
        } catch (InterruptedException | ExecutionException e) {
            new RuntimeException(e);
        }
        return text;
    }

    @GetMapping("test")
    public String hello(HttpServletRequest request) {
        return "Get From " + request.getRequestURL();
    }
}
