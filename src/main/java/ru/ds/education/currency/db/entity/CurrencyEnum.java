package ru.ds.education.currency.db.entity;

public enum CurrencyEnum {
RUB("RUB"),EUR("EUR"),USD("USD"),BYN("BYN"), CHF("CHF"),
    KZT("KZT"),ZAR("ZAR"),INR("INR"),CNY("CNY"),UZS("UZS"),
    AUD("AUD"),KRW("KRW"),JPY("JPY"),PLN("PLN"),GBP("GBP"),
    MDL("MDL"),AMD("AMD"),HUF("HUF"),TRY("TRY"),TJS("TJS"),
    HKD("HKD"),DKK("DKK"),CAD("CAD"),XDR("XDR"),BGN("BGN"),
    NOK("NOK"),RON("RON"),SGD("SGD"),AZN("AZN"),CZK("CZK"),
    KGS("KGS"),SEK("SEK"),TMT("TMT"),BRL("BRL"),UAH("UAH");

    String value;

    CurrencyEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
