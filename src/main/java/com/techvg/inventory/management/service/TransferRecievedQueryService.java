package com.techvg.inventory.management.service;

import com.techvg.inventory.management.domain.*; // for static metamodels
import com.techvg.inventory.management.domain.TransferRecieved;
import com.techvg.inventory.management.repository.TransferRecievedRepository;
import com.techvg.inventory.management.service.criteria.TransferRecievedCriteria;
import com.techvg.inventory.management.service.dto.TransferRecievedDTO;
import com.techvg.inventory.management.service.mapper.TransferRecievedMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TransferRecieved} entities in the database.
 * The main input is a {@link TransferRecievedCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransferRecievedDTO} or a {@link Page} of {@link TransferRecievedDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransferRecievedQueryService extends QueryService<TransferRecieved> {

    private final Logger log = LoggerFactory.getLogger(TransferRecievedQueryService.class);

    private final TransferRecievedRepository transferRecievedRepository;

    private final TransferRecievedMapper transferRecievedMapper;

    public TransferRecievedQueryService(
        TransferRecievedRepository transferRecievedRepository,
        TransferRecievedMapper transferRecievedMapper
    ) {
        this.transferRecievedRepository = transferRecievedRepository;
        this.transferRecievedMapper = transferRecievedMapper;
    }

    /**
     * Return a {@link List} of {@link TransferRecievedDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransferRecievedDTO> findByCriteria(TransferRecievedCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransferRecieved> specification = createSpecification(criteria);
        return transferRecievedMapper.toDto(transferRecievedRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransferRecievedDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransferRecievedDTO> findByCriteria(TransferRecievedCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransferRecieved> specification = createSpecification(criteria);
        return transferRecievedRepository.findAll(specification, page).map(transferRecievedMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransferRecievedCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransferRecieved> specification = createSpecification(criteria);
        return transferRecievedRepository.count(specification);
    }

    /**
     * Function to convert {@link TransferRecievedCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransferRecieved> createSpecification(TransferRecievedCriteria criteria) {
        Specification<TransferRecieved> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransferRecieved_.id));
            }
            if (criteria.getTransferDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTransferDate(), TransferRecieved_.transferDate));
            }
            if (criteria.getQtyTransfered() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQtyTransfered(), TransferRecieved_.qtyTransfered));
            }
            if (criteria.getQtyReceived() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQtyReceived(), TransferRecieved_.qtyReceived));
            }
            if (criteria.getComment() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComment(), TransferRecieved_.comment));
            }
            if (criteria.getFreeField1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFreeField1(), TransferRecieved_.freeField1));
            }
            if (criteria.getLastModified() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModified(), TransferRecieved_.lastModified));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), TransferRecieved_.lastModifiedBy));
            }
            if (criteria.getIsDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getIsDeleted(), TransferRecieved_.isDeleted));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), TransferRecieved_.isActive));
            }
            if (criteria.getSecurityUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSecurityUserId(),
                            root -> root.join(TransferRecieved_.securityUser, JoinType.LEFT).get(SecurityUser_.id)
                        )
                    );
            }
            if (criteria.getTransferId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransferId(),
                            root -> root.join(TransferRecieved_.transfer, JoinType.LEFT).get(Transfer_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
