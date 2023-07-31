package com.techvg.inventory.management.service;

import com.techvg.inventory.management.domain.TransferDetailsApprovals;
import com.techvg.inventory.management.domain.enumeration.Status;
import com.techvg.inventory.management.domain.enumeration.TransactionType;
import com.techvg.inventory.management.repository.TransferDetailsApprovalsRepository;
import com.techvg.inventory.management.service.dto.ProductInventoryDTO;
import com.techvg.inventory.management.service.dto.ProductTransactionDTO;
import com.techvg.inventory.management.service.dto.TransferDTO;
import com.techvg.inventory.management.service.dto.TransferDetailsApprovalsDTO;
import com.techvg.inventory.management.service.dto.WareHouseDTO;
import com.techvg.inventory.management.service.mapper.TransferDetailsApprovalsMapper;
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
 * Service Implementation for managing {@link TransferDetailsApprovals}.
 */
@Service
@Transactional
public class TransferDetailsApprovalsService {

    private final Logger log = LoggerFactory.getLogger(TransferDetailsApprovalsService.class);

    private final TransferDetailsApprovalsRepository transferDetailsApprovalsRepository;

    private final TransferDetailsApprovalsMapper transferDetailsApprovalsMapper;

    @Autowired
    private ProductTransactionService productTransactionService;

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private WareHouseService wareHouseService;

    public TransferDetailsApprovalsService(
        TransferDetailsApprovalsRepository transferDetailsApprovalsRepository,
        TransferDetailsApprovalsMapper transferDetailsApprovalsMapper
    ) {
        this.transferDetailsApprovalsRepository = transferDetailsApprovalsRepository;
        this.transferDetailsApprovalsMapper = transferDetailsApprovalsMapper;
    }

    /**
     * Save a transferDetailsApprovals.
     *
     * @param transferDetailsApprovalsDTO the entity to save.
     * @return the persisted entity.
     */
    public TransferDetailsApprovalsDTO save(TransferDetailsApprovalsDTO transferDetailsApprovalsDTO) {
        log.debug("Request to save TransferDetailsApprovals : {}", transferDetailsApprovalsDTO);
        TransferDetailsApprovals transferDetailsApprovals = transferDetailsApprovalsMapper.toEntity(transferDetailsApprovalsDTO);
        transferDetailsApprovals = transferDetailsApprovalsRepository.save(transferDetailsApprovals);
        return transferDetailsApprovalsMapper.toDto(transferDetailsApprovals);
    }

    /**
     * Partially update a transferDetailsApprovals.
     *
     * @param transferDetailsApprovalsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TransferDetailsApprovalsDTO> partialUpdate(TransferDetailsApprovalsDTO transferDetailsApprovalsDTO) {
        log.debug("Request to partially update TransferDetailsApprovals : {}", transferDetailsApprovalsDTO);

        return transferDetailsApprovalsRepository
            .findById(transferDetailsApprovalsDTO.getId())
            .map(existingTransferDetailsApprovals -> {
                transferDetailsApprovalsMapper.partialUpdate(existingTransferDetailsApprovals, transferDetailsApprovalsDTO);

                return existingTransferDetailsApprovals;
            })
            .map(transferDetailsApprovalsRepository::save)
            .map(transferDetailsApprovalsMapper::toDto);
    }

    /**
     * Get all the transferDetailsApprovals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TransferDetailsApprovalsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransferDetailsApprovals");
        return transferDetailsApprovalsRepository.findAll(pageable).map(transferDetailsApprovalsMapper::toDto);
    }

    /**
     * Get all the transferDetailsApprovals with eager load of many-to-many
     * relationships.
     *
     * @return the list of entities.
     */
    public Page<TransferDetailsApprovalsDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transferDetailsApprovalsRepository.findAllWithEagerRelationships(pageable).map(transferDetailsApprovalsMapper::toDto);
    }

    /**
     * Get one transferDetailsApprovals by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TransferDetailsApprovalsDTO> findOne(Long id) {
        log.debug("Request to get TransferDetailsApprovals : {}", id);
        return transferDetailsApprovalsRepository.findOneWithEagerRelationships(id).map(transferDetailsApprovalsMapper::toDto);
    }

    /**
     * Delete the transferDetailsApprovals by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TransferDetailsApprovals : {}", id);
        transferDetailsApprovalsRepository.deleteById(id);
    }

    /**
     * update a Product Inventory and Product transaction for consumption.
     *
     * @param transferDetailsApprovalsDTOList the entity to save.
     */

    public boolean addConsumptionOfInventory(List<TransferDetailsApprovalsDTO> transferApprovalsList) {
        List<ProductInventoryDTO> productInventoryDTOList = new ArrayList<>();
        ProductInventoryDTO proInventory = null;
        for (TransferDetailsApprovalsDTO transApprovalObj : transferApprovalsList) {
            if (transApprovalObj.getId() != null) {
                proInventory = new ProductInventoryDTO();
                proInventory.setInwardOutwardDate(transApprovalObj.getApprovalDate());
                proInventory.setOutwardQty(transApprovalObj.getQtyApproved().toString());
                // TODO proInventory.getLotNo()(transApprovalObj.);
                proInventory.setProduct(transApprovalObj.getProduct());
                proInventory.setSecurityUser(transApprovalObj.getSecurityUser());
                Optional<TransferDTO> transfer = transferService.findOne(transApprovalObj.getTransfer().getId());
                String sourceWh = transfer.get().getSourceWareHouse();
                Long whId = Long.valueOf(sourceWh).longValue();
                Optional<WareHouseDTO> wareHouseDTO = wareHouseService.findOne(whId);
                proInventory.setWareHouse(wareHouseDTO.get());
            }
            productInventoryDTOList.add(proInventory);
        }

        // Added Product Transaction Details
        ProductTransactionDTO prodTran = new ProductTransactionDTO();
        if (productInventoryDTOList.get(0).getOutwardQty() != null && !productInventoryDTOList.get(0).getOutwardQty().isEmpty()) {
            prodTran.setTransactionType(TransactionType.TRANSFER_STOCKS_OUTWARDS);
            prodTran.setTransactionStatus(Status.COMPLETED);
            prodTran.setTransactionDate(new Date().toString());
            prodTran.setDescription("Outward for transfer");
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
