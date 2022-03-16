package org.csu.mypetstore.controller.back;

import org.csu.mypetstore.domain.Order;
import org.csu.mypetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/manager/order")
public class BackOrderController {
    @Autowired
    private OrderService orderService;

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

    @GetMapping("/ship/{orderId}")
    public String shipOrder(@PathVariable("orderId") int orderId, Model model) {
        orderService.shipOrder(orderId);
        return viewOrderMain(model);
    }
}
