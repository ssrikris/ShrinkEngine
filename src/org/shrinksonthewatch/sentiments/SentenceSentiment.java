package org.shrinksonthewatch.sentiments;

/*
 * #%L
 * SentimentalJ
 * %%
 * Copyright (C) 2012 - 2013 The British Library
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SentenceSentiment {
	private static final String NEW_LINE = System.getProperty("line.separator");

	private int hits  = 0;
	private List<String> words = new ArrayList<String>();
	private List<String> adjectives = new ArrayList<String>();
	private String[] tokens;


	private void addPush(String t, int score){
		hits += score;
		words.add(t);
	}

	private void multiply(String t, int score){
		hits *= score;
		adjectives.add(t);
	}

	private void init( String phrase) {
		hits  = 0;
		words = new ArrayList<String>();
		adjectives = new ArrayList<String>();

		String noPunctuation = phrase.replaceAll("[^a-zA-Z ]+", " ").replaceAll(" {2,}"," ");
		tokens = noPunctuation.toLowerCase().split(" ");		 
	}

	// Calculates the negative sentiment of a sentence
	// -------------------------------------------------- //

	public Sentiment negativity( String phrase ) {
		init(phrase);

		for(String t : tokens) {
			if (SentimentStrings.neg5.indexOf(t) > -1) {
				addPush(t, 5);
			} else if (SentimentStrings.neg4.indexOf(t) > -1) {
				addPush(t, 4);
			} else if (SentimentStrings.neg3.indexOf(t) > -1) {
				addPush(t, 3);
			} else if (SentimentStrings.neg2.indexOf(t) > -1) {
				addPush(t, 2);
			} else if (SentimentStrings.neg1.indexOf(t) > -1) {
				addPush(t, 1);
			}
		}

		for(String t : tokens) {
			if (SentimentStrings.int3.indexOf(t) > -1) {
				multiply(t, 4);
			} else if (SentimentStrings.int2.indexOf(t) > -1) {
				multiply(t, 3);
			} else if (SentimentStrings.int1.indexOf(t) > -1) {
				multiply(t, 2);
			}
		}
		return new Sentiment(hits, tokens.length, words, adjectives );

	}

	public Sentiment negativity2( String phrase ) {
		int tokenPosInSentence = 0;
		
		init(phrase);

		for(String t : tokens) {
			tokenPosInSentence++;
			
			if (SentimentStrings.neg5.indexOf(t) > -1) {
				addPush(t, 5 * reversePolarityFactor(t, tokenPosInSentence));
			} else if (SentimentStrings.neg4.indexOf(t) > -1) {
				addPush(t, 4 * reversePolarityFactor(t, tokenPosInSentence));
			} else if (SentimentStrings.neg3.indexOf(t) > -1) {
				addPush(t, 3 * reversePolarityFactor(t, tokenPosInSentence));
			} else if (SentimentStrings.neg2.indexOf(t) > -1) {
				addPush(t, 2 * reversePolarityFactor(t, tokenPosInSentence));
			} else if (SentimentStrings.neg1.indexOf(t) > -1) {
				addPush(t, 1 * reversePolarityFactor(t, tokenPosInSentence));
			}
		}

		/*
		for(String t : tokens) {
			if (SentimentStrings.int3.indexOf(t) > -1) {
				multiply(t, 4);
			} else if (SentimentStrings.int2.indexOf(t) > -1) {
				multiply(t, 3);
			} else if (SentimentStrings.int1.indexOf(t) > -1) {
				multiply(t, 2);
			}
		}*/
		
		return new Sentiment(hits, tokens.length, words, adjectives );

	}

	private int reversePolarityFactor(String token, int tokenPos) {
		if (tokenPos - 2 < 0) {
			return 1;
		}
		
		String prevWord = null;
		String prevPrevWord = null;
		
		prevWord = tokens[tokenPos - 1];
		prevPrevWord = tokens[tokenPos - 2];
		
		if (SentimentStrings.int3.indexOf(prevWord) > -1) {
			multiply(token, 4);
		} else if (SentimentStrings.int2.indexOf(prevWord) > -1) {
			multiply(token, 3);
		} else if (SentimentStrings.int1.indexOf(prevWord) > -1) {
			multiply(token, 2);
		}
		
		if (SentimentStrings.negation.indexOf(prevWord) > -1) {
			return -1;
		}
		
		if (SentimentStrings.int3.indexOf(prevPrevWord) > -1) {
			multiply(token, 4);
		} else if (SentimentStrings.int2.indexOf(prevPrevWord) > -1) {
			multiply(token, 3);
		} else if (SentimentStrings.int1.indexOf(prevPrevWord) > -1) {
			multiply(token, 2);
		}
		
		if (SentimentStrings.negation.indexOf(prevWord) > -1) {
			return -1;
		}
		
		return 1;
	}
	
	// Calculates the positive sentiment  of a sentence
	// -------------------------------------------------- //

	public Sentiment positivity( String phrase) {
		
		init(phrase);

		for(String t : tokens) {
			if (SentimentStrings.pos5.indexOf(t) > -1) {
				addPush(t,5);
			} else if (SentimentStrings.pos4.indexOf(t) > -1) {
				addPush(t,4);
			} else if (SentimentStrings.pos3.indexOf(t) > -1) {
				addPush(t,3);
			} else if (SentimentStrings.pos2.indexOf(t) > -1) {
				addPush(t,2);
			} else if (SentimentStrings.pos1.indexOf(t) > -1) {
				addPush(t,1);
			}
		}

		for(String t : tokens) {
			if (SentimentStrings.int3.indexOf(t) > -1) {
				multiply(t, 4);
			} else if (SentimentStrings.int2.indexOf(t) > -1) {
				multiply(t, 3);
			} else if (SentimentStrings.int1.indexOf(t) > -1) {
				multiply(t, 2);
			}
		}

		return new Sentiment(hits, tokens.length, words, adjectives );
	}

	public Sentiment positivity2( String phrase) {
		int tokenPosInSentence = 0;
		
		init(phrase);

		for(String t : tokens) {
			tokenPosInSentence++;
			
			if (SentimentStrings.pos5.indexOf(t) > -1) {
				addPush(t, 5 * reversePolarityFactor(t, tokenPosInSentence));
			} else if (SentimentStrings.pos4.indexOf(t) > -1) {
				addPush(t, 4 * reversePolarityFactor(t, tokenPosInSentence));
			} else if (SentimentStrings.pos3.indexOf(t) > -1) {
				addPush(t, 3 * reversePolarityFactor(t, tokenPosInSentence));
			} else if (SentimentStrings.pos2.indexOf(t) > -1) {
				addPush(t, 2 * reversePolarityFactor(t, tokenPosInSentence));
			} else if (SentimentStrings.pos1.indexOf(t) > -1) {
				addPush(t, 1 * reversePolarityFactor(t, tokenPosInSentence));
			}
		}

		for(String t : tokens) {
			if (SentimentStrings.int3.indexOf(t) > -1) {
				multiply(t, 4);
			} else if (SentimentStrings.int2.indexOf(t) > -1) {
				multiply(t, 3);
			} else if (SentimentStrings.int1.indexOf(t) > -1) {
				multiply(t, 2);
			}
		}

		return new Sentiment(hits, tokens.length, words, adjectives );
	}

	
	
	// Calculates overall sentiment
	// -------------------------------------------------- //

	public Sentiment analyze( String phrase ) {
		Sentiment pos = positivity(phrase);
		Sentiment neg = negativity(phrase);
		return new Sentiment( pos.getScore() - neg.getScore(), 
				pos.getComparative() - neg.getComparative(), pos, neg );  
	}

	public static void main(String[] args) {
		SentenceSentiment obj = new SentenceSentiment();
		
		BufferedReader bw = null;
		String line = null;
		int score = 0;
		Sentiment sentiment = null;
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		//obj.getHourlySummaryForDay("" + format.format(date));
		obj.getWeeklySummary();
		
		/*
		try {
			bw = new BufferedReader((new FileReader(new File(("d:\\software\\yh\\datafiles\\jiahkhan\\SuicideNote2.txt")))));
			line = bw.readLine();
			
			while (line != null) {
				sentiment = obj.analyze(line);
				System.out.println (sentiment.getPositive().getScore() + "\t" + sentiment.getNegative().getScore() + "\t" + sentiment.getComparative() + "\t" + sentiment.getScore());
				line = bw.readLine();
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
		}*/
	}
	
	private String getHourlySummaryForDay (final String date) {
		String fileName = "smsnote-" + date;
		
		BufferedReader bw = null;
		String line = null;
		Sentiment sentiment = null;
		float prevScore = 0f;
		float currentScore = 0f;
		String timeString = null;
		StringBuilder retStringBldr = new StringBuilder();
		
		try {
			bw = new BufferedReader((new FileReader(new File(("d:\\software\\yh\\datafiles\\messages\\daily\\" + fileName + ".txt")))));
			line = bw.readLine();
			
			while (line != null) {
				retStringBldr.append("[");
				int indexOfSep = line.indexOf(" - ");
				
				timeString = line.substring(0, indexOfSep);
				retStringBldr.append("\'" + timeString + "\', ");
				
				sentiment = analyze(line.substring(indexOfSep + 3));
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
			br = new BufferedWriter(new FileWriter(new File("d:\\software\\yh\\datafiles\\messages\\daily\\" + fileName + "_computed.txt")));
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
	
	private String getWeeklySummary () {
		//String fileName = "smsnote-" + date;
		
		BufferedReader bw = null;
		String line = null;
		Sentiment sentiment = null;
		float prevScore = 0f;
		float currentScore = 0f;
		String dateString = null;
		StringBuilder retStringBldr = new StringBuilder();
		
		
		String[] fileNames = {"d:\\software\\yh\\datafiles\\messages\\daily\\SMSNote-07-07-2013.txt",
				"d:\\software\\yh\\datafiles\\messages\\daily\\SMSNote-08-07-2013.txt", 
				"d:\\software\\yh\\datafiles\\messages\\daily\\SMSNote-09-07-2013.txt",
				"d:\\software\\yh\\datafiles\\messages\\daily\\SMSNote-10-07-2013.txt",
				"d:\\software\\yh\\datafiles\\messages\\daily\\SMSNote-11-07-2013.txt",
				"d:\\software\\yh\\datafiles\\messages\\daily\\SMSNote-12-07-2013.txt",
				"d:\\software\\yh\\datafiles\\messages\\daily\\SMSNote-13-07-2013.txt"
		};
		
		int fileProcessingIndex = 0;
		
		for (String fileName : fileNames) {
			try {
				bw = new BufferedReader((new FileReader(new File((fileName)))));
				line = bw.readLine();
				
				retStringBldr.append("[");
				
				while (line != null) {
					
					int indexOfSep = line.indexOf(" - ");
					
					dateString = fileName.substring(fileName.indexOf("-") + 1, fileName.indexOf("."));
					
					
					sentiment = analyze(line.substring(indexOfSep + 3));
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
			br = new BufferedWriter(new FileWriter(new File("d:\\software\\yh\\datafiles\\messages\\daily\\SMSNote-WeekOf--13-07-2013" + "_computed.txt")));
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

}
