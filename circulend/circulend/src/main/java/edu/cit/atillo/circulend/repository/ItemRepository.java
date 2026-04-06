package edu.cit.atillo.circulend.repository;

import edu.cit.atillo.circulend.entity.Item;
import edu.cit.atillo.circulend.entity.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsByAssetTag(String assetTag);
    boolean existsByAssetTagAndItemIdNot(String assetTag, Long itemId);
    Optional<Item> findByAssetTag(String assetTag);

    @Query("""
    SELECT i FROM Item i
    WHERE (:query IS NULL OR
           LOWER(i.name) LIKE :pattern OR
           LOWER(i.description) LIKE :pattern)
      AND (:categoryId IS NULL OR i.category.categoryId = :categoryId)
      AND (:status IS NULL OR i.status = :status)
""")
    Page<Item> searchItems(
            @Param("query") String query,
            @Param("pattern") String pattern,   // ← new param
            @Param("categoryId") Long categoryId,
            @Param("status") ItemStatus status,
            Pageable pageable
    );
}