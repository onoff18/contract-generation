package org.onoff18.contract.generator;

import com.deepoove.poi.XWPFTemplate;
import org.onoff18.contract.model.Contract;
import org.onoff18.contract.model.Personal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DocumentGenerator {

    private static final Logger log = LoggerFactory.getLogger(DocumentGenerator.class);

    // Форматирование даты: "«24» июля 2026 г."
    // Используем Locale.of() вместо устаревшего new Locale()
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("'«'dd'»' MMMM yyyy 'г.'", Locale.of("ru"));

    private final String templateResourceName;

    public DocumentGenerator(String templateResourceName) {
        this.templateResourceName = templateResourceName;
    }

    public void generateContract(Personal personal, Contract contract, String outputPath) {
        log.info("Начинаем генерацию договора №{} для: {}", contract.getNumber(), personal.getName());

        try {
            Map<String, Object> data = new HashMap<>();

            // Данные договора
            data.put("contractNumber", contract.getNumber());
            data.put("contractDate", contract.getStartDate().format(DATE_FORMATTER));
            data.put("contractEndDate", contract.getEndDate().format(DATE_FORMATTER));

            // Данные исполнителя
            data.put("name", personal.getName());
            data.put("shortName", personal.getShortName());
            data.put("passport", personal.getPassport());
            data.put("passportIssuedBy", personal.getPassportIssuedBy());
            data.put("passportIssueDate", personal.getPassportIssueDate());
            data.put("address", personal.getAddress());
            data.put("inn", personal.getInn());
            data.put("npdRegistrationCity", personal.getNpdRegistrationCity());
            data.put("phone", personal.getPhone());

            // Банковские реквизиты
            data.put("bankName", personal.getBankName());
            data.put("bankAccount", personal.getBankAccount());
            data.put("correspondentAccount", personal.getCorrespondentAccount());
            data.put("bik", personal.getBankIdentifierCode());

            // Загружаем шаблон как ресурс из classpath
            InputStream templateStream = DocumentGenerator.class.getClassLoader()
                    .getResourceAsStream(templateResourceName);
            if (templateStream == null) {
                log.error("❌ Шаблон '{}' не найден в папке resources!", templateResourceName);
                return;
            }

            // Генерация файла
            XWPFTemplate template = XWPFTemplate.compile(templateStream).render(data);

            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                template.write(out);
                template.close();
            }

            log.info("✅ Договор успешно сгенерирован: {}", outputPath);

        } catch (IOException e) {
            log.error("❌ Ошибка генерации документа", e);
        }
    }
}