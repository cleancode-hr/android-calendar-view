package hr.cleancode.calendar;

import hr.cleancode.calendar.CalendarDisplayView.CalendarMode;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class MainActivity extends Activity {
	private CalendarDisplayView calendarView;
	private void moveCalendar(int amount) {
		if (calendarView.getMode() == CalendarMode.Day) {
			calendarView.setSelectedDate(calendarView.getSelectedDate().plusDays(amount));
		}
		else if (calendarView.getMode() == CalendarMode.Week) {
			calendarView.setSelectedDate(calendarView.getSelectedDate().plusWeeks(amount));
		} 
		else if (calendarView.getMode() == CalendarMode.Month) {
			calendarView.setSelectedDate(calendarView.getSelectedDate().plusMonths(amount));
		}
	}
	
	private static int[] getRealDeviceSizeInPixels(Activity p_activity)	{
	    WindowManager windowManager = p_activity.getWindowManager();
	    Display display = windowManager.getDefaultDisplay();
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    display.getMetrics(displayMetrics);

	    // since SDK_INT = 1;
	    int mWidthPixels = displayMetrics.widthPixels;
	    int mHeightPixels = displayMetrics.heightPixels;

	    // includes window decorations (statusbar bar/menu bar)
	    if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
	        try
	        {
	            mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
	            mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
	        }
	        catch (Exception ignored)
	        {
	        }
	    }

	    // includes window decorations (statusbar bar/menu bar)
	    if (Build.VERSION.SDK_INT >= 17) {
	        try
	        {
	            Point realSize = new Point();
	            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
	            mWidthPixels = realSize.x;
	            mHeightPixels = realSize.y;
	        }
	        catch (Exception ignored)
	        {
	        }
	    }
	    return new int[] {mWidthPixels, mHeightPixels};
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Spinner modeSelector = (Spinner) findViewById(R.id.spinner1);
		modeSelector.setSelection(2);
		modeSelector.setOnItemSelectedListener(new OnItemSelectedListener() {
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
		calendarView.setOnClickListener(new CalendarViewOnClickListener() {
			
			@Override
			public void onClick(CalendarMode mode, LocalDate selectedDate,
					Object selectedItem) {
				if (selectedItem != null){
					Toast.makeText(MainActivity.this, "Selected item: " + selectedItem.toString(), Toast.LENGTH_SHORT).show();
				}
				else {
					calendarView.setSelectedDate(selectedDate);
				}
				
			}

			@Override
			public void onLongClick(CalendarMode mode, LocalDate selectedDate,
					Object selectedItem) {
				if (mode == CalendarMode.Month) {
					calendarView.setModeAndDate(CalendarMode.Week, selectedDate);
					modeSelector.setSelection(1);
				}
				else if (selectedItem != null){
					Toast.makeText(MainActivity.this, "Selected item: " + selectedItem.toString(), Toast.LENGTH_SHORT).show();
				}
				else if (mode == CalendarMode.Week) {
					calendarView.setModeAndDate(CalendarMode.Day, selectedDate);
					modeSelector.setSelection(0);
				}
			}
		});
		List<String> dayOfWeekNames = new ArrayList<>();
		dayOfWeekNames.add("Pon");
		dayOfWeekNames.add("Uto");
		dayOfWeekNames.add("Sri");
		dayOfWeekNames.add("Čet");
		dayOfWeekNames.add("Pet");
		dayOfWeekNames.add("Sub");
		dayOfWeekNames.add("Ned");
		calendarView.setDayOfWeekNames(dayOfWeekNames);
		List<CalendarItem> testItems = new ArrayList<>();
		for (int i = - 200; i < 200; i++) {
			testItems.add(new CalendarItem(LocalDateTime.now().plusHours(i * 4), LocalDateTime.now().plusHours(i * 4 + 1), "Event " + String.valueOf(i)));
		}
		calendarView.addItems(testItems);
		int[] size = getRealDeviceSizeInPixels(this);
		calendarView.setGridWidth(size[0] - 20);
	}

	
}
