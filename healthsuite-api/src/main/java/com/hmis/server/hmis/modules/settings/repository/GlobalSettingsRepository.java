package com.hmis.server.hmis.modules.settings.repository;

import com.hmis.server.hmis.modules.settings.model.GlobalSettings;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {

    @Transactional
    @Modifying
    @Query("update GlobalSettings g set value =:value where g.key =:key")
    int updateGlobalSetting(@Param("key") String key, @Param("value") String value);

    GlobalSettings findByKey(@Param("key") String key);

    List<GlobalSettings> findBySection(@Param("section") String section);

}
