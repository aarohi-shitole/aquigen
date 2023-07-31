package com.techvg.inventory.management.service;

import com.techvg.inventory.management.domain.Product;
import com.techvg.inventory.management.domain.ProductInventory;
import com.techvg.inventory.management.domain.enumeration.ProductType;
import com.techvg.inventory.management.domain.enumeration.Status;
import com.techvg.inventory.management.domain.enumeration.TransactionType;
import com.techvg.inventory.management.repository.ProductInventoryRepository;
import com.techvg.inventory.management.service.criteria.ProductCriteria;
import com.techvg.inventory.management.service.criteria.ProductCriteria.ProductTypeFilter;
import com.techvg.inventory.management.service.criteria.ProductInventoryCriteria;
import com.techvg.inventory.management.service.criteria.WareHouseCriteria;
import com.techvg.inventory.management.service.dto.ProductDTO;
import com.techvg.inventory.management.service.dto.ProductInventoryDTO;
import com.techvg.inventory.management.service.dto.ProductTransactionDTO;
import com.techvg.inventory.management.service.dto.TransferDTO;
import com.techvg.inventory.management.service.dto.TransferDetailsApprovalsDTO;
import com.techvg.inventory.management.service.dto.WareHouseDTO;
import com.techvg.inventory.management.service.mapper.ProductInventoryMapper;
import com.techvg.inventory.management.web.rest.errors.BadRequestAlertException;
import com.techvg.inventory.management.web.rest.errors.NotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service Implementation for managing {@link ProductInventory}.
 */
@Service
@Transactional
public class ProductInventoryService {

    private final Logger log = LoggerFactory.getLogger(ProductInventoryService.class);

    private final ProductInventoryRepository productInventoryRepository;

    private final ProductInventoryMapper productInventoryMapper;

    @Autowired
    private ProductQueryService productQueryService;

    @Autowired
    private ProductInventoryQueryService productInventoryQueryService;

    @Autowired
    private WareHouseQueryService wareHouseQueryService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private ProductTransactionService productTransactionService;

    private final ProductService productService;

    public ProductInventoryService(
        ProductInventoryRepository productInventoryRepository,
        ProductInventoryMapper productInventoryMapper,
        ProductService productService
    ) {
        this.productInventoryRepository = productInventoryRepository;
        this.productInventoryMapper = productInventoryMapper;
        this.productService = productService;
    }

    /**
     * Save a productInventory.
     *
     * @param productInventoryDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductInventoryDTO save(ProductInventoryDTO productInventoryDTO) {
        log.debug("Request to save ProductInventory : {}", productInventoryDTO);
        ProductInventory productInventory = productInventoryMapper.toEntity(productInventoryDTO);
        productInventory = productInventoryRepository.save(productInventory);
        return productInventoryMapper.toDto(productInventory);
    }

    /**
     * Partially update a productInventory.
     *
     * @param productInventoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductInventoryDTO> partialUpdate(ProductInventoryDTO productInventoryDTO) {
        log.debug("Request to partially update ProductInventory : {}", productInventoryDTO);

        return productInventoryRepository
            .findById(productInventoryDTO.getId())
            .map(existingProductInventory -> {
                productInventoryMapper.partialUpdate(existingProductInventory, productInventoryDTO);

                return existingProductInventory;
            })
            .map(productInventoryRepository::save)
            .map(productInventoryMapper::toDto);
    }

    /**
     * Get all the productInventories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductInventoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductInventories");
        return productInventoryRepository.findAll(pageable).map(productInventoryMapper::toDto);
    }

    /**
     * Get all the productInventories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProductInventoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productInventoryRepository.findAllWithEagerRelationships(pageable).map(productInventoryMapper::toDto);
    }

    /**
     * Get one productInventory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductInventoryDTO> findOne(Long id) {
        log.debug("Request to get ProductInventory : {}", id);
        return productInventoryRepository.findOneWithEagerRelationships(id).map(productInventoryMapper::toDto);
    }

    /**
     * Delete the productInventory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductInventory : {}", id);
        productInventoryRepository.deleteById(id);
    }

    /**
     * Get productInventories Stock Count.
     *
     * @param ProductInventoryDTO List to calculate totalQuantityAvailable .
     * @return int totalQuantityAvailable count.
     */
    public int countInventoryStock(List<ProductInventoryDTO> stockList) {
        int sumInwardQty = 0;
        int sumOutwardQty = 0;
        for (ProductInventoryDTO stockObj : stockList) {
            if (stockObj.getInwardQty() != null) {
                double stockcount = Double.parseDouble(stockObj.getInwardQty());

                sumInwardQty += stockcount;
            } else if (stockObj.getOutwardQty() != null) {
                double stockcount = Double.parseDouble(stockObj.getOutwardQty());
                sumOutwardQty += stockcount;
            }
        }
        int totalQuantityAvailable = sumInwardQty - sumOutwardQty;
        return totalQuantityAvailable;
    }

