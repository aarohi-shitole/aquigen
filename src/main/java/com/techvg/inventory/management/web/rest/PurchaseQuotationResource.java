package com.techvg.inventory.management.web.rest;

import com.techvg.inventory.management.domain.Count;
import com.techvg.inventory.management.domain.ManagerDashboardData;
import com.techvg.inventory.management.domain.SalesByUser;
import com.techvg.inventory.management.domain.TotalMonthlySale;
import com.techvg.inventory.management.domain.enumeration.NotificationType;
import com.techvg.inventory.management.domain.enumeration.OrderType;
import com.techvg.inventory.management.domain.enumeration.Status;
import com.techvg.inventory.management.repository.PurchaseQuotationRepository;
import com.techvg.inventory.management.service.FileStorageService;
import com.techvg.inventory.management.service.NotificationService;
import com.techvg.inventory.management.service.PurchaseQuotationDetailsQueryService;
import com.techvg.inventory.management.service.PurchaseQuotationQueryService;
import com.techvg.inventory.management.service.PurchaseQuotationService;
import com.techvg.inventory.management.service.SecurityUserQueryService;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationCriteria;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationCriteria.OrderTypeFilter;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationCriteria.StatusFilter;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationDetailsCriteria;
import com.techvg.inventory.management.service.criteria.SecurityUserCriteria;
import com.techvg.inventory.management.service.dto.NotificationDTO;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDTO;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDetailsDTO;
import com.techvg.inventory.management.service.dto.SecurityRoleDTO;
import com.techvg.inventory.management.service.dto.SecurityUserDTO;
import com.techvg.inventory.management.web.rest.errors.BadRequestAlertException;
import com.techvg.inventory.management.web.rest.vm.UploadFileResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing
 * {@link com.techvg.inventory.management.domain.PurchaseQuotation}.
 */
@RestController
@RequestMapping("/api")
public class PurchaseQuotationResource {

    private final Logger log = LoggerFactory.getLogger(PurchaseQuotationResource.class);

    private static final String ENTITY_NAME = "purchaseQuotation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchaseQuotationService purchaseQuotationService;

    private final PurchaseQuotationRepository purchaseQuotationRepository;

    private final PurchaseQuotationQueryService purchaseQuotationQueryService;

    @Autowired
    private SecurityUserQueryService securityUserQueryService;

    @Autowired
    private PurchaseQuotationDetailsQueryService purchaseQuotationDetailsQueryService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private NotificationService notificationService;

    public PurchaseQuotationResource(
        PurchaseQuotationService purchaseQuotationService,
        PurchaseQuotationRepository purchaseQuotationRepository,
        PurchaseQuotationQueryService purchaseQuotationQueryService
    ) {
        this.purchaseQuotationService = purchaseQuotationService;
        this.purchaseQuotationRepository = purchaseQuotationRepository;
        this.purchaseQuotationQueryService = purchaseQuotationQueryService;
    }

