package com.techvg.inventory.management.service.mapper;

import com.techvg.inventory.management.domain.ProductInventory;
import com.techvg.inventory.management.service.dto.ProductInventoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductInventory} and its DTO {@link ProductInventoryDTO}.
 */
@Mapper(
    componentModel = "spring",
    uses = { ProductMapper.class, ProductTransactionMapper.class, SecurityUserMapper.class, WareHouseMapper.class }
)
public interface ProductInventoryMapper extends EntityMapper<ProductInventoryDTO, ProductInventory> {
    @Mapping(target = "product.id", source = "product.id")
    @Mapping(target = "productTransaction.id", source = "productTransaction.id")
    @Mapping(target = "securityUser", source = "securityUser", qualifiedByName = "login")
    @Mapping(target = "wareHouse.id", source = "wareHouse.id")
    ProductInventoryDTO toDto(ProductInventory s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductInventoryDTO toDtoId(ProductInventory productInventory);
}
