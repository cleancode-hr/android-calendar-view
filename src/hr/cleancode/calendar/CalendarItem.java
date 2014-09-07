package hr.cleancode.calendar;

import org.joda.time.LocalDateTime;

public class CalendarItem implements Comparable<CalendarItem>{
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
