package com.hmis.server.hmis.modules.others.repository;

import com.hmis.server.hmis.modules.others.model.Vendor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository< Vendor, Long > {
	List<Vendor> findAllBySupplierNameContainsIgnoreCaseOrPhoneNumberContainsIgnoreCase(String supplierName, String phoneNumber);
}
