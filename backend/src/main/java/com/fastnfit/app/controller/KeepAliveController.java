package com.fastnfit.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keep-alive")
public class KeepAliveController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public String keepAlive() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "Neon DB pinged successfully.";
        } catch (Exception e) {
            return "Error pinging Neon DB: " + e.getMessage();
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
