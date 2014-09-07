package hr.cleancode.calendar;

import org.joda.time.LocalDate;

import hr.cleancode.calendar.CalendarDisplayView.CalendarMode;

public interface CalendarViewOnClickListener {
	void onClick(CalendarMode mode, LocalDate selectedDate, Object selectedItem);
	void onLongClick(CalendarMode mode, LocalDate selectedDate, Object selectedItem);
}
