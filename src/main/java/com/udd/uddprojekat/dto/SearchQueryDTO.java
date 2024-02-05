package com.udd.uddprojekat.dto;

import java.util.List;

public record SearchQueryDTO(List<String> keywords, String phrase, boolean phraseQuery) {
}
