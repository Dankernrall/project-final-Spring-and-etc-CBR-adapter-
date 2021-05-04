package ru.ds.education.currency.jms.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import ru.ds.education.currency.jms.model.RequestMessage;
import ru.ds.education.currency.jms.model.ResponseMessage;

import java.time.LocalDate;

@Component
public class DefaultMapper extends ConfigurableMapper {
    @Override
    protected void configure(MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDate.class));
        factory.classMap(RequestMessage.class, ResponseMessage.class)
                .byDefault()
                .register();
    }
}
