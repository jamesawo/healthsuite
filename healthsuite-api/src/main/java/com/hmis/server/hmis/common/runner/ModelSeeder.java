package com.hmis.server.hmis.common.runner;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hmis.server.hmis.common.common.dto.DepartmentCategoryEnum;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import com.hmis.server.hmis.common.common.model.DrugClassification;
import com.hmis.server.hmis.common.common.model.DrugFormulation;
import com.hmis.server.hmis.common.common.model.EthnicGroup;
import com.hmis.server.hmis.common.common.model.Gender;
import com.hmis.server.hmis.common.common.model.MaritalStatus;
import com.hmis.server.hmis.common.common.model.MeansOfIdentification;
import com.hmis.server.hmis.common.common.model.Relationship;
import com.hmis.server.hmis.common.common.model.Religion;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.common.common.model.RevenueDepartmentType;
import com.hmis.server.hmis.common.common.model.Role;
import com.hmis.server.hmis.common.common.model.Surgery;
import com.hmis.server.hmis.common.common.repository.DepartmentCategoryRepository;
import com.hmis.server.hmis.common.common.repository.DepartmentRepository;
import com.hmis.server.hmis.common.common.repository.DrugClassificationRepository;
import com.hmis.server.hmis.common.common.repository.DrugFormulationRepository;
import com.hmis.server.hmis.common.common.repository.EthnicGroupRepository;
import com.hmis.server.hmis.common.common.repository.GenderRepository;
import com.hmis.server.hmis.common.common.repository.MaritalStatusRepository;
import com.hmis.server.hmis.common.common.repository.MeansOfIdentificationRepository;
import com.hmis.server.hmis.common.common.repository.RelationshipRepository;
import com.hmis.server.hmis.common.common.repository.ReligionRepository;
import com.hmis.server.hmis.common.common.repository.RevenueDepartmentRepository;
import com.hmis.server.hmis.common.common.repository.RevenueDepartmentTypeRepository;
import com.hmis.server.hmis.common.common.repository.RoleRepository;
import com.hmis.server.hmis.common.common.repository.SurgeryRepository;

@Service
public class ModelSeeder {

    @Autowired
    private GenderRepository genderRepository;
    @Autowired
    private MaritalStatusRepository maritalStatusRepository;
    @Autowired
    private RelationshipRepository relationshipRepository;
    @Autowired
    private ReligionRepository religionRepository;
    @Autowired
    private SurgeryRepository surgeryRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DrugClassificationRepository drugClassificationRepository;
    @Autowired
    private DrugFormulationRepository drugFormulationRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private RevenueDepartmentRepository revenueDepartmentRepository;
    @Autowired
    private EthnicGroupRepository ethnicGroupRepository;
    @Autowired
    private MeansOfIdentificationRepository meansOfIdentificationRepository;
    @Autowired
    private DepartmentCategoryRepository departmentCategoryRepository;

    @Autowired
    RevenueDepartmentTypeRepository revenueDepartmentTypeRepository;

    public void seedAllBaseModel() {
        this.seedGender();
        this.seedMaritalStatus();
        this.seedRelationship();
        this.seedReligion();
        this.seedSurgery();
        this.seedRole();
        this.seedDrugClassification();
        this.seedDrugFormulation();
        this.seedDepartment();
        this.seedRevenueDepartment();
        this.seedEthnicGroup();
        this.seedMeansOfIdentification();
    }

    public void seedGender() {
        if (this.genderRepository.findAll().isEmpty()) {

            this.genderRepository.saveAll(
                    List.of(new Gender("MALE"),
                            new Gender("FEMALE")));
            System.out.println("----- Seeding Gender -----");
        }
    }

    public void seedMaritalStatus() {
        if (this.maritalStatusRepository.findAll().isEmpty()) {
            this.maritalStatusRepository.saveAll(
                    List.of(new MaritalStatus("SINGLE"),
                            new MaritalStatus("MARRIED")));
            System.out.println("----- Seeding Marital Status -----");
        }

    }

