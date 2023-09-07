package com.hmis.server.hmis.common.common.model;

import com.hmis.server.hmis.common.common.dto.RelationshipDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Optional;

@Data
@Entity
@Table(name = "hmis_relationship_data")
@EqualsAndHashCode(callSuper = true)
public class Relationship extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Relationship() {
    }

    public Relationship(String name) {
        this.name = name;
    }

    public Relationship(Long id) {
        this.id = id;
    }

    public RelationshipDto toDto() {
        return new RelationshipDto(Optional.ofNullable(this.id), Optional.ofNullable(this.name));
    }
}
