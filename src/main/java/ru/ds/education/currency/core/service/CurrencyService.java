package ru.ds.education.currency.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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

import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
public class CurrencyService {

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    JmsTemplate jmsTemplate;
    protected Session session = null;
    @Resource
    protected ConnectionFactory factory;

    protected Connection connection;

    @Async
    public CompletableFuture<CurrencyModel> addCurrency(CurrencyModel currencyModel) {
        if (currencyModel.getCursModel() != 0
                && currencyModel.getCurs_dateModel() != null) {
            CurrencyEntity currencyEntityNew = currencyMapper.map(currencyModel, CurrencyEntity.class);
            currencyEntityNew = currencyRepository.save(currencyEntityNew);
            currencyModel = currencyMapper.map(currencyEntityNew, CurrencyModel.class);
            return CompletableFuture.completedFuture(currencyModel);
        } else throw new ApiBadData("Не все поля заполнены!");
    }
    @Async
    public CompletableFuture<CurrencyModel> getCurrency(Long id, HttpServletResponse response) {
        if (currencyRepository.existsById(id)) {
            CurrencyEntity currencyEntityDb =
                    currencyRepository.getOne(id);
            //response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return CompletableFuture.completedFuture(currencyMapper.map(currencyEntityDb, CurrencyModel.class));
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
    @Async
    public CompletableFuture<CurrencyModel> replaceCurrency(CurrencyModel currencyModel, Long idReplace, HttpServletResponse response) {
        if (currencyRepository.existsById(idReplace)) {
            if (currencyModel.getCurrencyModel() != null && currencyModel.getCursModel() != 0
                    && currencyModel.getCurs_dateModel() != null) {
                CurrencyEntity replaceCurrencyEntity = currencyRepository.getOne(idReplace);
                currencyModel.setIdModel(replaceCurrencyEntity.getIdEntity());
                CurrencyEntity replaceCurrency = currencyMapper.map(currencyModel, CurrencyEntity.class);
                replaceCurrency = currencyRepository.save(replaceCurrency);
                currencyModel = currencyMapper.map(replaceCurrency, CurrencyModel.class);
                return CompletableFuture.completedFuture(currencyModel);
            } else throw new ApiBadData("Не все поля заполнены!");
        } else throw new ApiRequestException("Валюты с id - " + idReplace + " не существует!");
    }
    @Async
    public CompletableFuture<CurrencyModel> getByDateAndId(CurrencyEnum currency, LocalDate Date, HttpServletResponse response) throws JMSException {
        CurrencyEntity whatFind = currencyRepository.findByDate(currency, Date);
        if (whatFind != null) {
            CurrencyModel result = currencyMapper.map(whatFind, CurrencyModel.class);
            return CompletableFuture.completedFuture(result);
        } else {
            CursRequestEntity cursRequestEntity = requestRepository.find(Date);
            if (cursRequestEntity != null && cursRequestEntity.getStatusEntity() != StatusEnum.FAILED) {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                return CompletableFuture.completedFuture(null);
            } else {
                MapMessage message;
                connection = factory.createConnection();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                message = session.createMapMessage();
                message.setString("Date", Date.toString());
                message.setJMSCorrelationID(UUID.randomUUID().toString());
                CursRequestEntity requestEntity = requestRepository.save(newRequestInBD(Date, StatusEnum.CREATED));
                jmsTemplate.convertAndSend("RU-DS-EDUCATION-CBR-REQUEST", message);
                requestEntity.setStatusEntity(StatusEnum.SENT);
                requestEntity.setCorrelation_idEntity(message.getJMSCorrelationID());
                requestRepository.save(requestEntity);
                MapMessage whatWeReceive = (MapMessage) jmsTemplate.receive("RU-SD-EDUCATION-CBR-RESPONSE");
                Enumeration elements = whatWeReceive.getMapNames();
                String key;
                while(elements.hasMoreElements()){
                    key = (String) elements.nextElement();
                    CurrencyEntity currencyForSaveOrReplaceEntity = newCurrencyInBD(CurrencyEnum.valueOf(key), Float.parseFloat(whatWeReceive.getString(key)), Date);
                    CurrencyEntity byDate = currencyRepository.findByDate(CurrencyEnum.valueOf(key), Date);
                    if (byDate !=null) {
                        CurrencyModel replaceCurrency = currencyMapper.map(currencyForSaveOrReplaceEntity, CurrencyModel.class);
                        replaceCurrency(replaceCurrency,byDate.getIdEntity(),response);
                    }
                     else{
                        currencyRepository.save(currencyForSaveOrReplaceEntity);
                        }
                }
                requestEntity.setStatusEntity(StatusEnum.PROCESSED);
                requestRepository.save(requestEntity);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    private CurrencyEntity newCurrencyInBD(CurrencyEnum currency, float curs, LocalDate Date) {
        CurrencyEntity currencyEntity = new CurrencyEntity();
        currencyEntity.setCurrencyEntity(currency);
        currencyEntity.setCursEntity(curs);
        currencyEntity.setCurs_dateEntity(Date);
        return currencyEntity;
    }

    private CursRequestEntity newRequestInBD(LocalDate Date, StatusEnum status) {
        CursRequestEntity cursRequestEntity = new CursRequestEntity();
        cursRequestEntity.setCurs_dateRequestEntity(Date);
        cursRequestEntity.setStatusEntity(status);
        cursRequestEntity.setRequest_dateEntity(LocalDateTime.now());
        return cursRequestEntity;
    }
}
