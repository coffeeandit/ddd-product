package br.com.sicredi.moeda;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

public class Price {

    private Currency currency = Currency.getInstance(new Locale("pt", "br"));
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    private BigDecimal amount;

    public Price(final BigDecimal amount) {

        this.amount = amount.setScale(currency.getDefaultFractionDigits(), DEFAULT_ROUNDING);

    }

    public String toString() {
        return currency.getSymbol() + " " + amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
