package org.csu.mypetstore.controller.back;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.service.AccountService;
import org.csu.mypetstore.service.CatalogService;
import org.csu.mypetstore.util.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Controller
@RequestMapping("/manager/account")
@SessionScope
public class BackAccountController {
    @Autowired
    private AccountService accountService;

    private String captchaCode;


    @GetMapping("/getAllAccount")
    public String getAllAccount(Model model) {
        List<Account> accountList = accountService.getAllAccount();
        model.addAttribute("accountList", accountList);
        return "management/account/account";
    }

    @GetMapping("/searchAccount")
    public String searchAccount(String keyword, Model model) {
        List<Account> accountList = accountService.searchAccount(keyword);
        model.addAttribute("accountList", accountList);
        return "management/account/account";
    }

    @GetMapping("/editAccount/{username}")
    public String editAccount(@PathVariable("username") String username, Model model) {
        Account account = accountService.getAccount(username);
        model.addAttribute("account", account);
        return "management/account/editAccount";
    }

    @RequestMapping("/loginForm")
    public String adminLogin(Model model) {
        List<String> captchaInfo = CaptchaUtil.generateCaptcha();
        captchaCode = captchaInfo.get(0);
        System.out.println(captchaCode);
        model.addAttribute("captchaPath", "../" + captchaInfo.get(1));
        return "management/login";
    }

    @RequestMapping("/login")
    public String login(String username, String password, String captcha, Model model) {
        if (!captchaCode.equalsIgnoreCase(captcha)) {
            model.addAttribute("msg", "Error : Incorrect CaptchaCode.");
            return "forward:/manager/account/loginForm";
        }
        if ("admin".equals(username) && "123456".equals(password)) {
            return "management/account/main";
        } else {
            model.addAttribute("msg", "Error: Incorrect Username or Password.");
            return "forward:/manager/account/loginForm";
        }
    }

    @PostMapping("/editAccount")
    public String editAccount(Account account, Model model, String listOption, String bannerOption) {
        account.setListOption(listOption != null);
        account.setBannerOption(bannerOption != null);
        if (!account.getPassword().isEmpty()) {
            account.setPassword(DigestUtils.md5DigestAsHex(account.getPassword().getBytes()));
        }
        accountService.updateAccount(account);
        model.addAttribute("account", account);
        return getAllAccount(model);
    }
}
