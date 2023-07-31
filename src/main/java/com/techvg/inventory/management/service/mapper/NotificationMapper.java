package com.techvg.inventory.management.service.mapper;

import com.techvg.inventory.management.domain.GoodsRecived;
import com.techvg.inventory.management.domain.Notification;
import com.techvg.inventory.management.domain.PurchaseQuotationDetails;
import com.techvg.inventory.management.service.dto.GoodsRecivedDTO;
import com.techvg.inventory.management.service.dto.NotificationDTO;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDetailsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(
    componentModel = "spring",
    uses = { SecurityUserMapper.class, SecurityRoleMapper.class, WareHouseMapper.class, PurchaseQuotationMapper.class }
)
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "securityUser", source = "securityUser", qualifiedByName = "login")
    @Mapping(target = "securityRole.id", source = "securityRole.id")
    @Mapping(target = "wareHouse.id", source = "wareHouse.id")
    @Mapping(target = "purchaseQuotationId", source = "purchaseQuotation.id")
    NotificationDTO toDto(Notification s);

    @Mapping(source = "securityRole.id", target = "securityRole.id")
    @Mapping(target = "wareHouse.id", source = "wareHouse.id")
    @Mapping(source = "purchaseQuotationId", target = "purchaseQuotation.id")
    Notification toEntity(NotificationDTO notificationDTO);
}
