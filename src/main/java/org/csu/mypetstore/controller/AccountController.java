package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.service.AccountService;
import org.csu.mypetstore.service.CartService;
import org.csu.mypetstore.service.CatalogService;
import org.csu.mypetstore.util.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/account")
@SessionAttributes(names = {"account", "myList", "isAuthenticated", "cart"})
@SessionScope
public class AccountController {
    private static final String SIGNON_FORM = "account/signonForm";
    private static final String MAIN = "catalog/main";
    private static final String EDIT_ACCOUNT_FORM = "account/editAccountForm";
    private static final String REGISTER_FORM = "account/newAccountForm";
    private Account account = new Account();
    private boolean isAuthenticated = false;
    private List<Product> myList;

    private String captchaCode;


    @Autowired
    private AccountService accountService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CartService cartService;


    @GetMapping("/signonForm")
    public String viewSignonForm(Model model) {
        List<String> captchaInfo = CaptchaUtil.generateCaptcha();
        captchaCode = captchaInfo.get(0);
        String path = captchaInfo.get(1);
        model.addAttribute("path", path);
        return SIGNON_FORM;
    }

    @PostMapping("/signon")
    public String signon(Account account, Model model, String captchaCode) {
        System.out.println(this.account.getUsername());
        if (!this.captchaCode.equalsIgnoreCase(captchaCode)) {
            model.addAttribute("msg", "Error: Captcha Code is not correct!");
            return viewSignonForm(model);
        }
        account = accountService.getAccount(account.getUsername(), DigestUtils.md5DigestAsHex(account.getPassword().getBytes()));
        if (account == null) {
            model.addAttribute("msg", "Invalid username or password.  Signon failed.");
            clear();
            model.addAttribute("account", null);
            model.addAttribute("myList", null);
            model.addAttribute("isAuthenticated", false);
            return "redirect:/account/signonForm";
        } else {
            account.setPassword(null);
            myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
            isAuthenticated = true;
            model.addAttribute("account", account);
            model.addAttribute("myList", myList);
            model.addAttribute("isAuthenticated", true);
            Cart cart = cartService.getCart(account.getUsername());
            model.addAttribute("cart", cart);
            return MAIN;
        }
    }

    @GetMapping("/registerForm")
    public String viewRegisterForm(Model model) {
        List<String> captchaInfo = CaptchaUtil.generateCaptcha();
        captchaCode = captchaInfo.get(0);
        String path = captchaInfo.get(1);
        model.addAttribute("path", path);
        model.addAttribute("account", account);
        return REGISTER_FORM;
    }

    @PostMapping("/register")
    public String register(Account account, Model model, String captchaCode, String repeatedPassword) {
        if (!this.captchaCode.equalsIgnoreCase(captchaCode)) {
            model.addAttribute("msg", "Error: Captcha Code is not correct!");
            model.addAttribute("account", account);
            return viewRegisterForm(model);
        }
        if (!Objects.equals(account.getPassword(), repeatedPassword)) {
            model.addAttribute("msg", "Error: Password and  repeat password are not same!");
            return viewRegisterForm(model);
        }
        account.setPassword(DigestUtils.md5DigestAsHex(account.getPassword().getBytes()));
        accountService.insertAccount(account);
        cartService.initCart(account.getUsername());
        cartService.updateCart(account.getUsername(), new Cart());
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
    public String editAccount(Account account, Model model, String listOption, String bannerOption) {
        account.setListOption(listOption != null);
        account.setBannerOption(bannerOption != null);
        if (!account.getPassword().isEmpty()) {
            account.setPassword(DigestUtils.md5DigestAsHex(account.getPassword().getBytes()));
        }
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



    /**
     *
     * @return the path of captcha image
     */

    @GetMapping("/usernameExist")
    @ResponseBody
    public boolean usernameExist(String username) {
        return accountService.getAccount(username) != null;
    }

}
