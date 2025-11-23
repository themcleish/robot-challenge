# Truck Robot Code Challenge – REST API

This is a small Spring Boot application that simulates a robot moving on a table. 
The API lets you place the robot, move it, turn it, and get its current state. 
I've also added an endpoint for running a list of commands in sequence so you can fire off a
set quite similar to the CLI example.

## Features
- Endpoints for PLACE, MOVE, LEFT, RIGHT, REPORT, RESET
- Batch command execution
- Domain exceptions for invalid actions
- Tests for the service layer and the controller

## Configuration

Table size defaulted to 5 but can be configured in `application.properties`:
```
robot.table.default.width=5
robot.table.default.height=5
```

## API Examples

### POST /robot/place
```
{
  "x": 1,
  "y": 2,
  "direction": "NORTH"
}
```

### POST /robot/move
```
POST /robot/move
```

### POST /robot/left
```
POST /robot/left
```

### POST /robot/right
```
POST /robot/right
```

### GET /robot/report
```
GET /robot/report
```
Example response:
```
{
    "position": {
        "x": 4,
        "y": 4
    },
    "direction": "EAST",
    "isPlaced": true
}
```

### POST /robot/commands
Takes a list of commands. Invalid ones are ignored.
```
[
  { "type": "PLACE", "x": 1, "y": 1, "direction": "NORTH" },
  { "type": "MOVE" },
  { "type": "RIGHT" },
  { "type": "MOVE" }
]
```

### POST /robot/reset
Resets the robot back to an unplaced state.
