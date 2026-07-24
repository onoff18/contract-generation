package org.onoff18.contract;

import lombok.extern.slf4j.Slf4j;
import org.onoff18.contract.model.Personal;
import org.onoff18.contract.repository.PersonalRepository;
import org.onoff18.contract.service.DaDataClient;

import java.util.List;
import java.util.Map;

@Slf4j // Магия Lombok: автоматически создает переменную log
public class App {
    public static void main(String[] args) {
        try {
            // Тест интеграции с DaData
            log.info("--- Тестирование DaData API ---");
            DaDataClient daDataClient = new DaDataClient();

            // Пробуем найти банк по БИК Сбербанка (044525225)
            Map<String, String> bankInfo = daDataClient.getBankByBik("044525225");

            if (!bankInfo.isEmpty()) {
                log.info("Название банка: {}", bankInfo.get("name"));
                log.info("Корр. счет: {}", bankInfo.get("correspondentAccount"));
                log.info("Город: {}", bankInfo.get("city"));
            } else {
                log.warn("Данные о банке не найдены. Проверь API ключ!");
            }

            // 1. Создаем репозиторий
            PersonalRepository repository = new PersonalRepository();

            // 2. Создаем тестового самозанятого
            Personal personal = Personal.builder()
                    .name("Иванов Иван Иванович")
                    .passport("1234 567890 10101")
                    .address("г. Москва, ул. Пушкина, д. 10")
                    .inn("123456789012")
                    .npdRegistrationCity("г. Москва")
                    .bankAccount("40817810099910004312")
                    .bankName("ПАО Сбербанк")
                    .correspondentAccount("30101810400000000225")
                    .bankIdentifierCode("044525225")
                    .phone("+79001234567")
                    .build();

            // 3. Сохраняем в JSON
            log.info("--- Начало сохранения данных ---");
            repository.save(List.of(personal));

            // 4. Загружаем из JSON
            log.info("--- Начало загрузки данных ---");
            List<Personal> loadedPersonals = repository.load();

            // 5. Выводим результат
            for (Personal p : loadedPersonals) {
                log.info("Загружен пользователь: {}", p.getName());
                log.info("ИНН: {}, Банк: {}", p.getInn(), p.getBankName());
            }

            log.info("--- Операция успешно завершена ---");

        } catch (Exception e) {
            // Профессиональная обработка ошибки
            log.error("Произошла критическая ошибка при работе с репозиторием", e);
        }
    }
}