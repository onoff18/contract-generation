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
public class Act {
    private String number;              // Номер акта (например, "1")
    private LocalDate date;             // Дата подписания акта

    // Ссылка на приложение, на основании которого составлен акт
    private Application application;

    private BigDecimal amount;          // Стоимость работ по данному акту
}