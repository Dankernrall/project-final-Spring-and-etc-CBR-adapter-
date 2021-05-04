package ru.ds.education.currency.cbr.service;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.cbr.web.DailyInfo;
import ru.cbr.web.DailyInfoSoap;
import ru.cbr.web.GetCursOnDateXMLResponse;
import ru.ds.education.currency.cbr.model.CurrencyCbrModel;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class ServiceCbr {
    @Autowired
    CurrencyCbrModel currencyCbrModel;

    public List<CurrencyCbrModel> cbr(LocalDateTime Date) throws DatatypeConfigurationException {
        List<CurrencyCbrModel> listOfCurrency = new ArrayList<>();
        String currencyName = null;
        double currencyValue = 0;
        DailyInfoSoap port = new DailyInfo().getDailyInfoSoap();

        GregorianCalendar gcal = GregorianCalendar.from(Date.atZone(ZoneId.systemDefault())); //Преобразуем LocalDateTime сначала в Gregorian Calendar,
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal); //а после в XMLGregorianCalendar,
        GetCursOnDateXMLResponse.GetCursOnDateXMLResult cursOnDateXML = port.getCursOnDateXML(xcal); //необходимый ЦБ.

        List<Object> list = cursOnDateXML.getContent(); //Заполняем лист ответом от ЦБ.
        ElementNSImpl e = (ElementNSImpl) list.get(0);
        NodeList chCodeList = e.getElementsByTagName("VchCode"); //Находим элементы по тэгу VchCode.

        for (int i = 0; i < chCodeList.getLength(); i++) {
            Node valuteChNode = chCodeList.item(i);
            Node parent = valuteChNode.getParentNode();
            NodeList nodeList = parent.getChildNodes();
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node currentNode = nodeList.item(j);
                String name = currentNode.getNodeName(); //Находим за что можно "зацепиться"
                Node currentValue = currentNode.getFirstChild();
                String value = currentValue.getNodeValue(); //Значение под элементом, имея name, мы можем вытаскивать его значение путем name.equalsIgnoreCase
                if (name.equalsIgnoreCase("VchCode")) { //Ищем нужные нам данные, под VchCode находится обозначение валюты.
                    currencyName = value.trim(); //Удаляем лишние пробелы и записываем в переменную
                }
                if (name.equalsIgnoreCase("Vcurs")) { // А под Vcurs ее курс от даты запроса.
                    double round = Double.parseDouble(value.trim());
                    currencyValue = (double) Math.round(round * 100) / 100; //Удаляем лишние пробелы, округляем до двух знаков после запятой и записываем в переменную
                }
                if (currencyName != null && currencyValue != 0) {
                    listOfCurrency.add(new CurrencyCbrModel(currencyName, currencyValue));
                    currencyName = null; //Обнуляем переменные, чтобы значения не дублировались, так как у каждой валюты
                    currencyValue = 0; //еще имеется Vname,Vcode и др.
                }
            }
        }
        return listOfCurrency;
    }
}
