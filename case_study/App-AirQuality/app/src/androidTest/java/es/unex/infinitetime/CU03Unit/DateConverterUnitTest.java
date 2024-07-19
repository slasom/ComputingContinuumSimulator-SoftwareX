package es.unex.infinitetime.CU03Unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.util.Date;

import es.unex.infinitetime.persistence.DateConverter;

public class DateConverterUnitTest {

    @Test
    public void toDate_isCorrect() {
        Long value = new Long(1586304000);
        Long value2 = new Long(1586389000);
        Date date = new Date(value);

        assertEquals(date, DateConverter.toDate(value));
        assertNotEquals(date, DateConverter.toDate(value2));
    }

    @Test
    public void toTimestamp_isCorrect() {
        Long value = new Long(1586304000);
        Long value2 = new Long(1586389000);
        Date date = new Date(value);

        assertEquals(value, DateConverter.toTimestamp(date));
        assertNotEquals(value2, DateConverter.toTimestamp(date));
    }
}
