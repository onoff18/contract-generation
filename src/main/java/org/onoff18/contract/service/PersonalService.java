package org.onoff18.contract.service;

import org.onoff18.contract.model.Personal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Сервис для работы с данными самозанятых.
 * Отвечает за бизнес-логику: валидацию, обогащение данных, интеграции.
 */
public class PersonalService {

    private static final Logger log = LoggerFactory.getLogger(PersonalService.class);

    private final DaDataClient daDataClient;

    public PersonalService(DaDataClient daDataClient) {
        this.daDataClient = daDataClient;
    }

    /**
     * Обогащает объект Personal данными из DaData.
     * Если указан БИК банка, автоматически заполняет название банка и корреспондентский счет.
     */
    public Personal enrichWithBankData(Personal personal) {
        log.info("Начинаем обогащение данных для: {}", personal.getName());

        String bik = personal.getBankIdentifierCode();

        // Проверяем, есть ли БИК для запроса
        if (bik == null || bik.trim().isEmpty()) {
            log.warn("БИК не указан, пропускаем обогащение банковских данных");
            return personal;
        }

        // Запрашиваем данные из DaData
        Map<String, String> bankData = daDataClient.getBankByBik(bik);

        // Если данные получены, заполняем поля
        if (!bankData.isEmpty()) {
            String bankName = bankData.get("name");
            String correspondentAccount = bankData.get("correspondentAccount");

            // Заполняем только если поля пустые (не перезаписываем существующие данные)
            if (personal.getBankName() == null || personal.getBankName().isEmpty()) {
                personal.setBankName(bankName);
                log.info("Автоматически заполнено название банка: {}", bankName);
            }

            if (personal.getCorrespondentAccount() == null || personal.getCorrespondentAccount().isEmpty()) {
                personal.setCorrespondentAccount(correspondentAccount);
                log.info("Автоматически заполнен корр. счет: {}", correspondentAccount);
            }
        } else {
            log.warn("Не удалось получить данные банка для БИК: {}", bik);
        }

        return personal;
    }
}