package com.hmis.server.hmis.modules.reports.iservice;

import com.hmis.server.hmis.modules.reports.dto.DailyCashCollectionSearchDto;

public interface IAccountService {
    byte[] generateDailyCashCollection(DailyCashCollectionSearchDto dto);
}
