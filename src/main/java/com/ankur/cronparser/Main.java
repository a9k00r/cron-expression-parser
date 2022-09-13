package com.ankur.cronparser;

import com.ankur.cronparser.exceptions.CronInvalidFieldException;
import com.ankur.cronparser.services.CronExpressionParserService;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (isValidCronExpression(args)) {
            try {
                CronExpressionParserService resultString = new CronExpressionParserService(args[0]);
                System.out.println(resultString);

            } catch (CronInvalidFieldException invalidCronExpression) {
                System.err.println(invalidCronExpression.getMessage());
            }
        }

    }

    private static boolean isValidCronExpression(String[] args) {
        if (args.length != 1) {
            System.err.println("invalid cron Expression format:" + Arrays.toString(args) + " Expected format: [minute] [hour] [day of month] [day of week] [commandString]");
            return false;
        }
        return true;
    }
}