    /**
     * Get productInventories Stock Count.
     *
     * @param Previous ProductInventoryDTO and new ProductInventoryDTO to calculate totalQuantityAvailable .
     * @return ProductInventoryDTO with totalQuantity count available.
     */
    public ProductInventoryDTO countTotalInventoryStock(ProductInventoryDTO oldObj, ProductInventoryDTO newObj) {
        Long inwStockOldcount = oldObj.getInwardQty() == null ? 0 : Long.valueOf(oldObj.getInwardQty()).longValue();
        Long inwStockNewcount = newObj.getInwardQty() == null ? 0 : Long.valueOf(newObj.getInwardQty()).longValue();

        Long otwStockOldcount = oldObj.getOutwardQty() == null ? 0 : Long.valueOf(oldObj.getOutwardQty()).longValue();
        Long otwStockNewcount = newObj.getOutwardQty() == null ? 0 : Long.valueOf(newObj.getOutwardQty()).longValue();

        Long sumInwardQty = inwStockOldcount + inwStockNewcount;
        Long sumOutwardQty = otwStockOldcount + otwStockNewcount;
        Long total = sumInwardQty - sumOutwardQty;
        oldObj.setInwardQty(sumInwardQty.toString());
        oldObj.setOutwardQty(sumOutwardQty.toString());
        oldObj.setTotalQuanity(total.toString());

        return oldObj;
    }

