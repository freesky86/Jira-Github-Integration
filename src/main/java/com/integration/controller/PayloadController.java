package com.integration.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/github")
public class PayloadController {

    @PostMapping("/test")
    public String test() {
        log.info("--test jira github integration");
        return "test jira github integration";
    }

    @PostMapping("/operation")
    public Object push(@RequestBody Object payload) {
        log.info("-- push: " + payload.getClass());
        log.info(JSON.toJSONString(payload));
        return payload;
    }
}
