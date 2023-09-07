package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IPaymentMethodService;
import com.hmis.server.hmis.common.common.dto.PaymentMethodDto;
import com.hmis.server.hmis.common.common.model.PaymentMethod;
import com.hmis.server.hmis.common.common.repository.PaymentMethodRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodServiceImpl implements IPaymentMethodService {

    private static  final String title = "Payment Method";

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Override
    public PaymentMethod findByName(String name){
        return this.paymentMethodRepository.findByName(name);
    }

    @Override
    public List<PaymentMethod> findAllRaw() {
        List<PaymentMethod> paymentMethodList =  new ArrayList<>();
        List<PaymentMethod> list = this.paymentMethodRepository.findAll();
        if (list.isEmpty()){
            return paymentMethodList;
        }
        return list;
    }

    @Override
    public PaymentMethodDto createOne(PaymentMethodDto paymentMethodDto) {
        return null;
    }

    @Override
    public List<PaymentMethodDto> createInBatch(List<PaymentMethodDto> paymentMethodDtoList) {
        return null;
    }

    @Override
    public List<PaymentMethodDto> findAll() {
	    return mapModelListToDtoList(paymentMethodRepository.findAll());
	    //	    return mapModelListToDtoList(paymentMethodRepository.findAllOrderByIdAsc());
    }

    @Override
    public PaymentMethodDto findOne(PaymentMethodDto paymentMethodDto) {
        return null;
    }

	@Override
	public PaymentMethod findOneRaw(Long id) {
		return this.paymentMethodRepository.getOne(id);
	}

    @Override
    public PaymentMethodDto findByName(PaymentMethodDto paymentMethodDto) {
        return null;
    }

    @Override
    public List<PaymentMethodDto> findByNameLike(PaymentMethodDto paymentMethodDto) {
        return null;
    }

    @Override
    public PaymentMethodDto findByCode(PaymentMethodDto paymentMethodDto) {
        return null;
    }

    @Override
    public PaymentMethodDto updateOne(PaymentMethodDto paymentMethodDto) {
        return null;
    }

    @Override
    public void updateInBatch(List<PaymentMethodDto> paymentMethodDtoList) {

    }

    @Override
    public void deactivateOne(PaymentMethodDto paymentMethodDto) {

    }

    @Override
    public void deleteOne(PaymentMethodDto paymentMethodDto) {

    }

    @Override
    public boolean isPaymentMethodExist(PaymentMethodDto paymentMethodDto) {
        if (paymentMethodDto.getName().isPresent()) {
            return paymentMethodRepository.findAll().stream().anyMatch(paymentMethod -> paymentMethod.getName()
            .compareToIgnoreCase(paymentMethodDto.getName().get()) == 0);
        }else throw new HmisApplicationException("Cannot Check Is Exist Without Name: "+title);
    }

//    @Override
//    public String seedDefaultData() {
//        if (paymentMethodRepository.findAll().size() < 1){
//            List<PaymentMethod> paymentMethods = new ArrayList<>();
//	        paymentMethods.add(new PaymentMethod("CASH"));
//	        paymentMethods.add(new PaymentMethod("CHEQUE"));
//            paymentMethods.add(new PaymentMethod("POS"));
//	        paymentMethods.add(new PaymentMethod("BANK TRANSFER"));
//	        paymentMethods.add(new PaymentMethod("MOBILE WALLET"));
//            List<PaymentMethod> paymentMethods1 = paymentMethodRepository.saveAll(paymentMethods);
//            if (paymentMethods1.size() > 0)
//                return HmisConstant.DEFAULT_DATA_SEED_SUCCESS;
//            else return HmisConstant.DEFAULT_DATA_SEED_FAILED;
//        }else return HmisConstant.DEFAULT_DATA_SEED_EXIST;
//    }

    public PaymentMethod mapDtoToModel(PaymentMethodDto paymentMethodDto){
        if (paymentMethodDto !=  null){

            PaymentMethod paymentMethod = new PaymentMethod();
            setModel(paymentMethodDto, paymentMethod);

            return paymentMethod;

        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR+title);

    }

    public PaymentMethodDto mapModelToDto(PaymentMethod paymentMethod){
        if (paymentMethod !=  null){

            PaymentMethodDto paymentMethodDto = new PaymentMethodDto();
            setDto(paymentMethodDto, paymentMethod);

            return paymentMethodDto;

        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR+title);
    }

    private List<PaymentMethod> mapDtoListToModelList(List<PaymentMethodDto> paymentMethodDtoList){
        if (paymentMethodDtoList != null && !paymentMethodDtoList.isEmpty()){
            List<PaymentMethod> paymentMethodList = new ArrayList<>();
            paymentMethodDtoList.forEach(paymentMethodDto -> {
                PaymentMethod paymentMethod = new PaymentMethod();
                setModel(paymentMethodDto, paymentMethod);
                paymentMethodList.add(paymentMethod);
            });
            return paymentMethodList;
        }else throw new HmisApplicationException("Cannot Map Empty DtoList To ModelList: "+title);
    }


    private List<PaymentMethodDto> mapModelListToDtoList(List<PaymentMethod> paymentMethodList){
        List<PaymentMethodDto> paymentMethodDtoList = new ArrayList<>();
        if ( paymentMethodList != null && !paymentMethodList.isEmpty()){
            paymentMethodList.forEach(paymentMethod -> {
                PaymentMethodDto paymentMethodDto = new PaymentMethodDto();
                setDto(paymentMethodDto, paymentMethod);
                paymentMethodDtoList.add(paymentMethodDto);
            });
            return paymentMethodDtoList;
        }
        return paymentMethodDtoList;
//        else throw new HmisApplicationException("Cannot Map Empty ModelList To DtoList: "+title);
    }

    private void setDto(PaymentMethodDto paymentMethodDto, PaymentMethod paymentMethod) {
        if (paymentMethod.getId() != null)
            paymentMethodDto.setId(Optional.of(paymentMethod.getId()));

        if (paymentMethod.getName() != null)
            paymentMethodDto.setName(Optional.of(paymentMethod.getName()));
    }

    private void setModel(PaymentMethodDto paymentMethodDto, PaymentMethod paymentMethod) {
        if (paymentMethodDto.getId().isPresent())
            paymentMethod.setId(paymentMethodDto.getId().get());

        if (paymentMethodDto.getName().isPresent())
            paymentMethod.setName(paymentMethodDto.getName().get());
    }


}
