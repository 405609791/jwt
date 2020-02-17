package fun.codefarmer.jwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ @ClassName HelloController
 * @ Descriotion TODO
 * @ Author admin
 * @ Date 2020/2/16 20:00
 **/
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello jwt";
    }

    @GetMapping("/admin")
    public String admin() {
        return "hello jwt admin";
    }
}
