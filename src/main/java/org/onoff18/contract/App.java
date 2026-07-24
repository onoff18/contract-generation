package org.onoff18.contract;

import lombok.extern.slf4j.Slf4j;
import org.onoff18.contract.generator.DocumentGenerator;
import org.onoff18.contract.model.*;
import org.onoff18.contract.repository.PersonalRepository;
import org.onoff18.contract.service.DaDataClient;
import org.onoff18.contract.service.PersonalService;

import java.math.BigDecimal;
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
            DocumentGenerator generator = new DocumentGenerator("template_contract.docx");

            // 2. Создаем самозанятого с минимальными данными (только БИК)
            log.info("--- Создание объекта Personal ---");
            Personal personal = Personal.builder()
                    .name("Иванов Иван Иванович")
                    .passport("4515 123456")
                    .passportIssuedBy("ОВД района Тверской г. Москвы")
                    .passportIssueDate("15.03.2015")
                    .address("г. Москва, ул. Пушкина, д. 10, кв. 5")
                    .inn("123456789012")
                    .npdRegistrationCity("г. Москва")
                    .bankAccount("40817810099910004312")
                    .bankIdentifierCode("044525225") // Только БИК!
                    .phone("+79001234567")
                    .build();

            // 3. Обогащаем данные через сервис (получаем название банка и К/С по БИК)
            log.info("--- Запуск обогащения данных через PersonalService ---");
            Personal enrichedPersonal = personalService.enrichWithBankData(personal);
            log.info("После обогащения: банк={}, корр.счет={}",
                    enrichedPersonal.getBankName(), enrichedPersonal.getCorrespondentAccount());

            // 4. Создаем Договор
            Contract contract = Contract.builder()
                    .number("СЗ-001/2026")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(12)) // Договор на 1 год
                    .build();

            // 5. Создаем Менеджера
            Manager manager = Manager.builder()
                    .name("Петрова Мария Сергеевна")
                    .phone("+7 (495) 984-79-74")
                    .email("petrova@mirvest.ru")
                    .position("Менеджер по работе с исполнителями")
                    .build();

            // 6. Создаем Приложение к договору
            Application application = Application.builder()
                    .number("1")
                    .date(LocalDate.now())
                    .campaignName("Летняя распродажа 2026")
                    .brandName("Coca-Cola")
                    .serviceName("Промоутерские услуги")
                    .description("Раздача листовок и образцов продукции в торговых центрах")
                    .eventLocation("ТЦ «Европейский», г. Москва, пл. Киевского Вокзала, 2")
                    .serviceStartDate(LocalDate.now())
                    .serviceEndDate(LocalDate.now().plusDays(14))
                    .amount(new BigDecimal("20373.50")) // Проверяем округление вверх!
                    .contract(contract)
                    .build();

            // Связываем приложение с менеджером
            manager.addApplication(application);

            // Проверка валидности договора
            if (!application.isContractValid()) {
                log.warn("⚠️ ВНИМАНИЕ: Договор недействителен на дату подписания приложения!");
            } else {
                log.info("✅ Договор действителен на дату приложения.");
            }

            // Тест конвертера сумм
            log.info("--- Тест NumberToWordsConverter ---");
            log.info("Сумма 20373.50 прописью: {}", application.getAmountInWords());

            // 7. Сохраняем данные в JSON
            log.info("--- Сохранение обогащенных данных в JSON ---");
            repository.save(List.of(enrichedPersonal));

            // === ФОРМИРОВАНИЕ СТРУКТУРЫ ПАПок ===
            // Базовая папка для всех документов
            String baseOutputDir = "output";

            // Папка ФИО (заменяем пробелы на подчёркивания)
            String personalFolder = personal.getName().replace(" ", "_");

            // Безопасный номер договора для имени файла
            String safeContractNumber = contract.getNumber().replace("/", "_");

            // === 8. Генерируем Договор ===
            log.info("--- Запуск генерации Договора ---");
            String contractDir = baseOutputDir + "/" + personalFolder + "/0_Договор";
            String contractOutputPath = contractDir + "/Договор_" + safeContractNumber + ".docx";
            generator.generateContract(enrichedPersonal, contract, contractOutputPath);

            // === 9. Генерируем Приложение ===
            log.info("--- Запуск генерации Приложения ---");
            String appDir = baseOutputDir + "/" + personalFolder + "/Приложение №" + application.getNumber();
            String appOutputPath = appDir + "/Приложение_" + application.getNumber() + ".docx";
            generator.generateApplication(enrichedPersonal, contract, application, appOutputPath);

            // === 10. Создаем Акт выполненных работ ===
            Act act = Act.builder()
                    .number("1")
                    .date(LocalDate.now())
                    .description("Была осуществлена выдача дегустационного продукта Молочный коктейль под товарным знаком «Растишка» в рамках Мероприятия, с целью узнаваемости продукции.")
                    .application(application)
                    .build();

            // === 11. Генерируем Акт ===
            log.info("--- Запуск генерации Акта ---");
            String actOutputPath = appDir + "/Акт_" + act.getNumber() + ".docx";
            generator.generateAct(enrichedPersonal, act, actOutputPath);

            log.info("--- 🎉 Операция успешно завершена! Проверьте папку: " + baseOutputDir + "/" + personalFolder + " ---");

        } catch (Exception e) {
            log.error("❌ Произошла критическая ошибка при работе приложения", e);
        }
    }
}