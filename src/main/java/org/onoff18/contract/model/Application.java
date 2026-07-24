package org.onoff18.contract.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.onoff18.contract.util.NumberToWordsConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    private String number;                    // Номер приложения
    private LocalDate date;                   // Дата подписания приложения
    private String campaignName;              // Название рекламной кампании
    private String brandName;                 // Название бренда
    private String serviceName;               // Наименование услуги
    private String description;               // Описание услуги
    private String eventLocation;             // Место проведения мероприятия
    private LocalDate serviceStartDate;       // Период начала оказания услуги
    private LocalDate serviceEndDate;         // Период окончания оказания услуги
    private BigDecimal amount;                // Стоимость услуг

    // Ссылки на связанные объекты
    private Contract contract;                // Договор, к которому относится приложение
    private Manager manager;                  // Менеджер, который инициировал приложение

    /**
     * Переводит сумму прописью на русском языке
     * Округляет до рубля вверх, если есть копейки
     */
    public String getAmountInWords() {
        return NumberToWordsConverter.convert(amount);
    }

    /**
     * Проверяет, действителен ли договор на дату подписания приложения
     */
    public boolean isContractValid() {
        if (contract == null || date == null) {
            return false;
        }
        return contract.isValidOnDate(date);
    }
}