package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {
    @Autowired
    private CatalogService catalogService;


    private static final String VIEW_CART = "/cart/cart";

    @GetMapping("/addItemToCart")
    public String addItemToCart(Cart cart, String workingItemId, Model model) {
        if (cart == null) cart = new Cart();
        if (cart.containsItemId(workingItemId)) {
            cart.incrementQuantityByItemId(workingItemId);
        } else {
            boolean isInStock = catalogService.isItemInStock(workingItemId);
            Item item = catalogService.getItem(workingItemId);
            cart.addItem(item, isInStock);
        }
        model.addAttribute("cart", cart);
        return VIEW_CART;
    }

    @GetMapping("/removeItem")
    public String removeItemFromCart(String workingItemId, Cart cart, Model model) {
        Item item = cart.removeItemById(workingItemId);

        if (item == null) {
            model.addAttribute("msg", "Attempted to remove null CartItem from Cart.");
            return "/common/error";
        } else {
            model.addAttribute("cart", cart);

            return VIEW_CART;
        }
    }

    @GetMapping("/updateCartQuantity")
    public String updateCartQuantity(HttpServletRequest httpServletRequest, Cart cart, Model model) {
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
        model.addAttribute("cart", cart);

        return VIEW_CART;
    }

    @GetMapping("/viewCart")
    public String viewCart(Cart cart, Model model) {
        if (cart == null) cart = new Cart();
        model.addAttribute("cart", cart);
        return VIEW_CART;
    }

    @GetMapping("checkout")
    public String checkout() {
        return "/cart/checkout";
    }
}
