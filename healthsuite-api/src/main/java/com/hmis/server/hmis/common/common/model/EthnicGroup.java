package com.hmis.server.hmis.common.common.model;

import javax.persistence.*;

import com.hmis.server.hmis.common.common.dto.EthnicGroupDto;
import lombok.*;

@Data
@Entity
@Table(name = "hmis_ethnic_group_data")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EthnicGroup extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public EthnicGroup(String name) {
        this.name = name;
    }

    public EthnicGroup(Long id) {
        this.id = id;
    }

    public EthnicGroupDto transformToDto() {
        return new EthnicGroupDto(this.id, this.name);
    }
}
