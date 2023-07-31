package com.techvg.inventory.management.repository;

import com.techvg.inventory.management.domain.PurchaseQuotationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the PurchaseQuotationDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchaseQuotationDetailsRepository
    extends JpaRepository<PurchaseQuotationDetails, Long>, JpaSpecificationExecutor<PurchaseQuotationDetails> {}
