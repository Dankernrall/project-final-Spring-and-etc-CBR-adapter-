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
            if(CompletableFuture.completedFuture(currencyModel).isDone() && !CompletableFuture.completedFuture(currencyModel).isCompletedExceptionally())
                return CompletableFuture.completedFuture(currencyModel);
            else
                return null;
        } else throw new ApiBadData("Не все поля заполнены!");
    }

    @Async
    public CompletableFuture<CurrencyModel> getCurrency(Long id, HttpServletResponse response) {
        if (currencyRepository.existsById(id)) {
            CurrencyEntity currencyEntityDb =
                    currencyRepository.getOne(id);
            if(CompletableFuture.completedFuture(currencyEntityDb).isDone() && !CompletableFuture.completedFuture(currencyEntityDb).isCompletedExceptionally())
                return CompletableFuture.completedFuture(currencyMapper.map(currencyEntityDb, CurrencyModel.class));
            else
                return null;
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
                if(CompletableFuture.completedFuture(currencyModel).isDone() && !CompletableFuture.completedFuture(currencyModel).isCompletedExceptionally())
                    return CompletableFuture.completedFuture(currencyModel);
                else
                    return null;
            } else throw new ApiBadData("Не все поля заполнены!");
        } else throw new ApiRequestException("Валюты с id - " + idReplace + " не существует!");
    }

    @Async
    public CompletableFuture<CurrencyModel> getByDateAndId(CurrencyEnum currency, LocalDate date, HttpServletResponse response) throws JMSException {
        CurrencyEntity whatFind = currencyRepository.findByDate(currency, date);
        if (whatFind != null) {
            CurrencyModel result = currencyMapper.map(whatFind, CurrencyModel.class);
            return CompletableFuture.completedFuture(result);
        } else {
            CursRequestEntity cursRequestEntity = requestRepository.find(date);
            if (cursRequestEntity != null && cursRequestEntity.getStatusEntity() != StatusEnum.FAILED) {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                return CompletableFuture.completedFuture(null);
            } else {
                MapMessage message = getMapMessageAndCreateSession();
                message.setString("Date", date.toString());
                message.setJMSCorrelationID(UUID.randomUUID().toString());

                newRequestInBD(date,message.getJMSCorrelationID());
                replaceStatusRequest(message.getJMSCorrelationID(),StatusEnum.CREATED);
                jmsTemplate.convertAndSend("RU-DS-EDUCATION-CBR-REQUEST", message);
                replaceStatusRequest(message.getJMSCorrelationID(),StatusEnum.SENT);
                System.out.println(message.getJMSCorrelationID());
                Message receive = jmsTemplate.receive("RU-SD-EDUCATION-CBR-RESPONSE");
                MapMessage whatWeReceive = (MapMessage) receive;
                if(whatWeReceive == null)
                    replaceStatusRequest(message.getJMSCorrelationID(), StatusEnum.FAILED);
                else{
                Enumeration elements = whatWeReceive.getMapNames();
                String key;
                while (elements.hasMoreElements()) {
                    key = (String) elements.nextElement();
                    CurrencyEntity currencyForSaveOrReplaceEntity = newCurrencyInBD(CurrencyEnum.valueOf(key), Float.parseFloat(whatWeReceive.getString(key)), date);
                    CurrencyEntity byDate = currencyRepository.findByDate(CurrencyEnum.valueOf(key), date);
                    if (byDate != null) {
                        CurrencyModel replaceCurrency = currencyMapper.map(currencyForSaveOrReplaceEntity, CurrencyModel.class);
                        replaceCurrency(replaceCurrency, byDate.getIdEntity(), response);
                    } else {
                        currencyRepository.save(currencyForSaveOrReplaceEntity);
                    }
                }
                replaceStatusRequest(message.getJMSCorrelationID(),StatusEnum.PROCESSED);}
            }
        }
        return CompletableFuture.completedFuture(null);
    }


    public void replaceStatusRequest(String correlationID, StatusEnum status){
                CursRequestEntity findRequest = requestRepository.findByCorrelationID(correlationID);
                findRequest.setStatusEntity(status);
                requestRepository.save(findRequest);
            }


    private MapMessage getMapMessageAndCreateSession() throws JMSException {
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MapMessage message = session.createMapMessage();
        return message;
    }

    private CurrencyEntity newCurrencyInBD(CurrencyEnum currency, float curs, LocalDate date) {
        CurrencyEntity currencyEntity = new CurrencyEntity();
        currencyEntity.setCurrencyEntity(currency);
        currencyEntity.setCursEntity(curs);
        currencyEntity.setCurs_dateEntity(date);
        return currencyEntity;
    }

    private void newRequestInBD(LocalDate date, String correlationID) {
        CursRequestEntity cursRequestEntity = new CursRequestEntity();
        cursRequestEntity.setCurs_dateRequestEntity(date);
        cursRequestEntity.setCorrelation_idEntity(correlationID);
        cursRequestEntity.setRequest_dateEntity(LocalDateTime.now());
        requestRepository.save(cursRequestEntity);
    }
}