    /**
     * {@code POST  /purchase-quotations} : Create a new purchaseQuotation.
     *
     * @param purchaseQuotationDTO the purchaseQuotationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new purchaseQuotationDTO, or with status
     *         {@code 400 (Bad Request)} if the purchaseQuotation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/purchase-quotations")
    public ResponseEntity<PurchaseQuotationDTO> createPurchaseQuotation(@RequestBody PurchaseQuotationDTO purchaseQuotationDTO)
        throws URISyntaxException {
        log.debug("REST request to save PurchaseQuotation : {}", purchaseQuotationDTO);
        if (purchaseQuotationDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchaseQuotation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (purchaseQuotationDTO.getRefrenceNumber() != null) {
            throw new BadRequestAlertException(
                "A new purchaseQuotation cannot already have a Refrence Number",
                ENTITY_NAME,
                "RefrenceNumberexists"
            );
        }

        PurchaseQuotationDTO result = purchaseQuotationService.save(purchaseQuotationDTO);
        if (purchaseQuotationDTO.getOrderType() == OrderType.TAX_INVOICE) {
            long quotationId = purchaseQuotationDTO.getInvoiceRefrence();
            PurchaseQuotationDTO purchaseDto = purchaseQuotationService.findOne(quotationId).get();
            purchaseDto.setInvoiceRefrence(result.getId());

            purchaseQuotationService.save(purchaseDto);
        }
        return ResponseEntity
            .created(new URI("/api/purchase-quotations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /purchase-quotations/:id} : Updates an existing
     * purchaseQuotation.
     *
     * @param id                   the id of the purchaseQuotationDTO to save.
     * @param purchaseQuotationDTO the purchaseQuotationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated purchaseQuotationDTO, or with status
     *         {@code 400 (Bad Request)} if the purchaseQuotationDTO is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         purchaseQuotationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/purchase-quotations/{id}")
    public ResponseEntity<PurchaseQuotationDTO> updatePurchaseQuotation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseQuotationDTO purchaseQuotationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PurchaseQuotation : {}, {}", id, purchaseQuotationDTO);
        if (purchaseQuotationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseQuotationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseQuotationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if (purchaseQuotationDTO.getOrderType() == OrderType.PURCHASE_ORDER && purchaseQuotationDTO.getOrderStatus() == Status.APPROVED) {
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setNotificationType(NotificationType.ALERT);
            notificationDTO.setIsActionRequired(true);
            notificationDTO.setIsRead(false);

            notificationDTO.setFreeField1(purchaseQuotationDTO.getId() + "");
            notificationDTO.setTitle("PO Approved");
            SecurityRoleDTO role = new SecurityRoleDTO();
            role.setId(5L);
            notificationDTO.setSecurityRole(role);
            notificationDTO.setMassage("Purchase order approved by " + purchaseQuotationDTO.getSecurityUser().getFirstName());
            notificationDTO.setSecurityUser(purchaseQuotationDTO.getSecurityUser());
            purchaseQuotationDTO.getId();
            notificationDTO.setPurchaseQuotationId(purchaseQuotationDTO.getId());
            notificationService.save(notificationDTO);
        }
        Optional<PurchaseQuotationDTO> result = purchaseQuotationService.partialUpdate(purchaseQuotationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchaseQuotationDTO.getId().toString()))
            .body(result.get());
    }

    /**
     * {@code PATCH  /purchase-quotations/:id} : Partial updates given fields of an
     * existing purchaseQuotation, field will ignore if it is null
     *
     * @param id                   the id of the purchaseQuotationDTO to save.
     * @param purchaseQuotationDTO the purchaseQuotationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated purchaseQuotationDTO, or with status
     *         {@code 400 (Bad Request)} if the purchaseQuotationDTO is not valid,
     *         or with status {@code 404 (Not Found)} if the purchaseQuotationDTO is
     *         not found, or with status {@code 500 (Internal Server Error)} if the
     *         purchaseQuotationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/purchase-quotations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PurchaseQuotationDTO> partialUpdatePurchaseQuotation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseQuotationDTO purchaseQuotationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PurchaseQuotation partially : {}, {}", id, purchaseQuotationDTO);
        if (purchaseQuotationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseQuotationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseQuotationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PurchaseQuotationDTO> result = purchaseQuotationService.partialUpdate(purchaseQuotationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchaseQuotationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /purchase-quotations} : get all the purchaseQuotations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of purchaseQuotations in body.
     */
    @GetMapping("/purchase-quotations")
    public ResponseEntity<List<PurchaseQuotationDTO>> getAllPurchaseQuotations(
        PurchaseQuotationCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PurchaseQuotations by criteria: {}", criteria);
        Page<PurchaseQuotationDTO> page = purchaseQuotationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /purchase-quotations} : get all the purchaseQuotations by client.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of purchaseQuotations in body.
     */
    @GetMapping("/client-purchase-quotations")
    public ResponseEntity<List<PurchaseQuotationDTO>> getAllPurchaseQuotationsByClient(
        PurchaseQuotationCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PurchaseQuotations by criteria: {}", criteria);
        // compulsory criteria clientId and ProductId (in freefield2)
        LongFilter prodIdFilter = new LongFilter();
        prodIdFilter.setEquals(Long.parseLong(criteria.getFreeField2().getEquals()));

        List<PurchaseQuotationDTO> arrPurchaseQuotations = new ArrayList<PurchaseQuotationDTO>();
        // remove freefield 2 from criteria
        criteria.setFreeField2(null);
        List<PurchaseQuotationDTO> page = purchaseQuotationQueryService.findByCriteria(criteria);

        for (PurchaseQuotationDTO obj : page) {
            for (PurchaseQuotationDetailsDTO details : obj.getPurchaseQuotationDetails()) {
                List<PurchaseQuotationDetailsDTO> arrPurchaseQuotationDetails = new ArrayList<PurchaseQuotationDetailsDTO>();
                if (details.getProduct().getId() == prodIdFilter.getEquals()) {
                    arrPurchaseQuotationDetails.add(details);
                    obj.setPurchaseQuotationDetails(arrPurchaseQuotationDetails);
                    arrPurchaseQuotations.add(obj);
                    break;
                }
            }
            // PurchaseQuotationDetailsCriteria quoCriteria = new
            // PurchaseQuotationDetailsCriteria();
            // quoCriteria.setProductId(prodIdFilter);
            // LongFilter quoIdFilter = new LongFilter();
            // quoIdFilter.setEquals(obj.getId());
            // quoCriteria.setPurchaseQuotationId(quoIdFilter);
            // List<PurchaseQuotationDetailsDTO> purchaseQuotationDetailsList =
            // purchaseQuotationDetailsQueryService.findByCriteria(quoCriteria);
            // arrPurchaseQuotationDetails.addAll(purchaseQuotationDetailsList);
        }

        return ResponseEntity.ok().body(arrPurchaseQuotations);
    }

