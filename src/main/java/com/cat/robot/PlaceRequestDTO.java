package com.cat.robot;

import jakarta.validation.constraints.NotNull;

public record PlaceRequestDTO(@NotNull Integer x, @NotNull Integer y, @NotNull Direction direction) {}
