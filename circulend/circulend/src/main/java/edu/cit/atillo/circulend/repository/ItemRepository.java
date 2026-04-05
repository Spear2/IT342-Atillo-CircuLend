package edu.cit.atillo.circulend.repository;

import edu.cit.atillo.circulend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsByAssetTag(String assetTag);
    boolean existsByAssetTagAndItemIdNot(String assetTag, Long itemId);
    Optional<Item> findByAssetTag(String assetTag);
}