// RoutineController.java
package com.fastnfit.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fastnfit.app.dto.RoutineDTO;
import com.fastnfit.app.service.RoutineService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/routines")
@CrossOrigin(origins = "*")
public class RoutineController {

    private final RoutineService routineService;

    @Autowired
    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoutineDTO>> getUserRoutines(@PathVariable Long userId) {
        List<RoutineDTO> routines = routineService.getUserRoutines(userId);
        return ResponseEntity.ok(routines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoutineDTO> getRoutineById(@PathVariable Long id) {
        RoutineDTO routine = routineService.getRoutineById(id);
        return ResponseEntity.ok(routine);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<RoutineDTO> createRoutine(
            @PathVariable Long userId,
            @Valid @RequestBody RoutineDTO routineDTO) {
        RoutineDTO createdRoutine = routineService.createRoutine(userId, routineDTO);
        return new ResponseEntity<>(createdRoutine, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoutineDTO> updateRoutine(
            @PathVariable Long id,
            @Valid @RequestBody RoutineDTO routineDTO) {
        RoutineDTO updatedRoutine = routineService.updateRoutine(id, routineDTO);
        return ResponseEntity.ok(updatedRoutine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id) {
        routineService.deleteRoutine(id);
        return ResponseEntity.noContent().build();
    }
}
