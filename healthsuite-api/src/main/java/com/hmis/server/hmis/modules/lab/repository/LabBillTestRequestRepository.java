package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.lab.model.LabTestRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabBillTestRequestRepository extends JpaRepository<LabTestRequest, Long> {
    Optional<LabTestRequest> findTopByOrderByIdDesc();

    List<LabTestRequest> findAllByInvoiceNumber(String invoiceNumber);

    List<LabTestRequest> findAllByReceiptNumber(String receiptNumber);

    List<LabTestRequest> findAllByLabNumber(String labNumber);

    List<LabTestRequest> findAllByPatient(PatientDetail patient);

    Optional<LabTestRequest> findByInvoiceNumber(String invoiceNumber);

    // Optional<LabBillTestRequest> findByReceiptNumber(String invoiceNumber);

    // Optional<LabBillTestRequest> findByLabNumber(String labNumber);

}