package ru.ds.education.currency.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

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
    public ResponseEntity<Object> handleEnumError2(ConversionFailedException e){
        ApiException apiException = new ApiException(
                "Данная валюта не поддерживается!",
                ZonedDateTime.now(ZoneId.of("Europe/Astrakhan"))
        );
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
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
