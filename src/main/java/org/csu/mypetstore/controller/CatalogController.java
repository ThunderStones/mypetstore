package org.csu.mypetstore.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.csu.mypetstore.domain.Category;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/viewCategoryList")
    public String viewCategoryList(Model model) {
        List<Category> categoryList = catalogService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        return "management/catalog/category";
    }

    @GetMapping("/viewAllProductList")
    public String viewAllProductList(Model model) {
        return viewProductList(null, model);
    }


    @GetMapping("/viewProductList/{categoryId}")
    public String viewProductList(@PathVariable("categoryId") String categoryId, Model model) {
        if (categoryId != null) {
            List<Product> productList = catalogService.getProductListByCategory(categoryId);
            model.addAttribute("productList", productList);
        } else {
            List<Category> categoryList = catalogService.getAllCategories();
            List<Product> productList = new ArrayList<>();
            categoryList.forEach(category -> {
                productList.addAll(catalogService.getProductListByCategory(category.getCategoryId()));
            });
            model.addAttribute("productList", productList);
        }
        return "management/catalog/product";
    }

    @GetMapping("/viewAllItemList")
    public String viewAllItemList(Model model) {
        return viewItemList(null, model);
    }

    @GetMapping("/viewItemList/{productId}")
    public String viewItemList(@PathVariable("productId") String productId, Model model) {
        if (productId != null) {
            List<Item> itemList = catalogService.getItemListByProduct(productId);
            model.addAttribute("itemList", itemList);
        } else {
            List<Category> categoryList = catalogService.getAllCategories();
            List<Product> productList = new ArrayList<>();
            categoryList.forEach(category -> {
                productList.addAll(catalogService.getProductListByCategory(category.getCategoryId()));
            });
            List<Item> itemList = new ArrayList<>();
            productList.forEach(product -> {
                itemList.addAll(catalogService.getItemListByProduct(product.getProductId()));
            });
            model.addAttribute("itemList", itemList);
        }
        return "management/catalog/item";
    }

    @GetMapping("/viewAddPage")
    public String viewAddPage(Model model) {
        List<Category> categoryList = catalogService.getAllCategories();
        List<Product> productList = new ArrayList<>();
        categoryList.forEach(category -> {
            productList.addAll(catalogService.getProductListByCategory(category.getCategoryId()));
        });
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("productList", productList);
        return "management/catalog/add";
    }

    @PostMapping("/add/category")
    public String addCategory(Category category, Model model) {
        if (category != null) {
            category.setCategoryId(category.getName().toUpperCase());
            catalogService.addCategory(category);
            return "redirect:/catalog/viewCategoryList";
        } else {
            model.addAttribute("errorMsg", "Category is null");
            return "management/catalog/add";
        }
    }

    @GetMapping("/delete/category/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") String categoryId, Model model) {
        if (!categoryId.isEmpty() && catalogService.getProductListByCategory(categoryId).size() == 0) {
            catalogService.deleteCategory(categoryId);
        } else {
            model.addAttribute("errorMsg", "Can not delete " + categoryId + ". Some products are in this category");
        }
        return viewCategoryList(model);
    }

    @GetMapping("/add/product")
    public String addProduct(Product product, Model model) {
        if (product != null) {
            catalogService.addProduct(product);
            return "redirect:/catalog/viewAllProductList";
        } else {
            model.addAttribute("errorMsg", "Product is null");
            return "management/catalog/add";
        }
    }

    @GetMapping("/delete/product/{productId}")
    public String deleteProduct(@PathVariable("productId") String productId, Model model) {
        if (!productId.isEmpty() && catalogService.getItemListByProduct(productId).size() == 0) {
            catalogService.deleteProduct(productId);
        } else {
            model.addAttribute("errorMsg", "Can not delete " + productId + ". Some items are in this product");
        }
        return viewProductList(null, model);
    }

    @GetMapping("/add/item")
    public String addItem(Item item, Model model) {
        if (item.getItemId() != null) {
            item.setSupplierId(1);
            item.setStatus("P");
            item.setQuantity(10000);
            catalogService.addItem(item);
            return "redirect:/catalog/viewAllItemList";
        } else {
            model.addAttribute("errorMsg", "Item is null");
            return "management/catalog/add";
        }
    }

    @GetMapping("/delete/item/{itemId}")
    public String deleteItem(@PathVariable("itemId") String itemId, Model model) {
        if (!itemId.isEmpty()) {
            catalogService.deleteItem(itemId);
        } else {
            model.addAttribute("errorMsg", "Can not delete " + itemId);
        }
        return viewItemList(null, model);
    }


}
