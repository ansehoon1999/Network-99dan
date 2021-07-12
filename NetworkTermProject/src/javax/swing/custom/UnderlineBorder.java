package javax.swing.custom;

import java.awt.*;
import java.util.concurrent.*;

import javax.swing.border.*;

public class UnderlineBorder extends AbstractBorder
{
	private Color prev = null;
	private Color color = null;
	private Component component = null;

	private int duration = 0;
	private long time = 0;
	private ExecutorService thread = null;

	public UnderlineBorder(Color color)
	{
		super();

		this.color = color;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public void changeColor(int duration, Color color)
	{
		prev = this.color;
		this.duration = duration;
		this.color = color;
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
					if (thread != null)
					{
						paintBorder(component, component.getGraphics(), 0, 0, component.getWidth(), component.getHeight());
					}

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
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		component = c;
		Color currentColor = color;

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
				double ratio = (double) elapsed / duration;
				currentColor = mixColor(color, prev, ratio);
			}
		}

		g.setColor(currentColor);
		g.drawLine(0, height - 1, width, height - 1);
	}

	private static Color mixColor(Color c1, Color c2, double ratio)
	{
		double _ratio = 1 - ratio;
		int r = (int) (c1.getRed() * ratio + c2.getRed() * _ratio);
		int g = (int) (c1.getGreen() * ratio + c2.getGreen() * _ratio);
		int b = (int) (c1.getBlue() * ratio + c2.getBlue() * _ratio);

		return new Color(r, g, b);
	}
}
