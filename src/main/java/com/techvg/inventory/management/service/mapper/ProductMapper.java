package com.techvg.inventory.management.service.mapper;

import com.techvg.inventory.management.domain.Product;
import com.techvg.inventory.management.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring", uses = { CategoriesMapper.class, UnitMapper.class, SecurityUserMapper.class })
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "categories.id", source = "categories.id")
    @Mapping(target = "unit.id", source = "unit.id")
    @Mapping(target = "securityUser.id", source = "securityUser.id")
    ProductDTO toDto(Product s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoId(Product product);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "productName", source = "productName")
    ProductDTO toDtoProductName(Product product);
}
