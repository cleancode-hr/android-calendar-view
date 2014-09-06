package hr.cleancode.calendar;

import hr.cleancode.calendar.CalendarDisplayView.CalendarItem;
import hr.cleancode.calendar.CalendarDisplayView.CalendarMode;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class MainActivity extends Activity {
	private CalendarDisplayView calendarView;
	private void moveCalendar(int amount) {
		if (calendarView.getMode() == CalendarMode.Day) {
			calendarView.setStartDate(calendarView.getStartDate().plusDays(amount));
		}
		else if (calendarView.getMode() == CalendarMode.Week) {
			calendarView.setStartDate(calendarView.getStartDate().plusWeeks(amount));
		} 
		else if (calendarView.getMode() == CalendarMode.Month) {
			calendarView.setStartDate(calendarView.getStartDate().plusMonths(amount));
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Spinner mode = (Spinner) findViewById(R.id.spinner1);
		mode.setSelection(2);
		mode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> p_parent, View p_view,
					int p_position, long p_id) {
				if (p_position == 0) {
					calendarView.setMode(CalendarMode.Day);
				}
				else if (p_position == 1) {
					calendarView.setMode(CalendarMode.Week);
				}
				else {
					calendarView.setMode(CalendarMode.Month);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> p_parent) {
			}
		});
		
		findViewById(R.id.bPrev).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View p_v) {
				moveCalendar(-1);
			}
		});
		
		findViewById(R.id.bNext).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View p_v) {
				moveCalendar(1);
			}
		});
		
		calendarView = (CalendarDisplayView) findViewById(R.id.calendarDisplayView1);
		calendarView.setMode(CalendarMode.Month);
		
		List<CalendarItem> testItems = new ArrayList<>();
		for (int i = - 200; i < 200; i++) {
			testItems.add(new CalendarDisplayView.CalendarItem(LocalDateTime.now().plusHours(i * 4), LocalDateTime.now().plusHours(i * 4 + 1), "Event " + String.valueOf(i)));
		}
		calendarView.addItems(testItems);
	}

	
}
