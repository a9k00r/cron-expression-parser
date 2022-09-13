package services;

import com.ankur.cronparser.enums.CronFieldTypeEnum;
import com.ankur.cronparser.exceptions.CronInvalidFieldException;
import com.ankur.cronparser.model.CronField;
import com.ankur.cronparser.services.CronExpressionParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CronExpressionParserServiceTest {

    private CronExpressionParserService cronExpressionParserService;
    private final String cronExpression = "*/15 */4 1,15 * 1-5 /usr/bin/find";

    @BeforeEach
    public void init(){
        cronExpressionParserService = new CronExpressionParserService(cronExpression);
    }

    @Test
    public void shouldParseCronExpression() {
        String expectedResultString = "minute        0 15 30 45\n" +
                "hour          0 4 8 12 16 20\n" +
                "day of month  1 15\n" +
                "month         1 2 3 4 5 6 7 8 9 10 11 12\n" +
                "day of week   1 2 3 4 5\n" +
                "command       /usr/bin/find\n";

        CronExpressionParserService cronExpressionParserService = new CronExpressionParserService(cronExpression);
        assertEquals(expectedResultString, cronExpressionParserService.toString());
    }

    @Test
    public void shouldTrowCronInvalidFieldException() {
        String cronExpression = "*/15 */4 1,15 * 1-5";
        assertThrows(CronInvalidFieldException.class, () -> new CronExpressionParserService(cronExpression));

    }

    @Test
    public void shouldHaveCorrectRange() throws CronInvalidFieldException {
        CronField cronField = new CronField("1-5", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 2 3 4 5", cronField.toString());
        cronExpressionParserService.parse(cronField);
        cronField = new CronField("1-1", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1", cronField.toString());
        cronExpressionParserService.parse(cronField);
        cronField = new CronField("1-2", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 2", cronField.toString());
        cronExpressionParserService.parse(cronField);
        cronField = new CronField("1-15", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15", cronField.toString());


        cronField = new CronField("0-1", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0 1", cronField.toString());
        cronExpressionParserService.parse(cronField);
        cronField = new CronField("0-3", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0 1 2 3", cronField.toString());
        cronField = new CronField("0", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0", cronField.toString());
        cronExpressionParserService.parse(cronField);
        cronField = new CronField("23", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("23", cronField.toString());
    }

    @Test
    public void shouldHaveIncorrectRange() {
        canParse("0-5", CronFieldTypeEnum.DAY_OF_MONTH, "outside valid range");
        canParse("0-5-6", CronFieldTypeEnum.DAY_OF_MONTH, "Invalid number");
        canParse("1-32", CronFieldTypeEnum.DAY_OF_MONTH, "outside valid range");
        canParse("1-0", CronFieldTypeEnum.DAY_OF_MONTH, "ends before it starts");
    }

    @Test
    public void shouldCorrectConstantValues() throws CronInvalidFieldException {
        CronField cronField = new CronField("1", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1", cronField.toString());
        cronExpressionParserService.parse(cronField);
        cronField = new CronField("1,2,3,4,5", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 2 3 4 5", cronField.toString());
        cronExpressionParserService.parse(cronField);
        cronField = new CronField("1,1,1", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1", cronField.toString());
        cronExpressionParserService.parse(cronField);
        cronField = new CronField("1,2", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 2", cronField.toString());
        cronExpressionParserService.parse(cronField);
        cronField = new CronField("2,1,3,5,6,7,4", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 2 3 4 5 6 7", cronField.toString());


        cronField = new CronField("0,1", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0 1", cronField.toString());
        cronField = new CronField("0,1,2,3", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0 1 2 3", cronField.toString());
        cronField = new CronField("0", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0", cronField.toString());
        cronField = new CronField("0,0", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0", cronField.toString());
        cronField = new CronField("23,0", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0 23", cronField.toString());
    }

    @Test
    public void shouldBeIncorrectConstantValues() {
        canParse("0,5", CronFieldTypeEnum.DAY_OF_MONTH, "outside valid range");
        canParse("1,32", CronFieldTypeEnum.DAY_OF_MONTH, "outside valid range");
    }

    @Test
    public void ShouldBeCorrectIntervals() throws CronInvalidFieldException {
        CronField cronField = new CronField("*/10", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 11 21 31", cronField.toString());
        cronField = new CronField("*/20", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 21", cronField.toString());
        cronField = new CronField("*/30", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 31", cronField.toString());
        cronField = new CronField("*/40", CronFieldTypeEnum.DAY_OF_MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1", cronField.toString());


        cronField = new CronField("*/10", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0 10 20", cronField.toString());
        cronField = new CronField("*/15", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0 15", cronField.toString());
        cronField = new CronField("*/20", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0 20", cronField.toString());
        cronField = new CronField("*/23", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0 23", cronField.toString());
        cronField = new CronField("*/24", CronFieldTypeEnum.HOURS);
        cronExpressionParserService.parse(cronField);
        assertEquals("0", cronField.toString());

        cronField = new CronField("*", CronFieldTypeEnum.DAY_OF_WEEK);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 2 3 4 5 6 7", cronField.toString());

        cronField = new CronField("*", CronFieldTypeEnum.MONTH);
        cronExpressionParserService.parse(cronField);
        assertEquals("1 2 3 4 5 6 7 8 9 10 11 12", cronField.toString());
    }

    @Test
    public void ShouldBeIncorrectIntervals() {
        canParse("*/0", CronFieldTypeEnum.DAY_OF_MONTH, "interval is 0");
        canParse("0/0", CronFieldTypeEnum.DAY_OF_MONTH, "Invalid number '0/0'");
        canParse("*/10/10", CronFieldTypeEnum.DAY_OF_MONTH, "has extra intervals");
        canParse("*/A", CronFieldTypeEnum.DAY_OF_MONTH, "Invalid number 'A'");
        canParse("0/15", CronFieldTypeEnum.DAY_OF_MONTH, "Invalid number '0/15'");
        canParse("A/A", CronFieldTypeEnum.DAY_OF_MONTH, "Invalid number 'A/A'");
    }

    private void canParse(String incomingText, CronFieldTypeEnum fieldType, String msg) {
        try {
            CronField cronField = new CronField(incomingText, fieldType);
            CronField field = cronExpressionParserService.parse(cronField);
            assertFalse(field.getSortedValues().isEmpty());
            fail(incomingText + " should not be a valid " + fieldType);
        } catch (CronInvalidFieldException e) {
            assertTrue(e.getMessage().contains(msg));
            assertTrue(e.getMessage().contains(fieldType.toString()));
        }
    }

}