    /**
     * Get all the productInventories Stock Count.
     *
     * @param ProductInventoryCriteria criteria to calculate.
     * @return the list of product entities.
     * @throws Exception
     */
    public Page<ProductDTO> countProductInventoriesStock(ProductInventoryCriteria criteria, ProductCriteria pdCriteria, Pageable page)
        throws Exception {
        List<ProductDTO> productsList = new ArrayList<ProductDTO>();

        // --------------------------If product search criteria is given

        if (pdCriteria.getCasNumber() != null || pdCriteria.getCatlogNumber() != null || pdCriteria.getProductName() != null) {
            List<ProductDTO> productList = productQueryService.findByCriteria(pdCriteria);

            if (criteria.getWareHouseId() != null) {
                for (ProductDTO productObj : productList) {
                    LongFilter idFilter = new LongFilter();
                    idFilter.setEquals(productObj.getId());
                    criteria.setProductId(idFilter);

                    List<ProductInventoryDTO> stockList = productInventoryQueryService.findByCriteria(criteria);
                    int productStock = this.countInventoryStock(stockList);
                    productObj.setTotalStock(productStock);
                    productObj.setWareHouseId(criteria.getWareHouseId().getEquals());
                    productsList.add(productObj);
                }
                return this.convertListIntoPagable(productsList, page);
            } else if (criteria.getWareHouseId() == null) {
                WareHouseCriteria wareHouseCri = new WareHouseCriteria();
                List<WareHouseDTO> wareHouseList = wareHouseQueryService.findByCriteria(wareHouseCri);

                for (WareHouseDTO wareaHouseObj : wareHouseList) {
                    LongFilter idFilter = new LongFilter();
                    idFilter.setEquals(wareaHouseObj.getId());
                    criteria.setWareHouseId(idFilter);
                    //List<ProductDTO> productLists = productQueryService.findByCriteria(pdCriteria);
                    for (ProductDTO productObj : productList) {
                        idFilter = new LongFilter();
                        idFilter.setEquals(productObj.getId());
                        criteria.setProductId(idFilter);
                        List<ProductInventoryDTO> stockLists = productInventoryQueryService.findByCriteria(criteria);
                        if (!stockLists.isEmpty()) {
                            int productStock = this.countInventoryStock(stockLists);
                            productObj.setTotalStock(productStock);
                            productObj.setWareHouseId(wareaHouseObj.getId());
                            productsList.add(productObj);
                        }
                    }
                }
                return this.convertListIntoPagable(productsList, page);
            }
        } else if (criteria != null) { //inventory criteria is not null
            // ---------------- Calculate Stock when ProductId not given in
            // Criteria---------------
            if (criteria.getProductId() == null && criteria.getWareHouseId() != null) {
                List<ProductDTO> productLists = productQueryService.findByCriteria(pdCriteria);

                for (ProductDTO productObj : productLists) {
                    LongFilter idFilter = new LongFilter();
                    idFilter.setEquals(productObj.getId());
                    criteria.setProductId(idFilter);

                    List<ProductInventoryDTO> stockList = productInventoryQueryService.findByCriteria(criteria);
                    int productStock = this.countInventoryStock(stockList);
                    productObj.setTotalStock(productStock);
                    productObj.setWareHouseId(criteria.getWareHouseId().getEquals());
                    if (productStock > 0) {
                        productsList.add(productObj);
                    }
                }
                return this.convertListIntoPagable(productsList, page);
            } // both product Id and warehouse id available
            else if (criteria.getWareHouseId() != null && criteria.getProductId() != null) {
                List<ProductInventoryDTO> stockPage = productInventoryQueryService.findByCriteria(criteria);

                if (!stockPage.isEmpty()) {
                    int productStock = this.countInventoryStock(stockPage);
                    ProductDTO product = stockPage.iterator().next().getProduct();
                    product.setWareHouseId(criteria.getWareHouseId().getEquals());
                    product.setTotalStock(productStock);
                    if (productStock > 0) {
                        productsList.add(product);
                    }
                } else {
                    pdCriteria.setId(criteria.getProductId());
                    List<ProductDTO> productList = productQueryService.findByCriteria(pdCriteria);
                    ProductDTO product = productList.get(0);
                    product.setWareHouseId(criteria.getWareHouseId().getEquals());
                }
                return this.convertListIntoPagable(productsList, page);
            }
            // ---------------------Calculate Stock when productId is given in
            // Criteria---------------

            if (criteria.getWareHouseId() == null && criteria.getProductId() != null) {
                WareHouseCriteria wareHouseCri = new WareHouseCriteria();
                List<WareHouseDTO> wareHouseList = wareHouseQueryService.findByCriteria(wareHouseCri);
                for (WareHouseDTO wareaHouseObj : wareHouseList) {
                    LongFilter idFilter = new LongFilter();
                    idFilter.setEquals(wareaHouseObj.getId());
                    criteria.setWareHouseId(idFilter);

                    List<ProductInventoryDTO> stockLists = productInventoryQueryService.findByCriteria(criteria);
                    if (!stockLists.isEmpty()) {
                        int productStock = this.countInventoryStock(stockLists);
                        ProductDTO product = stockLists.iterator().next().getProduct();
                        product.setTotalStock(productStock);
                        product.setWareHouseId(wareaHouseObj.getId());
                        if (productStock > 0) {
                            productsList.add(product);
                        }
                    }

                    return this.convertListIntoPagable(productsList, page);
                }
            }
            // ---------------- Calculate Stock when WareHouseId and productId not given in
            // Criteria---------------
            else {
                //-------------------------------
                WareHouseCriteria wareHouseCri = new WareHouseCriteria();
                //get all warehouses
                List<WareHouseDTO> wareHouseList = wareHouseQueryService.findByCriteria(wareHouseCri);
                int productStock = 0;

                for (WareHouseDTO wareaHouseObj : wareHouseList) {
                    LongFilter idFilter = new LongFilter();
                    idFilter.setEquals(wareaHouseObj.getId());
                    criteria.setWareHouseId(idFilter);

                    List<Long> productIdList = productInventoryRepository.findInventoryProductByWarehouse(wareaHouseObj.getId());

                    if (productIdList == null || productIdList.size() == 0) {
                        continue;
                    }

                    for (Long productid : productIdList) {
                        if (productid == null || productid == 0) {
                            continue;
                        }

                        Optional<ProductDTO> productObj = productService.findOne(productid);
                        ProductDTO productDTO = productObj.get();
                        if (pdCriteria != null && pdCriteria.getProductType() != null && !pdCriteria.getProductType().getIn().isEmpty()) {
                            if (pdCriteria.getProductType().getIn().get(0) != productDTO.getProductType()) {
                                continue;
                            }
                        }

                        idFilter = new LongFilter();
                        idFilter.setEquals(productid);
                        criteria.setProductId(idFilter);

                        List<ProductInventoryDTO> stockLists = productInventoryQueryService.findByCriteria(criteria);

                        if (!stockLists.isEmpty()) {
                            productStock = this.countInventoryStock(stockLists);
                            if (productStock > 0) {
                                productDTO.setTotalStock(productStock);
                                productDTO.setWareHouseId(wareaHouseObj.getId());
                                productsList.add(productDTO);
                            }
                        }
                    }
                }
                return this.convertListIntoPagable(productsList, page);
            }
        }
        return this.convertListIntoPagable(productsList, page);
    }

