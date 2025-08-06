package com.fastnfit.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/keep-alive")
public class KeepAliveController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public String keepAlive() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain; charset=UTF-8");
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return new ResponseEntity<>("Neon DB pinged successfully.", headers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("DB keep-alive error: " + e.getMessage());
            return new ResponseEntity<>("Error pinging Neon DB: " + e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/check_connection")
    public String checkConnection() {
        try {
            String sql = "SELECT * FROM keep_alive";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            StringBuilder sb = new StringBuilder("Rows:\n");
            for (Map<String, Object> row : rows) {
                sb.append("id=").append(row.get("id"))
                .append(", name=").append(row.get("name"))
                .append(", value=").append(row.get("value"))
                .append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            return "Error querying Neon DB: " + e.getMessage();
        }
    }
}
