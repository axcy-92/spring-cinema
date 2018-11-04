package com.axcy.springcinema.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Aleksei_Cherniavskii
 */
@Controller
public class IndexController {

    @RequestMapping({"/"})
    public String printWelcome(ModelMap model) {
        model.addAttribute("title", "Spring Cinema");
        return "index";
    }

}
