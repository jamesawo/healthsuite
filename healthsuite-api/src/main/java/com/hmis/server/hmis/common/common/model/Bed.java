package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

//if JPQL or native queries are in used, update deleted columns  manually

@Data
@Entity
// override the default delete and set the//
@SQLDelete(sql = "UPDATE hmis_bed_data SET deleted = TRUE WHERE id = ?  ")
@Where(clause = "deleted IS NULL") // add filter to remove soft deleted records
@Table(name = "hmis_bed_data")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Bed extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @Column
    private Boolean isOccupied = false;

    @Column
    private String state;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @Column(name = "last_allocate_date")
    LocalDate lastAllocateDate;

    @Column(name = "last_deallocate_date")
    LocalDate lastDeallocateDate;

}