    /**
     * {@code GET  /purchase-quotations} : get all the purchaseQuotations by client.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of purchaseQuotations in body.
     */
    @GetMapping("/product-purchase-quotations")
    public ResponseEntity<List<PurchaseQuotationDTO>> getAllPurchaseQuotationsByProduct(
        PurchaseQuotationCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PurchaseQuotations by criteria: {}", criteria);
        // compulsory criteria ProductId (in freefield2)
        List<PurchaseQuotationDTO> arrQuotations = new ArrayList<PurchaseQuotationDTO>();

        LongFilter prodIdFilter = new LongFilter();
        prodIdFilter.setEquals(Long.parseLong(criteria.getFreeField2().getEquals()));
        PurchaseQuotationDetailsCriteria quoCriteria = new PurchaseQuotationDetailsCriteria();
        quoCriteria.setProductId(prodIdFilter);
        Page<PurchaseQuotationDetailsDTO> purchaseQuotationDetailsList = purchaseQuotationDetailsQueryService.findByCriteria(
            quoCriteria,
            pageable
        );

        for (PurchaseQuotationDetailsDTO details : purchaseQuotationDetailsList.getContent()) {
            Optional<PurchaseQuotationDTO> findOne = purchaseQuotationService.findOne(details.getPurchaseQuotationId());
            ArrayList<PurchaseQuotationDetailsDTO> arrDetails = new ArrayList<PurchaseQuotationDetailsDTO>();
            arrDetails.add(details);
            findOne.get().setPurchaseQuotationDetails(arrDetails);
            arrQuotations.add(findOne.get());
        }
        return ResponseEntity.ok().body(arrQuotations);
    }