    public void seedRelationship() {
        if (this.relationshipRepository.findAll().isEmpty()) {
            this.relationshipRepository.saveAll(
                    List.of(new Relationship("FATHER"),
                            new Relationship("MOTHER")));
            System.out.println("----- Seeding Relationship -----");
        }
    }

    public void seedReligion() {
        if (this.religionRepository.findAll().isEmpty()) {
            this.religionRepository.saveAll(
                    List.of(new Religion("CHRISTIANITY"),
                            new Religion("ISLAMIC")));
            System.out.println("----- Seeding Religion -----");
        }
    }

    public void seedSurgery() {
        if (this.surgeryRepository.findAll().isEmpty()) {
            this.surgeryRepository.save(new Surgery("TEST SURGERY", "S100"));
            System.out.println("----- Seeding Surgery -----");
        }
    }

    public void seedRole() {
        if (this.roleRepository.findAll().size() < 2) {
            this.roleRepository.save(
                    new Role("CASHIER ROLE",
                            "A ROLE FOR SYSTEM CASHIER AND OPERATORS "));

            System.out.println("----- Seeding Role -----");
        }
    }

    public void seedDrugClassification() {
        if (this.drugClassificationRepository.findAll().isEmpty()) {
            this.drugClassificationRepository.save(
                    new DrugClassification("PAIN KILLER"));
            System.out.println("----- Seeding drug classification -----");
        }
    }

    public void seedDrugFormulation() {
        if (this.drugFormulationRepository.findAll().isEmpty()) {
            this.drugFormulationRepository.save(new DrugFormulation("TABLET"));
            System.out.println("----- Seeding drug formulation -----");
        }
    }

    public void seedDepartment() {

        if (this.departmentRepository.findAll().size() < 3) {
            Optional<DepartmentCategory> clinic = departmentCategoryRepository
                    .findByName(DepartmentCategoryEnum.Clinic.name());

            if (clinic.isPresent()) {
                Department department = new Department();
                department.setName("GOPD CLINIC");
                department.setCode("DEPT103");
                department.setDepartmentCategory(clinic.get());
                this.departmentRepository.save(department);
                System.out.println("----- Seeding clinic department -----");
            }

            Optional<DepartmentCategory> ward = departmentCategoryRepository
                    .findByName(DepartmentCategoryEnum.Ward.name());
            if (ward.isPresent()) {
                Department department = new Department();
                department.setName("MALE WARD");
                department.setCode("DEPT104");
                department.setDepartmentCategory(ward.get());
                this.departmentRepository.save(department);
                System.out.println("----- Seeding ward department -----");
            }
        }

    }

    public void seedRevenueDepartment() {
        if (this.revenueDepartmentRepository.findAll().isEmpty()) {
            List<RevenueDepartmentType> revDepTypes = revenueDepartmentTypeRepository.findAll();
            if (revDepTypes.size() > 0) {
                RevenueDepartment revenueDepartment = new RevenueDepartment();
                revenueDepartment.setRevenueDepartmentType(revDepTypes.get(0));
                revenueDepartment.setName("ACCOUNT REVENUE DEPARTMENT");
                revenueDepartment.setHandleDeposit(true);
                revenueDepartment.setCode("REVD100");
                revenueDepartmentRepository.save(revenueDepartment);
                System.out.println("----- Seeding Rev. department -----");
            }
        }
    }

    public void seedEthnicGroup() {
        if (this.ethnicGroupRepository.findAll().isEmpty()) {
            this.ethnicGroupRepository.saveAll(List.of(new EthnicGroup("YORUBA"), new EthnicGroup("IGBO")));
            System.out.println("----- Seeding Ethnic groups -----");
        }
    }

    public void seedMeansOfIdentification() {
        if (this.meansOfIdentificationRepository.findAll().isEmpty()) {
            this.meansOfIdentificationRepository.save(new MeansOfIdentification("INTL PASSPORT"));
            System.out.println("----- Seeding Means of Identification -----");
        }
    }

}
