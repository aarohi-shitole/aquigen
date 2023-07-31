package com.techvg.inventory.management.service;

import com.techvg.inventory.management.domain.PurchaseQuotation;
import com.techvg.inventory.management.domain.PurchaseQuotationDetails;
import com.techvg.inventory.management.domain.SecurityRole;
import com.techvg.inventory.management.domain.enumeration.NotificationType;
import com.techvg.inventory.management.domain.enumeration.OrderType;
import com.techvg.inventory.management.domain.enumeration.Status;
import com.techvg.inventory.management.repository.PurchaseQuotationDetailsRepository;
import com.techvg.inventory.management.repository.PurchaseQuotationRepository;
import com.techvg.inventory.management.service.dto.NotificationDTO;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDTO;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDetailsDTO;
import com.techvg.inventory.management.service.dto.SecurityRoleDTO;
import com.techvg.inventory.management.service.dto.UserAccessDTO;
import com.techvg.inventory.management.service.mapper.PurchaseQuotationDetailsMapper;
import com.techvg.inventory.management.service.mapper.PurchaseQuotationMapper;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service Implementation for managing {@link PurchaseQuotation}.
 */
@Service
@Transactional
public class PurchaseQuotationService {

    private final Logger log = LoggerFactory.getLogger(PurchaseQuotationService.class);

    private final PurchaseQuotationRepository purchaseQuotationRepository;

    private final PurchaseQuotationMapper purchaseQuotationMapper;

    private final PurchaseQuotationQueryService purchaseQuotationQueryService;

    @Autowired
    private PurchaseQuotationDetailsRepository purchaseQuotationDetailsRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PurchaseQuotationDetailsMapper purchaseQuotationDetailsMapper;

    public PurchaseQuotationService(
        PurchaseQuotationRepository purchaseQuotationRepository,
        PurchaseQuotationMapper purchaseQuotationMapper,
        PurchaseQuotationQueryService purchaseQuotationQueryService
    ) {
        this.purchaseQuotationRepository = purchaseQuotationRepository;
        this.purchaseQuotationMapper = purchaseQuotationMapper;
        this.purchaseQuotationQueryService = purchaseQuotationQueryService;
    }

