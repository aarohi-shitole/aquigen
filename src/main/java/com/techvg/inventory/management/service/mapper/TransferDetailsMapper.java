package com.techvg.inventory.management.service.mapper;

import com.techvg.inventory.management.domain.TransferDetails;
import com.techvg.inventory.management.service.dto.TransferDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link TransferDetails} and its DTO {@link TransferDetailsDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, TransferMapper.class })
public abstract class TransferDetailsMapper implements EntityMapper<TransferDetailsDTO, TransferDetails> {

    @Mapping(target = "product.id", source = "product.id")
    @Mapping(target = "transferId", source = "transfer.id")
    public abstract TransferDetailsDTO toDto(TransferDetails s);

    @Mapping(source = "product.id", target = "product.id")
    @Mapping(source = "transferId", target = "transfer.id")
    public abstract TransferDetails toEntity(TransferDetailsDTO transferDetailsDTO);
}
