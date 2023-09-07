package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.PaymentMethodDto;
import com.hmis.server.hmis.common.common.model.PaymentMethod;
import java.util.List;

public interface IPaymentMethodService {
    PaymentMethod findByName(String name);

    List<PaymentMethod> findAllRaw();

    PaymentMethodDto createOne(PaymentMethodDto paymentMethodDto);

    List<PaymentMethodDto> createInBatch(List<PaymentMethodDto> paymentMethodDtoList);

    List<PaymentMethodDto> findAll();

    PaymentMethodDto findOne(PaymentMethodDto paymentMethodDto);

	PaymentMethod findOneRaw(Long id);

	PaymentMethodDto findByName(PaymentMethodDto paymentMethodDto);

    List<PaymentMethodDto> findByNameLike(PaymentMethodDto paymentMethodDto);

    PaymentMethodDto findByCode(PaymentMethodDto paymentMethodDto);

    PaymentMethodDto updateOne(PaymentMethodDto paymentMethodDto);

    void updateInBatch(List<PaymentMethodDto> paymentMethodDtoList);

    void deactivateOne(PaymentMethodDto paymentMethodDto);

    void deleteOne(PaymentMethodDto paymentMethodDto);

    boolean isPaymentMethodExist(PaymentMethodDto paymentMethodDto);

    // String seedDefaultData();
}
