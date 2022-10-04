package com.integration.controller;

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

}
