package com.techvg.inventory.management.repository;

import com.techvg.inventory.management.domain.Product;
import com.techvg.inventory.management.domain.ProductInventory;
import com.techvg.inventory.management.service.dto.ProductDTO;
import com.techvg.inventory.management.service.dto.ProductInventoryDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProductInventory entity.
 */
@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long>, JpaSpecificationExecutor<ProductInventory> {
    default Optional<ProductInventory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductInventory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductInventory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct productInventory from ProductInventory productInventory left join fetch productInventory.product left join fetch productInventory.securityUser",
        countQuery = "select count(distinct productInventory) from ProductInventory productInventory"
    )
    Page<ProductInventory> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct productInventory from ProductInventory productInventory left join fetch productInventory.product left join fetch productInventory.securityUser"
    )
    List<ProductInventory> findAllWithToOneRelationships();

    @Query(
        "select productInventory from ProductInventory productInventory left join fetch productInventory.product left join fetch productInventory.securityUser where productInventory.id =:id"
    )
    Optional<ProductInventory> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        value = "SELECT DISTINCT(product_id) FROM product_inventory  WHERE product_inventory.ware_house_id =:wareHouseId",
        nativeQuery = true
    )
    List<Long> findInventoryProductByWarehouse(@Param("wareHouseId") Long wareHouseId);
}
