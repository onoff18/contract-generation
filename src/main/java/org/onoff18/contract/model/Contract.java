package org.onoff18.contract.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Contract {

    private String number;                    // Номер договора (например, "СЗ-001")
    private LocalDate startDate;              // Дата начала договора
    private LocalDate endDate;                // Дата окончания договора

    @Builder.Default
    private List<Application> applications = new ArrayList<>();  // Список приложений

    // Метод для проверки, действителен ли договор на указанную дату
    public boolean isValidOnDate(LocalDate date) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}