package ru.ds.education.currency.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.ds.education.currency.cbr.service.ServiceCbr;
import ru.ds.education.currency.core.mapper.CurrencyMapper;
import ru.ds.education.currency.core.model.CurrencyModel;
import ru.ds.education.currency.db.entity.CurrencyEntity;
import ru.ds.education.currency.db.entity.CurrencyEnum;
import ru.ds.education.currency.db.entity.CursRequestEntity;
import ru.ds.education.currency.db.entity.StatusEnum;
import ru.ds.education.currency.db.repository.CurrencyRepository;
import ru.ds.education.currency.db.repository.RequestRepository;
import ru.ds.education.currency.exceptions.ApiBadData;
import ru.ds.education.currency.exceptions.ApiRequestException;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class CurrencyService {

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ServiceCbr serviceCbr;

    @Autowired
    JmsTemplate jmsTemplate;

    public CurrencyModel addCurrency(CurrencyModel currencyModel) {
        if (currencyModel.getCursModel() != 0
                && currencyModel.getCurs_dateModel() != null) {
            CurrencyEntity currencyEntityNew = currencyMapper.map(currencyModel, CurrencyEntity.class);
            currencyEntityNew = currencyRepository.save(currencyEntityNew);
            currencyModel = currencyMapper.map(currencyEntityNew, CurrencyModel.class);
            return currencyModel;
        } else throw new ApiBadData("Не все поля заполнены!");
    }

    public CurrencyModel getCurrency(Long id, HttpServletResponse response) {
        if (currencyRepository.existsById(id)) {
            CurrencyEntity currencyEntityDb =
                    currencyRepository.getOne(id);
            //response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return currencyMapper.map(currencyEntityDb, CurrencyModel.class);
        } else {

            throw new ApiRequestException("Валюты с id - " + id + " не существует!");
            //
        }
    }

    public void deleteCurrency(Long id) {
        if (currencyRepository.existsById(id)) {
            currencyRepository.deleteById(id);
        } else {
            throw new ApiRequestException("Валюты с id - " + id + " не существует!");
        }
    }

    public CurrencyModel replaceCurrency(CurrencyModel currencyModel, Long idReplace, HttpServletResponse response) {
        if (currencyRepository.existsById(idReplace)) {
            if (currencyModel.getCurrencyModel() != null && currencyModel.getCursModel() != 0
                    && currencyModel.getCurs_dateModel() != null) {
                CurrencyEntity replaceCurrencyEntity = currencyRepository.getOne(idReplace);
                currencyModel.setIdModel(replaceCurrencyEntity.getIdEntity());
                CurrencyEntity replaceCurrency = currencyMapper.map(currencyModel, CurrencyEntity.class);
                replaceCurrency = currencyRepository.save(replaceCurrency);
                currencyModel = currencyMapper.map(replaceCurrency, CurrencyModel.class);
                return currencyModel;
            } else throw new ApiBadData("Не все поля заполнены!");
        } else throw new ApiRequestException("Валюты с id - " + idReplace + " не существует!");
    }

    public List<CurrencyModel> getByDateAndId(CurrencyEnum currency, LocalDate Date, HttpServletResponse response) {
        List<CurrencyEntity> whatFind = currencyRepository.findByDate(currency, Date);
        if (!whatFind.isEmpty()) {
            List<CurrencyModel> result = currencyMapper.mapAsList(whatFind, CurrencyModel.class);
            return result;
        }
        else{CursRequestEntity cursRequestEntity = requestRepository.find(Date);
        if(cursRequestEntity != null && cursRequestEntity.getStatusEntity() != StatusEnum.FAILED){
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return null;}
        else{
            requestRepository.save(newRequestInBD(Date, StatusEnum.CREATED));

        }
        }
        return null;
    }


    private CursRequestEntity newRequestInBD(LocalDate Date, StatusEnum status){
        CursRequestEntity cursRequestEntity = new CursRequestEntity();
        cursRequestEntity.setCurs_dateRequestEntity(Date);
        cursRequestEntity.setStatusEntity(status);
        cursRequestEntity.setRequest_dateEntity(LocalDateTime.now());
        return cursRequestEntity;
    }
}
