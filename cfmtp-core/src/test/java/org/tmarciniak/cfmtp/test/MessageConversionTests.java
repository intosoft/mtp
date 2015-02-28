package org.tmarciniak.cfmtp.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tmarciniak.cfmtp.config.ApplicationConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
public class MessageConversionTests extends TestCase {

	@Inject
	ApplicationConfig applicationConfig;

	@Test
	public void testDateConversion() {
		String format = applicationConfig.getDateFormat();
		
		Date date = null;
		try {
			date = new SimpleDateFormat(format, Locale.ENGLISH)
					.parse("14-JAN-15 10:27:44");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 14);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.YEAR, 2015);
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 27);
		calendar.set(Calendar.SECOND, 44);
		calendar.set(Calendar.MILLISECOND, 0);

		Calendar calendarToCompare = Calendar.getInstance();
		calendarToCompare.setTime(date);

		assertEquals(calendar.getTimeInMillis(),
				calendarToCompare.getTimeInMillis());
	}
	
}
