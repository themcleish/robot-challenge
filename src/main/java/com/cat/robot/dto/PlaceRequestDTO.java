package com.cat.robot.dto;

import com.cat.robot.model.Direction;
import jakarta.validation.constraints.NotNull;

public record PlaceRequestDTO(@NotNull Integer x, @NotNull Integer y, @NotNull Direction direction) {}
