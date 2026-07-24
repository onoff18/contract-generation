package org.onoff18.contract.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
    private String name;                      // ФИО менеджера
    private String phone;                     // Телефон менеджера
    private String email;                     // Email менеджера (опционально)
    private String position;                  // Должность (например, "Менеджер по работе с исполнителями")

    // Список всех приложений, которые инициировал этот менеджер
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    /**
     * Добавляет приложение в список менеджера
     */
    public void addApplication(Application application) {
        if (application == null) {
            return;
        }
        this.applications.add(application);
        // Устанавливаем обратную ссылку
        application.setManager(this);
    }

    /**
     * Возвращает количество приложений, инициированных менеджером
     */
    public int getApplicationsCount() {
        return applications.size();
    }

    /**
     * Возвращает общую сумму всех приложений менеджера
     */
    public java.math.BigDecimal getTotalAmount() {
        return applications.stream()
                .map(Application::getAmount)
                .filter(amount -> amount != null)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}