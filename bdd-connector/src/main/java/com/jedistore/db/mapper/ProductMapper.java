package com.jedistore.db.mapper;

import com.jedistore.core.domain.entity.Product;
import com.jedistore.db.entity.ProductEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper responsable de la conversion entre l'entité domaine {@link com.jedistore.core.domain.entity.Product}
 * et l'entité JPA {@link com.jedistore.db.entity.ProductEntity}.
 */
@Component
public class ProductMapper {

    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setPrice(entity.getPrice());
        product.setCategory(entity.getCategory());
        return product;
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setCategory(domain.getCategory());
        return entity;
    }
}
