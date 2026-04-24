package com.airtel.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AssetStatus;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByAssetTagIgnoreCase(String assetTag);

    Optional<Asset> findBySerialNumberIgnoreCase(String serialNumber);

    List<Asset> findByStatusOrderByAssetTag(AssetStatus status);
}
