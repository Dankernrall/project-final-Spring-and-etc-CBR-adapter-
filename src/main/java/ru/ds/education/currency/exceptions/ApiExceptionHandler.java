package ru.ds.education.currency.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.ds.education.currency.core.service.CurrencyService;
import ru.ds.education.currency.db.entity.StatusEnum;
import ru.ds.education.currency.db.repository.RequestRepository;

import javax.jms.JMSException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@ControllerAdvice
public class ApiExceptionHandler {
    @Autowired
    CurrencyService currencyService;
    @Autowired
    RequestRepository requestRepository;

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e){
        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Europe/Astrakhan"))
        );
        return new ResponseEntity<>(apiException, badRequest);
    }
    @ExceptionHandler
    public ResponseEntity<Object> handleEnumError(InvalidFormatException e){
        ApiException apiException = new ApiException(
                "Данная валюта не поддерживается!",
                ZonedDateTime.now(ZoneId.of("Europe/Astrakhan"))
        );
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<Object> handleJMSError(JMSException e){
        ApiException apiException = new ApiException(
                e.getMessage() +
                "\nJMS Exception: Что-то пошло не так!",
                ZonedDateTime.now(ZoneId.of("Europe/Astrakhan"))
        );
        return new ResponseEntity<>(apiException, HttpStatus.BAD_GATEWAY);
    }
    @ExceptionHandler(value = {ApiServiceCbrError.class})
    public ResponseEntity<Object> handleServiceCbrError(ApiServiceCbrError e){
        currencyService.replaceStatusRequest(e.getMessage().trim(), StatusEnum.FAILED);
        return new ResponseEntity<>("", HttpStatus.BAD_GATEWAY);
    }
    @ExceptionHandler
    public ResponseEntity<Object> handleEnumError2(ConversionFailedException e){
        ApiException apiException = new ApiException(
                "Данная валюта не поддерживается!",
                ZonedDateTime.now(ZoneId.of("Europe/Astrakhan"))
        );
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<Object> handleListenerError(ListenerExecutionFailedException e){
        ApiException apiException = new ApiException(
                e.getMessage() + " Ошибка при считывании от ЦБ!",
                ZonedDateTime.now(ZoneId.of("Europe/Astrakhan"))
        );
        return new ResponseEntity<>(apiException, HttpStatus.BAD_GATEWAY);
    }
    @ExceptionHandler(value = {ApiBadData.class})
    public ResponseEntity<Object> handleBadDataException(ApiBadData e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Europe/Astrakhan"))
        );
        return new ResponseEntity<>(apiException, badRequest);
    }
}
