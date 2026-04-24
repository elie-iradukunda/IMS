package com.airtel.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airtel.inventory.domain.AssetMovement;

public interface AssetMovementRepository extends JpaRepository<AssetMovement, Long> {

    boolean existsByAssetId(Long assetId);

    List<AssetMovement> findByReturnedAtIsNullOrderByIssuedAtDesc();

    Optional<AssetMovement> findFirstByAssetIdAndReturnedAtIsNull(Long assetId);

    List<AssetMovement> findTop20ByOrderByIssuedAtDesc();

    List<AssetMovement> findAllByOrderByIssuedAtDesc();
}
