package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.Order;
import org.csu.mypetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Controller
@SessionAttributes(names = {"cart", "shippingAddressRequired"})
@RequestMapping("/order")
@SessionScope
public class OrderController {

    private static final String CONFIRM_ORDER = "order/confirmOrder";
    private static final String LIST_ORDERS = "order/listOrders";
    private static final String NEW_ORDER = "order/newOrderForm";
    private static final String SHIPPING = "order/shippingForm";
    private static final String VIEW_ORDER = "order/ViewOrder";

    @Autowired
    private OrderService orderService;

    private Order savedOrder;



    @GetMapping("/list")
    public String listOrder(@SessionAttribute("account") Account account, Model model) {
        if (account == null) {
            model.addAttribute("msg", "You MUST sign on before viewing your order.");
            return "common/error";
        }
        List<Order> orderList = orderService.getOrdersByUsername(account.getUsername());
        model.addAttribute("orderList", orderList);
        return LIST_ORDERS;
    }

    @GetMapping("/newOrderForm")
    public String newOrderForm(@SessionAttribute(name = "cart") Cart cart, @SessionAttribute("account") Account account,
                               @SessionAttribute("isAuthenticated") boolean isAuthenticated, Model model) {
        System.out.println(isAuthenticated);
        if (account == null || !isAuthenticated) {
            System.out.println(account);
            model.addAttribute("msg", "You must sign on before attempting to check out.  Please sign on and try checking out again.");
            return "account/signonForm";
        } else if (cart != null && cart.getNumberOfItems() != 0) {
            savedOrder = new Order();
            savedOrder.initOrder(account, cart);
            model.addAttribute("_order", savedOrder);
            model.addAttribute("confirmed", false);
            model.addAttribute("cart", new Cart());
            return NEW_ORDER;
        } else {
            model.addAttribute("msg", "An order could not be created because a cart could not be found.");
            return "common/error";
        }
    }

    @RequestMapping("/newOrder")
    public String newOrder(boolean shippingAddressRequired, boolean confirmed, Model model, Order order, int from) {
        if (from == 0) {
            savedOrder.setOrderInfo(order);
            model.addAttribute("_order", savedOrder);
            if (shippingAddressRequired) {
                savedOrder.setShippingInfo(order);
                model.addAttribute("shippingAddressRequired", false);
            }
            return CONFIRM_ORDER;

        } else if (from == 1) {
            model.addAttribute("_order", savedOrder);
            savedOrder.setShippingInfo(order);
            return CONFIRM_ORDER;
        } else if (from == 2 || savedOrder != null) {
            orderService.insertOrder(savedOrder);
            model.addAttribute("msg", "Thank you, your order has been submitted.");
            model.addAttribute("_order", savedOrder);
            clear();
            return VIEW_ORDER;
        } else {
            model.addAttribute("msg", "An error occurred processing your order (order was null).");
            return "common/error";
        }
    }

    private void clear() {
        savedOrder = new Order();
    }

    @GetMapping("/viewOrder")
    public String viewOrder(@SessionAttribute("account") Account account, int orderId, Model model) {
        if (account == null) {
            model.addAttribute("msg", "You MUST sign on before viewing your order.");
            return "common/error";
        }
        Order order = orderService.getOrder(orderId);
        if (account.getUsername().equals(order.getUsername())) {
            model.addAttribute("_order", order);
            return VIEW_ORDER;
        } else {
            model.addAttribute("msg", "You can only view your own orders.");
            return "common/error";
        }
    }
}