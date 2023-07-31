package com.techvg.inventory.management.service.mapper;

import com.techvg.inventory.management.domain.PurchaseQuotationDetails;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDetailsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseQuotationDetails} and its DTO {@link PurchaseQuotationDetailsDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, PurchaseQuotationMapper.class })
public abstract class PurchaseQuotationDetailsMapper implements EntityMapper<PurchaseQuotationDetailsDTO, PurchaseQuotationDetails> {

    @Mapping(target = "product.id", source = "product.id")
    @Mapping(target = "purchaseQuotationId", source = "purchaseQuotation.id")
    public abstract PurchaseQuotationDetailsDTO toDto(PurchaseQuotationDetails s);

    @Mapping(source = "product.id", target = "product.id")
    @Mapping(source = "purchaseQuotationId", target = "purchaseQuotation.id")
    public abstract PurchaseQuotationDetails toEntity(PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO);
}
