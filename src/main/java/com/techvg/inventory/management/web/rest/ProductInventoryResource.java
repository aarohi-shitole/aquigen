package com.techvg.inventory.management.web.rest;

import com.techvg.inventory.management.domain.enumeration.ProductType;
import com.techvg.inventory.management.domain.enumeration.Status;
import com.techvg.inventory.management.domain.enumeration.TransactionType;
import com.techvg.inventory.management.repository.ProductInventoryRepository;
import com.techvg.inventory.management.service.ProductInventoryQueryService;
import com.techvg.inventory.management.service.ProductInventoryService;
import com.techvg.inventory.management.service.ProductTransactionService;
import com.techvg.inventory.management.service.criteria.ProductCriteria;
import com.techvg.inventory.management.service.criteria.ProductInventoryCriteria;
import com.techvg.inventory.management.service.dto.ProductDTO;
import com.techvg.inventory.management.service.dto.ProductInventoryDTO;
import com.techvg.inventory.management.service.dto.ProductTransactionDTO;
import com.techvg.inventory.management.service.dto.SecurityUserDTO;
import com.techvg.inventory.management.service.dto.WareHouseDTO;
import com.techvg.inventory.management.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing
 * {@link com.techvg.inventory.management.domain.ProductInventory}.
 */
@RestController
@RequestMapping("/api")
public class ProductInventoryResource {

    private final Logger log = LoggerFactory.getLogger(ProductInventoryResource.class);

    private static final String ENTITY_NAME = "productInventory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductInventoryService productInventoryService;

    private final ProductInventoryRepository productInventoryRepository;

    private final ProductInventoryQueryService productInventoryQueryService;

    @Autowired
    ProductTransactionService productTransactionService;

    public ProductInventoryResource(
        ProductInventoryService productInventoryService,
        ProductInventoryRepository productInventoryRepository,
        ProductInventoryQueryService productInventoryQueryService
    ) {
        this.productInventoryService = productInventoryService;
        this.productInventoryRepository = productInventoryRepository;
        this.productInventoryQueryService = productInventoryQueryService;
    }

    /**
     * {@code POST  /product-inventories} : Create a new productInventory.
     *
     * @param productInventoryDTO the productInventoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new productInventoryDTO, or with status
     *         {@code 400 (Bad Request)} if the productInventory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-inventories")
    public ResponseEntity<ProductInventoryDTO> createProductInventory(@RequestBody ProductInventoryDTO productInventoryDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProductInventory : {}", productInventoryDTO);
        if (productInventoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new productInventory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductInventoryDTO result = productInventoryService.save(productInventoryDTO);
        return ResponseEntity
            .created(new URI("/api/product-inventories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-inventories/:id} : Updates an existing productInventory.
     *
     * @param id                  the id of the productInventoryDTO to save.
     * @param productInventoryDTO the productInventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated productInventoryDTO, or with status
     *         {@code 400 (Bad Request)} if the productInventoryDTO is not valid, or
     *         with status {@code 500 (Internal Server Error)} if the
     *         productInventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-inventories/{id}")
    public ResponseEntity<ProductInventoryDTO> updateProductInventory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductInventoryDTO productInventoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductInventory : {}, {}", id, productInventoryDTO);
        if (productInventoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productInventoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productInventoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductInventoryDTO result = productInventoryService.save(productInventoryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productInventoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /product-inventories/:id} : Partial updates given fields of an
     * existing productInventory, field will ignore if it is null
     *
     * @param id                  the id of the productInventoryDTO to save.
     * @param productInventoryDTO the productInventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated productInventoryDTO, or with status
     *         {@code 400 (Bad Request)} if the productInventoryDTO is not valid, or
     *         with status {@code 404 (Not Found)} if the productInventoryDTO is not
     *         found, or with status {@code 500 (Internal Server Error)} if the
     *         productInventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-inventories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductInventoryDTO> partialUpdateProductInventory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductInventoryDTO productInventoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductInventory partially : {}, {}", id, productInventoryDTO);
        if (productInventoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productInventoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productInventoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductInventoryDTO> result = productInventoryService.partialUpdate(productInventoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productInventoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-inventories} : get all the productInventories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of productInventories in body.
     */
    @GetMapping("/product-inventories")
    public ResponseEntity<List<ProductInventoryDTO>> getAllProductInventories(
        ProductInventoryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ProductInventories by criteria: {}", criteria);
        Page<ProductInventoryDTO> page = productInventoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-inventories/count} : count all the productInventories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/product-inventories/count")
    public ResponseEntity<Long> countProductInventories(ProductInventoryCriteria criteria) {
        log.debug("REST request to count ProductInventories by criteria: {}", criteria);
        return ResponseEntity.ok().body(productInventoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /product-inventories/stockCount} : count all the productInventories Stock.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     * @throws Exception
     */
    @GetMapping("/product-inventories/stockCount")
    public ResponseEntity<List<ProductDTO>> countProductInventoriesStock(
        ProductInventoryCriteria criteria,
        ProductCriteria pdCriteria,
        @org.springdoc.api.annotations.ParameterObject Pageable page
    ) throws Exception {
        log.debug("REST request to count ProductInventories by criteria: {}", criteria, page);

        Page<ProductDTO> stockCount = productInventoryService.countProductInventoriesStock(criteria, pdCriteria, page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), stockCount);
        return ResponseEntity.ok().headers(headers).body(stockCount.getContent());
    }

