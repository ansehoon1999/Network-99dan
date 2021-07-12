package javax.swing.custom;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class SmoothCaret extends DefaultCaret
{
	private String mark = "|";
	private long time = 0;
	private int blinkRate = 0;

	public SmoothCaret(int blinkRate)
	{
		setBlinkRate(blinkRate);
		time = System.currentTimeMillis();
	}
	public SmoothCaret()
	{
		this(1000);
	}

	@Override
	public void setBlinkRate(int rate)
	{
		blinkRate = rate;
		super.setBlinkRate(rate);
	}

	@Override
	public void paint(Graphics g)
	{
		JTextComponent comp = getComponent();
		if (comp == null)
		{
			return;
		}

		int dot = getDot();
		Rectangle r = null;
		try
		{
			r = comp.modelToView(dot);
		}
		catch (BadLocationException e)
		{
			return;
		}
		if (r == null)
		{
			return;
		}

		if ((x != r.x) || (y != r.y))
		{
			repaint(); // erase previous location of caret
			damage(r);
		}
		
		if (isActive())
		{
			FontMetrics fm = comp.getFontMetrics(comp.getFont());

			long tm = System.currentTimeMillis() - time;
			if (tm > blinkRate)
			{
				time = System.currentTimeMillis();
				tm = blinkRate;
			}

			Color baseColor = comp.getCaretColor();
			g.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), getAlpha(tm)));
			g.drawString(mark, x + fm.getDescent() - 1, fm.getAscent());
		}
		
		try
		{
			Thread.sleep(1);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private int getAlpha(long time)
	{
		double velocity = blinkRate / 3.0;

		if (time < velocity)
		{
			return (int) (time * 255 / velocity);
		}
		else if (time <= velocity * 2)
		{
			return 255;
		}
		else
		{
			return (int) ((blinkRate - time) * 255 / velocity);
		}
	}
}
