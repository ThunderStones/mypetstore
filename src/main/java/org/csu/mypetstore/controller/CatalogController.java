package org.csu.mypetstore.controller;


import org.csu.mypetstore.domain.Category;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/catalog")
public class CatalogController {
    private static final String MAIN = "catalog/main";
    private static final String CATEGORY = "catalog/category";
    private static final String PRODUCT = "catalog/product";
    private static final String SEARCH = "catalog/searchProducts";
    private static final String ITEM = "catalog/item";

    @Autowired
    private CatalogService catalogService;


    @GetMapping("/viewMain")
    public String viewMain() {
        return MAIN;
    }

    @GetMapping("/viewCategory")
    public String viewCategory(String categoryId, Model model) {
        if (categoryId != null) {
            Category category = catalogService.getCategory(categoryId);
            List<Product> productList = catalogService.getProductListByCategory(categoryId);
            model.addAttribute("category", category);
            model.addAttribute("productList", productList);
            return CATEGORY;
        }
        return MAIN;
    }

    @GetMapping("/viewProduct")
    public String viewProduct(String productId, Model model) {
        if (productId != null) {
            List<Item> itemList = catalogService.getItemListByProduct(productId);
            Product product = catalogService.getProduct(productId);
            model.addAttribute("itemList", itemList);
            model.addAttribute("product", product);
            return PRODUCT;
        }
        return MAIN;
    }

    @GetMapping("/search")
    public String searchProduct(String keyword, Model model) {
        if (keyword != null) {
            List<Product> resultList = catalogService.searchProductList(keyword);
            model.addAttribute("resultList", resultList);
            return SEARCH;
        }
        return MAIN;
    }

    @GetMapping("/viewItem")
    public String viewItem(String itemId, Model model) {
        if (itemId != null) {
            Item item = catalogService.getItem(itemId);
            Product product = item.getProduct();
            model.addAttribute("item", item);
            model.addAttribute("product", product);
            return ITEM;
        }
        return MAIN;
    }

    @GetMapping("/previewCategory")
    @ResponseBody
    public List<Product> previewCategory(String categoryId) {
        if (categoryId != null) {
            return catalogService.getProductListByCategory(categoryId);
        }
        return null;
    }

    @GetMapping("/searchAutoComplete")
    @ResponseBody
    public List<Product> searchAutoComplete(String keywords) {
        if (keywords != null) {
            return catalogService.searchProductList(keywords);
        }
        return null;
    }
}
