package com.techvg.inventory.management.service.mapper;

import com.techvg.inventory.management.domain.TransferDetailsApprovals;
import com.techvg.inventory.management.service.dto.TransferDetailsApprovalsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransferDetailsApprovals} and its DTO {@link TransferDetailsApprovalsDTO}.
 */
@Mapper(componentModel = "spring", uses = { SecurityUserMapper.class, TransferMapper.class })
public interface TransferDetailsApprovalsMapper extends EntityMapper<TransferDetailsApprovalsDTO, TransferDetailsApprovals> {
    @Mapping(target = "securityUser", source = "securityUser", qualifiedByName = "login")
    @Mapping(target = "transfer.id", source = "transfer.id")
    @Mapping(target = "product.id", source = "product.id")
    @Mapping(target = "productName", source = "product.productName")
    TransferDetailsApprovalsDTO toDto(TransferDetailsApprovals s);
}
