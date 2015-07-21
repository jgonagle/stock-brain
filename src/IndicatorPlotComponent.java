import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

@SuppressWarnings("serial")
public class IndicatorPlotComponent extends JComponent
{
	private double[] indicator;
	private int screenWidth;
	private int screenHeight;
	private double minPrice;
	private double maxPrice;
	private Color color;
	
	public IndicatorPlotComponent(double[] i, int sW, int sH, double miP, double maP, Color c)
	{
		indicator = i;
		screenWidth = sW;
		screenHeight = sH;
		minPrice = miP;
		maxPrice = maP;
		color = c;
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		IndicatorPlot plot =  new IndicatorPlot(indicator, screenWidth, screenHeight, minPrice, maxPrice, color);
				
		plot.drawIndicator(g2);
	}
}
