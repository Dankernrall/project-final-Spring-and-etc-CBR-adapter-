package ru.ds.education.currency.jms.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import ru.ds.education.currency.cbr.service.ServiceCbr;
import ru.ds.education.currency.exceptions.ApiServiceCbrError;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.xml.datatype.DatatypeConfigurationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

@Controller
public class JmsController {

    @Autowired
    ServiceCbr serviceCbr;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JmsTemplate jmsTemplate;

    @Async
    @JmsListener(destination = "RU-DS-EDUCATION-CBR-REQUEST")
    public void getMessage(MapMessage message) throws JMSException, DatatypeConfigurationException, InterruptedException, ExecutionException {
        try{
        objectMapper.registerModule(new JavaTimeModule());
        String correlationID = null;
        if(message.getJMSCorrelationID()!=null)
            correlationID = message.getJMSCorrelationID();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String whatGet = message.getString("Date");
        LocalDate Date = LocalDate.parse(whatGet, formatter);
        message = serviceCbr.cbr(Date.atTime(12,0),message).get(); //Берем дату и отправляем ее с временем 12:00.
        if(correlationID!=null && message != null)
            message.setJMSCorrelationID(correlationID);
        System.out.println(message.getJMSCorrelationID());
        jmsTemplate.convertAndSend(message);}
        catch(IllegalArgumentException | NullPointerException | JMSException | ListenerExecutionFailedException | InterruptedException e){throw new ApiServiceCbrError(message.getJMSCorrelationID());}
    }
}