    /**
     * Get all the productInventories Stock Count lot number wise.
     *
     * @param ProductInventoryCriteria criteria to calculate.
     * @return the Page of ProductInventoryDTO .
     * @throws Exception
     */

    public Page<ProductInventoryDTO> findStockCountByLotNumber(ProductInventoryCriteria criteria, Pageable page) throws Exception {
        //if both product Id and warehouse id available

        if (criteria.getWareHouseId() != null && criteria.getProductId() != null) {
            Page<ProductInventoryDTO> stockPage = productInventoryQueryService.findByCriteria(criteria, page);
            List<ProductInventoryDTO> stockList = stockPage.getContent();

            HashMap<String, ProductInventoryDTO> stockMap = new HashMap<>();

            List<ProductInventoryDTO> lotWiseList = new ArrayList<ProductInventoryDTO>();
            if (!stockList.isEmpty()) {
                for (ProductInventoryDTO inventoryObj : stockList) {
                    String lotNo = inventoryObj.getLotNo();
                    if (stockMap.containsKey(lotNo)) {
                        stockMap.put(lotNo, countTotalInventoryStock(stockMap.get(lotNo), inventoryObj));
                    } else {
                        //singleMap.put(lotNo, inventoryObj);
                        stockMap.put(lotNo, countTotalInventoryStock(inventoryObj, new ProductInventoryDTO()));
                    }
                }
            }
            //-------------iterate HashMap (singleMap) for ProductInventoryDTO List
            for (Entry<String, ProductInventoryDTO> entry : stockMap.entrySet()) {
                ProductInventoryDTO invObject = entry.getValue();
                lotWiseList.add(invObject);
            }

            //--------------Convert List to page for pagination
            int startOfPage = page.getPageNumber() * page.getPageSize();
            if (startOfPage > lotWiseList.size()) {
                new PageImpl<>(new ArrayList<>(), page, 0);
            }

            int endOfPage = Math.min(startOfPage + page.getPageSize(), lotWiseList.size());
            Page<ProductInventoryDTO> stockPages = new PageImpl<>(lotWiseList.subList(startOfPage, endOfPage), page, lotWiseList.size());

            return stockPages;
        } else {
            throw new NotFoundException("wareahouseId and productId could not be null");
        }
    }

