package org.onoff18.contract.generator;

import com.deepoove.poi.XWPFTemplate;
import org.onoff18.contract.model.Application;
import org.onoff18.contract.model.Contract;
import org.onoff18.contract.model.Manager;
import org.onoff18.contract.model.Personal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DocumentGenerator {

    private static final Logger log = LoggerFactory.getLogger(DocumentGenerator.class);

    // Формат для договора: "«24» июля 2026 г."
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("'«'dd'»' MMMM yyyy 'г.'", Locale.of("ru"));

    // Формат для дат в приложении: "16.03.2003"
    private static final DateTimeFormatter DATE_FORMATTER_SIMPLE =
            DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.of("ru"));

    private final String templateContractName;

    public DocumentGenerator(String templateContractName) {
        this.templateContractName = templateContractName;
    }

    /**
     * Форматирует телефон в единый вид: 8 (903) 123 45 67
     */
    private String formatPhone(String phone) {
        if (phone == null) return "";
        // Оставляем только цифры
        String digits = phone.replaceAll("\\D", "");

        if (digits.length() == 11) {
            if (digits.startsWith("8") || digits.startsWith("7")) {
                return String.format("8 (%s) %s %s %s",
                        digits.substring(1, 4),
                        digits.substring(4, 7),
                        digits.substring(7, 9),
                        digits.substring(9, 11));
            }
        }
        // Если формат нестандартный, возвращаем как есть
        return phone;
    }

    /**
     * Генерирует Договор
     */
    public void generateContract(Personal personal, Contract contract, String outputPath) {
        log.info("Начинаем генерацию договора №{} для: {}", contract.getNumber(), personal.getName());

        try {
            Map<String, Object> data = new HashMap<>();

            data.put("contractNumber", contract.getNumber());
            data.put("contractDate", contract.getStartDate().format(DATE_FORMATTER));
            data.put("contractEndDate", contract.getEndDate().format(DATE_FORMATTER));

            data.put("name", personal.getName());
            data.put("shortName", personal.getShortName());
            data.put("passport", personal.getPassport());
            data.put("passportIssuedBy", personal.getPassportIssuedBy());
            data.put("passportIssueDate", personal.getPassportIssueDate());
            data.put("address", personal.getAddress());
            data.put("inn", personal.getInn());
            data.put("npdRegistrationCity", personal.getNpdRegistrationCity());
            data.put("phone", formatPhone(personal.getPhone())); // Форматируем телефон

            data.put("bankName", personal.getBankName());
            data.put("bankAccount", personal.getBankAccount());
            data.put("correspondentAccount", personal.getCorrespondentAccount());
            data.put("bik", personal.getBankIdentifierCode());

            InputStream templateStream = DocumentGenerator.class.getClassLoader()
                    .getResourceAsStream(templateContractName);
            if (templateStream == null) {
                log.error("❌ Шаблон '{}' не найден в папке resources!", templateContractName);
                return;
            }

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

    /**
     * Генерирует Приложение к договору
     */
    public void generateApplication(Personal personal, Contract contract, Application application, String outputPath) {
        log.info("Начинаем генерацию Приложения №{} к договору №{}", application.getNumber(), contract.getNumber());

        try {
            Map<String, Object> data = new HashMap<>();

            data.put("contractNumber", contract.getNumber());
            data.put("contractDate", contract.getStartDate().format(DATE_FORMATTER));

            data.put("applicationNumber", application.getNumber());
            data.put("applicationDate", application.getDate().format(DATE_FORMATTER));
            data.put("campaignName", application.getCampaignName());
            data.put("brandName", application.getBrandName());
            data.put("serviceName", application.getServiceName());
            data.put("description", application.getDescription());
            data.put("eventLocation", application.getEventLocation());

            // Используем простой формат дат для периода услуг
            data.put("serviceStartDate", application.getServiceStartDate().format(DATE_FORMATTER_SIMPLE));
            data.put("serviceEndDate", application.getServiceEndDate().format(DATE_FORMATTER_SIMPLE));

            // Округление и форматирование суммы
            long roundedAmount = application.getAmount().setScale(0, RoundingMode.CEILING).longValue();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("ru"));
            symbols.setGroupingSeparator(' ');
            DecimalFormat df = new DecimalFormat("#,##0", symbols);
            String formattedAmount = df.format(roundedAmount);

            data.put("serviceAmount", formattedAmount);
            data.put("serviceAmountInWords", application.getAmountInWords()); // Вызовет обновленный конвертер

            data.put("name", personal.getName());
            data.put("shortName", personal.getShortName());
            data.put("passport", personal.getPassport());
            data.put("passportIssuedBy", personal.getPassportIssuedBy());
            data.put("passportIssueDate", personal.getPassportIssueDate());
            data.put("address", personal.getAddress());
            data.put("inn", personal.getInn());
            data.put("npdRegistrationCity", personal.getNpdRegistrationCity());
            data.put("phone", formatPhone(personal.getPhone())); // Форматируем телефон

            data.put("bankName", personal.getBankName());
            data.put("bankAccount", personal.getBankAccount());
            data.put("correspondentAccount", personal.getCorrespondentAccount());
            data.put("bik", personal.getBankIdentifierCode());

            Manager manager = application.getManager();
            if (manager != null) {
                data.put("managerName", manager.getName());
                data.put("managerPhone", formatPhone(manager.getPhone())); // Форматируем телефон менеджера
            } else {
                data.put("managerName", "Мамедова Лариса Аликовна");
                data.put("managerPhone", "8 (495) 984 79 74");
            }

            InputStream templateStream = DocumentGenerator.class.getClassLoader()
                    .getResourceAsStream("template_application.docx");
            if (templateStream == null) {
                log.error("❌ Шаблон 'template_application.docx' не найден в папке resources!");
                return;
            }

            XWPFTemplate template = XWPFTemplate.compile(templateStream).render(data);

            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                template.write(out);
                template.close();
            }

            log.info("✅ Приложение успешно сгенерировано: {}", outputPath);

        } catch (IOException e) {
            log.error("❌ Ошибка генерации приложения", e);
        }
    }
}