    /**
     * {@code GET  /product-inventories/:id} : get the "id" productInventory.
     *
     * @param id the id of the productInventoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the productInventoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-inventories/{id}")
    public ResponseEntity<ProductInventoryDTO> getProductInventory(@PathVariable Long id) {
        log.debug("REST request to get ProductInventory : {}", id);
        Optional<ProductInventoryDTO> productInventoryDTO = productInventoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productInventoryDTO);
    }

    /**
     * {@code DELETE  /product-inventories/:id} : delete the "id" productInventory.
     *
     * @param id the id of the productInventoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-inventories/{id}")
    public ResponseEntity<Void> deleteProductInventory(@PathVariable Long id) {
        log.debug("REST request to delete ProductInventory : {}", id);
        productInventoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /product-inventories/list} : Create a new productInventories.
     *
     * @param productInventoryDTOList the productInventoryDTOList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new productInventoryDTOList, or with status
     *         {@code 400 (Bad Request)} if the productInventory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-inventories/list")
    public ResponseEntity<List<ProductInventoryDTO>> createProductInventoryList(
        @RequestBody List<ProductInventoryDTO> productInventoryDTOList
    ) throws URISyntaxException {
        log.debug("REST request to save ProductInventory : {}", productInventoryDTOList);

        //Added Product Transaction Details
        ProductTransactionDTO prodTran = new ProductTransactionDTO();
        if (productInventoryDTOList.get(0).getInwardQty() != null && !productInventoryDTOList.get(0).getInwardQty().isEmpty()) {
            prodTran.setTransactionType(TransactionType.INWARD_STOCKS);
            prodTran.setTransactionStatus(Status.COMPLETED);
            prodTran.setTransactionDate(new Date().toString());
            prodTran.setDescription("Manual Inward without GR");
        } else {
            if (
                productInventoryDTOList.get(0).getProductTransaction() != null &&
                productInventoryDTOList.get(0).getProductTransaction().getProject() != null
            ) prodTran.setProject(productInventoryDTOList.get(0).getProductTransaction().getProject());

            if (productInventoryDTOList.get(0).getProduct().getProductType() == ProductType.PRODUCT) {
                prodTran.setTransactionType(TransactionType.PRODUCT_CONSUMPTION);
            } else prodTran.setTransactionType(TransactionType.OUTWARDS_CONSUMPTION);

            prodTran.setTransactionStatus(Status.COMPLETED);
            prodTran.setTransactionDate(new Date().toString());
            prodTran.setDescription("Manual Outward without GR");
        }

        if (productInventoryDTOList.size() > 0) {
            prodTran.setWareHouse(productInventoryDTOList.get(0).getWareHouse());
        }
        if (productInventoryDTOList.size() > 0) {
            prodTran.setSecurityUser(productInventoryDTOList.get(0).getSecurityUser());
        }
        ProductTransactionDTO resultProdTran = productTransactionService.save(prodTran);
        //Finished Saving Product Transaction Object

        ProductInventoryDTO result = null;
        List<ProductInventoryDTO> productInventoryList = new ArrayList<ProductInventoryDTO>();
        for (ProductInventoryDTO inventoryObj : productInventoryDTOList) {
            if (inventoryObj.getId() != null) {
                throw new BadRequestAlertException("A new productInventory cannot already have an ID", ENTITY_NAME, "idexists");
            } else if (inventoryObj.getWareHouse() != null && inventoryObj.getProduct() != null) {
                inventoryObj.setProductTransaction(resultProdTran);
                result = productInventoryService.save(inventoryObj);
                productInventoryList.add(result);
            }
        }
        return ResponseEntity
            .created(new URI("/api/product-inventories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(productInventoryList);
    }

    @GetMapping("/product-inventories/lotNo-stockCount")
    public ResponseEntity<List<ProductInventoryDTO>> getLotNoWiseProductInventories(
        ProductInventoryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) throws Exception {
        log.debug("REST request to get ProductInventories by criteria: {}", criteria);
        Page<ProductInventoryDTO> page = productInventoryService.findStockCountByLotNumber(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
