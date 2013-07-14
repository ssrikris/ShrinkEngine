package org.shrinksonthewatch.summary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.shrinksonthewatch.sentiments.SentenceSentiment;
import org.shrinksonthewatch.sentiments.Sentiment;

public class WeeklySummary implements Summary {
	private SummaryPeriod period;
	private static final SentenceSentiment ss = new SentenceSentiment();
	private static final String NEW_LINE = System.getProperty("line.separator");

	public WeeklySummary(final SummaryPeriod period) {
		this.period = period;
	}
	
	@Override
	public String getSummary() {
		return getWeeklySummary(period);
	}

	private String getWeeklySummary (final SummaryPeriod period) {
		//String fileName = "smsnote-" + date;
		
		BufferedReader bw = null;
		String line = null;
		Sentiment sentiment = null;
		float prevScore = 0f;
		float currentScore = 0f;
		String dateString = null;
		StringBuilder retStringBldr = new StringBuilder();
		
		
		String[] fileNames = getFileNamesForPeriod(period);
		
		int fileProcessingIndex = 0;
		
		for (String fileName : fileNames) {
			try {
				bw = new BufferedReader((new FileReader(new File((fileName)))));
				line = bw.readLine();
				
				retStringBldr.append("[");
				
				while (line != null) {
					
					int indexOfSep = line.indexOf(" - ");
					
					dateString = fileName.substring(fileName.indexOf("-") + 1, fileName.indexOf("."));
					
					
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

					line = bw.readLine();
				}
				
				fileProcessingIndex++;
				
				System.out.println (dateString + " - " + currentScore);
				
				retStringBldr.append("\'" + dateString + "\', ");
				retStringBldr.append(currentScore);

				if (fileProcessingIndex != fileNames.length) {
					retStringBldr.append("],");
				}
				else {
					retStringBldr.append("]");
				}
				retStringBldr.append(NEW_LINE);
				prevScore = currentScore;
				
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
		}
		
		BufferedWriter br = null;
		

		try {
			br = new BufferedWriter(new FileWriter(new File("C:\\hacks\\data\\SMSNote-WeekOf--13-07-2013" + "_computed.txt")));
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
	
	private String[] getFileNamesForPeriod (final SummaryPeriod period) {
		return new String[] {"C:\\hacks\\data\\SMSNote-07-07-2013.txt",
				"C:\\hacks\\data\\SMSNote-08-07-2013.txt", 
				"C:\\hacks\\data\\SMSNote-09-07-2013.txt",
				"C:\\hacks\\data\\SMSNote-10-07-2013.txt",
				"C:\\hacks\\data\\SMSNote-11-07-2013.txt",
				"C:\\hacks\\data\\SMSNote-12-07-2013.txt",
				"C:\\hacks\\data\\SMSNote-13-07-2013.txt"
		};
	}
	
	public static void main(String[] args) {
		SummaryPeriod period = new SummaryPeriod("07-07-2013", "13-07-2013");
		WeeklySummary wSummary = new WeeklySummary(period);
		wSummary.getSummary();
	}
	
}
