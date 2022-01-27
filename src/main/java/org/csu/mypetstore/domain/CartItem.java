package org.csu.mypetstore.domain;

import javax.swing.text.html.Option;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

public class CartItem implements Serializable {

    private Item item;
    private int quantity;
    private boolean inStock;
    private BigDecimal total;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        calculateTotal();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotal();
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void calculateTotal() {
        total = Optional.ofNullable(item).map(Item::getListPrice).map(v -> v.multiply(new BigDecimal(quantity))).orElse(null);
    }
    public void incrementQuantity() {
        quantity++;
        calculateTotal();
    }


}
