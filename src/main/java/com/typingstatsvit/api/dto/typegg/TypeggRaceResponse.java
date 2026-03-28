package com.typingstatsvit.api.dto.typegg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TypeggRaceResponse(
        List<TypeggRace> races
) {
}

