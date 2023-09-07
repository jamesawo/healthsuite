package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.common.common.model.DrugClassification;
import com.hmis.server.hmis.common.common.model.DrugFormulation;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugRegisterRepository extends JpaRepository<DrugRegister, Long> {
    Optional<DrugRegister> findByFormulationAndClassificationAndGenericNameIgnoreCaseAndBrandNameIgnoreCase(DrugFormulation formulation, DrugClassification classification, String genericName,
                                                                                                            String brandName);
    Optional<DrugRegister> findTopByOrderByIdDesc();

    List<DrugRegister> findByBrandNameIsContainingIgnoreCase(String brandName);

    List<DrugRegister> findByGenericNameIsContainingIgnoreCase(String genericName);

    List<DrugRegister> findByBrandNameIsContainingIgnoreCaseOrGenericNameIsContainingIgnoreCase(String brandName, String genericName);

    List<DrugRegister> findByBrandNameIsContainingIgnoreCaseAndClassificationId(String brandName, Long classificationId);

    List<DrugRegister> findByBrandNameIsContainingIgnoreCaseAndFormulationId(String brandName, Long formulationId);
}
