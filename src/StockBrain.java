import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class StockBrain
{
	static StockBrain brain = new StockBrain();
	
	public static void main(String[] args)
	{
		brain.runMetaStockBrain();
	}
	
	public void runStockBrain()
	{	
		StockBrainGUI gui = new StockBrainGUI();
		
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
				
				//double[] MACD = brain.findMACD(allQuotes, 26, 12);
				
				//double[] chaikinMoneyFlow = brain.findChaikinMoneyFlow(allQuotes, 21);
				
				//double[] relativeStrengthIndex = brain.findRelativeStrengthIndex(allQuotes, 14);
				
				double[] parabolicSAR = brain.findParabolicSAR(allQuotes);
				
				double[][] DI = brain.findDI(allQuotes, 14);
				
				double[] diPlus = brain.findDIPlus(DI);
			
				double[] diNegative = brain.findDINegative(DI);
				
				double[] adx = brain.findADX(allQuotes, 14);
		
				int frameWidth = 1000;
				int frameHeight = 600;
				JFrame frame = new JFrame();
				frame.setSize(frameWidth, frameHeight);
				frame.setTitle(stockName + " from " + start + " to " + end);
				
				brain.plotStockPrices(allQuotes, frame);
				brain.plotOnStockIndicator(allQuotes, parabolicSAR, frame, Color.BLUE);
				brain.plotOffStockIndicator(adx, "ADX", Color.GREEN, 0, 0, false);
				brain.plotOffStockIndicator(diPlus, "Di Plus", Color.BLACK, 0, 0, false);
				brain.plotOffStockIndicator(diNegative, "Di Negative", Color.RED, 0, 0, false);
				
				double strategyReturn = brain.calculatePSARADXStrategy(allQuotes, parabolicSAR, adx, diPlus, diNegative, 14, 0);
				double percentReturn = strategyReturn - 100;
				System.out.println("\nAmount returned on " + stockName + " from " + start + " to " + end + ": " + percentReturn + "%");
				
				gui.switchNext();
			}
		}
	}
	
	public void runMetaStockBrain()
	{	
		String[] volatileStocks =  {"ANO",
								   "ANSV",
								   "BIOD",
								   "CEM",
								   "CLMS",
								   "CSIQ",
								   "CSAR",
								   "CETV",
								   "CENX",
								   "CHTR",
								   "JRJC",
								   "CPBY",
								   "CPSL",
								   "CTDC",
								   "CHBT",
								   "CVGI",
								   "DAN",
								   "DVAX",
								   "EGT",
								   "EPIX",
								   "EXM",
								   "FCSX",
								   "FTK",
								   "FOR",
								   "GGP",
								   "GNW",
								   "GENT",
								   "HIG",
								   "HSWI",
								   "HAYZ",
								   "ICOP",
								   "JASO",
								   "LVS",
								   "LDK",
								   "MGM",
								   "MAPP",
								   "MXC",
								   "NM",
								   "NOBL",
								   "PCX",
								   "PPO",
								   "SRZ",
								   "STP",
								   "SVNT",
								   "SSTR",
								   "SOLF",
								   "TBSI",
								   "TSL",
								   "XCR"};
		
		double volatileStockReturn = 0;
		
		String start = "03302008";
		String end = "03302009";
		
		for (int i = 0; i < volatileStocks.length; i++)
		{
			String stockName = volatileStocks[i];
			
			String parsedTable = brain.parseStockPrices(stockName, start, end);
		
			ArrayList<StockQuotes> allQuotes = brain.makeQuoteArray(parsedTable);
			
			double[] parabolicSAR = brain.findParabolicSAR(allQuotes);
				
			double[][] DI = brain.findDI(allQuotes, 14);
				
			double[] diPlus = brain.findDIPlus(DI);
			
			double[] diNegative = brain.findDINegative(DI);
				
			double[] adx = brain.findADX(allQuotes, 14);
				
			double strategyReturn = brain.calculatePSARADXStrategy(allQuotes, parabolicSAR, adx, diPlus, diNegative, 14, 0);
			double percentReturn = strategyReturn - 100;
				
			volatileStockReturn += Math.max(percentReturn, -100.0);
			
			System.out.println("Amount returned on " + stockName + " from " + start + " to " + end + ": " + percentReturn + "%");
		}
		
		volatileStockReturn /= volatileStocks.length;
		
		System.out.println("Average amount returned on all stocks from " + start + " to " + end + ": " + volatileStockReturn + "%");
	}
	
	
	public double calculatePSARADXStrategy(ArrayList<StockQuotes> allQuotes, double[] parabolicSAR,
										   double[] adx, double[] diPlus, double[] diNegative, int averagePeriod, double alpha)
	{	
		double buyPrice = 0;
		double sellPrice = 0;
		double money = 100;
		double shares = 0;
		
		boolean buy = false;
		boolean sell = false;
				
		for (int i = allQuotes.size() - averagePeriod * 2 - 2; i  >= 0; i--)
		{
			double derivADX = adx[i + 1] - adx[i + 2];
			double diff = 0;
			
			if (derivADX > alpha)
			{
				if (parabolicSAR[i + 1] < allQuotes.get(i + 1).getClose() && diPlus[i + 1] > diNegative[i + 1])
				{
					if (buy == false && sell == false)
					{
						buy = true;
						buyPrice = allQuotes.get(i).getOpen();
						shares = money / buyPrice;
					
						//System.out.println("BUY LONG");
					}
					
					if (buy == false && sell == true)
					{
						buy = true;
						sell = false;
						
						buyPrice = allQuotes.get(i).getOpen();
						
						diff = sellPrice - buyPrice;
						money += shares * diff;
						
						//System.out.println("BUY TO COVER: " + money);
						//System.out.println("BUY LONG");
					}
					
					//System.out.println(allQuotes.get(i).getDate() + " : LONG :");
				}
				else if (parabolicSAR[i + 1] > allQuotes.get(i + 1).getClose() && diPlus[i + 1] < diNegative[i + 1])
				{
					if (buy == false && sell == false)
					{
						sell = true;
						sellPrice = allQuotes.get(i).getOpen();
						shares = money / sellPrice;
						
						//System.out.println("SELL SHORT");
					}
					
					if (buy == true && sell == false)
					{
						buy = false;
						sell = true;
						
						sellPrice = allQuotes.get(i).getOpen();
						
						diff = sellPrice - buyPrice;
						money += shares * diff;
						
						//System.out.println("SELL LONG: " + money);
						//System.out.println("SELL SHORT");
					}
					
					//System.out.println(allQuotes.get(i).getDate() + " : SHORT :");
				}
				else
				{
					if (buy == true)
					{
						buy = false;
						
						sellPrice = allQuotes.get(i).getOpen();
						diff = sellPrice - buyPrice;
						money += shares * diff;
						shares = 0;
						
						//System.out.println("SELL LONG: " + money);
					}
					else if (sell == true)
					{
						sell = false;
						
						buyPrice = allQuotes.get(i).getOpen();
						diff = sellPrice - buyPrice;
						money += shares * diff;
						shares = 0;
						
						//System.out.println("BUY TO COVER: " + money);
					}
					
					//System.out.println(allQuotes.get(i).getDate() + " : NOTHING :");
				}
			}
			else
			{
				if (buy == true)
				{
					buy = false;
					
					sellPrice = allQuotes.get(i).getOpen();
					diff = sellPrice - buyPrice;
					money += shares * diff;
					shares = 0;
					
					//System.out.println("SELL LONG: " + money);
				}
				else if (sell == true)
				{
					sell = false;
					
					buyPrice = allQuotes.get(i).getOpen();
					diff = sellPrice - buyPrice;
					money += shares * diff;
					shares = 0;
					
					//System.out.println("BUY TO COVER: " + money);
				}
				
				//System.out.println(allQuotes.get(i).getDate() + " : NOTHING :");
			}
		}
		
		return money;
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
	
	public double[] findIndicatorEMA(ArrayList<StockQuotes> allQuotes, double[] indicator, int averagePeriod)
	{

		double[] exponentialMovingAverage = new double[indicator.length];
		
		double sum = 0;
		
		for(int i = indicator.length - averagePeriod; i < indicator.length; i++)
		{
			sum += indicator[i];
		}
		
		exponentialMovingAverage[indicator.length - averagePeriod] = sum / averagePeriod;
		
		for (int i = indicator.length - averagePeriod - 1; i >= 0; i--)
		{						
			exponentialMovingAverage[i] = 1.0 / averagePeriod * indicator[i] +  (averagePeriod - 1.0) / averagePeriod * exponentialMovingAverage[i + 1];
			
			//System.out.println(allQuotes.get(i).getDate() + " : EMA : " + exponentialMovingAverage[i]);
		}
		
		return exponentialMovingAverage;
	}
	
	public double[] findExponentialMovingAverage(ArrayList<StockQuotes> allQuotes, int averagePeriod)
	{
		double[] exponentialMovingAverage = new double[allQuotes.size()];
		
		double sum = 0;
		
		for(int i = allQuotes.size() - averagePeriod; i < allQuotes.size(); i++)
		{
			sum += allQuotes.get(i).getClose();
		}
		
		exponentialMovingAverage[allQuotes.size() - averagePeriod] = sum / averagePeriod;
		
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
	
	public double[] findParabolicSAR(ArrayList<StockQuotes> allQuotes)
	{
		double[] parabolicSAR = new double[allQuotes.size()];
		parabolicSAR[allQuotes.size() - 1] = 0;
		
		double extremePoint;
		
		double alpha = .02;
		boolean newTrade = false;
		boolean longTrade = true;
		
		parabolicSAR[allQuotes.size() - 2] = allQuotes.get(allQuotes.size() - 1).getLow();
		extremePoint = Math.max(allQuotes.get(allQuotes.size() - 2).getHigh(),
								allQuotes.get(allQuotes.size() - 1).getHigh());
		
		for (int i = allQuotes.size() - 3; i >= 0; i--)
		{		
			double alphaDifference = alpha * Math.abs(extremePoint - parabolicSAR[i + 1]);
		
			if (longTrade)
			{
				parabolicSAR[i] = parabolicSAR[i +  1] + alphaDifference;
			}
			else
			{
				parabolicSAR[i] = parabolicSAR[i + 1] - alphaDifference;
			}
				
			if (longTrade)
			{
				if (parabolicSAR[i] > allQuotes.get(i).getLow())
				{
					longTrade = false;
					newTrade = true;
					}
			}
			else
			{
				if (parabolicSAR[i] < allQuotes.get(i).getHigh())
				{
					longTrade = true;
					newTrade = true;
				}
			}
			double lowRange;
			double highRange;
			
			if (longTrade)
			{
				if (allQuotes.get(i).getLow() < allQuotes.get(i + 1).getLow())
				{
					lowRange = allQuotes.get(i).getLow();
				}
				else
				{
					lowRange = allQuotes.get(i + 1).getLow();
				}
				
				if (parabolicSAR[i] <= lowRange)
				{
					parabolicSAR[i] = Math.min(lowRange, parabolicSAR[i]);
				}
			}
			else
			{
				if (allQuotes.get(i).getHigh() > allQuotes.get(i + 1).getHigh())
				{
					highRange = allQuotes.get(i).getHigh();
				}
				else
				{
					highRange = allQuotes.get(i + 1).getHigh();
				}
				
				if (parabolicSAR[i] >= highRange)
				{
					parabolicSAR[i] = Math.max(highRange, parabolicSAR[i]);
				}
			}
			
			if (newTrade)
			{
				newTrade = false;
				alpha = .02;
				parabolicSAR[i] = extremePoint;
				
				if (longTrade)
				{
					extremePoint = allQuotes.get(i).getHigh();
				}
				else
				{
					extremePoint = allQuotes.get(i).getLow();
				}
			}
			
			if (longTrade)
			{
				if (extremePoint < allQuotes.get(i).getHigh())
				{
					extremePoint = allQuotes.get(i).getHigh();
					
					if (alpha < .2)
					{
						alpha += .02;
					}	
				}
			}
			else
			{
				if (extremePoint > allQuotes.get(i).getLow())
				{
					extremePoint = allQuotes.get(i).getLow();
				
					if (alpha < .2)
					{
						alpha += .02;
					}
				}
			}
		}
				
		return parabolicSAR;
	}
	
	public double[] findDIPlus(double[][] DI)
	{
		double[] diPlus = new double[DI.length];
		
		for (int i = 0; i < diPlus.length; i++)
		{
			diPlus[i] = DI[i][0];
		}
		
		return diPlus;
	}
	
	public double[] findDINegative(double[][] DI)
	{
		double[] diNegative = new double[DI.length];
		
		for (int i = 0; i < diNegative.length; i++)
		{
			diNegative[i] = DI[i][1];
		}
		
		return diNegative;
	}
	
	public double[][] findDI(ArrayList<StockQuotes> allQuotes, int averagePeriod)
	{
		//DI Plus is first element, DI Negative is second element
		double[][] di = new double[allQuotes.size()][2];
		double[] dmPlus = new double[allQuotes.size()];
		double[] dmNegative = new double[allQuotes.size()];
		double[] trueRange = new double[allQuotes.size()];
		
		for (int i = allQuotes.size() - 2; i >= 0; i--)
		{
			double highDiff = allQuotes.get(i).getHigh() - allQuotes.get(i + 1).getHigh();
			double lowDiff = allQuotes.get(i + 1).getLow() - allQuotes.get(i).getLow();
			
			if (highDiff < 0 && lowDiff < 0)
			{
				dmPlus[i] = 0;
				dmNegative[i] = 0;
			}
			else if (highDiff > lowDiff)
			{
				dmPlus[i] = highDiff;
				dmNegative[i] = 0;
			}
			else
			{
				dmPlus[i] = 0;
				dmNegative[i] = lowDiff;
			}
		
			//System.out.println(allQuotes.get(i).getDate() + " : DI+ : " + dmPlus[i]);
			//System.out.println(allQuotes.get(i).getDate() + " : DI- : " + dmNegative[i]);
			
			double todayDiff = allQuotes.get(i).getHigh() - allQuotes.get(i).getLow();
			double highCloseDiff = Math.abs(allQuotes.get(i).getHigh() - allQuotes.get(i + 1).getClose());
			double lowCloseDiff = Math.abs(allQuotes.get(i).getLow() - allQuotes.get(i + 1).getClose());
			
			double maxValue;
			
			if (todayDiff > highCloseDiff)
			{
				maxValue = todayDiff;
			}
			else
			{
				maxValue = highCloseDiff;
			}
			
			if (lowCloseDiff > maxValue)
			{
				maxValue = lowCloseDiff;
			}
		
			trueRange[i] = maxValue;
			
			//System.out.println(allQuotes.get(i).getDate() + " : TRUE RANGE : " + trueRange[i]);
		}
		
		double[] dmPlusPeriod = brain.findIndicatorEMA(allQuotes, dmPlus, averagePeriod);
		double[] dmNegativePeriod = brain.findIndicatorEMA(allQuotes, dmNegative, averagePeriod);
		double[] trueRangePeriod = brain.findIndicatorEMA(allQuotes, trueRange, averagePeriod);
		
		
		
		for (int i = 0; i < di.length - averagePeriod; i++)
		{
			di[i][0] = 100 * dmPlusPeriod[i] / trueRangePeriod[i];
			di[i][1] = 100 * dmNegativePeriod[i] / trueRangePeriod[i];
		}
		
		return di;
	}

	public double[] findADX(ArrayList<StockQuotes> allQuotes, int averagePeriod)
	{
		double[] adx = new double[allQuotes.size()];
		double[] directionalIndex = new double[allQuotes.size()];
		
		double[][] DI = brain.findDI(allQuotes, averagePeriod);
		double[] diPlus = brain.findDIPlus(DI);
		double[] diNegative = brain.findDINegative(DI);
		
		for (int i = 0; i < directionalIndex.length - averagePeriod; i++)
		{
			directionalIndex[i] = 100 * Math.abs(diPlus[i] - diNegative[i]) / (diPlus[i] + diNegative[i]);
			
			//System.out.println(allQuotes.get(i).getDate() + ": DI PLUS : " + diPlus[i]);
			//System.out.println(allQuotes.get(i).getDate() + ": DI NEG : " + diNegative[i]);
		}
		
		double sum = 0;
		
		for(int i = directionalIndex.length - averagePeriod * 2; i < directionalIndex.length - averagePeriod; i++)
		{
			sum += directionalIndex[i];
		}
		
		adx[directionalIndex.length - averagePeriod * 2] = sum / averagePeriod;
		
		for (int i = directionalIndex.length - averagePeriod * 2 - 1; i >= 0; i--)
		{						
			adx[i] = 1.0 / averagePeriod * directionalIndex[i] +  (averagePeriod - 1.0) / averagePeriod * adx[i + 1];
			
			//System.out.println(allQuotes.get(i).getDate() + " : ADX : " + adx[i]);
		}
		
		return adx;
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
