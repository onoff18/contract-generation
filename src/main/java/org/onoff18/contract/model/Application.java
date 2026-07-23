package org.onoff18.contract.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    private String number;              // Номер приложения (например, "1")
    private LocalDate date;             // Дата подписания приложения
    private String description;         // Описание выполненных работ или услуг
    private BigDecimal amount;          // Стоимость работ (сумма)
}