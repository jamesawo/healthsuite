package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.modules.pharmacy.model.PharmacyReceivedGoods;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyReceivedGoodsRepository extends JpaRepository< PharmacyReceivedGoods, Long > {
	Optional< PharmacyReceivedGoods > findTopByOrderByIdDesc();
}
