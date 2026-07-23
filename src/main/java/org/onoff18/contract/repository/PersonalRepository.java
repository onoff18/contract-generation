package org.onoff18.contract.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.onoff18.contract.model.Personal;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PersonalRepository {

    // ObjectMapper — это главный инструмент Jackson для работы с JSON
    private final ObjectMapper objectMapper;
    private final String filePath = "personal.json"; // Имя файла, куда будем сохранять

    public PersonalRepository() {
        this.objectMapper = new ObjectMapper();
        // Настройка для красивого форматирования JSON (с отступами)
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Метод для сохранения списка самозанятых в файл
    public void save(List<Personal> personals) throws IOException {
        objectMapper.writeValue(new File(filePath), personals);
        System.out.println("Данные успешно сохранены в " + filePath);
    }

    // Метод для загрузки списка самозанятых из файла
    public List<Personal> load() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Файл не найден, возвращаем пустой список.");
            return List.of(); // Возвращаем пустой список
        }
        return objectMapper.readValue(file, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Personal.class));
    }
}