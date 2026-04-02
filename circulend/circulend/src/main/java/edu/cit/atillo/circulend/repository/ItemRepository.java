package edu.cit.atillo.circulend.repository;

import edu.cit.atillo.circulend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByAssetTag(String assetTag);
    boolean existsByAssetTag(String assetTag);
}