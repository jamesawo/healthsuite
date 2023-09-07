package com.hmis.server.hmis.common.common.model;


import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table(name = "hmis_revenue_department_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class RevenueDepartment extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String code;

	@Column
	@ColumnDefault( "false" )
	private boolean handleDeposit;

	@OneToOne
    private RevenueDepartmentType revenueDepartmentType;

    public RevenueDepartment(Long id) {
        this.id = id;
    }
}
