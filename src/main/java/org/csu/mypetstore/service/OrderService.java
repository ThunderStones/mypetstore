package org.csu.mypetstore.service;

import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.domain.Order;
import org.csu.mypetstore.domain.Sequence;
import org.csu.mypetstore.persistence.ItemMapper;
import org.csu.mypetstore.persistence.LineItemMapper;
import org.csu.mypetstore.persistence.OrderMapper;
import org.csu.mypetstore.persistence.SequenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SequenceMapper sequenceMapper;
    @Autowired
    private LineItemMapper lineItemMapper;

    /**
     * Insert order
     *
     * @param order the order
     */
    @Transactional
    public void insertOrder(Order order) {
        order.setOrderId(getNextId("ordernum"));
        order.getLineItems().forEach(lineItem -> {
            String itemId = lineItem.getItemId();
            Integer increment = lineItem.getQuantity();
            Map<String, Object> param = new HashMap<>(2);
            param.put("itemId", itemId);
            param.put("increment", increment);
            itemMapper.updateInventoryQuantity(param);
        });

        orderMapper.insertOrder(order);
        orderMapper.insertOrderStatus(order);
        order.getLineItems().forEach(lineItem -> {
            lineItem.setOrderId(order.getOrderId());
            lineItemMapper.insertLineItem(lineItem);
        });
    }

    /**
     * Get the order by orderId
     * @param orderId the orderId
     * @return the order
     */
    public Order getOrder(int orderId) {
        Order order = orderMapper.getOrder(orderId);
        order.setLineItems(lineItemMapper.getLineItemsByOrderId(orderId));

        order.getLineItems().forEach(lineItem -> {
            Item item = itemMapper.getItem(lineItem.getItemId());
            item.setQuantity(itemMapper.getInventoryQuantity(lineItem.getItemId()));
            lineItem.setItem(item);
        });
        return order;
    }

    /**
     * Get orders by username
     * @param username the username
     * @return the orders
     */
    public List<Order> getOrdersByUsername(String username) {
        return orderMapper.getOrdersByUsername(username);
    }

    /**
     * Get the next id
     *
     * @param name the name
     * @return the next id
     */
    public int getNextId(String name) {
        Sequence sequence = sequenceMapper.getSequence(new Sequence(name, -1));
        if (sequence == null) {
            throw new RuntimeException("Error: A null sequence was returned from the database (could not get next " + name + " sequence).");
        }
        Sequence parameterObject = new Sequence(name, sequence.getNextId() + 1);
        sequenceMapper.updateSequence(parameterObject);
        return sequence.getNextId();
    }

    public List<Order> getAllOrder() {
        return orderMapper.getAllOrder();
    }

    public void updateOrder(Order order) {
        orderMapper.updateOrder(order);
        orderMapper.updateOrderStatus(order);
    }

    public void deleteOrder(int orderId) {
        orderMapper.deleteOrder(orderId);
        orderMapper.deleteOrderStatus(orderId);
        lineItemMapper.deleteLineItemByOrderId(orderId);
    }
}
