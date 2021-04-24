package ru.ds.education.currency.core.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import ru.ds.education.currency.core.model.CurrencyModel;
import ru.ds.education.currency.db.entity.CurrencyEntity;

@Component
public class CurrencyMapper extends ConfigurableMapper {


    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(CurrencyEntity.class, CurrencyModel.class)
                .field("idEntity","idModel")
                .field("currencyEntity","currencyModel")
                .field("cursEntity","cursModel")
                .field("curs_dateEntity","curs_dateModel")
                .register();
    }
}
