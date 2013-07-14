package org.shrinksonthewatch.summary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.shrinksonthewatch.sentiments.SentenceSentiment;
import org.shrinksonthewatch.sentiments.Sentiment;

public class DailySummary implements Summary {
	private SummaryPeriod period;
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final SentenceSentiment ss = new SentenceSentiment();
	
	public DailySummary(final SummaryPeriod period) {
		this.period = period;
	}
	
	@Override
	public String getSummary() {
		return getHourlySummaryForDay(period.getSummaryStartDateAsString());
	}

	private String getHourlySummaryForDay (final String date) {
		BufferedReader bw = null;
		String line = null;
		Sentiment sentiment = null;
		float prevScore = 0f;
		float currentScore = 0f;
		String timeString = null;
		StringBuilder retStringBldr = new StringBuilder();
		
		try {
			bw = new BufferedReader((new FileReader(new File((getFileNameForPeriod(period) + ".txt")))));
			line = bw.readLine();
			
			while (line != null) {
				retStringBldr.append("[");
				int indexOfSep = line.indexOf(" - ");
				
				timeString = line.substring(0, indexOfSep);
				retStringBldr.append("\'" + timeString + "\', ");
				
				sentiment = ss.analyze(line.substring(indexOfSep + 3));
				currentScore = sentiment.getScore();
				
				if (prevScore < currentScore) {
					//Sentiment became happier
					currentScore = prevScore/2 + currentScore;
				}
				else if (prevScore > currentScore) {
					//Sentiment became sadder
					currentScore = currentScore + prevScore;
				}
				else {
					//No change, retain the earlier score
					currentScore /= 2;
				}
				
				retStringBldr.append(currentScore);
				
				line = bw.readLine();
				
				if (null != line) {
					retStringBldr.append("],");
				}
				else {
					retStringBldr.append("]");
				}
				retStringBldr.append(NEW_LINE);
				prevScore = currentScore;
				
				System.out.println (timeString + " - " + currentScore);
				
			}
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				bw.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		BufferedWriter br = null;
		
		try {
			br = new BufferedWriter(new FileWriter(new File(getFileNameForPeriod(period) + "_computed.txt")));
			br.write(retStringBldr.toString());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				br.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return retStringBldr.toString();
	}	
	
	private String getFileNameForPeriod(final SummaryPeriod period) {
		String fileName = "smsnote-" + period.getSummaryStartDateAsString();
		
		return "C:\\hacks\\data\\" + fileName;
		
	}
	
	public static void main(String[] args) {
		SummaryPeriod period = new SummaryPeriod("07-07-2013", "07-07-2013");
		DailySummary dSummary = new DailySummary(period);
		dSummary.getSummary();
	}
}
