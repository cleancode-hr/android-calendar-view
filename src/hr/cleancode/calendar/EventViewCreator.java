package hr.cleancode.calendar;

import hr.cleancode.calendar.CalendarDisplayView.CalendarMode;
import android.view.View;
import android.view.ViewGroup;

public interface EventViewCreator {
	View createView(Object item, CalendarMode mode, ViewGroup parent);
}