    /**
     * Save a purchaseQuotation.
     *
     * @param purchaseQuotationDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseQuotationDTO save(PurchaseQuotationDTO purchaseQuotationDTO) {
        log.debug("Request to save PurchaseQuotation : {}", purchaseQuotationDTO);

        //-------create RefrenceNumber for PurchaseQuotation/PurchaseOrder/TaxInvoice
        if (purchaseQuotationDTO.getRefrenceNumber() == null && purchaseQuotationDTO.getId() == null) {
            String refrenceNo = this.getRefrenceNumber(purchaseQuotationDTO.getOrderType());
            purchaseQuotationDTO.setRefrenceNumber(refrenceNo);
        }
        PurchaseQuotation purchaseQuotation = purchaseQuotationMapper.toEntity(purchaseQuotationDTO);
        purchaseQuotation = purchaseQuotationRepository.save(purchaseQuotation);

        // -------------------------------------------------------
        // -------------Create PurchaseQuotationDetails product wise
        if (purchaseQuotationDTO.getPurchaseQuotationDetails() != null && !purchaseQuotationDTO.getPurchaseQuotationDetails().isEmpty()) {
            List<PurchaseQuotationDetailsDTO> quotationDetailsList = purchaseQuotationDTO.getPurchaseQuotationDetails();
            for (int i = 0; i < quotationDetailsList.size(); i++) {
                PurchaseQuotationDetailsDTO detailsDto = quotationDetailsList.get(i);

                log.debug("Request to save PurchaseQuotationDetails : {}", detailsDto);

                if (detailsDto != null) {
                    detailsDto.setPurchaseQuotationId(purchaseQuotation.getId());
                    PurchaseQuotationDetails purchaseQuotationDetails = purchaseQuotationDetailsMapper.toEntity(detailsDto);
                    purchaseQuotationDetails = purchaseQuotationDetailsRepository.save(purchaseQuotationDetails);
                }
            }
        }

        //for update notification

        if (purchaseQuotationDTO.getOrderType() == OrderType.PO_REQUISITION) {
            NotificationDTO notificationDTO = new NotificationDTO();

            notificationDTO.setNotificationType(NotificationType.ALERT);
            notificationDTO.setIsActionRequired(true);
            notificationDTO.setIsRead(false);

            notificationDTO.setTitle("New PO Requistion");
            SecurityRoleDTO role = new SecurityRoleDTO();
            role.setId(5L);
            notificationDTO.setSecurityRole(role);
            notificationDTO.setMassage("New PO Requistion avliable for" + purchaseQuotationDTO.getProject().getProjectName());
            notificationDTO.setSecurityUser(purchaseQuotationDTO.getSecurityUser());
            purchaseQuotationDTO.getId();

            notificationDTO.setPurchaseQuotationId(purchaseQuotation.getId());

            notificationService.save(notificationDTO);
        } else if (purchaseQuotationDTO.getOrderType() == OrderType.PURCHASE_ORDER) {
            NotificationDTO notificationDTO = new NotificationDTO();

            notificationDTO.setNotificationType(NotificationType.ALERT);
            notificationDTO.setIsActionRequired(true);
            notificationDTO.setIsRead(false);

            notificationDTO.setTitle("New Po for approval.");
            notificationDTO.setMassage("New po avaliable from " + purchaseQuotation.getSecurityUser().getFirstName());
            notificationDTO.setSecurityUser(purchaseQuotationDTO.getApprovalId());
            notificationDTO.setPurchaseQuotationId(purchaseQuotation.getId());

            notificationService.save(notificationDTO);
        }

        //Update Requisition status on creation of its PO
        if (
            purchaseQuotationDTO.getOrderType() == OrderType.PURCHASE_ORDER &&
            purchaseQuotationDTO.getInvoiceRefrence() != null &&
            purchaseQuotationDTO.getInvoiceRefrence() > 0
        ) {
            Optional<PurchaseQuotationDTO> findOne = findOne((purchaseQuotationDTO.getInvoiceRefrence()));

            if (findOne.get().getOrderType() == OrderType.PO_REQUISITION) {
                PurchaseQuotationDTO purchaseRequistion = new PurchaseQuotationDTO();
                purchaseRequistion.setId(findOne.get().getId());
                purchaseRequistion.setOrderStatus(Status.COMPLETED);
                purchaseRequistion.setInvoiceRefrence(purchaseQuotation.getId());
                partialUpdate(purchaseRequistion);
            }
        }

        return purchaseQuotationMapper.toDto(purchaseQuotation);
    }

    /**
     * Partially update a purchaseQuotation.
     *
     * @param purchaseQuotationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PurchaseQuotationDTO> partialUpdate(PurchaseQuotationDTO purchaseQuotationDTO) {
        log.debug("Request to partially update PurchaseQuotation : {}", purchaseQuotationDTO);

        return purchaseQuotationRepository
            .findById(purchaseQuotationDTO.getId())
            .map(existingPurchaseQuotation -> {
                purchaseQuotationMapper.partialUpdate(existingPurchaseQuotation, purchaseQuotationDTO);

                return existingPurchaseQuotation;
            })
            .map(purchaseQuotationRepository::save)
            .map(purchaseQuotationMapper::toDto);
    }

    /**
     * Get all the purchaseQuotations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseQuotationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PurchaseQuotations");
        return purchaseQuotationRepository.findAll(pageable).map(purchaseQuotationMapper::toDto);
    }

    /**
     * Get all the purchaseQuotations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PurchaseQuotationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return purchaseQuotationRepository.findAllWithEagerRelationships(pageable).map(purchaseQuotationMapper::toDto);
    }

    /**
     * Get one purchaseQuotation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseQuotationDTO> findOne(Long id) {
        log.debug("Request to get PurchaseQuotation : {}", id);
        Optional<PurchaseQuotation> quotation = purchaseQuotationRepository.findOneWithEagerRelationships(id);
        return quotation.map(purchaseQuotationMapper::toDto);
    }

    /**
     * Delete the purchaseQuotation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PurchaseQuotation : {}", id);
        purchaseQuotationRepository.deleteById(id);
    }

    private String getRefrenceNumber(OrderType orderType) {
        String nextRefrenceString = null;

        int nextRefrenceNo = purchaseQuotationRepository.findNextRefrenceNo(orderType.toString());
        purchaseQuotationRepository.updateNextRefrenceNo(nextRefrenceNo, nextRefrenceNo + 1, orderType.toString());

        String formtedString = String.format("%05d", nextRefrenceNo);

        int year = Calendar.getInstance().get(Calendar.YEAR);

        if (OrderType.PURCHASE_ORDER.equals(orderType)) {
            nextRefrenceString = "AQ-PO" + year + "-" + formtedString;
        } else if (OrderType.PRODUCT_QUATATION.equals(orderType)) {
            nextRefrenceString = "AQ-QUOT" + year + "-" + formtedString;
        } else if (OrderType.PO_REQUISITION.equals(orderType)) {
            nextRefrenceString = "AQ-POREQ" + year + "-" + formtedString;
        } else {
            nextRefrenceString = "AQ-TAXIN" + year + "-" + formtedString;
        }
        return nextRefrenceString;
    }
    //------------TODO here for dashboard api

    //	public void findtotalsInQuotations(PurchaseQuotationCriteria criteria) {
    //
    //	List<PurchaseQuotationDTO> quotqtionList = purchaseQuotationQueryService.findByCriteria(criteria);
    //		//-----------for total sales -------------
    //	if(criteria == null) {
    //
    //		OrderTypeFilter oderFilter = new OrderTypeFilter();
    //		oderFilter.equals(OrderType.TAX_INVOICE);
    //		criteria.setOrderType(oderFilter);
    //	}

    //	}
}
