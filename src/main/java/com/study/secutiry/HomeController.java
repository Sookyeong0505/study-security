package com.study.secutiry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String home() {
        String name = "Home";
        log.trace(name);
        log.debug(name);
        log.info(name);
        log.warn(name);
        log.error(name);
        return "home";
    }
}
