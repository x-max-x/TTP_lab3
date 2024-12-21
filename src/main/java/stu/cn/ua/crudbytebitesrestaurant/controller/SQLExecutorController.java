package stu.cn.ua.crudbytebitesrestaurant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import stu.cn.ua.crudbytebitesrestaurant.service.SQLValidationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sql")
public class SQLExecutorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SQLValidationService sqlValidationService;

    @PostMapping("/execute")
    public List<Object> executeMultipleQueries(@RequestBody Map<String, Object> requestBody) {
        // Отримуємо SQL-запит з тіла запиту
        String query = (String) requestBody.get("query");

        System.out.println("Received query: " + query); // Логування запиту

        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query cannot be empty");
        }

        // ЗАХИЩЕНИЙ РЕЖИМ
        // Увімкніть цей блок для перевірки захисту
        sqlValidationService.validateQuery(query);

        // НЕЗАХИЩЕНИЙ РЕЖИМ
        // Закоментуйте захищений режим для перевірки SQL Injection (не виконується валідація, запити йдуть напряму).

        String[] queries = query.split(";");
        List<Object> results = new ArrayList<>();

        for (String singleQuery : queries) {
            singleQuery = singleQuery.trim();
            if (singleQuery.isEmpty()) {
                continue;
            }

            try {
                // Виконуємо SELECT-запити
                if (singleQuery.toLowerCase().startsWith("select")) {
                    List<Map<String, Object>> resultList = jdbcTemplate.queryForList(singleQuery);
                    results.add(resultList.isEmpty() ? "No data found." : resultList);
                } else {
                    // Виконуємо інші типи запитів (INSERT, UPDATE, DELETE тощо)
                    int rowsAffected = jdbcTemplate.update(singleQuery);
                    results.add(Map.of("query", singleQuery, "message", rowsAffected + " rows affected"));
                }
            } catch (DataAccessException e) {
                System.err.println("Error executing query: " + singleQuery);
                System.err.println("Database error: " + e.getMessage());
                results.add(Map.of("query", singleQuery, "error", e.getMessage()));
            }
        }

        return results;
    }
}

