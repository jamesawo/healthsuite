package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.billing.repository.PatientPaymentRepository;
import com.hmis.server.hmis.modules.emr.dto.PatientTypeEnum;
import com.hmis.server.hmis.modules.reports.dto.DailyCashCollectionSearchDto;
import com.hmis.server.hmis.modules.reports.dto.DailyCollectionFilterTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientPaymentSearchService {
    @Autowired
    private PatientPaymentRepository paymentRepository;

    List<PatientPayment> searchForDailyCollectionReport(DailyCashCollectionSearchDto dto){
        return paymentRepository.findAll(PaymentPaymentSearchSpecification.findPaymentsWithFilter(dto));
    }


    private static class PaymentPaymentSearchSpecification{
        private static Specification<PatientPayment> findPaymentsWithFilter(DailyCashCollectionSearchDto dto){
            return new Specification<PatientPayment>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<PatientPayment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    return null;
                }
            };
        }
    }

}
