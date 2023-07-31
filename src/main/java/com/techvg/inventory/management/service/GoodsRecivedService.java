package com.techvg.inventory.management.service;

import com.techvg.inventory.management.domain.GoodsRecived;
import com.techvg.inventory.management.domain.enumeration.Status;
import com.techvg.inventory.management.domain.enumeration.TransactionType;
import com.techvg.inventory.management.repository.GoodsRecivedRepository;
import com.techvg.inventory.management.service.dto.GoodsRecivedDTO;
import com.techvg.inventory.management.service.dto.ProductInventoryDTO;
import com.techvg.inventory.management.service.dto.ProductTransactionDTO;
import com.techvg.inventory.management.service.dto.SecurityUserDTO;
import com.techvg.inventory.management.service.dto.WareHouseDTO;
import com.techvg.inventory.management.service.mapper.GoodsRecivedMapper;
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
 * Service Implementation for managing {@link GoodsRecived}.
 */
@Service
@Transactional
public class GoodsRecivedService {

    private final Logger log = LoggerFactory.getLogger(GoodsRecivedService.class);

    private final GoodsRecivedRepository goodsRecivedRepository;

    private final GoodsRecivedMapper goodsRecivedMapper;

    @Autowired
    private WareHouseService whService;

    @Autowired
    private SecurityUserService securityUserService;

    @Autowired
    private ProductTransactionService productTransactionService;

    @Autowired
    private ProductInventoryService productInventoryService;

    public GoodsRecivedService(GoodsRecivedRepository goodsRecivedRepository, GoodsRecivedMapper goodsRecivedMapper) {
        this.goodsRecivedRepository = goodsRecivedRepository;
        this.goodsRecivedMapper = goodsRecivedMapper;
    }

    /**
     * Save a goodsRecived.
     *
     * @param goodsRecivedDTO the entity to save.
     * @return the persisted entity.
     */
    public GoodsRecivedDTO save(GoodsRecivedDTO goodsRecivedDTO) {
        log.debug("Request to save GoodsRecived : {}", goodsRecivedDTO);
        GoodsRecived goodsRecived = goodsRecivedMapper.toEntity(goodsRecivedDTO);
        goodsRecived = goodsRecivedRepository.save(goodsRecived);
        return goodsRecivedMapper.toDto(goodsRecived);
    }

    /**
     * Partially update a goodsRecived.
     *
     * @param goodsRecivedDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GoodsRecivedDTO> partialUpdate(GoodsRecivedDTO goodsRecivedDTO) {
        log.debug("Request to partially update GoodsRecived : {}", goodsRecivedDTO);

        return goodsRecivedRepository
            .findById(goodsRecivedDTO.getId())
            .map(existingGoodsRecived -> {
                goodsRecivedMapper.partialUpdate(existingGoodsRecived, goodsRecivedDTO);

                return existingGoodsRecived;
            })
            .map(goodsRecivedRepository::save)
            .map(goodsRecivedMapper::toDto);
    }

    /**
     * Get all the goodsReciveds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<GoodsRecivedDTO> findAll(Pageable pageable) {
        log.debug("Request to get all GoodsReciveds");
        return goodsRecivedRepository.findAll(pageable).map(goodsRecivedMapper::toDto);
    }

    /**
     * Get one goodsRecived by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GoodsRecivedDTO> findOne(Long id) {
        log.debug("Request to get GoodsRecived : {}", id);
        return goodsRecivedRepository.findById(id).map(goodsRecivedMapper::toDto);
    }

    /**
     * Delete the goodsRecived by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GoodsRecived : {}", id);
        goodsRecivedRepository.deleteById(id);
    }

    /**
     * update a Product Inventory and Product transaction for consumption.
     *
     * @param TransferRecievedDTOList the entity to save.
     */

    public boolean addProductOfInventory(List<GoodsRecivedDTO> goodRecievedList) {
        // Added Product Transaction Details
        ProductTransactionDTO prodTran = new ProductTransactionDTO();
        prodTran.setTransactionType(TransactionType.INWARD_STOCKS);
        prodTran.setTransactionStatus(Status.COMPLETED);
        prodTran.setTransactionDate(new Date().toString());
        prodTran.setDescription("Stock Inward with GR");
        //Get warehouse from Id
        Optional<WareHouseDTO> findOneWh = whService.findOne(goodRecievedList.get(0).getWarehouseId());
        prodTran.setWareHouse(findOneWh.get());
        Optional<SecurityUserDTO> securityUSer = securityUserService.findOne(Long.parseLong(goodRecievedList.get(0).getFreeField3()));
        prodTran.setSecurityUser(securityUSer.get()); //set security user
        ProductTransactionDTO resultProdTran = productTransactionService.save(prodTran);

        ProductInventoryDTO proInventory = null;
        for (GoodsRecivedDTO goodRecievedObj : goodRecievedList) {
            proInventory = new ProductInventoryDTO();
            proInventory.setInwardOutwardDate(goodRecievedObj.getGrDate());
            proInventory.setInwardQty(goodRecievedObj.getQtyRecieved().toString());
            proInventory.setLotNo(goodRecievedObj.getLotNo());
            proInventory.setProduct(goodRecievedObj.getProduct());
            proInventory.setWareHouse(findOneWh.get());
            proInventory.setSecurityUser(securityUSer.get());
            // proInventory.setInventoryTypeId(goodRecievedObj.getId().toString());
            proInventory.setPricePerUnit(goodRecievedObj.getFreeField1());
            proInventory.setExpiryDate(goodRecievedObj.getExpiryDate());
            proInventory.setProductTransaction(resultProdTran);
            productInventoryService.save(proInventory);
        }
        return true;
    }
}
