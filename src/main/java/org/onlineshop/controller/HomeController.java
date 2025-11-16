package org.onlineshop.controller;

import lombok.Generated;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Generated
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/modal/{name}")
    public String getModal(@PathVariable String name) {
        return "modal/" + name;
    }
}