package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.NationalityDto;
import com.hmis.server.hmis.common.common.model.Nationality;

import java.util.List;

public interface INationalityService {

    Nationality createNationality(NationalityDto nationalityDto);

    Nationality updateNationality(NationalityDto nationalityDto);

    Nationality createParent(String parentName);

    List<Nationality> updateChildren(List<String> children, Long parentId);

    List<Nationality> findAllNationality();

    List<Nationality> findAllByParent(Long parentId);

    List<Nationality> findParentOnly();

    Nationality createParentAndChildren(NationalityDto nationalityDto);

    List<Nationality> findAllGroupByParent();
}
