package hr.cleancode.calendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CalendarDisplayView extends LinearLayout {
	public CalendarDisplayView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setOrientation(VERTICAL);
	}

	public CalendarDisplayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
	}

	public CalendarDisplayView(Context context) {
		super(context);
		setOrientation(VERTICAL);
	}

	enum CalendarMode {
		Day,
		Week,
		Month
	}
	
	public static class CalendarItem implements Comparable<CalendarItem>{
		private LocalDateTime eventStart;
		private LocalDateTime eventEnd;
		private Object data;
		
		public CalendarItem(LocalDateTime eventStart,
				LocalDateTime eventEnd, Object data) {
			super();
			this.eventStart = eventStart;
			this.eventEnd = eventEnd;
			this.data = data;
		}

		public LocalDateTime getEventStart() {
			return eventStart;
		}

		public LocalDateTime getEventEnd() {
			return eventEnd;
		}

		public Object getData() {
			return data;
		}

		@Override
		public int compareTo(CalendarItem another) {
			if (another == null) {
				return -1;
			}
			return eventStart.compareTo(another.getEventStart());
		}
	}
	
	public interface EventViewCreator {
		View createView(Object item, CalendarMode mode, ViewGroup parent);
	}
	
	public LocalDate getWeekMonday(LocalDate date) {
		return date.minusDays(date.getDayOfWeek());
	}
	public LocalDate getMonthStart(LocalDate date) {
		return date.minusDays(date.getDayOfMonth() - 1);
	}

	private CalendarMode mode = CalendarMode.Month;
	private LocalDate selectedDate = LocalDate.now();
	private List<CalendarItem> items = new ArrayList<CalendarDisplayView.CalendarItem>();
	private int daysToShow = 7;
	private int gridWidth = 600;
	
	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
		render();
	}

	public void setDaysToShow(int daysToShow) {
		this.daysToShow = daysToShow;
		render();
	}
	public void addItems(List<CalendarItem> itemsToAdd) {
		items.addAll(itemsToAdd);
		Collections.sort(items);
		render();
	}
	
	private EventViewCreator eventViewCreator = new EventViewCreator() {
		@Override
		public View createView(
				Object item, 
				CalendarMode mode,
				ViewGroup parent) {
			TextView result = new TextView(getContext());
			if (mode == CalendarMode.Month) {
				result.setTextSize(8);
			}
			if (item == null) {
				result.setText("");
			}
			else {
				result.setText(item.toString());
			}
			return result;
		}
	};
	
	public CalendarMode getMode() {
		return mode;
	}

	public void setMode(CalendarMode mode) {
		this.mode = mode;
		render();
	}

	public LocalDate getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(LocalDate selectedDate) {
		this.selectedDate = selectedDate;
		render();
	}

	public void setEventViewCreator(EventViewCreator eventViewCreator) {
		this.eventViewCreator = eventViewCreator;
		render();
	}
	
	public void render() {
		removeAllViews();
		switch (this.mode) {
			case Day:
				addView(createDayView(this.selectedDate), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				break;
			case Week:
				addView(createWeekView(this.selectedDate), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				break;
			case Month:
				addView(createMonthView(this.selectedDate), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				break;
		}
	}
	private List<CalendarItem> getItemsForPeriod(LocalDateTime from, LocalDateTime to) {
		List<CalendarItem> result = new ArrayList<CalendarDisplayView.CalendarItem>();
		for (CalendarItem item : this.items) {
			if (item.getEventStart().compareTo(from) >= 0 && item.getEventStart().compareTo(to) < 0) {
				result.add(item);
			}
		}
		return result;
	}
	
	private View createDayHeaderView(LocalDate date) {
		TextView dateInfo = new TextView(getContext());
		dateInfo.setText(date.toString());
		return dateInfo;
	}
	
	private View createDayView(LocalDate date) {
		LinearLayout result = new LinearLayout(getContext());
		result.setOrientation(VERTICAL);
		View dateInfo = createDayHeaderView(date);
		LinearLayout.LayoutParams dayHeaderParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		dayHeaderParams.gravity = Gravity.CENTER_HORIZONTAL;
		result.addView(dateInfo, dayHeaderParams);
		ViewGroup scrollContent = null;
		scrollContent = new ScrollView(getContext());
		
		LinearLayout hours = new LinearLayout(getContext());
		hours.setOrientation(LinearLayout.VERTICAL);
		boolean odd = false;
		for (CalendarItem item : getItemsForPeriod(date.toLocalDateTime(LocalTime.MIDNIGHT), date.plusDays(1).toLocalDateTime(LocalTime.MIDNIGHT))) {
			LinearLayout itemView = new LinearLayout(getContext());
			itemView.addView(this.eventViewCreator.createView(item.data, this.mode, itemView), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			itemView.setBackgroundColor(odd ? Color.WHITE : Color.LTGRAY);
			odd = !odd;
			hours.addView(itemView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		}
		scrollContent.addView(hours, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams scrollerParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
		result.addView(scrollContent, scrollerParams);
		return result;
	}
	
	private View createWeekView(LocalDate date) {
		HorizontalScrollView result = new HorizontalScrollView(getContext());
		LinearLayout daysView = new LinearLayout(getContext());
		daysView.setOrientation(HORIZONTAL);
		LocalDate startDate = getWeekMonday(date);
		for (int i = 0; i < daysToShow; i ++) {
			View dayView = createDayView(startDate.plusDays(i));
			LinearLayout.LayoutParams dayViewParams = new LayoutParams(Math.round(gridWidth / daysToShow), LinearLayout.LayoutParams.WRAP_CONTENT);
			daysView.addView(dayView, dayViewParams);
		}
		result.addView(daysView, new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		return result;
	}
	private View createDayHeader(String day) {
		TextView result = new TextView(getContext());
		result.setText(day);
		return result;
	}
	private List<String> daysOfWeek = new ArrayList<>();
	{
		daysOfWeek.add("PON");
		daysOfWeek.add("UTO");
		daysOfWeek.add("SRI");
		daysOfWeek.add("ČET");
		daysOfWeek.add("PET");
		daysOfWeek.add("SUB");
		daysOfWeek.add("NED");
	}
	private View createMonthView(LocalDate date) {
		ScrollView result = new ScrollView(getContext());
		HorizontalScrollView horizontal = new HorizontalScrollView(getContext());
		GridLayout gridLayout = new GridLayout(getContext());
		gridLayout.setColumnCount(daysToShow);
		for (int i = 0 ; i < daysToShow; i++) {
			GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(0, 1, GridLayout.CENTER), GridLayout.spec(i, 1, GridLayout.CENTER));
			params.width = Math.round(gridWidth / daysToShow);
			params.setGravity(Gravity.CENTER_HORIZONTAL);
			gridLayout.addView(createDayHeader(daysOfWeek.get(i)), params);
		}
		LocalDate currentDate = getMonthStart(date);
		int month = currentDate.getMonthOfYear();
		do {
			LinearLayout dayOfMonthView = new LinearLayout(getContext());
			dayOfMonthView.setOrientation(VERTICAL);
			dayOfMonthView.setBackgroundResource(R.drawable.border_cell);
			TextView dayNum = new TextView(getContext());
			dayNum.setText(String.valueOf(currentDate.getDayOfMonth()) + ".");
			LinearLayout.LayoutParams dayOfMonthParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			dayOfMonthParams.gravity = Gravity.RIGHT;
			dayOfMonthView.addView(dayNum, dayOfMonthParams);
			LinearLayout dayEventsView = new LinearLayout(getContext());
			dayEventsView.setOrientation(VERTICAL);
			
			for (CalendarItem item : getItemsForPeriod(currentDate.toLocalDateTime(LocalTime.MIDNIGHT), currentDate.plusDays(1).toLocalDateTime(LocalTime.MIDNIGHT))) {
				LinearLayout itemView = new LinearLayout(getContext());
				itemView.addView(this.eventViewCreator.createView(item.data, this.mode, itemView), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				dayEventsView.addView(itemView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			}
			
			dayOfMonthView.addView(dayEventsView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			int col = currentDate.getDayOfWeek() - 1;
			int row = Math.round((currentDate.getDayOfMonth() + getMonthStart(date).getDayOfWeek() - 1 - 1) / 7) + 1;
			GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(row, 1, GridLayout.CENTER), GridLayout.spec(col, 1, GridLayout.CENTER));
			params.width = Math.round(gridWidth / daysToShow);
			params.height = 200;
			params.leftMargin = 1;
			params.topMargin = 1;
			params.setGravity(Gravity.CENTER_HORIZONTAL);
			gridLayout.addView(dayOfMonthView, params);
			currentDate = currentDate.plusDays(1);
		}
		while (currentDate.getMonthOfYear() == month);
		result.addView(horizontal, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		horizontal.addView(gridLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		return result;
	}
}