    @GetMapping("/purchase-quotations/sale")
    public ResponseEntity<List<TotalMonthlySale>> getHistorialSales(PurchaseQuotationCriteria poCriteria) {
        List<TotalMonthlySale> totalMonthlySaleList = new ArrayList<TotalMonthlySale>();

        for (int i = 0; i < 8; i++) {
            PurchaseQuotationCriteria criteria = new PurchaseQuotationCriteria();
            OrderTypeFilter orderType = new OrderTypeFilter();
            orderType.setEquals(OrderType.PRODUCT_QUATATION);
            criteria.setOrderType(orderType);
            if (poCriteria != null && poCriteria.getSecurityUserId() != null) {
                criteria.setSecurityUserId(poCriteria.getSecurityUserId());
            }
            InstantFilter poDate = new InstantFilter();
            Instant instantNow = getInstantDate(-i);
            poDate.setLessThanOrEqual(instantNow);
            Instant instantNow1 = getInstantDate(-(i + 1));
            poDate.setGreaterThan(instantNow1);
            criteria.setPoDate(poDate);

            StatusFilter orderStatus = new StatusFilter();
            List<Status> in = new ArrayList<>();
            in.add(Status.REQUESTED);
            in.add(Status.APPROVED);
            in.add(Status.COMPLETED);
            orderStatus.setIn(in);
            criteria.setOrderStatus(orderStatus);
            // Approved Clients
            // Requested is give Quate

            Pageable pageable = Pageable.ofSize(1000000);
            Page<PurchaseQuotationDTO> page = purchaseQuotationQueryService.findByCriteria(criteria, pageable);

            TotalMonthlySale totalMonthlySale = new TotalMonthlySale();

            Calendar c1 = Calendar.getInstance();

            if (i != 0) {
                c1.add(Calendar.MONTH, -i);
            }

            Date dateOne = c1.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            totalMonthlySale.date = sdf.format(dateOne);

            int month = i + 1;
            totalMonthlySale.month = "Month " + month;
            totalMonthlySale.totalQuoate = page.getContent().size();
            for (PurchaseQuotationDTO purchaseQuotationDTO : page.getContent()) {
                if (purchaseQuotationDTO.getOrderStatus() == Status.COMPLETED) {
                    totalMonthlySale.convertedClients += 1;
                }
            }

            totalMonthlySaleList.add(totalMonthlySale);
        }

        return ResponseEntity.ok().body(totalMonthlySaleList);
    }

    private Instant getInstantDate(int monthAgo) {
        Calendar c1 = Calendar.getInstance();

        if (monthAgo != 0) {
            c1.add(Calendar.MONTH, monthAgo);
        }

        Date dateOne = c1.getTime();

        return dateOne.toInstant();
    }

    @GetMapping("/purchase-quotations/users-sale")
    public ResponseEntity<List<SalesByUser>> getHistorialSalesByUser() {
        List<SalesByUser> totalMonthlySaleList = new ArrayList<SalesByUser>();

        Pageable pageable = Pageable.ofSize(3000000);

        SecurityUserCriteria userCriteria = new SecurityUserCriteria();

        LongFilter securityRoleId = new LongFilter();
        Long roleId = 6L;
        securityRoleId.setEquals(roleId);
        userCriteria.setSecurityRoleId(securityRoleId);

        Page<SecurityUserDTO> userPage = securityUserQueryService.findByCriteria(userCriteria, pageable);

        for (SecurityUserDTO securityUserDTO : userPage.getContent()) {
            PurchaseQuotationCriteria criteria = new PurchaseQuotationCriteria();

            LongFilter securityUserId = new LongFilter();
            securityUserId.setEquals(securityUserDTO.getId());
            criteria.setSecurityUserId(securityUserId);

            OrderTypeFilter orderType = new OrderTypeFilter();
            orderType.setEquals(OrderType.PRODUCT_QUATATION);
            criteria.setOrderType(orderType);

            InstantFilter poDate = new InstantFilter();
            Instant instantNow = getInstantDate(0);
            poDate.setLessThanOrEqual(instantNow);
            Instant instantNow1 = getInstantDate(-1);
            poDate.setGreaterThan(instantNow1);
            criteria.setPoDate(poDate);

            StatusFilter orderStatus = new StatusFilter();
            List<Status> in = new ArrayList<>();
            in.add(Status.REQUESTED);
            in.add(Status.APPROVED);
            in.add(Status.COMPLETED);
            orderStatus.setIn(in);
            criteria.setOrderStatus(orderStatus);
            Page<PurchaseQuotationDTO> page = purchaseQuotationQueryService.findByCriteria(criteria, pageable);

            SalesByUser salesByUser = new SalesByUser();
            salesByUser.totalQuoate = page.getContent().size();
            salesByUser.securityUser = securityUserDTO;
            salesByUser.convertedClients = 0;
            for (PurchaseQuotationDTO purchaseQuotationDTO : page.getContent()) {
                if (purchaseQuotationDTO.getOrderStatus() == Status.COMPLETED) {
                    salesByUser.convertedClients += 1;
                }
            }

            totalMonthlySaleList.add(salesByUser);
        }

        return ResponseEntity.ok().body(totalMonthlySaleList);
    }

