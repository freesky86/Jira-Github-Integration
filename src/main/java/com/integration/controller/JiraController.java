package com.integration.controller;

import com.integration.jira.JiraFeignClient;
import com.integration.jira.JiraVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/jira")
public class JiraController {

    @Autowired
    private JiraFeignClient jiraFeignClient;

    @PostMapping("/{jiraNo}/addcomment")
    public String addComment(@RequestBody JiraVo jiraVo, @PathVariable String jiraNo) {
        jiraFeignClient.addComments(jiraVo,jiraNo);

        return "add comment successfully!";
    }
}
