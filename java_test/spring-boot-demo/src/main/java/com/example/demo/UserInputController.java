package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserInputController {
    @Autowired
    private UserInputRepository repository;

    @GetMapping("/")
    public String index(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10; // 每页显示10条
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<UserInput> inputPage = repository.findAll(pageable);
        model.addAttribute("inputPage", inputPage);
        return "index";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam String content) {
        repository.save(new UserInput(content));
        return "redirect:/";
    }
}
