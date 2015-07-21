import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class IndicatorPlot 
{
	private double[] indicator;
	private int screenWidth;
	private int screenHeight;
	private double minPrice;
	private double maxPrice;
	private int numDays;
	private double dayWidth;
	private double priceHeight;
	private Color color;
	
	
	public IndicatorPlot(double[] i, int sW, int sH, double miP, double maP, Color c)
	{
		indicator = i;
		screenWidth = sW;
		screenHeight = sH * 4 / 5;
		minPrice = miP;
		maxPrice = maP;
		numDays = indicator.length;
		dayWidth = screenWidth * 1.0 / numDays;
		priceHeight = screenHeight / (maxPrice - minPrice);
		color = c;
	}

	public double priceLevel(Double price)
	{
		double priceLevel = screenHeight * 1.0 / 10 + (maxPrice - price) * priceHeight;
		
		return priceLevel;
	}
	
	public void drawIndicator(Graphics2D g2)
	{
		g2.setColor(color);
		
		for (int i = numDays - 2; i >= 0; i--)
		{		
			if (indicator[i + 1] != 0)
			{
				double dayBegin = dayWidth * (numDays - i - 1);
				double dayEnd = dayWidth * (numDays - i);
				double dayMidCur = (dayBegin + dayEnd) / 2;
				double dayMidPrev = dayMidCur - dayWidth;
				
				double indPrev = this.priceLevel(indicator[i + 1]);
				double indCur = this.priceLevel(indicator[i]);
		
				Point2D.Double r1 = new Point2D.Double(dayMidPrev, indPrev);
				Point2D.Double r2 = new Point2D.Double(dayMidCur, indCur);
		
				Line2D.Double indLine = new Line2D.Double(r1, r2);
			
				g2.draw(indLine);
			}
		}
	}
}
