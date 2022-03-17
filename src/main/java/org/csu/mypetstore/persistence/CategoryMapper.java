package org.csu.mypetstore.persistence;

import org.csu.mypetstore.domain.Category;
import org.csu.mypetstore.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper {
    Category getCategory(String categoryId);

    List<Category> getAllCategories();

    void insertCategory(Category category);

    void deleteCategory(String categoryId);

    void updateCategoryName(String categoryId, String newCategoryId);
}
