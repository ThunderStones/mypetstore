package org.csu.mypetstore.controller;

import jdk.jfr.internal.tool.Main;
import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.persistence.AccountMapper;
import org.csu.mypetstore.service.AccountService;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/account")
@SessionAttributes(names = {"account", "myList", "isAuthenticated"})
public class AccountController {
    private static final String SIGNON_FORM = "/account/signonForm";
    private static final String MAIN = "/catalog/main";
    private static final String EDIT_ACCOUNT_FORM = "/account/editAccountForm";
    private static final String REGISTER_FORM = "/account/newAccountForm";
    private Account account = new Account();
    private boolean isAuthenticated = false;
    private List<Product> myList;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CatalogService catalogService;
    @GetMapping("/signonForm")
    public String viewSignonForm() {
        return SIGNON_FORM;
    }

    @PostMapping("/signon")
    public String signon(Account account, Model model) {
        System.out.println(this.account.getUsername());
        account = accountService.getAccount(account.getUsername(), account.getPassword());
        if (account == null) {
            model.addAttribute("msg", "Invalid username or password.  Signon failed.");
            clear();
            model.addAttribute("account", null);
            model.addAttribute("myList", null);
            model.addAttribute("isAuthenticated", false);
            return SIGNON_FORM;
        } else {
            account.setPassword(null);
            myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
            isAuthenticated = true;
            model.addAttribute("account", account);
            model.addAttribute("myList", myList);
            model.addAttribute("isAuthenticated", true);
            return MAIN;
        }
    }

    @GetMapping("/registerForm")
    public String viewRegisterForm(Model model) {
        model.addAttribute("account", account);
        return REGISTER_FORM;
    }

    @PostMapping("/register")
    public String register(Account account, Model model) {
        accountService.insertAccount(account);
        account = accountService.getAccount(account.getUsername());
        myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
        isAuthenticated = true;
        model.addAttribute("account", account);
        model.addAttribute("myList", myList);
        model.addAttribute("isAuthenticated", isAuthenticated);
        return MAIN;
    }

    @GetMapping("signOut")
    public String signOut(Model model) {
        clear();
        model.addAttribute("account", account);
        model.addAttribute("myList", myList);
        model.addAttribute("isAuthenticated", isAuthenticated);
        return MAIN;
    }

    @GetMapping("/editAccountForm")
    public String viewEditAccountForm() {
        return EDIT_ACCOUNT_FORM;
    }

    @PostMapping("/editAccount")
    public String editAccount(Account account, Model model) {
        accountService.updateAccount(account);
        account = accountService.getAccount(account.getUsername());
        myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
        model.addAttribute("account", account);
        model.addAttribute("myList", myList);
        model.addAttribute("isAuthenticated", isAuthenticated);
        return EDIT_ACCOUNT_FORM;
    }

    public void clear() {
        account = new Account();
        isAuthenticated = false;
        myList = null;
    }
}
