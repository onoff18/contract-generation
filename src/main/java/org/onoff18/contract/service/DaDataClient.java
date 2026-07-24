package org.onoff18.contract.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class DaDataClient {

    private static final Logger log = LoggerFactory.getLogger(DaDataClient.class);


    private static final String API_KEY = "d6a2bf6896233e382cdc65037e9b4fe65b21bfda";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public DaDataClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Получает реквизиты банка по его БИК
     */
    public Map<String, String> getBankByBik(String bik) {
        Map<String, String> result = new HashMap<>();

        try {
            // 1. Формируем URL для запроса к DaData
            String url = "https://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/bank";

            // 2. Создаем тело запроса (JSON)
            String body = "{\"query\":\"" + bik + "\"}";

            // 3. Создаем HTTP запрос
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Token " + API_KEY) // Авторизация
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            // 4. Отправляем запрос и получаем ответ
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                log.info("Успешно получили данные от DaData для БИК: {}", bik);
                // 5. Парсим JSON ответ
                return parseBankResponse(response.body());
            } else {
                log.error("Ошибка API DaData. Код ответа: {}", response.statusCode());
            }
        } catch (Exception e) {
            log.error("Не удалось связаться с DaData", e);
        }

        return result;
    }

    // Улучшенный и безопасный метод для разбора JSON от DaData
    private Map<String, String> parseBankResponse(String json) {
        Map<String, String> bankData = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode suggestions = root.path("suggestions");

            // Исправление 1: используем isEmpty() вместо size() > 0
            if (suggestions.isArray() && !suggestions.isEmpty()) {
                JsonNode firstSuggestion = suggestions.get(0);
                JsonNode data = firstSuggestion.path("data");

                // 1. Имя банка: пробуем несколько вариантов
                String bankName = data.path("name").path("full").textValue();
                if (bankName == null || bankName.isEmpty()) {
                    bankName = data.path("name").path("payment").textValue();
                }
                if (bankName == null || bankName.isEmpty()) {
                    bankName = firstSuggestion.path("value").textValue();
                }
                if (bankName == null || bankName.isEmpty()) {
                    bankName = "Неизвестный банк";
                }

                // 2. Корреспондентский счет
                String corrAccount = data.path("correspondent_account").textValue();
                if (corrAccount == null) {
                    corrAccount = "";
                }

                // 3. Город (адрес)
                String city = data.path("city").textValue();
                if (city == null || city.isEmpty()) {
                    city = data.path("address").path("value").textValue();
                }
                if (city == null) {
                    city = "";
                }

                bankData.put("name", bankName);
                bankData.put("correspondentAccount", corrAccount);
                bankData.put("city", city);

                log.info("Успешно распарсено: Банк={}, К/С={}, Город={}", bankName, corrAccount, city);
            } else {
                log.warn("DaData вернула пустой список suggestions для БИК");
            }
        } catch (Exception e) {
            log.error("Ошибка при парсинге ответа DaData. Полученный JSON: {}", json, e);
        }
        return bankData;
    }
}