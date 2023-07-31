package com.techvg.inventory.management.scheduler;

import com.techvg.inventory.management.domain.enumeration.NotificationType;
import com.techvg.inventory.management.domain.enumeration.OrderType;
import com.techvg.inventory.management.domain.enumeration.Status;
import com.techvg.inventory.management.service.NotificationService;
import com.techvg.inventory.management.service.PurchaseQuotationQueryService;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationCriteria;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationCriteria.OrderTypeFilter;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationCriteria.StatusFilter;
import com.techvg.inventory.management.service.dto.NotificationDTO;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDTO;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PurchaseQuotationQueryService purchaseQuotationQueryService;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "${schedular.quotation.notification.cron}")
    public void reportCurrentTime() {
        log.info("Schedular Quotation Reminder");

        try {
            checkPurchaseQuotation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPurchaseQuotation() {
        PurchaseQuotationCriteria criteria = new PurchaseQuotationCriteria();
        OrderTypeFilter orderType = new OrderTypeFilter();
        orderType.setEquals(OrderType.PRODUCT_QUATATION);
        criteria.setOrderType(orderType);

        //		InstantFilter poDate = new InstantFilter();
        //		Instant instantNow = getInstantDate(-3);
        //		poDate.setLessThanOrEqual(instantNow);
        //		Instant instantNow1 = getInstantDate(-10);
        //		poDate.setGreaterThan(instantNow1);
        //		criteria.setPoDate(poDate);

        StatusFilter orderStatus = new StatusFilter();
        List<Status> in = new ArrayList<>();
        in.add(Status.REQUESTED);
        in.add(Status.APPROVED);
        orderStatus.setIn(in);
        criteria.setOrderStatus(orderStatus);

        Pageable pageable = Pageable.ofSize(1000000);
        Page<PurchaseQuotationDTO> page = purchaseQuotationQueryService.findByCriteria(criteria, pageable);

        for (PurchaseQuotationDTO purchaseQuotationDTO : page.getContent()) {
            Instant today = getInstantDate(0);
            Instant quotationGivenDate = purchaseQuotationDTO.getPoDate();

            ZoneId zone = ZoneId.of("Asia/Kolkata");
            int diff = 1;
            try {
                Period period = Period.between(LocalDate.ofInstant(today, zone), LocalDate.ofInstant(quotationGivenDate, zone));
                diff = Math.abs(period.getDays());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (diff > 3) {
                NotificationDTO notificationDTO = new NotificationDTO();

                notificationDTO.setIsActionRequired(true);
                notificationDTO.setIsRead(false);
                notificationDTO.setMassage("Please update quotation status for " + purchaseQuotationDTO.getRefrenceNumber());
                notificationDTO.setFreeField1(purchaseQuotationDTO.getId() + "");
                notificationDTO.setTitle("Please update quotation status for " + purchaseQuotationDTO.getRefrenceNumber());
                notificationDTO.setSecurityUser(purchaseQuotationDTO.getSecurityUser());
                notificationDTO.setNotificationType(NotificationType.QUOTATION_REMINDER);
                notificationService.save(notificationDTO);
            }
        }
    }

    private Instant getInstantDate(int monthAgo) {
        Calendar c1 = Calendar.getInstance();

        if (monthAgo != 0) {
            c1.add(Calendar.DAY_OF_MONTH, monthAgo);
        }

        Date dateOne = c1.getTime();

        return dateOne.toInstant();
    }
}
