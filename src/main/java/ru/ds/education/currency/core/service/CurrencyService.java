package ru.ds.education.currency.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ds.education.currency.core.mapper.CurrencyMapper;
import ru.ds.education.currency.core.model.CurrencyModel;
import ru.ds.education.currency.db.entity.CurrencyEntity;
import ru.ds.education.currency.db.entity.CurrencyEnum;
import ru.ds.education.currency.db.repository.CurrencyRepository;
import ru.ds.education.currency.exceptions.ApiBadData;
import ru.ds.education.currency.exceptions.ApiRequestException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class CurrencyService {

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private CurrencyRepository currencyRepository;

    public CurrencyModel addCurrency(CurrencyModel currencyModel) {
        if (currencyModel.getCursModel() != 0
                && currencyModel.getCurs_dateModel() != null) {
            CurrencyEntity currencyEntityNew = currencyMapper.map(currencyModel,CurrencyEntity.class);
            currencyEntityNew = currencyRepository.save(currencyEntityNew);
            currencyModel = currencyMapper.map(currencyEntityNew, CurrencyModel.class);
            return currencyModel;
        } else throw new ApiBadData("Не все поля заполнены!");
    }

    public CurrencyModel getCurrency(Long id) {
        if (currencyRepository.existsById(id)) {
            CurrencyEntity currencyEntityDb =
                    currencyRepository.getOne(id);
            return currencyMapper.map(currencyEntityDb,CurrencyModel.class);
        } else {
            throw new ApiRequestException("Валюты с id - " + id + " не существует!");
        }
    }

    public void deleteCurrency(Long id) {
        if (currencyRepository.existsById(id)) {
            currencyRepository.deleteById(id);
        } else {
            throw new ApiRequestException("Валюты с id - " + id + " не существует!");
        }
    }

    public CurrencyModel replaceCurrency(CurrencyModel currencyModel, Long idReplace) {
        if (currencyRepository.existsById(idReplace)) {
            if (currencyModel.getCurrencyModel() != null && currencyModel.getCursModel() != 0
                    && currencyModel.getCurs_dateModel() != null) {
                CurrencyEntity replaceCurrencyEntity = currencyRepository.getOne(idReplace);
                CurrencyEntity replaceCurrency = currencyMapper.map(replaceCurrencyEntity,CurrencyEntity.class);
                replaceCurrency = currencyRepository.save(replaceCurrency);
                currencyModel = currencyMapper.map(replaceCurrency, CurrencyModel.class);
                return currencyModel;
            } else throw new ApiBadData("Не все поля заполнены!");
        } else throw new ApiRequestException("Валюты с id - " + idReplace + " не существует!");
    }

    public List<CurrencyModel> getByDateAndId(CurrencyEnum currencyEntity, LocalDate Date){
        List<CurrencyEntity> whatFind = currencyRepository.findByDate(currencyEntity,Date);
        List<CurrencyModel> result = currencyMapper.mapAsList(whatFind,CurrencyModel.class);
        return result;
    }
}
