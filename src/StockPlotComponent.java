import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

@SuppressWarnings("serial")
public class StockPlotComponent extends JComponent
{
	private StockQuotes quote;
	private double prevClose;
	private int screenWidth;
	private int screenHeight;
	private double minPrice;
	private double maxPrice;
	private int numDays;
	private int dayNum;
	
	public StockPlotComponent(StockQuotes q, double pC, int sW, int sH, double miP, double maP, int nD, int dN)
	{
		quote = q;
		prevClose = pC;
		screenWidth = sW;
		screenHeight = sH;
		minPrice = miP;
		maxPrice = maP;
		numDays = nD;
		dayNum = dN;
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		StockPlot plot =  new StockPlot(quote, prevClose, screenWidth, screenHeight, minPrice, maxPrice, numDays, dayNum);
				
		plot.drawOHLC(g2);
	}
}
