package com.techvg.inventory.management.repository;

import com.techvg.inventory.management.domain.Notification;
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
 * Spring Data SQL repository for the Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    default Optional<Notification> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Notification> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Notification> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct notification from Notification notification left join fetch notification.securityUser left join fetch notification.wareHouse",
        countQuery = "select count(distinct notification) from Notification notification"
    )
    Page<Notification> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct notification from Notification notification left join fetch notification.securityUser left join fetch notification.wareHouse"
    )
    List<Notification> findAllWithToOneRelationships();

    @Query(
        "select notification from Notification notification left join fetch notification.securityUser left join fetch notification.wareHouse where notification.id =:id"
    )
    Optional<Notification> findOneWithToOneRelationships(@Param("id") Long id);
}