    @GetMapping("/purchase-quotations/manager-data")
    public ResponseEntity<ManagerDashboardData> getManagerDashboardData(PurchaseQuotationCriteria poCriteria) {
        ManagerDashboardData totalDashboardData = new ManagerDashboardData();
        //set type PO_REQUISITION
        OrderTypeFilter orderType = new OrderTypeFilter();
        orderType.setEquals(OrderType.PO_REQUISITION);
        poCriteria.setOrderType(orderType);

        InstantFilter poDate = new InstantFilter();
        Instant instantNow = getInstantDate(0);
        poDate.setLessThanOrEqual(instantNow);
        Instant instantNow1 = getInstantDate(-1);
        poDate.setGreaterThan(instantNow1);
        poCriteria.setPoDate(poDate);

        Pageable pageable = Pageable.ofSize(1000000);
        Page<PurchaseQuotationDTO> page = purchaseQuotationQueryService.findByCriteria(poCriteria, pageable);
        double total = 0;

        totalDashboardData.poRequsitionCount = page.getContent().size();

        for (PurchaseQuotationDTO purchaseQuotationDTO : page.getContent()) {
            total += purchaseQuotationDTO.getTotalPOAmount();
        }
        totalDashboardData.poRequsitionAmount = total;

        PurchaseQuotationCriteria criteria = new PurchaseQuotationCriteria();
        OrderTypeFilter orderType1 = new OrderTypeFilter();
        orderType1.setEquals(OrderType.PURCHASE_ORDER);
        criteria.setOrderType(orderType1);

        StatusFilter orderStatus = new StatusFilter();
        orderStatus.setEquals(Status.APPROVED);
        criteria.setOrderStatus(orderStatus);

        //for get approval id for approval users
        criteria.setApprovalIdId(poCriteria.getSecurityUserId());

        InstantFilter poDate1 = new InstantFilter();
        Instant instantNow2 = getInstantDate(0);
        poDate1.setLessThanOrEqual(instantNow2);
        Instant instantNow3 = getInstantDate(-1);
        poDate1.setGreaterThan(instantNow3);

        criteria.setPoDate(poDate1);

        double total2 = 0;
        Pageable pageable1 = Pageable.ofSize(1000000);
        Page<PurchaseQuotationDTO> po = purchaseQuotationQueryService.findByCriteria(criteria, pageable1);

        totalDashboardData.approvalPoCount = po.getContent().size();
        for (PurchaseQuotationDTO purchaseQuotationDTO1 : po.getContent()) {
            total2 += purchaseQuotationDTO1.getTotalPOAmount();
        }

        totalDashboardData.approvalPoAmount = total2;

        return ResponseEntity.ok().body(totalDashboardData);
    }

