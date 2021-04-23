package ru.ds.education.currency.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private CurrencyRepository currencyRepository;

    public CurrencyModel addCurrency(CurrencyModel currencyModel) {
        if (currencyModel.getCursModel() != 0
                && currencyModel.getCurs_dateModel() != null) {
            CurrencyEntity currencyEntityNew =
                    new CurrencyEntity(currencyModel.getCurrencyModel(),
                            currencyModel.getCursModel(),
                            currencyModel.getCurs_dateModel());
            currencyEntityNew = currencyRepository.save(currencyEntityNew);
            currencyModel.setIdModel(currencyEntityNew.getIdEntity());
            return currencyModel;
        } else throw new ApiBadData("Не все поля заполнены!");
    }

    public CurrencyModel getCurrency(Long id) {
        if (currencyRepository.existsById(id)) {
            CurrencyEntity currencyEntityDb =
                    currencyRepository.getOne(id);
            return new CurrencyModel(currencyEntityDb.getIdEntity(), currencyEntityDb.getCurrencyEntity(),
                    currencyEntityDb.getCursEntity(), currencyEntityDb.getCurs_dateEntity());
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
                replaceCurrencyEntity.setCurrencyEntity(currencyModel.getCurrencyModel());
                replaceCurrencyEntity.setCursEntity(currencyModel.getCursModel());
                replaceCurrencyEntity.setCurs_dateEntity(currencyModel.getCurs_dateModel());
                currencyRepository.save(replaceCurrencyEntity);
                return new CurrencyModel(idReplace, currencyModel.getCurrencyModel(), currencyModel.getCursModel(), currencyModel.getCurs_dateModel());
            } else throw new ApiBadData("Не все поля заполнены!");
        } else throw new ApiRequestException("Валюты с id - " + idReplace + " не существует!");
    }

    public List<CurrencyModel> getByDateAndId(CurrencyEnum currencyEntity, LocalDate Date){
        List<CurrencyEntity> whatFind = currencyRepository.findByDate(currencyEntity,Date);
        List<CurrencyModel> result = new ArrayList<>();
        for(CurrencyEntity what : whatFind) {
            CurrencyModel currency = new CurrencyModel(what.getIdEntity(),what.getCurrencyEntity(),
                    what.getCursEntity(),what.getCurs_dateEntity());
            result.add(new CurrencyModel(what.getIdEntity(),what.getCurrencyEntity(),what.getCursEntity(),what.getCurs_dateEntity()));
        }
        return result;
    }
}
