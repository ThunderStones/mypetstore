package org.csu.mypetstore.persistence;

import org.csu.mypetstore.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    List<Product> getProductByCategory(String categoryId);

    Product getProduct(String productId);

    List<Product> searchProductList(String keyword);

    void insertProduct(Product product);

    void deleteProduct(String productId);
}
