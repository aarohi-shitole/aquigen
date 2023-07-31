package com.techvg.inventory.management.service;

import com.techvg.inventory.management.domain.Transfer;
import com.techvg.inventory.management.domain.TransferDetails;
import com.techvg.inventory.management.repository.TransferDetailsRepository;
import com.techvg.inventory.management.repository.TransferRepository;
import com.techvg.inventory.management.service.criteria.TransferDetailsApprovalsCriteria;
import com.techvg.inventory.management.service.dto.TransferDTO;
import com.techvg.inventory.management.service.dto.TransferDetailsApprovalsDTO;
import com.techvg.inventory.management.service.dto.TransferDetailsDTO;
import com.techvg.inventory.management.service.mapper.TransferDetailsMapper;
import com.techvg.inventory.management.service.mapper.TransferMapper;
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
 * Service Implementation for managing {@link Transfer}.
 */
@Service
@Transactional
public class TransferService {

    private final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final TransferRepository transferRepository;

    private final TransferMapper transferMapper;

    @Autowired
    private TransferDetailsMapper transferDetailsMapper;

    @Autowired
    private TransferDetailsRepository transferDetailsRepository;

    @Autowired
    private TransferDetailsApprovalsQueryService transferDetailsApprovalsQueryService;

    @Autowired
    private ProductTransactionService productTransactionService;

    @Autowired
    private ProductInventoryService productInventoryService;

    public TransferService(TransferRepository transferRepository, TransferMapper transferMapper) {
        this.transferRepository = transferRepository;
        this.transferMapper = transferMapper;
    }

    /**
     * Save a transfer.
     *
     * @param transferDTO the entity to save.
     * @return the persisted entity.
     */
    public TransferDTO save(TransferDTO transferDTO) {
        log.debug("Request to save Transfer : {}", transferDTO);
        Transfer transfer = transferMapper.toEntity(transferDTO);
        transfer = transferRepository.save(transfer);

        // -------------------------------------------------------
        // -------------Create TransferDetails product wise
        if (!transferDTO.getTransferDetails().isEmpty()) {
            List<TransferDetailsDTO> transferDetailsList = transferDTO.getTransferDetails();
            for (TransferDetailsDTO detailsDto : transferDetailsList) {
                log.debug("Request to save PurchaseQuotationDetails : {}", detailsDto);

                if (detailsDto != null) {
                    detailsDto.setTransferId(transfer.getId());
                    TransferDetails transferDetails = transferDetailsMapper.toEntity(detailsDto);
                    transferDetails = transferDetailsRepository.save(transferDetails);
                }
            }
        }
        return transferMapper.toDto(transfer);
    }

    /**
     * Partially update a transfer.
     *
     * @param transferDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TransferDTO> partialUpdate(TransferDTO transferDTO) {
        log.debug("Request to partially update Transfer : {}", transferDTO);

        return transferRepository
            .findById(transferDTO.getId())
            .map(existingTransfer -> {
                transferMapper.partialUpdate(existingTransfer, transferDTO);

                return existingTransfer;
            })
            .map(transferRepository::save)
            .map(transferMapper::toDto);
    }

    /**
     * Get all the transfers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TransferDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Transfers");
        return transferRepository.findAll(pageable).map(transferMapper::toDto);
    }

    /**
     * Get all the transfers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TransferDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transferRepository.findAllWithEagerRelationships(pageable).map(transferMapper::toDto);
    }

    /**
     * Get one transfer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TransferDTO> findOne(Long id) {
        log.debug("Request to get Transfer : {}", id);
        return transferRepository.findOneWithEagerRelationships(id).map(transferMapper::toDto);
    }

    /**
     * Delete the transfer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Transfer : {}", id);
        transferRepository.deleteById(id);
    }

    public void updateInventoryTransferReceived(TransferDTO transferDto) {
        LongFilter lf = new LongFilter();
        lf.setEquals(transferDto.getId());
        TransferDetailsApprovalsCriteria criteria = new TransferDetailsApprovalsCriteria();
        criteria.setTransferId(lf);
        List<TransferDetailsApprovalsDTO> findByCriteria = transferDetailsApprovalsQueryService.findByCriteria(criteria);
        boolean addTransferedStockOfInventory = productInventoryService.addTransferedStockOfInventory(findByCriteria);
    }
}
