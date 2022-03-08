package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.Order;
import org.csu.mypetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@SessionAttributes(names = {"order", "shippingAddressRequired"})
@RequestMapping("/order")
public class OrderController {

    private static final String CONFIRM_ORDER = "order/confirmOrder";
    private static final String LIST_ORDERS = "order/listOrders";
    private static final String NEW_ORDER = "order/newOrderForm";
    private static final String SHIPPING = "order/shippingForm";
    private static final String VIEW_ORDER = "order/viewOrder";

    @Autowired
    private OrderService orderService;

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
            Order order = new Order();
            order.initOrder(account, cart);
            model.addAttribute("order", order);
            model.addAttribute("confirmed", false);
            return NEW_ORDER;
        } else {
            model.addAttribute("msg", "An order could not be created because a cart could not be found.");
            return "common/error";
        }
    }

    @RequestMapping("newOrder")
    public String newOrder(boolean shippingAddressRequired, Order order, boolean confirmed, Model model) {
        if (shippingAddressRequired) {
            model.addAttribute("shippingAddressRequired", false);
            return SHIPPING;
        } else if (!confirmed) {
            return CONFIRM_ORDER;
        } else if (order != null) {
            orderService.insertOrder(order);
            model.addAttribute("msg", "Thank you, your order has been submitted.");
            return VIEW_ORDER;
        } else {
            model.addAttribute("msg", "An error occurred processing your order (order was null).");
            return "common/error";
        }
    }

    @GetMapping("/viewOrder")
    public String viewOrder(@SessionAttribute("account") Account account, int orderId, Model model) {
        if (account == null) {
            model.addAttribute("msg", "You MUST sign on before viewing your order.");
            return "common/error";
        }
        Order order = orderService.getOrder(orderId);
        if (account.getUsername().equals(order.getUsername())) {
            model.addAttribute("order", order);
            return VIEW_ORDER;
        } else {
            model.addAttribute("msg", "You can only view your own orders.");
            return "common/error";
        }
    }

    @GetMapping("/orderMain")
    public String viewOrderMain(Model model) {
        List<Order> orderList = orderService.getAllOrder();
        model.addAttribute("orderList", orderList);
        return "management/order/order";
    }

    @GetMapping("/orderDetail/{orderId}")
    public String viewOrderDetail(@PathVariable("orderId") int orderId, Model model) {
        Order order = orderService.getOrder(orderId);
        model.addAttribute("order", order);
        return "management/order/orderDetail";
    }

    @PostMapping("/edit")
    public String editOrder(Order order, Model model) {
        orderService.updateOrder(order);
        return "management/order/orderDetail";
    }

    @GetMapping("/delete/{orderId}")
    public String deleteOrder(@PathVariable("orderId") int orderId, Model model) {
        orderService.deleteOrder(orderId);
        return viewOrderMain(model);
    }
}