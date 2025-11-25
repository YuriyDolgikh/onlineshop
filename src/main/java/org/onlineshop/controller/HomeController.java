package org.onlineshop.controller;

import lombok.Generated;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Generated
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}