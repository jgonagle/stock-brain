import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class StockPlot 
{
	private StockQuotes quote;
	private double prevClose;
	private int screenWidth;
	private int screenHeight;
	private double minPrice;
	private double maxPrice;
	private int numDays;
	private int dayNum;
	private double dayWidth;
	private double priceHeight;
	
	
	public StockPlot(StockQuotes q, double pC, int sW, int sH, double miP, double maP, int nD, int dN)
	{
		quote = q;
		prevClose = pC;
		screenWidth = sW;
		screenHeight = sH * 4 / 5;
		minPrice = miP;
		maxPrice = maP;
		numDays = nD;
		dayNum = dN;
		dayWidth = screenWidth * 1.0 / numDays;
		priceHeight = screenHeight / (maxPrice - minPrice);
	}
	
	public double priceLevel(Double price)
	{
		double priceLevel = screenHeight * 1.0 / 10 + (maxPrice - price) * priceHeight;
		
		return priceLevel;
	}
	
	public void drawOHLC(Graphics2D g2)
	{
		if (prevClose > quote.getClose())
		{
			g2.setColor(Color.RED);
		}
		else
		{
			g2.setColor(Color.GREEN);
		}
		
		double dayBegin = dayWidth * dayNum;
		double dayEnd = dayWidth * (dayNum + 1);
		double dayMid = (dayBegin + dayEnd) / 2;
		double priceOpen = this.priceLevel(quote.getOpen());
		double priceHigh = this.priceLevel(quote.getHigh());
		double priceLow = this.priceLevel(quote.getLow());
		double priceClose = this.priceLevel(quote.getClose());
		
		Point2D.Double r1 = new Point2D.Double(dayBegin + 1.0 / 5 * (dayMid - dayBegin), priceOpen);
		Point2D.Double r2 = new Point2D.Double(dayMid, priceOpen);
		
		Point2D.Double r3 = new Point2D.Double(dayMid, priceHigh);
		Point2D.Double r4 = new Point2D.Double(dayMid, priceLow);
		
		Point2D.Double r5 = new Point2D.Double(dayMid, priceClose);
		Point2D.Double r6 = new Point2D.Double(dayEnd - 1.0 / 5 * (dayEnd - dayMid), priceClose);
		
		Line2D.Double openLine = new Line2D.Double(r1, r2);
		Line2D.Double rangeLine = new Line2D.Double(r3, r4);
		Line2D.Double closeLine = new Line2D.Double(r5, r6);
		
		g2.draw(openLine);
		g2.draw(rangeLine);
		g2.draw(closeLine);		
	}
}
