import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class StockBrainNeuralNet
{	
	static StockBrainNeuralNet brain = new StockBrainNeuralNet();
	
	Matrix trainingDataMatrix;
	static NeuralNetwork neuralNet;
	
	public void runStockBrainNeuralNet() throws IOException
	{		
		int[] neuralNetFrame = {3, 4, 1};
		int inputDataSize = neuralNetFrame[0];
		int outputDataSize = neuralNetFrame[neuralNetFrame.length - 1];
		
		brain.makeTrainingInput();
		
		//brain.getTrainingData(inputDataSize, outputDataSize);
		//brain.trainNeuralNetwork(neuralNetFrame, inputDataSize, outputDataSize);
		
		//Matrix in = new Matrix(3, 1);
		//in.setEntry(0, 0, -1.6388062277336175);
		//in.setEntry(1, 0, 0.04347357692091285);
		//in.setEntry(2, 0, 29.32644567956524);
		
		//neuralNet.forwardPropagate(in);
		//neuralNet.getFinalOutputs().printMatrix();
	}
	
	
	
	public void trainNeuralNetwork(int[] networkFrame, int inputDataSize, int outputDataSize)
	{		
		neuralNet = new NeuralNetwork(networkFrame, .5);
		
		for (int i = 0; i < trainingDataMatrix.getRows(); i++)
		{
			Matrix out = new Matrix(outputDataSize, 1);
			
			for (int j = 0; j < out.getRows(); j++)
			{
				out.setEntry(j, 0, trainingDataMatrix.getEntry(i, j));
			}
			
			Matrix in = new Matrix(inputDataSize, 1);
			
			for (int j = 0; j < in.getRows(); j++)
			{
				in.setEntry(j, 0, trainingDataMatrix.getEntry(i, (j + outputDataSize)));
			}
			
			neuralNet.forwardPropagate(in);
			neuralNet.backwardPropagate(out);
		}
	}
		
	public void getTrainingData(int inDataSize, int outDataSize) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader("TrainingData.txt"));
		ArrayList<String> trainingData = new ArrayList<String>();
		
		while (in.ready())
		{
			String inputLine = in.readLine();
			trainingData.add(inputLine);
		}
		
		trainingDataMatrix = new Matrix(trainingData.size(), (inDataSize + outDataSize));
		
		for (int i = 0; i < trainingDataMatrix.getRows(); i++)
		{
			String inputLine = trainingData.get(i);
			
			for (int j = 0; j < outDataSize; j++)
			{
				trainingDataMatrix.setEntry(i, j, Double.parseDouble(inputLine.substring(0, inputLine.indexOf(" "))));
				inputLine = inputLine.substring(inputLine.indexOf(" ") + 1, inputLine.length());
			}
			
			inputLine = inputLine.substring(inputLine.indexOf(" ") + 1, inputLine.length());
			inputLine = inputLine.substring(inputLine.indexOf(" ") + 1, inputLine.length());
			
			for (int j = outDataSize; j < (outDataSize + inDataSize); j++)
			{
				if (j != (outDataSize + inDataSize - 1))
				{
					trainingDataMatrix.setEntry(i, j, Double.parseDouble(inputLine.substring(0, inputLine.indexOf(" "))));
				}
				else
				{
					trainingDataMatrix.setEntry(i, j, Double.parseDouble(inputLine.substring(0, inputLine.length())));
				}
				
				inputLine = inputLine.substring(inputLine.indexOf(" ") + 1, inputLine.length());
			}
		}
	}
	
	public void makeTrainingInput() throws IOException
	{	
		StockBrainGUI gui = new StockBrainGUI();
		
		BufferedWriter out = new BufferedWriter(new FileWriter("TrainingInput.txt"));
		
		gui.doStockBrainGUI();
		
		while (gui.getDone() == false)
		{
			if (gui.getNext() == true)
			{
				String stockName = gui.getStock();
				String start = gui.getStart();
				String end = gui.getEnd();
		
				String parsedTable = brain.parseStockPrices(stockName, start, end);
		
				ArrayList<StockQuotes> allQuotes = brain.makeQuoteArray(parsedTable);
				
				double[] MACD = brain.findMACD(allQuotes, 26, 12);
				
				double[] chaikinMoneyFlow = brain.findChaikinMoneyFlow(allQuotes, 21);
				
				double[] relativeStrengthIndex = brain.findRelativeStrengthIndex(allQuotes, 14);
				
				for (int i = 0; i < allQuotes.size(); i++)					
				{
					if (MACD[i] != 0)
					{
						out.write(" " + stockName + " " + allQuotes.get(i).getDate() + " " + MACD[i] + 
								  " " + chaikinMoneyFlow[i] + " " + relativeStrengthIndex[i]);
						out.newLine();
						out.flush();
					}
				}
				
				gui.switchNext();
			}
		}
		
		out.close();
	}
	
	public void plotOffStockIndicator(double[] indicator, String name, Color color, int high, int low, boolean useHighLow)
	{
		JFrame frame = new JFrame();
		frame.setTitle(name);
		
		int pixelHeight = 200;
		int pixelWidth = 1400;
		frame.setSize(pixelWidth, pixelHeight);
		
		
		double maxValue = -1000000;
		double minValue = 1000000;
		
		if (useHighLow)
		{
			minValue = low;
			maxValue = high;			
		}
		else
		{
			for (int i = 0; i < indicator.length; i++)
			{
				if (indicator[i] > maxValue)
				{
					maxValue = indicator[i];
				}
				if (indicator[i] < minValue)
				{
					minValue = indicator[i];
				}
			}
		}
		
		IndicatorPlotComponent indicatorComponent = new IndicatorPlotComponent(indicator, pixelWidth, pixelHeight, 
				   															   minValue, maxValue, color);

		frame.add(indicatorComponent);
		frame.setVisible(true);
	}
	
	public void plotOnStockIndicator(ArrayList<StockQuotes> allQuotes, double[] indicator, JFrame frame, Color color)
	{
		int pixelHeight = frame.getHeight();
		int pixelWidth = frame.getWidth();
		
		double maxPrice = 0;
		double minPrice = 1000000;
		int numDays = allQuotes.size();
		
		for (int i = 0; i < numDays; i++)
		{
			if (allQuotes.get(i).getHigh() > maxPrice)
			{
				maxPrice = allQuotes.get(i).getHigh();
			}
			if (allQuotes.get(i).getLow() < minPrice)
			{
				minPrice = allQuotes.get(i).getLow();
			}
		}
		
		IndicatorPlotComponent indicatorComponent = new IndicatorPlotComponent(indicator, pixelWidth, pixelHeight, 
				   															   minPrice, maxPrice, color);

		frame.add(indicatorComponent);
		frame.setVisible(true);
	}
	
	public void plotStockPrices(ArrayList<StockQuotes> allQuotes, JFrame frame)
	{
		int pixelHeight = frame.getHeight();
		int pixelWidth = frame.getWidth();
		
		double maxPrice = 0;
		double minPrice = 1000000;
		int numDays = allQuotes.size();
		
		for (int i = 0; i < numDays; i++)
		{
			if (allQuotes.get(i).getHigh() > maxPrice)
			{
				maxPrice = allQuotes.get(i).getHigh();
			}
			if (allQuotes.get(i).getLow() < minPrice)
			{
				minPrice = allQuotes.get(i).getLow();
			}
		}
		
		for (int i = 0; i < numDays; i++)
		{
			StockPlotComponent stockComponent;
			
			//System.out.println(allQuotes.get(i).getDate());
			
			if (i == numDays - 1)
			{
				stockComponent = new StockPlotComponent(allQuotes.get(i), allQuotes.get(i).getOpen(), pixelWidth, pixelHeight, 
																		   minPrice, maxPrice, numDays, numDays - i - 1);
			}
			else
			{
				stockComponent = new StockPlotComponent(allQuotes.get(i), allQuotes.get(i + 1).getClose(), pixelWidth, pixelHeight, 
																     	   minPrice, maxPrice, numDays, numDays - i - 1);
			}  
			
			frame.add(stockComponent);
			frame.setVisible(true);
		}
	}
	
	public double[] findSimpleMovingAverage(ArrayList<StockQuotes> allQuotes, int averagePeriod)
	{
		double[] simpleMovingAverage = new double[allQuotes.size()];
		
		for (int i = 0; i < allQuotes.size() - averagePeriod + 1; i++)
		{
			double sum = 0;
			
			for(int j = i; j < i + averagePeriod; j++)
			{
				sum += allQuotes.get(j).getClose();
			}
			
			simpleMovingAverage[i] = sum / averagePeriod;
			//System.out.println(allQuotes.get(i).getDate() + " " + simpleMovingAverage[i]);
		}
		
		return simpleMovingAverage;
	}
	
	public double[] findExponentialMovingAverage(ArrayList<StockQuotes> allQuotes, int averagePeriod)
	{
		double[] exponentialMovingAverage = new double[allQuotes.size()];
		
		double sum = 0;
		
		for(int i = allQuotes.size() - averagePeriod; i < allQuotes.size(); i++)
		{
			sum += allQuotes.get(i).getClose();
		}
		
		exponentialMovingAverage[allQuotes.size() - averagePeriod] = (double) Math.round(100 * (sum / averagePeriod)) / 100;
		
		//System.out.println(allQuotes.get(allQuotes.size() - averagePeriod).getDate() + " " + exponentialMovingAverage[allQuotes.size() - averagePeriod]);
		
		for (int i = allQuotes.size() - averagePeriod - 1; i >= 0; i--)
		{						
			exponentialMovingAverage[i] = (allQuotes.get(i).getClose() - exponentialMovingAverage[i + 1]) * 
										   2 / (1 + averagePeriod) + exponentialMovingAverage[i + 1];
			
			//System.out.println(allQuotes.get(i).getDate() + " " + exponentialMovingAverage[i]);
		}
		
		return exponentialMovingAverage;
	}
	
	public double[] findMACD(ArrayList<StockQuotes> allQuotes, int longPeriod, int shortPeriod)
	{
		double[] MACD = new double[allQuotes.size()];
		
		double[] longEMA = brain.findExponentialMovingAverage(allQuotes, longPeriod);
		double[] shortEMA = brain.findExponentialMovingAverage(allQuotes, shortPeriod);
		
		for (int i = 0; i < allQuotes.size(); i++)
		{
			if (longEMA[i] == 0)
			{
				MACD[i] = 0;
			}
			else
			{
				MACD[i] = shortEMA[i] - longEMA[i];
			}
			
			//System.out.println(allQuotes.get(i).getDate() + " " + MACD[i]);
		}
		
		return MACD;
	}
	
	public double[] findAccDisLine(ArrayList<StockQuotes> allQuotes)
	{
		double[] CLV = new double[allQuotes.size()];
		double[] CLVVolume = new double[allQuotes.size()];
		
		for (int i = 0; i < allQuotes.size(); i++)
		{
			CLV[i] = ((allQuotes.get(i).getClose() - allQuotes.get(i).getLow()) 
					- (allQuotes.get(i).getHigh() - allQuotes.get(i).getClose()))
					/ (allQuotes.get(i).getHigh() - allQuotes.get(i).getLow());
			CLVVolume[i] = CLV[i] * allQuotes.get(i).getVolume();
		}
		
		double[] accDisLine = new double[allQuotes.size()];
		
		double sum = 0;
		
		for (int i = allQuotes.size() - 1; i >= 0; i--)
		{
			sum += CLVVolume[i];
			accDisLine[i] = sum;
			
			//System.out.println(allQuotes.get(i).getDate() + " " + accDisLine[i]);
		}
		
		return accDisLine;
	}
	
	public double[] findChaikinMoneyFlow(ArrayList<StockQuotes> allQuotes, int averagePeriod)
	{
		double[] CLV = new double[allQuotes.size()];
		double[] CLVVolume = new double[allQuotes.size()];
		
		double[] chaikinMoneyFlow = new double[allQuotes.size()];
		
		for (int i = 0; i < allQuotes.size(); i++)
		{
			CLV[i] = ((allQuotes.get(i).getClose() - allQuotes.get(i).getLow()) 
					- (allQuotes.get(i).getHigh() - allQuotes.get(i).getClose()))
					/ (allQuotes.get(i).getHigh() - allQuotes.get(i).getLow());
			CLVVolume[i] = CLV[i] * allQuotes.get(i).getVolume();
		}
		
		for (int i = 0; i < CLVVolume.length - averagePeriod + 1; i++)
		{
			double accDisSum = 0;
			double volSum = 0;
			
			for(int j = i; j < i + averagePeriod; j++)
			{
				accDisSum += CLVVolume[j];
				volSum += allQuotes.get(j).getVolume();
			}
			
			chaikinMoneyFlow[i] = accDisSum / volSum;
			
			//System.out.println(allQuotes.get(i).getDate() + " " + chaikinMoneyFlow[i]);
		}
		
		return chaikinMoneyFlow;
	}
	
	public double[] findRelativeStrengthIndex(ArrayList<StockQuotes> allQuotes, int averagePeriod)
	{
		double[] gains = new double[allQuotes.size()];
		double[] losses = new double[allQuotes.size()];
		
		double[] relativeStrengthIndex = new double[allQuotes.size()];
		
		for (int i = 0; i < allQuotes.size() - 2; i++)
		{
			if (allQuotes.get(i).getClose() < allQuotes.get(i + 1).getClose())
			{
				losses[i] = allQuotes.get(i + 1).getClose() - allQuotes.get(i).getClose();
			}
			else
			{
				gains[i] = allQuotes.get(i).getClose() - allQuotes.get(i + 1).getClose();
			}
		}
		
		double[] averageGain = new double[allQuotes.size()];
		double[] averageLoss = new double[allQuotes.size()];
		
		double gainSum = 0;
		double lossSum = 0;
		
		for(int i = allQuotes.size() - averagePeriod - 1; i < allQuotes.size() - 1; i++)
		{
			gainSum += gains[i];
			lossSum += losses[i];
		}
		
		averageGain[allQuotes.size() - averagePeriod -1] = gainSum / averagePeriod;
		averageLoss[allQuotes.size() - averagePeriod -1] = lossSum / averagePeriod;
		relativeStrengthIndex[allQuotes.size() - averagePeriod -1] = 100 - 100 / 
																	 (1 + averageGain[allQuotes.size() - averagePeriod -1] / 
																     averageLoss[allQuotes.size() - averagePeriod -1]);
		
		//System.out.println(allQuotes.get(allQuotes.size() - averagePeriod -1).getDate() + " " + relativeStrengthIndex[allQuotes.size() - averagePeriod -1]);
		
		for (int i = allQuotes.size() - averagePeriod - 2; i >= 0; i--)
		{
			averageGain[i] = (averageGain[i + 1] * (averagePeriod - 1) + gains[i]) / averagePeriod;
			averageLoss[i] = (averageLoss[i + 1] * (averagePeriod - 1) + losses[i]) / averagePeriod;
			relativeStrengthIndex[i] = 100 - 100 / (1 + averageGain[i] / averageLoss[i]);
			
			//System.out.println(allQuotes.get(i).getDate() + " " + relativeStrengthIndex[i]);
		}
		
		return relativeStrengthIndex;
	}
	
	public ArrayList<StockQuotes> makeQuoteArray(String parsedTable)
	{
		parsedTable = parsedTable.substring(1, parsedTable.length());
		ArrayList<StockQuotes> allQuotes = new ArrayList<StockQuotes>();
		
		while (parsedTable.length() > 15)
		{
			String daysQuotes = parsedTable.substring(0, parsedTable.indexOf(" \n"));
			daysQuotes = daysQuotes.substring(0, daysQuotes.lastIndexOf(" ") + 1);
			String date = daysQuotes.substring(0, daysQuotes.indexOf(" "));
			daysQuotes = daysQuotes.substring(daysQuotes.indexOf(" ") + 1, daysQuotes.length());
			double open = Double.parseDouble(daysQuotes.substring(0, daysQuotes.indexOf(" ")));
			daysQuotes = daysQuotes.substring(daysQuotes.indexOf(" ") + 1, daysQuotes.length());
			double high = Double.parseDouble(daysQuotes.substring(0, daysQuotes.indexOf(" ")));
			daysQuotes = daysQuotes.substring(daysQuotes.indexOf(" ") + 1, daysQuotes.length());
			double low = Double.parseDouble(daysQuotes.substring(0, daysQuotes.indexOf(" ")));
			daysQuotes = daysQuotes.substring(daysQuotes.indexOf(" ") + 1, daysQuotes.length());
			double close = Double.parseDouble(daysQuotes.substring(0, daysQuotes.indexOf(" ")));
			daysQuotes = daysQuotes.substring(daysQuotes.indexOf(" ") + 1, daysQuotes.length());
			int volume = Integer.parseInt((daysQuotes.substring(0, daysQuotes.indexOf(" "))).replaceAll("(?<=\\d),(?=\\d)", ""));
			daysQuotes = daysQuotes.substring(daysQuotes.indexOf(" ") + 1, daysQuotes.length());
			parsedTable = parsedTable.substring(parsedTable.indexOf(" \n") + 2, parsedTable.length());
			
			StockQuotes quotes = new StockQuotes(date, open, high, low, close, volume);
			allQuotes.add(quotes);
		}
		
		return allQuotes;
	}
	
	public String parseStockPrices(String stockName, String start, String end)
	{
		String parsedTable = "";
		
		for (int i = 0; i != -1; i++)
		{
			StringBuilder websiteHTML = brain.getWebsiteHTML(stockName, start, end, i);
		
			String website = websiteHTML.toString();
			String goneTooFar = "Historical quote data is unavailable for the specified date range.";
			
			if (website.indexOf(goneTooFar) != -1)
			{
				i = -2;
			}
			else
			{
				String tableStart = "</tr></table><table width=";
				String tableEnd = "Close price adjusted for dividends and splits";
		
				String table = website.substring(website.indexOf(tableStart), website.indexOf(tableEnd));
				
				parsedTable += brain.parseWebsiteHTML(table);		
			}
		}
		
		return parsedTable + " \n";
	}
	
	public String parseWebsiteHTML(String table)
	{
		String parsedTable = "";
		String fieldStart = "<td class=\"yfnc_tabledata1\" align=\"right\">";
		String fieldEnd = "</td>";
		String dateStartGarble = "</tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">";
		String dividendStartGarble = "<td class=\"yfnc_tabledata1\" align=\"center\" colspan=\"6\">";
		String tableEndGarble = "</tr><tr><td class=\"yfnc_tabledata1\" colspan=\"7\" align=\"center\"> * <small>";
		
		//isolates useful parts of the table
		table = table.substring(table.indexOf(dateStartGarble), table.indexOf(tableEndGarble));
		
		while(table.length() > 0)
		{
			//removes dividend date field
			if (!(table.indexOf(dateStartGarble) < table.indexOf(dividendStartGarble) 
				  && table.indexOf(dividendStartGarble) < table.indexOf(fieldStart)))
			{
				//removes dividend field
				if (!(table.indexOf(dividendStartGarble) < table.indexOf(dateStartGarble) && table.indexOf(dividendStartGarble) != -1))
				{
					//adds date field
					if (table.indexOf(dateStartGarble) < table.indexOf(fieldStart) && table.indexOf(dateStartGarble) != -1)
					{
						parsedTable += "\n" + table.substring(table.indexOf(dateStartGarble) + 58, table.indexOf(fieldEnd)) + " ";
					}
					//adds price field
					else
					{
						parsedTable += table.substring(table.indexOf(fieldStart) + 42, table.indexOf(fieldEnd)) + " ";
					}
				}
			}
			
			//removes parsed part from table
			table = table.substring(table.indexOf(fieldEnd) + 5, table.length());
		}
		
		return parsedTable;
	}
	
	public StringBuilder getWebsiteHTML (String stockName, String start, String end, int pageNum)
	{
		StringBuilder builder = new StringBuilder();
		
		int URLPageNum = 66 * pageNum;
		
		int startDate = Integer.parseInt(start.substring(2,4));
		int startMonth = Integer.parseInt(start.substring(0,2)) - 1;
		int startYear = Integer.parseInt(start.substring(4,8));
		int endDate = Integer.parseInt(end.substring(2,4));
		int endMonth = Integer.parseInt(end.substring(0,2)) - 1;
		int endYear = Integer.parseInt(end.substring(4,8));
		
		try 
		{
			URL url = new URL("http://finance.yahoo.com/q/hp?s=" + stockName + "&a=" + startMonth + "&b=" + startDate + "&c=" + startYear +
							  "&d=" + endMonth +"&e=" + endDate +"&f=" + endYear + "&g=d&z=66&y=" + URLPageNum);
            URLConnection urlc = url.openConnection();
            
            BufferedInputStream buffer = new BufferedInputStream(urlc.getInputStream());       
            
            int byteRead;
            
            while ((byteRead = buffer.read()) != -1)
                builder.append((char) byteRead);
            
            buffer.close();
        } 
		
		catch (MalformedURLException ex) {}
		
		catch (IOException ex) {}
		
		return builder;
	}
}
