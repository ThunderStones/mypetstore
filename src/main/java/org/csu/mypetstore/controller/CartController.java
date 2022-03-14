package org.csu.mypetstore.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.service.CartService;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Objects;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CartService cartService;

    private static final String VIEW_CART = "cart/cart";

    @GetMapping("/addItemToCart")
    public String addItemToCart(Cart cart, String workingItemId, Model model,
                                @SessionAttribute(value = "account", required = false) Account account) {
        if (account == null) {
            model.addAttribute("msg", "You must be logged in to add items to your cart.");
            return "forward:/account/signonForm";
        }
        if (cart.containsItemId(workingItemId)) {
            cart.incrementQuantityByItemId(workingItemId);
        } else {
            boolean isInStock = catalogService.isItemInStock(workingItemId);
            Item item = catalogService.getItem(workingItemId);
            cart.addItem(item, isInStock);
        }
        cartService.updateCart(account.getUsername(), cart);
        model.addAttribute("cart", cart);

        return "forward:/cart/viewCart";
    }

    @GetMapping("/removeItem")
    public String removeItemFromCart(String workingItemId, Cart cart, Model model,
                                     @SessionAttribute(value = "account", required = false) Account account) {
        if (account == null) {
            model.addAttribute("msg", "You must be logged in to add items to your cart.");
            return "forward:/account/signonForm";
        }

        Item item = cart.removeItemById(workingItemId);
        if (item == null) {
            model.addAttribute("msg", "Attempted to remove null CartItem from Cart.");
            return "common/error";
        } else {
            model.addAttribute("cart", cart);
            cartService.updateCart(account.getUsername(), cart);
            return "forward:/cart/viewCart";
        }
    }

    @GetMapping("/updateCartQuantity")
    public String updateCartQuantity(HttpServletRequest httpServletRequest, Cart cart, Model model,
                                     @SessionAttribute(value = "account", required = false) Account account) {
        if (account == null) {
            model.addAttribute("msg", "You must be logged in to add items to your cart.");
            return "forward:/account/signonForm";
        }
        Iterator<CartItem> cartItems = cart.getAllCartItems();
        while (cartItems.hasNext()) {
            CartItem cartItem = cartItems.next();
            String itemId = cartItem.getItem().getItemId();
            try {
                int quantity = Integer.parseInt(httpServletRequest.getParameter(itemId));
                cart.setQuantityByItemId(itemId, quantity);
                if (quantity < 1) {
                    cartItems.remove();
                }
            } catch (Exception e) {
                // ignore parse exceptions on purpose
            }
        }
        cartService.updateCart(account.getUsername(), cart);
        model.addAttribute("cart", cart);

        return "forward:/cart/viewCart";
    }

    @GetMapping("/viewCart")
    public String viewCart(Cart cart, Model model) {
        if (cart == null) {
            cart = new Cart();
        }
        model.addAttribute("cart", cart);
        return VIEW_CART;
    }

    //update cart
    @PostMapping(value = "/updateCart", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateCart(Cart cart, Account account, HttpServletRequest request, Model model) {
        Iterator<CartItem> cartItems = cart.getAllCartItems();
        JSONArray responseData = new JSONArray();
        while (cartItems.hasNext()) {
            CartItem cartItem = cartItems.next();
            String itemId = cartItem.getItem().getItemId();
            int quantity = Integer.parseInt(request.getParameter(itemId));
            cart.setQuantityByItemId(itemId, quantity);
            if (quantity < 1) {
                cartItems.remove();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", cartItem.getItem().getItemId());
            jsonObject.put("totalPrice", cartItem.getTotal());
            responseData.add(jsonObject);

        }
        return responseData.toJSONString();
    }
}
