package org.csu.mypetstore.service;

import org.csu.mypetstore.domain.Category;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.persistence.CategoryMapper;
import org.csu.mypetstore.persistence.ItemMapper;
import org.csu.mypetstore.persistence.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ItemMapper itemMapper;

    public Category getCategory(String categoryId) {
        return categoryMapper.getCategory(categoryId);
    }

    public Product getProduct(String productId) {
        return productMapper.getProduct(productId);
    }

    public List<Product> getProductListByCategory(String categoryId) {
        return productMapper.getProductByCategory(categoryId);
    }

    public List<Product> searchProductList(String keywords) {
        List<Product> productList = new ArrayList<>();
        for (String keyword : keywords.split("\\s+")) {
            productList.addAll(productMapper.searchProductList("%" + keyword + "%"));
        }
        return productList;
    }

    public Item getItem(String itemId) {
        return itemMapper.getItem(itemId);
    }

    public List<Item> getItemListByProduct(String productId) {
        return itemMapper.getItemListByProduct(productId);
    }

    public boolean isItemInStock(String itemId) {
        return itemMapper.getInventoryQuantity(itemId) > 0;
    }

    public List<Category> getAllCategories() {
        return categoryMapper.getAllCategories();
    }

    public void addCategory(Category category) {
        categoryMapper.insertCategory(category);
    }

    public void deleteCategory(String categoryId) {
        categoryMapper.deleteCategory(categoryId);
    }

    public void addProduct(Product product) {
        productMapper.insertProduct(product);
    }

    public void deleteProduct(String productId) {
        productMapper.deleteProduct(productId);
    }
    @Transactional
    public void addItem(Item item) {
        itemMapper.insertItem(item);
        itemMapper.insertItemInventory(item);
    }

    @Transactional
    public void deleteItem(String itemId) {
        itemMapper.deleteItem(itemId);
        itemMapper.deleteItemInventory(itemId);
    }

    public void updateCategory(String categoryId, String newCategoryId) {
        categoryMapper.updateCategoryName(categoryId, newCategoryId);
    }

    public void updateItem(Item item) {
        itemMapper.editItem(item);
    }
}
