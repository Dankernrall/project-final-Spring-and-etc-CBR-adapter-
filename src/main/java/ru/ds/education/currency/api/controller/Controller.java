package ru.ds.education.currency.api.controller;

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

    @PostMapping
    public CurrencyModel addCurrency(@RequestBody CurrencyModel currencyModel) {
        return currencyService.addCurrency(currencyModel);
    }

    @RequestMapping(value = {"{id}"}, method = RequestMethod.GET)
    public CurrencyModel getCurrency(@PathVariable("id") Long id) {
        return currencyService.getCurrency(id);
    }

    @DeleteMapping(value = {"{id}"})
    public CurrencyModel deleteCurrency(@PathVariable(value = "id") Long id) {
        CurrencyModel tmp = currencyService.getCurrency(id);
        currencyService.deleteCurrency(id);
        return tmp;
    }

    @PutMapping(value = "{id}")
    public CurrencyModel putCurrency(@RequestBody CurrencyModel currencyModel, @PathVariable(value = "id") Long id) {
        return currencyService.replaceCurrency(currencyModel, id);
    }

    @GetMapping
    public List<CurrencyModel> getByDateAndId(@RequestParam CurrencyEnum currency,@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        return currencyService.getByDateAndId(currency, date);
    }

    //Поиск по валюте и по дате
}
