package ru.ds.education.currency.jms.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import ru.ds.education.currency.cbr.model.CurrencyCbrModel;
import ru.ds.education.currency.cbr.service.ServiceCbr;
import ru.ds.education.currency.jms.mapper.DefaultMapper;
import ru.ds.education.currency.jms.model.RequestMessage;
import ru.ds.education.currency.jms.model.ResponseMessage;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;
import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
public class JmsController {

    @Autowired
    DefaultMapper defaultMapper;
    @Autowired
    ServiceCbr serviceCbr;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JmsTemplate jmsTemplate;

    @JmsListener(destination = "RU-DS-EDUCATION-CBR-REQUEST")
    public void getMessage(MapMessage message) throws JMSException, DatatypeConfigurationException, InterruptedException, ExecutionException {
        objectMapper.registerModule(new JavaTimeModule());
        String correlationID = null;
        if(message.getJMSCorrelationID()!=null)
            correlationID = message.getJMSCorrelationID();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String whatGet = message.getString("Date");
        LocalDate Date = LocalDate.parse(whatGet, formatter);
        message.clearBody();
        System.out.println(Thread.currentThread().getName());
        message = serviceCbr.cbr(Date.atTime(12,0),message).get(); //Берем дату и отправляем ее с временем 12:00.
        if(correlationID!=null)
            message.setJMSCorrelationID(correlationID);
        jmsTemplate.convertAndSend(message);
    }
}
