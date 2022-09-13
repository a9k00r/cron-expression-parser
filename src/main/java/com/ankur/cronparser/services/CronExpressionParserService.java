package com.ankur.cronparser.services;

import com.ankur.cronparser.exceptions.CronInvalidFieldException;
import com.ankur.cronparser.model.CronField;
import com.ankur.cronparser.enums.CronFieldTypeEnum;

import static com.ankur.cronparser.enums.CronFieldTypeEnum.*;
import static java.lang.String.format;

public class CronExpressionParserService {

    private final CronField minutes;
    private final CronField hours;
    private final CronField dayOfMonth;
    private final CronField month;
    private final CronField dayOfWeek;
    private final String commandString;

    public CronExpressionParserService(String cronExpressionString) throws CronInvalidFieldException {
        String[] cronTokens = cronExpressionString.split("\\s+");

        if (cronTokens.length != 6) {
            throw new CronInvalidFieldException("Invalid Cron Expression String format: " + cronExpressionString + ", Expected format [minute] [hour] [day of month] [day of week] [command]");
        }

        this.minutes = parse(new CronField(cronTokens[0], MINUTES));
        this.hours = parse(new CronField(cronTokens[1], HOURS));
        this.dayOfMonth = parse(new CronField(cronTokens[2], DAY_OF_MONTH));
        this.month = parse(new CronField(cronTokens[3], MONTH));
        this.dayOfWeek = parse(new CronField(cronTokens[4], DAY_OF_WEEK));
        this.commandString = cronTokens[5];

    }

    public CronField parse(CronField cronField) {
        parseConstantCronString(cronField);
        parseRangeCronString(cronField);
        parseCronIntervalsString(cronField);

        if (cronField.getSortedValues().isEmpty()) {
            cronField.getSortedValues().add(parseNumber(cronField.getToken(), cronField.getType()));
        }
        return cronField;
    }

    private void parseCronIntervalsString(CronField cronField) throws CronInvalidFieldException {
        if (cronField.getToken().startsWith("*")) {
            int interval = 1;
            String[] intervals = cronField.getToken().split("/");

            if (intervals.length > 2) {
                throw new CronInvalidFieldException("Number: " + cronField + " for " + cronField.getType() + "has extra intervals");
            }

            if (intervals.length == 2) {
                interval = parseNumber(intervals[1], cronField.getType());
            }
            populateValues(cronField, cronField.getType().min, cronField.getType().max, interval);
        }
    }

    private void parseRangeCronString(CronField cronField) throws CronInvalidFieldException {
        String[] range = cronField.getToken().split("-");
        if (range.length == 2) {
            int start = parseNumber(range[0], cronField.getType());
            int end = parseNumber(range[1], cronField.getType());
            populateValues(cronField, start, end, 1);
        }
    }

    private void parseConstantCronString(CronField cronField) throws CronInvalidFieldException {
        String[] Dates = cronField.getToken().split(",");
        if (Dates.length > 1) {
            for (String date : Dates) {
                int val = parseNumber(date, cronField.getType());
                populateValues(cronField, val, val, 1);
            }
        }
    }

    private Integer parseNumber(String number, CronFieldTypeEnum type) throws CronInvalidFieldException {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            throw new CronInvalidFieldException("Invalid number '" + number + "' in field " + type.name() + ": " + nfe.getMessage());
        }
    }

    private void populateValues(CronField cronField, int start, int end, int increment) throws CronInvalidFieldException {
        if (increment == 0) {
            throw new CronInvalidFieldException("Number " + cronField.getToken() + " for " + cronField.getType() + " interval is 0");
        }
        if (end < start) {
            throw new CronInvalidFieldException("Number " + cronField.getToken() + " for " + cronField.getType() + " ends before it starts");
        }
        if (start < cronField.getType().min || end > cronField.getType().max) {
            throw new CronInvalidFieldException("Number " + cronField + " for " + cronField.getType() + " is outside valid range (" + cronField.getType().min + "-" + cronField.getType().max + ")");
        }
        for (int i = start; i <= end; i += increment) {
            cronField.getSortedValues().add(i);
        }
    }


    public String toString() {
        return format("%-14s%s\n", "minute", minutes.toString()) +
                format("%-14s%s\n", "hour", hours.toString()) +
                format("%-14s%s\n", "day of month", dayOfMonth.toString()) +
                format("%-14s%s\n", "month", month.toString()) +
                format("%-14s%s\n", "day of week", dayOfWeek.toString()) +
                format("%-14s%s\n", "command", commandString);
    }

}
