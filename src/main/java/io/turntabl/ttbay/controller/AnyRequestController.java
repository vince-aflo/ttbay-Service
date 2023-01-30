package io.turntabl.ttbay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class AnyRequestController {
// this is just a test route
    @GetMapping("/")
    public String hello(){
        System.out.println("--------------------");
        return "<h1>Hello</>";
    }
}
