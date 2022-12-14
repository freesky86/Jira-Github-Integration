package com.integration.jira;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="jira-rest-api", url="${jira.server}", configuration = FeignClientConfiguration.class)
public interface JiraFeignClient {
    @PostMapping("/{jiraNo}/comment")
    Object addComments(@RequestBody JiraVo comments, @PathVariable String jiraNo);
}
