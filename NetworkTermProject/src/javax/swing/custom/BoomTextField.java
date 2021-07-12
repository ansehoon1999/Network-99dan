package javax.swing.custom;

import java.awt.*;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.text.Document;

public class BoomTextField extends JTextField
{
	private ExecutorService thread = null;
	private long time = 0;
	private int duration = 0;
	private Color actualBackground = Color.white;
	private Color actualForeground = Color.black;
	private Color boomBackground = null;
	private Color boomForeground = null;

	public BoomTextField()
	{
		super();
		actualBackground = this.getBackground();
		actualForeground = this.getForeground();
	}

	public BoomTextField(int columns)
	{
		super(columns);
		actualBackground = this.getBackground();
		actualForeground = this.getForeground();
	}

	public BoomTextField(String text)
	{
		super(text);
		actualBackground = this.getBackground();
		actualForeground = this.getForeground();
	}

	public BoomTextField(String text, int columns)
	{
		super(text, columns);
		actualBackground = this.getBackground();
		actualForeground = this.getForeground();
	}

	public BoomTextField(Document doc, String text, int columns)
	{
		super(doc, text, columns);
		actualBackground = this.getBackground();
		actualForeground = this.getForeground();
	}
	
	public void setActualBackground(Color color)
	{
		actualBackground = color;
	}
	
	public Color getActualBackground()
	{
		return actualBackground;
	}
	
	public void setActualForeground(Color color)
	{
		actualForeground = color;
	}
	
	public Color getActualForeground()
	{
		return actualForeground;
	}
	
	public void boom(int duration, Color background, Color foreground)
	{
		boomBackground = background;
		boomForeground = foreground;
		this.duration = duration;
		time = System.currentTimeMillis();
		
		startThread();
	}

	private void startThread()
	{
		if (thread != null)
		{
			thread.shutdownNow();
			thread = null;
		}

		thread = Executors.newSingleThreadExecutor();
		thread.execute(new Runnable()
		{
			@Override
			public void run()
			{
				while (System.currentTimeMillis() - time <= duration)
				{
					repaint();

					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
					}
				}
			}
		});
	}

	@Override
	public void paint(Graphics g)
	{
		Color bg = actualBackground;
		Color fg = actualForeground;
		
		if (thread != null)
		{	
			long elapsed = System.currentTimeMillis() - time;
			
			if (elapsed > duration)
			{
				thread.shutdownNow();
				thread = null;
			}
			else
			{
				double _ratio = (double)elapsed / duration;
				double ratio = 1 - _ratio;
				bg = new Color(
						(int)(boomBackground.getRed() * ratio + actualBackground.getRed() * _ratio),
						(int)(boomBackground.getGreen() * ratio + actualBackground.getGreen() * _ratio),
						(int)(boomBackground.getBlue() * ratio + actualBackground.getBlue() * _ratio));
				fg = new Color(
						(int)(boomForeground.getRed() * ratio + actualForeground.getRed() * _ratio),
						(int)(boomForeground.getGreen() * ratio + actualForeground.getGreen() * _ratio),
						(int)(boomForeground.getBlue() * ratio + actualForeground.getBlue() * _ratio));
			}
		}
		
		super.setBackground(bg);
		super.setForeground(fg);
		
		super.paint(g);
	}
}
