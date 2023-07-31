package com.techvg.inventory.management.service.mapper;

import com.techvg.inventory.management.domain.PurchaseQuotation;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDTO;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDetailsDTO;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper for the entity {@link PurchaseQuotation} and its DTO {@link PurchaseQuotationDTO}.
 */
@Mapper(componentModel = "spring", uses = { SecurityUserMapper.class, ProjectMapper.class, ClientDetailsMapper.class })
public abstract class PurchaseQuotationMapper implements EntityMapper<PurchaseQuotationDTO, PurchaseQuotation> {

    @Mapping(target = "securityUser.id", source = "securityUser.id")
    @Mapping(target = "approvalId.id", source = "approvalId.id")
    @Mapping(target = "project.id", source = "project.id")
    @Mapping(target = "clientDetails.id", source = "clientDetails.id")
    public abstract PurchaseQuotationDTO toDto(PurchaseQuotation s);

    @Mapping(target = "purchaseQuotationDetails", ignore = true)
    @Mapping(target = "removePurchaseQuotationDetails", ignore = true)
    @Mapping(target = "securityUser.id", source = "securityUser.id")
    @Mapping(target = "approvalId.id", source = "approvalId.id")
    @Mapping(target = "project.id", source = "project.id")
    @Mapping(target = "clientDetails.id", source = "clientDetails.id")
    public abstract PurchaseQuotation toEntity(PurchaseQuotationDTO purchaseQuotationDTO);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public abstract PurchaseQuotationDTO toDtoId(PurchaseQuotation purchaseQuotation);
}
