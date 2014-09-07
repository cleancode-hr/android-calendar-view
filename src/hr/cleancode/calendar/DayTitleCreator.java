package hr.cleancode.calendar;

import org.joda.time.LocalDate;

import hr.cleancode.calendar.CalendarDisplayView.CalendarMode;

public interface DayTitleCreator {
	public String getDayTitle(CalendarMode mode, LocalDate date);
}
