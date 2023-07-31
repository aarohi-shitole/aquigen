package com.techvg.inventory.management.service.mapper;

import com.techvg.inventory.management.domain.Transfer;
import com.techvg.inventory.management.service.dto.TransferDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Transfer} and its DTO {@link TransferDTO}.
 */
@Mapper(componentModel = "spring", uses = { SecurityUserMapper.class, WareHouseMapper.class })
public abstract class TransferMapper implements EntityMapper<TransferDTO, Transfer> {

    @Mapping(target = "securityUser.id", source = "securityUser.id")
    @Mapping(target = "wareHouse.id", source = "wareHouse.id")
    public abstract TransferDTO toDto(Transfer s);

    @Mapping(target = "transferDetails", ignore = true)
    @Mapping(target = "removeTransferDetails", ignore = true)
    @Mapping(target = "securityUser.id", source = "securityUser.id")
    @Mapping(target = "wareHouse.id", source = "wareHouse.id")
    public abstract Transfer toEntity(TransferDTO transferDTO);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public abstract TransferDTO toDtoId(Transfer transfer);
}
