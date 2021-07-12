package javax.swing.custom;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.border.*;

import generic.MouseClickListener;

public class FlatButton extends JLabel
{
	private final int AnimationDuration = 100;

	private Boolean gotCursor = false;
	private double progress = 0;
	private long time = 0;
	
	private int maxLineHeight = -1;

	public FlatButton()
	{
		this.setBorder(new EmptyBorder(5, 15, 5, 15));
		this.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				synchronized (gotCursor)
				{
					gotCursor = true;
					time = System.currentTimeMillis();
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				synchronized (gotCursor)
				{
					gotCursor = false;
					time = System.currentTimeMillis();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					synchronized (gotCursor)
					{
						gotCursor = false;
						time = System.currentTimeMillis();
					}
				}
			}
		});

		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		threadPool.execute(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					synchronized (gotCursor)
					{
						if (gotCursor)
						{
							if (progress < 1)
							{
								progress = (double) (System.currentTimeMillis() - time) / AnimationDuration;
								if (progress > 1)
								{
									progress = 1;
								}

								repaint();
							}
						}
						else
						{
							if (progress > 0)
							{
								progress = 1 - (double) (System.currentTimeMillis() - time) / AnimationDuration;
								if (progress < 0)
								{
									progress = 0;
								}

								repaint();
							}
						}
					}

					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public void setMaxLineHeight(int maxHeight)
	{
		maxLineHeight = maxHeight;
		repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		Rectangle bounds = g.getClipBounds();
		double verticalMarginRatio = 0.4;
		int height = maxLineHeight != -1 ? maxLineHeight : bounds.height;
		height = (int) (height * (verticalMarginRatio * progress + 1 - verticalMarginRatio));

		g.setColor(this.getForeground());
		g.fillRect(0, (bounds.height - height) / 2, 3, height);

		super.paint(g);
	}
}
