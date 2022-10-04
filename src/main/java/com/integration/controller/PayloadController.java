package com.integration.controller;

import com.integration.jira.JiraFeignClient;
import com.integration.jira.JiraVo;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private final String JIRA_ADDRESS = "https://freeskymax.atlassian.net/rest/api/2/issue/{jiraNo}/comment";

    @Autowired
    private JiraFeignClient jiraFeignClient;

    @PostMapping("/test")
    public String test() {
        log.info("--test jira github integration");
        return "test jira github integration";
    }

    @PostMapping("/operation")
    public String push(@RequestBody Object payload) {

        if (payload instanceof LinkedHashMap) {
            LinkedHashMap map = (LinkedHashMap) payload;

            return handlePayload(map);
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
        log.info("-- body: " + body);

        String htmlUrl = (String) pullRequest.get(HTML_URL);

        if (StringUtils.isEmpty(body)) {
            return "pull request has no comments";
        }
        int pos = body.indexOf(" ");
        if (pos > 0) {
            String jiraNo = body.substring(0, pos);
            String comments = null;
            if (pos < body.length()) {
                comments = body.substring(pos + 1);
            }
            log.info(jiraNo);
            log.info(comments);

            sendToJira(jiraNo, comments, htmlUrl);
        }

        return "Pull Request successfully handled!";
    }

    protected void sendToJira(String jiraNo, String comments, String htmlUrl) {
        Map<String, String> values = new HashMap<>();
        values.put("jiraNo", jiraNo);

        String jiraUrl = StringSubstitutor.replace(JIRA_ADDRESS, values, "{", "}");
        log.info(jiraUrl);

        JiraVo vo = new JiraVo();
        vo.setBody(comments + " " + htmlUrl);
        jiraFeignClient.addComments(vo, jiraNo);
        log.info("--JIRA: add comments successfully!");
    }

}
