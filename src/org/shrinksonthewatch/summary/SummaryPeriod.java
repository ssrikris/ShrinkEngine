package org.shrinksonthewatch.summary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SummaryPeriod {
	private String summaryStartDate;
	private String summaryEndDate;
	private static final DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	
	public SummaryPeriod(final String start, final String end) {
		this.summaryStartDate = start;
		this.summaryEndDate = end;
	}
	
	public String getSummaryStartDateAsString() {
		return summaryStartDate;
	}
	public void setSummaryStartDate(String summaryStartDate) {
		this.summaryStartDate = summaryStartDate;
	}
	public String getSummaryEndDateAsString() {
		return summaryEndDate;
	}
	public void setSummaryEndDate(String summaryEndDate) {
		this.summaryEndDate = summaryEndDate;
	}
	
	public Date getSummaryStartDate() {
		try {
			return format.parse(summaryStartDate);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	public Date getSummaryEndDate() {
		try {
			return format.parse(summaryEndDate);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;		
	}
}
