package com.techvg.inventory.management.service.mapper;

import com.techvg.inventory.management.domain.GoodsRecived;
import com.techvg.inventory.management.service.dto.GoodsRecivedDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GoodsRecived} and its DTO {@link GoodsRecivedDTO}.
 */
@Mapper(componentModel = "spring", uses = { PurchaseQuotationMapper.class, ProductMapper.class })
public interface GoodsRecivedMapper extends EntityMapper<GoodsRecivedDTO, GoodsRecived> {
    @Mapping(target = "purchaseQuotation.id", source = "purchaseQuotation.id")
    @Mapping(target = "product.id", source = "product.id")
    GoodsRecivedDTO toDto(GoodsRecived s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GoodsRecivedDTO toDtoId(GoodsRecived goodsRecived);
}
