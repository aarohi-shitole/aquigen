package com.techvg.inventory.management.service;

import com.techvg.inventory.management.domain.TransferRecieved;
import com.techvg.inventory.management.domain.WareHouse;
import com.techvg.inventory.management.domain.enumeration.Status;
import com.techvg.inventory.management.domain.enumeration.TransactionType;
import com.techvg.inventory.management.repository.TransferRecievedRepository;
import com.techvg.inventory.management.service.dto.ProductInventoryDTO;
import com.techvg.inventory.management.service.dto.ProductTransactionDTO;
import com.techvg.inventory.management.service.dto.TransferDTO;
import com.techvg.inventory.management.service.dto.TransferRecievedDTO;
import com.techvg.inventory.management.service.dto.WareHouseDTO;
import com.techvg.inventory.management.service.mapper.TransferRecievedMapper;
import com.techvg.inventory.management.web.rest.errors.BadRequestAlertException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransferRecieved}.
 */
@Service
@Transactional
public class TransferRecievedService {

    private final Logger log = LoggerFactory.getLogger(TransferRecievedService.class);

    @Autowired
    private WareHouseService wareHouseService;

    private final TransferRecievedRepository transferRecievedRepository;

    private final TransferRecievedMapper transferRecievedMapper;

    @Autowired
    private ProductTransactionService productTransactionService;

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private TransferService transferService;

    public TransferRecievedService(TransferRecievedRepository transferRecievedRepository, TransferRecievedMapper transferRecievedMapper) {
        this.transferRecievedRepository = transferRecievedRepository;
        this.transferRecievedMapper = transferRecievedMapper;
    }

    /**
     * Save a transferRecieved.
     *
     * @param transferRecievedDTO the entity to save.
     * @return the persisted entity.
     */
    public TransferRecievedDTO save(TransferRecievedDTO transferRecievedDTO) {
        log.debug("Request to save TransferRecieved : {}", transferRecievedDTO);
        TransferRecieved transferRecieved = transferRecievedMapper.toEntity(transferRecievedDTO);
        transferRecieved = transferRecievedRepository.save(transferRecieved);
        return transferRecievedMapper.toDto(transferRecieved);
    }

    /**
     * Partially update a transferRecieved.
     *
     * @param transferRecievedDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TransferRecievedDTO> partialUpdate(TransferRecievedDTO transferRecievedDTO) {
        log.debug("Request to partially update TransferRecieved : {}", transferRecievedDTO);

        return transferRecievedRepository
            .findById(transferRecievedDTO.getId())
            .map(existingTransferRecieved -> {
                transferRecievedMapper.partialUpdate(existingTransferRecieved, transferRecievedDTO);

                return existingTransferRecieved;
            })
            .map(transferRecievedRepository::save)
            .map(transferRecievedMapper::toDto);
    }

    /**
     * Get all the transferRecieveds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TransferRecievedDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransferRecieveds");
        return transferRecievedRepository.findAll(pageable).map(transferRecievedMapper::toDto);
    }

    /**
     * Get all the transferRecieveds with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TransferRecievedDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transferRecievedRepository.findAllWithEagerRelationships(pageable).map(transferRecievedMapper::toDto);
    }

    /**
     * Get one transferRecieved by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TransferRecievedDTO> findOne(Long id) {
        log.debug("Request to get TransferRecieved : {}", id);
        return transferRecievedRepository.findOneWithEagerRelationships(id).map(transferRecievedMapper::toDto);
    }

    /**
     * Delete the transferRecieved by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TransferRecieved : {}", id);
        transferRecievedRepository.deleteById(id);
    }

    /**
     * update a Product Inventory and Product transaction for consumption.
     *
     * @param TransferRecievedDTOList the entity to save.
     */

    public boolean addConsumptionOfInventory(List<TransferRecievedDTO> transferRecievedList) {
        List<ProductInventoryDTO> productInventoryDTOList = new ArrayList<>();
        ProductInventoryDTO proInventory = null;
        for (TransferRecievedDTO transferRecievedObj : transferRecievedList) {
            if (transferRecievedObj.getId() != null) {
                proInventory = new ProductInventoryDTO();
                proInventory.setInwardOutwardDate(transferRecievedObj.getTransferDate());
                proInventory.setInwardQty(transferRecievedObj.getQtyReceived().toString());
                // TODO proInventory.getLotNo()(transApprovalObj.);
                proInventory.setProduct(transferRecievedObj.getProduct());
                proInventory.setSecurityUser(transferRecievedObj.getSecurityUser());
                Optional<TransferDTO> transfer = transferService.findOne(transferRecievedObj.getTransfer().getId());
                String destinationWh = transfer.get().getDestinationWareHouse();
                Long whId = Long.valueOf(destinationWh).longValue();
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
                inventoryObj.setProductTransaction(resultProdTran);
                productInventoryService.save(inventoryObj);
            }
        }
        return true;
    }
}
