package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NationalityRepository  extends JpaRepository<Nationality, Long> {
    List<Nationality> findAllByParent(Nationality parent);

    List<Nationality> findAllByParentNull();
}
