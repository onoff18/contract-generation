package org.onoff18.contract.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Personal {

    // Личные данные
    private String name;
    private String passport;               // Серия и номер паспорта
    private String passportIssuedBy;       // Кем выдан паспорт (НОВОЕ)
    private String passportIssueDate;      // Дата выдачи паспорта (НОВОЕ)
    private String address;
    private String inn;
    private String npdRegistrationCity;
    private String phone;

    // Банковские реквизиты
    private String bankAccount;              // Расчетный счет
    private String bankName;                 // Название банка
    private String correspondentAccount;     // Корреспондентский счет
    private String bankIdentifierCode;       // БИК

    /**
     * Автоматически генерирует сокращенное ФИО.
     * "Иванов Иван Иванович" -> "Иванов И. И."
     */
    public String getShortName() {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder shortName = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                shortName.append(" ").append(parts[i].charAt(0)).append(".");
            }
        }
        return shortName.toString();
    }

}