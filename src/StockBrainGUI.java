import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class StockBrainGUI extends JPanel implements ActionListener
{
	JTextField stockField;
	JTextField startField;
	JTextField endField;
	static String stock;
	static String start;
	static String end;
	static boolean next;
	static boolean done;
	static JCheckBox smaCheck;
	static JCheckBox emaCheck;
	
	public StockBrainGUI()
	{
		super(new GridBagLayout());
		
		next = false;
		done = false;
		
		String stockName = "  Stock Name: ";
		String startTime = "  Start (MMDDYYYY): ";
		String endTime = "  End (MMDDYYYY): ";
		
		JLabel stockLabel = new JLabel(stockName);
		JLabel startLabel = new JLabel(startTime);
		JLabel endLabel = new JLabel(endTime);
		
		stockField = new JTextField(10);
		
		startField = new JTextField(10);
		
		endField = new JTextField(10);
		
		JButton nextButton = new JButton("Next");
		nextButton.addActionListener(this);
		
		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(this);
				
		GridBagConstraints c = new GridBagConstraints();
		
		add(stockLabel, c);
		add(stockField, c);
		add(startLabel, c);
		add(startField, c);
		add(endLabel, c);
		add(endField, c);
		add(nextButton, c);
		add(doneButton, c);
    }
	
	public void actionPerformed(ActionEvent evt) 
	{
		if (evt.getActionCommand().equals("Next"))
		{
			stock = stockField.getText();
			start = startField.getText();
			end = endField.getText();
			next = true;
		}
        
        if (evt.getActionCommand().equals("Done"))
        {
        	done = true;
        }
    }

	private static void createAndShowGUI() 
	{
        JFrame frame = new JFrame("Stock Brain");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new StockBrainGUI());

        frame.pack();
        frame.setVisible(true);
    }
	
	public void doStockBrainGUI()
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() 
		{
            public void run() 
            {
                createAndShowGUI();
            }
		});
	}
	
	public String getStock()
	{
		return stock;
	}
	
	public String getStart()
	{
		return start;
	}
	
	public String getEnd()
	{
		return end;
	}
	
	public boolean getNext()
	{
		return next;
	}
	
	public boolean getDone()
	{
		return done;
	}
	
	public void switchNext()
	{
		next = !next;
	}
}
