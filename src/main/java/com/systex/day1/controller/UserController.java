package com.systex.day1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.systex.day1.model.User;
import com.systex.day1.repository.UserRepository;
import com.systex.day1.sercurity.PasswordBCrypt;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String showHomePage() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, String username, String password, Model model) {
        User user = userRepository.findByUsername(username);
        if (user == null || !PasswordBCrypt.matchesPassword(password, user.getPassword())) {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "login";
        }
        request.getSession().setAttribute("user", user);
        return "redirect:/lottery";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(String username, String password, Model model) {
        //1.檢查是否有重複的帳號
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "帳號已存在");
            return "register";
        }
        
        //2.密碼加密
        String encodedPassword = PasswordBCrypt.encryptPassword(password);
        
        //3.新增用戶到資料庫
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword); 
        userRepository.save(user);

        return "redirect:/login";
    }
    
    @PostMapping("/user/logout")
    public String logout(HttpServletRequest request) {
        //無效化session
        request.getSession().invalidate();
        return "redirect:/login";
    }

    
    @GetMapping("/lottery")
    public String showLotteryPage() {
    	return "lottery"; 
    }
}
