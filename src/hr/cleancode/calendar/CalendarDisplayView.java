package hr.cleancode.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
	enum CalendarMode {
		Day,
		Week,
		Month
	}
	
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

	public LocalDate getWeekMonday(LocalDate date) {
		return date.minusDays(date.getDayOfWeek());
	}
	public LocalDate getMonthStart(LocalDate date) {
		return date.minusDays(date.getDayOfMonth() - 1);
	}

	private CalendarMode mode = CalendarMode.Month;
	private LocalDate selectedDate = LocalDate.now();
	private List<CalendarItem> items = new ArrayList<CalendarItem>();
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
	private List<String> dayOfWeekNames = new ArrayList<>();
	{
		dayOfWeekNames.add("Mon");
		dayOfWeekNames.add("Tue");
		dayOfWeekNames.add("Wed");
		dayOfWeekNames.add("Thu");
		dayOfWeekNames.add("Fri");
		dayOfWeekNames.add("Sat");
		dayOfWeekNames.add("Sun");
	}
	
	public void setDayOfWeekNames(List<String> dayOfWeekNames) {
		this.dayOfWeekNames = dayOfWeekNames;
		render();
	}
	
	private List<String> monthNames = new ArrayList<>();
	{
		monthNames.add("January");
		monthNames.add("February");
		monthNames.add("March");
		monthNames.add("April");
		monthNames.add("May");
		monthNames.add("Jun");
		monthNames.add("July");
		monthNames.add("August");
		monthNames.add("September");
		monthNames.add("October");
		monthNames.add("November");
		monthNames.add("December");
	}
	
	public void setMonthNames(List<String> monthNames) {
		this.monthNames = monthNames;
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
	
	private DayTitleCreator dayTitleCreator = new DayTitleCreator() {
		private SimpleDateFormat dayFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		private SimpleDateFormat weekFormatter = new SimpleDateFormat("dd.MM.", Locale.getDefault());
		@Override
		public String getDayTitle(CalendarMode mode, LocalDate date) {
			switch (mode) {
				case Day:
					return dayOfWeekNames.get(date.getDayOfWeek() - 1) + ", " + dayFormatter.format(date.toDate());
				case Week:
					return dayOfWeekNames.get(date.getDayOfWeek() - 1) + ", " + weekFormatter.format(date.toDate());
				case Month:
					return String.valueOf(date.getDayOfMonth() + ".");
				
			}
			return null;
		}
	};
	
	public void setDayTitleCreator(DayTitleCreator dayTitleCreator) {
		this.dayTitleCreator = dayTitleCreator;
	}

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
	
	public String getCurrentMonthName() {
		return monthNames.get(this.selectedDate.getMonthOfYear() - 1);
	}
	
	public void setSelectedDate(LocalDate selectedDate) {
		this.selectedDate = selectedDate;
		render();
	}

	public void setModeAndDate(CalendarMode mode, LocalDate selectedDate) {
		this.mode = mode;
		this.selectedDate = selectedDate;
		render();
	}
	
	public void setEventViewCreator(EventViewCreator eventViewCreator) {
		this.eventViewCreator = eventViewCreator;
		render();
	}
	
	private CalendarViewOnClickListener onClickListener;
	
	public CalendarViewOnClickListener getOnClickListener() {
		return onClickListener;
	}

	public void setOnClickListener(CalendarViewOnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public void render() {
		removeAllViews();
		switch (this.mode) {
			case Day:
				addView(createDayView(this.selectedDate), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
				break;
			case Week:
				addView(createWeekView(this.selectedDate), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
				break;
			case Month:
				addView(createMonthView(this.selectedDate), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
				break;
		}
	}
	private List<CalendarItem> getItemsForPeriod(LocalDateTime from, LocalDateTime to) {
		List<CalendarItem> result = new ArrayList<CalendarItem>();
		for (CalendarItem item : this.items) {
			if (item.getEventStart().compareTo(from) >= 0 && item.getEventStart().compareTo(to) < 0) {
				result.add(item);
			}
		}
		return result;
	}
	
	private View createDayHeaderView(LocalDate date) {
		TextView dateInfo = new TextView(getContext());
		dateInfo.setText(dayTitleCreator.getDayTitle(mode, date));
		return dateInfo;
	}
	
	private View createDayView(final LocalDate date) {
		LinearLayout result = new LinearLayout(getContext());
		result.setOrientation(VERTICAL);
		View dateInfo = createDayHeaderView(date);
		LinearLayout.LayoutParams dayHeaderParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		if (CalendarMode.Day == this.mode) {
			dayHeaderParams.gravity = Gravity.CENTER_HORIZONTAL;
		}
		result.addView(dateInfo, dayHeaderParams);
		
		final OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("KLIK!!!!");
				if (onClickListener != null) {
					onClickListener.onClick(mode, date, null);
				}
			}
		}; 
		final OnLongClickListener longClickListener = new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (onClickListener != null) {
					onClickListener.onLongClick(mode, date, null);
				}
				return false;
			}
		};
		result.setOnClickListener(clickListener);
		result.setOnLongClickListener(longClickListener);
		
		ScrollView scrollContent = new ScrollView(getContext());
		scrollContent.setFillViewport(true);
		
		LinearLayout hours = new LinearLayout(getContext());
		hours.setOrientation(LinearLayout.VERTICAL);
		hours.setOnClickListener(clickListener);
		hours.setOnLongClickListener(longClickListener);;
		boolean odd = false;
		for (final CalendarItem item : getItemsForPeriod(date.toLocalDateTime(LocalTime.MIDNIGHT), date.plusDays(1).toLocalDateTime(LocalTime.MIDNIGHT))) {
			LinearLayout itemView = new LinearLayout(getContext());
			itemView.addView(this.eventViewCreator.createView(item.getData(), this.mode, itemView), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			itemView.setBackgroundColor(odd ? Color.WHITE : Color.LTGRAY);
			odd = !odd;
			hours.addView(itemView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onClickListener != null) {
						onClickListener.onClick(mode, date, item.getData());
					}
				}
			});
			
			itemView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (onClickListener != null) {
						onClickListener.onLongClick(mode, date, item.getData());
					}
					return false;
				}
			});
		}
		scrollContent.addView(hours, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams scrollerParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
		result.addView(scrollContent, scrollerParams);
		return result;
	}
	private int determineBackground(LocalDate date) {
		if (LocalDate.now().equals(date)) {
			return R.drawable.border_cell_today;
		}
		if (selectedDate.equals(date)) {
			return R.drawable.border_cell_selected;
		}
		else {
			return R.drawable.border_cell;
		}
	}
	private View createWeekView(LocalDate date) {
		HorizontalScrollView result = new HorizontalScrollView(getContext());
		LinearLayout daysView = new LinearLayout(getContext());
		daysView.setOrientation(HORIZONTAL);
		LocalDate startDate = getWeekMonday(date);
		for (int i = 0; i < daysToShow; i ++) {
			View dayView = createDayView(startDate.plusDays(i));
			LinearLayout.LayoutParams dayViewParams = new LayoutParams(Math.round(gridWidth / daysToShow), LinearLayout.LayoutParams.MATCH_PARENT);
			dayViewParams.rightMargin = 1;
			dayViewParams.bottomMargin = 1;
			daysView.addView(dayView, dayViewParams);
			dayView.setBackgroundResource(determineBackground(startDate.plusDays(i)));
		}
		result.addView(daysView, new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
		
		return result;
	}
	private View createDayHeader(String day) {
		TextView result = new TextView(getContext());
		result.setText(day);
		return result;
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
			gridLayout.addView(createDayHeader(dayOfWeekNames.get(i)), params);
		}
		LocalDate currentDate = getMonthStart(date);
		int month = currentDate.getMonthOfYear();
		do {
			LinearLayout dayOfMonthView = new LinearLayout(getContext());
			final LocalDate dateWhichIsDrawing = currentDate;
			dayOfMonthView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onClickListener != null) {
						onClickListener.onClick(mode, dateWhichIsDrawing, null);
					}					
				}
			});
			
			dayOfMonthView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if (onClickListener != null) {
						onClickListener.onLongClick(mode, dateWhichIsDrawing, null);
					}
					return false;
				}
				
			});
			
			dayOfMonthView.setOrientation(VERTICAL);
			dayOfMonthView.setBackgroundResource(determineBackground(currentDate));
			TextView dayNum = new TextView(getContext());
			dayNum.setText(dayTitleCreator.getDayTitle(mode, currentDate));
			LinearLayout.LayoutParams dayOfMonthParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			dayOfMonthParams.gravity = Gravity.RIGHT;
			dayOfMonthView.addView(dayNum, dayOfMonthParams);
			LinearLayout dayEventsView = new LinearLayout(getContext());
			dayEventsView.setOrientation(VERTICAL);
			
			for (CalendarItem item : getItemsForPeriod(currentDate.toLocalDateTime(LocalTime.MIDNIGHT), currentDate.plusDays(1).toLocalDateTime(LocalTime.MIDNIGHT))) {
				LinearLayout itemView = new LinearLayout(getContext());
				itemView.addView(this.eventViewCreator.createView(item.getData(), this.mode, itemView), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				dayEventsView.addView(itemView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			}
			
			dayOfMonthView.addView(dayEventsView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			int col = currentDate.getDayOfWeek() - 1;
			int row = Math.round((currentDate.getDayOfMonth() + getMonthStart(date).getDayOfWeek() - 1 - 1) / this.daysToShow) + 1;
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