    public Page<ProductDTO> convertListIntoPagable(List<ProductDTO> productsList, Pageable page) {
        int startOfPage = page.getPageNumber() * page.getPageSize();
        if (startOfPage > productsList.size()) {
            return new PageImpl<>(new ArrayList<>(), page, 0);
        }

        int endOfPage = Math.min(startOfPage + page.getPageSize(), productsList.size());
        return new PageImpl<>(productsList.subList(startOfPage, endOfPage), page, productsList.size());
    }

    //Add inventory after transfer recieved
    /**
     * update a Product Inventory and Product transaction for consumption.
     *
     * @param transferDetailsApprovalsDTOList the entity to save.
     */

    public boolean addTransferedStockOfInventory(List<TransferDetailsApprovalsDTO> transferApprovalsList) {
        List<ProductInventoryDTO> productInventoryDTOList = new ArrayList<>();
        ProductInventoryDTO proInventory = null;
        for (TransferDetailsApprovalsDTO transApprovalObj : transferApprovalsList) {
            if (transApprovalObj.getId() != null) {
                proInventory = new ProductInventoryDTO();
                proInventory.setInwardOutwardDate(transApprovalObj.getApprovalDate());
                proInventory.setInwardQty(transApprovalObj.getQtyApproved().toString());
                // TODO proInventory.getLotNo()(transApprovalObj.);
                proInventory.setProduct(transApprovalObj.getProduct());
                proInventory.setSecurityUser(transApprovalObj.getSecurityUser());
                Optional<TransferDTO> transfer = transferService.findOne(transApprovalObj.getTransfer().getId());
                String sourceWh = transfer.get().getDestinationWareHouse();
                Long whId = Long.valueOf(sourceWh).longValue();
                Optional<WareHouseDTO> wareHouseDTO = wareHouseService.findOne(whId);
                proInventory.setWareHouse(wareHouseDTO.get());
            }
            productInventoryDTOList.add(proInventory);
        }

        // Added Product Transaction Details
        ProductTransactionDTO prodTran = new ProductTransactionDTO();
        if (productInventoryDTOList.get(0).getInwardQty() != null && !productInventoryDTOList.get(0).getInwardQty().isEmpty()) {
            prodTran.setTransactionType(TransactionType.TRANSFER_STOCKS_INWARD);
            prodTran.setTransactionStatus(Status.COMPLETED);
            prodTran.setTransactionDate(new Date().toString());
            prodTran.setDescription("Inward for transfer");
        }

        if (productInventoryDTOList.size() > 0) {
            prodTran.setWareHouse(productInventoryDTOList.get(0).getWareHouse());
        }
        if (productInventoryDTOList.size() > 0) {
            prodTran.setSecurityUser(productInventoryDTOList.get(0).getSecurityUser());
        }
        ProductTransactionDTO resultProdTran = productTransactionService.save(prodTran);
        // Finished Saving Product Transaction Object

        for (ProductInventoryDTO inventoryObj : productInventoryDTOList) {
            if (inventoryObj.getId() != null) {
                throw new BadRequestAlertException("A new productInventory cannot already have an ID", "productInventory", "idexists");
            } else if (inventoryObj.getWareHouse() != null && inventoryObj.getProduct() != null) {
                ProductInventory productInventory = productInventoryMapper.toEntity(inventoryObj);
                inventoryObj.setProductTransaction(resultProdTran);
                productInventoryRepository.save(productInventory);
            }
        }
        return true;
    }
}
