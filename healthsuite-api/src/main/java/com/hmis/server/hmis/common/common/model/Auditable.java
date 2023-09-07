package com.hmis.server.hmis.common.common.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable<U>
{
    @JsonIgnore
    @Column
    private Boolean status = true;

    @CreatedBy
    @Column(updatable = false)
    protected U createdBy;

    @JsonIgnore
    @CreatedDate
    @Temporal(TIMESTAMP)
    @Column(updatable = false)
    protected Date createdDate;

    @JsonIgnore
    @LastModifiedBy
    protected U lastModifiedBy;

    @JsonIgnore
    @LastModifiedDate
    @Temporal(TIMESTAMP)
    protected Date lastModifiedDate;


    @Column(updatable = false)
    protected U deletedBy;

    @JsonIgnore
    @Temporal(TIMESTAMP)
    @Column(updatable = false)
    protected Date deletedDate;

    @JsonIgnore
    @Column
    private Boolean deleted = null;

    private String getUsername(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername();
        } else {
            return principal.toString();
        }
    }

}
