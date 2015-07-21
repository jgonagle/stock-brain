public class StockQuotes
{
	private String date;
	private double open;
	private double high;
	private double low;
	private double close;
	private int volume;
	
	public StockQuotes(String d, double o, double h, double l, double c, int v)
	{
		date = d;
		open = o;
		high = h;
		low = l;
		close = c;
		volume = v;
	}
	
	public String getDate()
	{
		return date;
	}
	
	public double getOpen()
	{
		return open;
	}
	
	public double getHigh()
	{
		return high;
	}
	
	public double getLow()
	{
		return low;
	}
	
	public double getClose()
	{
		return close;
	}	
	
	public double getVolume()
	{
		return volume;
	}	
	
	public void printQuotes()
	{
		System.out.println(date + " " + open + " " + high + " " + low + " " + close + " " + volume);
	}
}
