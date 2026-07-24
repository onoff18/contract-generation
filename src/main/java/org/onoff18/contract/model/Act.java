package org.onoff18.contract.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.onoff18.contract.util.NumberToWordsConverter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Act {
    private String number;                    // Номер акта
    private LocalDate date;                   // Дата составления акта
    private String description;               // Уникальное описание услуг для акта

    // Ссылка на приложение, к которому относится акт
    private Application application;

    // Форматтеры для дат
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("'«'dd'»' MMMM yyyy 'г.'", Locale.of("ru"));
    private static final DateTimeFormatter DATE_FORMATTER_SIMPLE =
            DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.of("ru"));

    /**
     * Возвращает номер приложения
     */
    public String getApplicationNumber() {
        return application != null ? application.getNumber() : "";
    }

    /**
     * Возвращает дату приложения в формате «дд» месяца yyyy г.
     */
    public String getApplicationDate() {
        if (application == null || application.getDate() == null) return "";
        return application.getDate().format(DATE_FORMATTER);
    }

    /**
     * Возвращает номер договора
     */
    public String getContractNumber() {
        if (application == null || application.getContract() == null) return "";
        return application.getContract().getNumber();
    }

    /**
     * Возвращает дату договора в формате «дд» месяца yyyy г.
     */
    public String getContractDate() {
        if (application == null || application.getContract() == null) return "";
        return application.getContract().getStartDate().format(DATE_FORMATTER);
    }

    /**
     * Возвращает наименование услуги из связанного приложения
     */
    public String getServiceName() {
        return application != null ? application.getServiceName() : "";
    }

    /**
     * Возвращает период оказания услуг в формате дд.мм.гггг – дд.мм.гггг
     */
    public String getServicePeriod() {
        if (application == null || application.getServiceStartDate() == null || application.getServiceEndDate() == null) {
            return "";
        }
        return application.getServiceStartDate().format(DATE_FORMATTER_SIMPLE) + " – " +
                application.getServiceEndDate().format(DATE_FORMATTER_SIMPLE);
    }

    /**
     * Возвращает сумму услуги числом (округленную вверх, с пробелом)
     * Например: 20 374
     */
    public String getServiceAmountNumber() {
        if (application == null || application.getAmount() == null) return "0";

        long roundedAmount = application.getAmount().setScale(0, RoundingMode.CEILING).longValue();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("ru"));
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(roundedAmount);
    }

    /**
     * Возвращает сумму услуги прописью
     * Например: двадцать тысяч триста семьдесят четыре
     */
    public String getServiceAmountWords() {
        if (application == null || application.getAmount() == null) return "ноль";
        return NumberToWordsConverter.convert(application.getAmount());
    }
}