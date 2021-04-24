package ru.ds.education.currency.api.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.ds.education.currency.core.model.CurrencyModel;
import ru.ds.education.currency.core.service.CurrencyService;
import ru.ds.education.currency.db.entity.CurrencyEnum;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/currency")
public class Controller {


    @Autowired
    private CurrencyService currencyService;

    @ApiOperation(value = "Добавление новых данных")
    @PostMapping
    public CurrencyModel addCurrency(@RequestBody CurrencyModel currencyModel) {
        return currencyService.addCurrency(currencyModel);
    }

    @ApiOperation(value = "Получение всей информации по валюте через id")
    @RequestMapping(value = {"{id}"}, method = RequestMethod.GET)
    public CurrencyModel getCurrency(@PathVariable("id") Long id) {
        return currencyService.getCurrency(id);
    }

    @ApiOperation(value = "Удаление данных по id")
    @DeleteMapping(value = {"{id}"})
    public CurrencyModel deleteCurrency(@PathVariable(value = "id") Long id) {
        CurrencyModel tmp = currencyService.getCurrency(id);
        currencyService.deleteCurrency(id);
        return tmp;
    }

    @ApiOperation(value = "Замена данных по id")
    @PutMapping(value = "{id}")
    public CurrencyModel putCurrency(@RequestBody CurrencyModel currencyModel, @PathVariable(value = "id") Long id) {
        return currencyService.replaceCurrency(currencyModel, id);
    }

    @ApiOperation(value = "Получение данных по валюте и дате")
    @GetMapping
    public List<CurrencyModel> getByDateAndId(@RequestParam CurrencyEnum currency,@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        return currencyService.getByDateAndId(currency, date);
    }

}
