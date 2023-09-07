package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.modules.pharmacy.model.PharmacyReceivedGoodsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyReceivedGoodsItemRepository extends JpaRepository< PharmacyReceivedGoodsItem, Long > {
}
