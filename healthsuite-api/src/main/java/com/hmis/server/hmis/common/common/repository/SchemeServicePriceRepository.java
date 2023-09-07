package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.common.common.model.SchemeServicePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchemeServicePriceRepository extends JpaRepository<SchemeServicePrice, Long> {
	Optional<SchemeServicePrice> findBySchemeAndService( Scheme scheme, ProductService service );

}
