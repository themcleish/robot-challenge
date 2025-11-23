package com.cat.robot.controller;

import com.cat.robot.dto.CommandDTO;
import com.cat.robot.dto.PlaceRequestDTO;
import com.cat.robot.model.Position;
import com.cat.robot.model.RobotState;
import com.cat.robot.service.RobotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/robot")
public class RobotController {

    private final RobotService robotService;

    public RobotController(RobotService robotService) {
        this.robotService = robotService;
    }

    @PostMapping("/place")
    public ResponseEntity<RobotState> place(@Valid @RequestBody PlaceRequestDTO request) {
        Position position = new Position(request.x(), request.y());
        boolean placed = robotService.place(position, request.direction());

        if (!placed) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        RobotState state = robotService.getReport();
        return ResponseEntity.ok(state);
    }

    @PostMapping("/move")
    public ResponseEntity<RobotState> move() {
        boolean moved = robotService.move();

        if (!moved) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        RobotState state = robotService.getReport();
        return ResponseEntity.ok(state);
    }

    @PostMapping("/left")
    public ResponseEntity<RobotState> left() {
        boolean turned = robotService.turnLeft();

        if (!turned) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        RobotState state = robotService.getReport();
        return ResponseEntity.ok(state);
    }

    @PostMapping("/right")
    public ResponseEntity<RobotState> right() {
        boolean turned = robotService.turnRight();

        if (!turned) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        RobotState state = robotService.getReport();
        return ResponseEntity.ok(state);
    }

    @GetMapping("/report")
    public ResponseEntity<RobotState> report() {
        RobotState state = robotService.getReport();
        return ResponseEntity.ok(state);
    }


    @PostMapping("/commands")
    public ResponseEntity<RobotState> executeCommands(@RequestBody List<CommandDTO> commands) {
        RobotState finalState = robotService.executeCommands(commands);
        return ResponseEntity.ok(finalState);
    }
}