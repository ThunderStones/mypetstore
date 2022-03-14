package org.csu.mypetstore.service;

import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.persistence.CartMapper;
import org.csu.mypetstore.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CartService {
    @Autowired
    private CartMapper cartMapper;

    public void initCart(String username) {
        cartMapper.createCart(username);
    }

    public void updateCart(String username, Cart cart) {
        byte[] binaryCartData = new byte[0];
        try {
            binaryCartData = SerializeUtil.serialize(cart);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cartMapper.updateCart(username, binaryCartData);
    }

    public Cart getCart(String username) {
        byte[] binaryCartData = (byte[]) cartMapper.getCart(username).get("DATA");
        Cart cart = null;
        try {
            cart = (Cart) SerializeUtil.serializeToObject(binaryCartData);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cart;
    }


}
