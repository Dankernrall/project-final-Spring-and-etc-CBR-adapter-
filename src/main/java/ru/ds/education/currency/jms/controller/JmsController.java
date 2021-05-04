package ru.ds.education.currency.jms.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.ds.education.currency.cbr.model.CurrencyCbrModel;
import ru.ds.education.currency.cbr.service.ServiceCbr;
import ru.ds.education.currency.jms.mapper.DefaultMapper;
import ru.ds.education.currency.jms.model.RequestMessage;
import ru.ds.education.currency.jms.model.ResponseMessage;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.datatype.DatatypeConfigurationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class JmsController {

    @Autowired
    DefaultMapper defaultMapper;
    @Autowired
    ServiceCbr serviceCbr;
    @Autowired
    ObjectMapper objectMapper;


    @JmsListener(destination = "RU-DS-EDUCATION-CBR-REQUEST")
    @SendTo("RU-SD-EDUCATION-CBR-RESPONSE")
    public List<CurrencyCbrModel> getMessage(LocalDate Date) throws JMSException, JsonProcessingException, DatatypeConfigurationException {
        String correlationId = null;
       // if (message.getJMSCorrelationID() != null)
           // correlationId = message.getJMSCorrelationID(); //Вытаскиваем CorrelationID
        objectMapper.registerModule(new JavaTimeModule());
        List<CurrencyCbrModel> getCurrency = serviceCbr.cbr(Date.atTime(12, 00)); //Берем дату и отправляем ее с временем 12:00.
       // ResponseMessage responseMessage = defaultMapper.map(requestMessage, ResponseMessage.class); //  Так как ЦБ дает вчерашнюю валюту при времени 00:00.
        //responseMessage.setCurrencyList(getCurrency); //добавляем все валюты, полученные от ЦБ
       // String response = objectMapper.writeValueAsString(responseMessage);
       // message.clearBody(); //Очищаем все, чтобы мы смогли записать свою информацию
        //if (correlationId != null)
           // message.setJMSCorrelationID(correlationId);
       // message.setText(response); //Запись нашей информации
        return getCurrency;
    }
}
