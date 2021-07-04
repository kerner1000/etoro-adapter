package com.github.kerner1000.etoro.stats.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChartController {

    @GetMapping("/sector-breakdown")
    public String sector()  {
        return "sector-index.html";
    }

    @GetMapping("/industry-breakdown")
    public String industry()  {
        return "industry-index.html";
    }

}
