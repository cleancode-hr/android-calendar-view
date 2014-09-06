package hr.cleancode.calendar;

import hr.cleancode.calendar.CalendarDisplayView.CalendarItem;
import hr.cleancode.calendar.CalendarDisplayView.CalendarMode;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	private CalendarDisplayView calendarView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout content = new LinearLayout(this);
		content.setOrientation(LinearLayout.VERTICAL);
		
		calendarView = new CalendarDisplayView(this);
		calendarView.setMode(CalendarMode.Month);
		
		List<CalendarItem> testItems = new ArrayList<>();
		for (int i = - 200; i < 200; i++) {
			testItems.add(new CalendarDisplayView.CalendarItem(LocalDateTime.now().plusHours(i * 4), LocalDateTime.now().plusHours(i * 4 + 1), "Event " + String.valueOf(i)));
		}
		calendarView.addItems(testItems);
		content.addView(calendarView);
		setContentView(content);
	}

	
}
