package javax.swing.custom;

import java.awt.*;

import javax.swing.border.*;

public class RoundedBorder extends AbstractBorder
{
	private Color backgroundColor = new Color(0, 0, 0, 0);
	private Color color = Color.BLACK;
	private int rounding = 0;
	private float width = 1F;
	private boolean usePadding = true;

	public RoundedBorder(Color color, Color background, float width, int rounding)
	{
		setBackground(background);
		setColor(color);
		setWidth(width);
		setRounding(rounding);
	}

	public Color getBackground()
	{
		return backgroundColor;
	}

	public void setBackground(Color bg)
	{
		if (backgroundColor != bg)
		{
			backgroundColor = bg;
		}
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		if (color != null)
		{
			this.color = color;
		}
	}

	public int getRounding()
	{
		return rounding;
	}

	public int getActualHalfRounding(Component c)
	{
		return Math.min(Math.min(c.getWidth(), c.getHeight()) / 2, rounding);
	}

	public int getActualRounding(Component c)
	{
		return getActualHalfRounding(c) * 2;
	}

	public void setRounding(int rounding)
	{
		if (rounding != this.rounding)
		{
			this.rounding = rounding > 0 ? rounding : 0;
		}
	}

	public float getWidth()
	{
		return width;
	}

	public void setWidth(float width)
	{
		if (width != this.width)
		{
			this.width = width > 0 ? width : 0;
		}
	}

	public boolean getUsePadding()
	{
		return usePadding;
	}

	public void setUsePadding(boolean usePadding)
	{
		this.usePadding = usePadding;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		if (width > 0)
		{
			Graphics2D graphics = (Graphics2D) g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			final int halfRounding = getActualHalfRounding(c);
			final int fullRounding = halfRounding * 2;
			final int halfWidth = (int) this.width / 2;

			graphics.setColor(backgroundColor);
			graphics.fillRoundRect(0, 0, width - 1, height - 1, fullRounding, fullRounding);

			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(this.width));
			graphics.drawRoundRect(0, 0, width - 1, height - 1, fullRounding, fullRounding);
		}
	}

	@Override
	public Insets getBorderInsets(Component c)
	{
		final int actualRounding = getActualHalfRounding(c);
		return getBorderInsets(c, new Insets(actualRounding, actualRounding, actualRounding, actualRounding));
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets)
	{
		if (usePadding)
		{
			final int actualRounding = getActualHalfRounding(c);
			insets.left = insets.top = insets.right = insets.bottom = actualRounding;
			return insets;
		}
		else
		{
			return new Insets(0, 0, 0, 0);
		}
	}
}
