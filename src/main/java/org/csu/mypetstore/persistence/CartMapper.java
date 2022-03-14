package org.csu.mypetstore.persistence;

import org.csu.mypetstore.domain.Cart;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface CartMapper {
    void createCart(String username);

    void updateCart(String username, byte[] cartBinaryData);

    Map getCart(String username);
}
