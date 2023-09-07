package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Gender;
import com.hmis.server.hmis.modules.emr.dto.PatientTypeEnum;
import com.hmis.server.hmis.modules.emr.model.PatientContactDetail;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.repository.PatientDetailRepository;
import com.hmis.server.hmis.modules.reports.dto.PatientRegisterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientSearchService {

    private final PatientDetailRepository detailRepository;

    @Autowired
    public PatientSearchService(
            PatientDetailRepository detailRepository
    ) {
        this.detailRepository = detailRepository;
    }

    @Transactional
    public List<PatientDetail> searchPatients(String searchTerm){
        return detailRepository.findAll(PatientSearchSpecification.findByCriteria(searchTerm));
    }

    @Transactional
    public Page<PatientDetail> searchPatientsWithFilterPageable(PatientRegisterDto dto, LocalDate start, LocalDate end, Pageable pageable){
        return detailRepository.findAll(PatientSearchSpecification.findByCriteria(dto, start, end), pageable);
    }

    @Transactional
    public List<PatientDetail> searchPatientsWithFilterAll(PatientRegisterDto dto, LocalDate start, LocalDate end){
        return detailRepository.findAll(PatientSearchSpecification.findByCriteria(dto, start, end));
    }

    private static class PatientSearchSpecification {
        private  static Specification<PatientDetail> findByCriteria(final String searchTerm){
            return new Specification<PatientDetail>() {
                @Override
                public Predicate toPredicate(Root<PatientDetail> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    Predicate firstNamePredicate = criteriaBuilder.like( criteriaBuilder.lower( root.get("firstName") ), "%" + searchTerm.toLowerCase() + "%");
                    Predicate lastNamePredicate = criteriaBuilder.like( criteriaBuilder.lower( root.get("lastName") ), "%" + searchTerm.toLowerCase() + "%");
                    Predicate otherNamePredicate = criteriaBuilder.like( criteriaBuilder.lower( root.get("otherName") ), "%" + searchTerm.toLowerCase() + "%");
                    Predicate patientNumberPredicate = criteriaBuilder.like( criteriaBuilder.lower( root.get("patientNumber") ), "%" + searchTerm.toLowerCase() + "%");
                    // patient phone number search criteria
                    Join<PatientDetail, PatientContactDetail> patientContactDetailJoin = root.join("patientContactDetail");
                    Predicate patientPhoneNumberPredicate = criteriaBuilder.like( patientContactDetailJoin.get("phoneNumber"), "%" + searchTerm + "%" );
                    predicates.add(criteriaBuilder.or(firstNamePredicate, lastNamePredicate, otherNamePredicate, patientNumberPredicate, patientPhoneNumberPredicate));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
                }
            };
        }

        private static Specification<PatientDetail> findByCriteria(final PatientRegisterDto dto, LocalDate start, LocalDate end){
            return new Specification<PatientDetail>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<PatientDetail> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    // use metaModel to replace magic strings
                    if (dto.getGender() != null && dto.getGender().getId().isPresent()){
                        Join<PatientDetail, Gender> genderJoin = root.join("gender");
                        predicates.add(criteriaBuilder.equal(genderJoin.get("id"), dto.getGender().getId().get()));
                    }

                    if (dto.getDepartment() != null && dto.getDepartment().getId().isPresent()){
                        Join<PatientDetail, Department> departmentJoin = root.join("department");
                        predicates.add(criteriaBuilder.equal(departmentJoin.get("id"), dto.getDepartment().getId().get()));
                    }

                    if (dto.getType() != null && dto.getType() == PatientTypeEnum.OLD) {
                        predicates.add(criteriaBuilder.equal(root.get("patientTypeEnum"), PatientTypeEnum.OLD));
                    }else if (dto.getType() != null && dto.getType() == PatientTypeEnum.NEW) {
                        predicates.add(criteriaBuilder.equal(root.get("patientTypeEnum"), PatientTypeEnum.NEW));
                    }else{
                        Predicate oldPatient = criteriaBuilder.equal(root.get("patientTypeEnum"), PatientTypeEnum.OLD);
                        Predicate newPatient = criteriaBuilder.equal(root.get("patientTypeEnum"), PatientTypeEnum.NEW);
                        predicates.add(criteriaBuilder.or(oldPatient, newPatient));
                    }

                    Predicate endDatePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("registerDate"), end);
                    Predicate startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("registerDate"), start);
                    predicates.add(criteriaBuilder.and(endDatePredicate,startDatePredicate) );
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get("registerDate")));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));

                }
            };
        }
    }

}
