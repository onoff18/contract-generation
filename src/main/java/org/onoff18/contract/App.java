package org.onoff18.contract;

import lombok.extern.slf4j.Slf4j;
import org.onoff18.contract.generator.DocumentGenerator;
import org.onoff18.contract.model.Contract;
import org.onoff18.contract.model.Personal;
import org.onoff18.contract.repository.PersonalRepository;
import org.onoff18.contract.service.DaDataClient;
import org.onoff18.contract.service.PersonalService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class App {
    public static void main(String[] args) {
        try {
            // 1. Инициализируем компоненты
            PersonalRepository repository = new PersonalRepository();
            DaDataClient daDataClient = new DaDataClient();
            PersonalService personalService = new PersonalService(daDataClient);

            // 2. Создаем самозанятого с МИНИМАЛЬНЫМИ данными (только БИК)
            log.info("--- Создание объекта Personal с минимальными данными ---");
            Personal personal = Personal.builder()
                    .name("Иванов Иван Иванович")
                    .passport("4515 123456")
                    .passportIssuedBy("ОВД района Тверской г. Москвы") // НОВОЕ
                    .passportIssueDate("15.03.2015")                   // НОВОЕ
                    .address("г. Москва, ул. Пушкина, д. 10, кв. 5")
                    .inn("123456789012")
                    .npdRegistrationCity("г. Москва")
                    .bankAccount("40817810099910004312")
                    .bankIdentifierCode("044525225")
                    .phone("+79001234567")
                    .build();

            log.info("До обогащения: банк={}, корр.счет={}",
                    personal.getBankName(), personal.getCorrespondentAccount());

            // 3. Обогащаем данные через сервис
            log.info("--- Запуск обогащения данных через PersonalService ---");
            Personal enrichedPersonal = personalService.enrichWithBankData(personal);

            log.info("После обогащения: банк={}, корр.счет={}",
                    enrichedPersonal.getBankName(), enrichedPersonal.getCorrespondentAccount());

            // 4. Сохраняем уже полный объект в JSON
            log.info("--- Сохранение обогащенных данных ---");
            repository.save(List.of(enrichedPersonal));

            // 5. Загружаем обратно для проверки
            log.info("--- Загрузка данных из JSON ---");
            List<Personal> loadedPersonals = repository.load();

            for (Personal p : loadedPersonals) {
                log.info("Загружен: {}", p.getName());
                log.info("  Банк: {}", p.getBankName());
                log.info("  Корр. счет: {}", p.getCorrespondentAccount());
                log.info("  БИК: {}", p.getBankIdentifierCode());
            }

            log.info("--- Операция успешно завершена ---");

            // 6. Создаем объект Договора
            Contract contract = Contract.builder()
                    .number("СЗ-001/2026")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(12)) // Договор на 1 год
                    .build();

            // 7. Генерируем Word-документ!
            log.info("--- Запуск генерации Word-документа ---");

            // Просто имя файла, который лежит в src/main/resources
            String templateResourceName = "template.docx";

            // Заменяем пробелы и слеши на подчеркивания для безопасного имени файла
            String safeNumber = contract.getNumber().replace("/", "_");
            String safeName = personal.getName().replace(" ", "_");
            String outputPath = "Договор_" + safeNumber + "_" + safeName + ".docx";

            DocumentGenerator generator = new DocumentGenerator(templateResourceName);
            generator.generateContract(enrichedPersonal, contract, outputPath);

        } catch (Exception e) {
            log.error("Произошла критическая ошибка при работе приложения", e);
        }

    }
}