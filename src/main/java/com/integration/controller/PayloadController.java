package com.integration.controller;

import com.integration.jira.JiraFeignClient;
import com.integration.jira.JiraVo;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

/**
 * THis is used to receive the payload from github.
 */

@Slf4j
@RestController
@RequestMapping("/github")
public class PayloadController {

    private final String PULL_REQUEST = "pull_request";
    private final String BODY = "body";
    private final String HTML_URL = "html_url";
    private final String USER = "user";
    private final String LOGIN = "login";

    @Autowired
    private JiraFeignClient jiraFeignClient;

    @PostMapping("/test")
    public String test() {
        log.info("--test jira github integration");
        return "test jira github integration";
    }

    @PostMapping("/operation")
    public String pullRequest(@RequestBody Object payload) {
        if (payload instanceof LinkedHashMap) {
            LinkedHashMap map = (LinkedHashMap) payload;
            String result = handlePayload(map);
            return result;
        }
        return "This is unknown request!";
    }

    protected String handlePayload(LinkedHashMap map) {
        Object obj = map.get(PULL_REQUEST);
        if (null == obj) {
            return "This is not pull request!";
        }

        LinkedHashMap pullRequest = (LinkedHashMap) obj;
        String body = (String) pullRequest.get(BODY);
        String htmlUrl = (String) pullRequest.get(HTML_URL);
        LinkedHashMap userMap = (LinkedHashMap) pullRequest.get(USER);
        String login = (String) userMap.get(LOGIN);

        if (StringUtils.isEmpty(body)) {
            return "pull request has no comments: [" + login +"]: " + htmlUrl;
        }
        int pos = body.indexOf(" ");
        if (pos > 0) {
            String jiraNo = body.substring(0, pos);
            String comments = null;
            if (pos < body.length()) {
                comments = body.substring(pos + 1).trim();
            }

            return sendToJira(jiraNo, comments, htmlUrl, login);
        }

        return "Pull Request has NO comments";
    }

    protected String sendToJira(String jiraNo, String comments, String htmlUrl, String login) {
        JiraVo vo = new JiraVo();
        vo.setBody("[" + login + "]: " + comments + " " + htmlUrl);
        jiraFeignClient.addComments(vo, jiraNo);
        log.info("--JIRA: add comments successfully!");

        return "Pull Request successfully handled!";
    }

}