    @GetMapping("/purchase-quotations/revenue")
    public ResponseEntity<List<TotalMonthlySale>> getHistorialRevenue() {
        List<TotalMonthlySale> totalWeeklyRevenueList = new ArrayList<TotalMonthlySale>();
        for (int i = 0; i < 8; i++) {
            PurchaseQuotationCriteria criteria = new PurchaseQuotationCriteria();
            OrderTypeFilter orderType = new OrderTypeFilter();
            orderType.setEquals(OrderType.PRODUCT_QUATATION);
            criteria.setOrderType(orderType);

            InstantFilter poDate = new InstantFilter();
            Instant now = Instant.now();
            Instant day = now.minus(i, ChronoUnit.DAYS);

            poDate.setGreaterThan(day);
            Instant yesterday = day.minus(-1, ChronoUnit.DAYS);
            poDate.setLessThanOrEqual(yesterday);
            criteria.setPoDate(poDate);
            Pageable pageable = Pageable.ofSize(1000000);
            Page<PurchaseQuotationDTO> page = purchaseQuotationQueryService.findByCriteria(criteria, pageable);

            TotalMonthlySale totalWeeklyRevenue = new TotalMonthlySale();
            totalWeeklyRevenue.date = poDate.getGreaterThan().toString();
            totalWeeklyRevenue.convertedClients = page.getTotalElements();

            totalWeeklyRevenueList.add(totalWeeklyRevenue);
        }

        return ResponseEntity.ok().body(totalWeeklyRevenueList);
    }

    /**
     * {@code GET  /purchase-quotations/count} : count all the purchaseQuotations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/purchase-quotations/count")
    public ResponseEntity<Count> countPurchaseQuotations(PurchaseQuotationCriteria criteria) {
        log.debug("REST request to count PurchaseQuotations by criteria: {}", criteria);
        Count count = new Count();
        count.count = purchaseQuotationQueryService.countByCriteria(criteria);
        return ResponseEntity.ok().body(count);
    }

    /**
     * {@code GET  /purchase-quotations/:id} : get the "id" purchaseQuotation.
     *
     * @param id the id of the purchaseQuotationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the purchaseQuotationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/purchase-quotations/{id}")
    public ResponseEntity<PurchaseQuotationDTO> getPurchaseQuotation(@PathVariable Long id) {
        log.debug("REST request to get PurchaseQuotation : {}", id);
        Optional<PurchaseQuotationDTO> purchaseQuotationDTO = purchaseQuotationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchaseQuotationDTO);
    }

    /**
     * {@code DELETE  /purchase-quotations/:id} : delete the "id" purchaseQuotation.
     *
     * @param id the id of the purchaseQuotationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/purchase-quotations/{id}")
    public ResponseEntity<Void> deletePurchaseQuotation(@PathVariable Long id) {
        log.debug("REST request to delete PurchaseQuotation : {}", id);
        purchaseQuotationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    // ------------TODO here for dashboard api-------------

    /**
     * {@code GET  /purchase-quotations/count} : count all the purchaseQuotations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    /*
     * @GetMapping("/purchase-quotations/dashboardCount") public
     * ResponseEntity<Long> countQuotationsForDashboard(PurchaseQuotationCriteria
     * criteria) {
     * log.debug("REST request to count PurchaseQuotations by criteria: {}",
     * criteria);
     *
     * purchaseQuotationService.findtotalsInQuotations(criteria); return
     * ResponseEntity.ok().body(); }
     */

    @PostMapping("/purchase-quotations/uploadPdfFile/{purchaseQuotationId}")
    public ResponseEntity<UploadFileResponse> uploadFile(
        @RequestParam(name = "file", required = false) MultipartFile file,
        @PathVariable Long purchaseQuotationId
    ) {
        String fileName = "po_" + purchaseQuotationId + ".pdf";

        fileStorageService.storeFile(file, fileName);

        UploadFileResponse uploadResponse = new UploadFileResponse(fileName);
        uploadResponse.setPurchaseQuotationId(purchaseQuotationId);
        if (uploadResponse != null) {
            Optional<PurchaseQuotationDTO> purchaseQuotationDTO = purchaseQuotationService.findOne(purchaseQuotationId);
            purchaseQuotationDTO.get().setId(purchaseQuotationId);
            purchaseQuotationDTO.get().setPoFileName(fileName);
            PurchaseQuotationDTO result = purchaseQuotationService.save(purchaseQuotationDTO.get());
        }
        return ResponseEntity.ok().body(uploadResponse);
    }

    @GetMapping("/purchase-quotations/download-file/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.print("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/pdf";
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
}
