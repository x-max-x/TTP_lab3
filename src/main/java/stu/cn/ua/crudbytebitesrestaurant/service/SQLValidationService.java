package stu.cn.ua.crudbytebitesrestaurant.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SQLValidationService {

    // Метод для валідації SQL-запитів
    public void validateQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query cannot be empty.");
        }

        String normalizedQuery = query.trim().replaceAll("\\s+", " ").toLowerCase();

        // Заборона DELETE без WHERE
        if (normalizedQuery.startsWith("delete") && !normalizedQuery.contains("where")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "DELETE without WHERE is not allowed.");
        }

        // Заборона TRUNCATE
        if (normalizedQuery.startsWith("truncate")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "TRUNCATE is not allowed.");
        }

        // Заборона DROP
        if (normalizedQuery.startsWith("drop")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "DROP operations are not allowed.");
        }

        // Перевірка на SQL injection
        if (normalizedQuery.contains("' or '") || normalizedQuery.contains("--")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Potential SQL injection detected.");
        }

        // Перевірка на неприпустимі логічні оператори
        if (normalizedQuery.contains(" or ") || normalizedQuery.contains(" and ")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Logical operators without proper context are not allowed.");
        }
    }
}
