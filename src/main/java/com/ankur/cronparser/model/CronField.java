package com.ankur.cronparser.model;

import com.ankur.cronparser.enums.CronFieldTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class CronField {
    private final String token;
    private final CronFieldTypeEnum type;
    private final Set<Integer> sortedValues = new TreeSet<>();

    public String toString() {
        return sortedValues.stream().map(Object::toString).collect(Collectors.joining(" "));
    }
}
