package com.hmis.server.hmis.common.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.ProductServiceDto;
import com.hmis.server.hmis.common.common.dto.ServiceUsageEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_service_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true)
public class ProductService extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, name = "cost_price", columnDefinition="Decimal(10,2) default '0.00'")
    private Double costPrice = 0.00;

    @Column(nullable = false, name = "unit_cost", columnDefinition="Decimal(10,2) default '0.00'")
    private Double unitCost;

    @Column(nullable = false, name = "regular_selling_price", columnDefinition="Decimal(10,2) default '0.00'")
    private Double regularSellingPrice;

    @Column(nullable = false, name = "nhis_selling_price", columnDefinition="Decimal(10,2) default '0.00'")
    private Double nhisSellingPrice;

    @Column(name = "discount", columnDefinition="Decimal(10,2) default '0.00'")
    private Double discount = 0.00;

    @Column(name = "apply_discount")
    private Boolean applyDiscount = false;

    @Column(name = "used_for", nullable = false)
    private String usedFor;

    @ManyToOne()
    @JoinColumn(name = "revenue_department")
    private RevenueDepartment revenueDepartment;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "department")
    private Department department;

    public ProductServiceDto mapToDto(){
        ProductServiceDto productServiceDto = new ProductServiceDto();
        if (this.id != null ) {
            productServiceDto.setId(this.id);
        }
        productServiceDto.setName(this.name);
        productServiceDto.setCode(this.code);
        productServiceDto.setApplyDiscount(this.applyDiscount);
        productServiceDto.setRegularSellingPrice(this.regularSellingPrice);
        productServiceDto.setNhisSellingPrice(this.nhisSellingPrice);
        productServiceDto.setCostPrice(this.costPrice);
        productServiceDto.setDiscount(this.discount);
        productServiceDto.setUnitCost(this.unitCost);
        productServiceDto.setUsedFor(ServiceUsageEnum.valueOf(this.usedFor));
        productServiceDto.setDepartmentId(this.department.getId());
        productServiceDto.setRevenueDepartmentId(this.revenueDepartment.getId());
        return productServiceDto;
    }

    public ProductService(Long id) {
        this.id = id;
    }
}
