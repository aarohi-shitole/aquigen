package com.techvg.inventory.management.repository;

import com.techvg.inventory.management.domain.PurchaseQuotation;
import com.techvg.inventory.management.domain.enumeration.OrderType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the PurchaseQuotation entity.
 */
@Repository
public interface PurchaseQuotationRepository extends JpaRepository<PurchaseQuotation, Long>, JpaSpecificationExecutor<PurchaseQuotation> {
    default Optional<PurchaseQuotation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PurchaseQuotation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PurchaseQuotation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct purchaseQuotation from PurchaseQuotation purchaseQuotation left join fetch purchaseQuotation.securityUser left join fetch purchaseQuotation.project",
        countQuery = "select count(distinct purchaseQuotation) from PurchaseQuotation purchaseQuotation"
    )
    Page<PurchaseQuotation> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct purchaseQuotation from PurchaseQuotation purchaseQuotation left join fetch purchaseQuotation.securityUser left join fetch purchaseQuotation.project"
    )
    List<PurchaseQuotation> findAllWithToOneRelationships();

    @Query(
        "select purchaseQuotation from PurchaseQuotation purchaseQuotation left join fetch purchaseQuotation.securityUser left join fetch purchaseQuotation.project where purchaseQuotation.id =:id"
    )
    Optional<PurchaseQuotation> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        value = "SELECT next_val FROM refrence_number_generator WHERE refrence_number_generator.orderType=:orderType ",
        nativeQuery = true
    )
    int findNextRefrenceNo(@Param("orderType") String orderType);

    @Modifying
    @Query(
        value = "UPDATE refrence_number_generator SET refrence_number_generator.next_val=:nextRefrenceNo where refrence_number_generator.next_val=:lastRefrenceNo AND refrence_number_generator.orderType=:orderType",
        nativeQuery = true
    )
    void updateNextRefrenceNo(
        @Param("lastRefrenceNo") int lastRefrenceNo,
        @Param("nextRefrenceNo") int nextRefrenceNo,
        @Param("orderType") String orderType
    );
}
