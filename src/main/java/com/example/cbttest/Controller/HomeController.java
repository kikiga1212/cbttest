package com.example.cbttest.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 시작페이지 처리
 */
@Controller
@RequiredArgsConstructor
public class HomeController {
    //대시보드에 데이터

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }
}
