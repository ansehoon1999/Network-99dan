package contents.chat;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class ListItemStyle extends JLabel implements ListCellRenderer
{
	private static final Color EmptyColor = new Color(0, 0, 0, 0);
	private static final Color OnColor = new Color(0x88FFFF);
	private static final Color OffColor = new Color(0xFF88FF);
	private Color markColor = EmptyColor;
	private Color borderColor = EmptyColor;

	public ListItemStyle()
	{
		this.setOpaque(true);
		this.setBorder(new EmptyBorder(3, 10, 3, 0));
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		String strValue = value.toString();

		if (strValue.startsWith("AAA"))
		{
			markColor = EmptyColor;
		}
		else if (strValue.startsWith("CAN"))
		{
			markColor = OnColor;
		}
		else if (strValue.startsWith("NOT"))
		{
			markColor = OffColor;
		}
		setText(strValue.substring(3));

		if (isSelected)
		{
			borderColor = Color.WHITE;
		}
		else
		{
			borderColor = EmptyColor;
		}

		this.setBackground(list.getBackground());
		this.setForeground(list.getForeground());
		this.setFont(list.getFont());

		return this;
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		Graphics2D graphics = (Graphics2D) g;
		Rectangle bounds = g.getClipBounds();

		final int padding = (int) (bounds.height * 0.3);
		final int doublePadding = padding * 2;
		final int borderWidth = 4;

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (markColor.equals(EmptyColor))
		{
			graphics.setColor(this.getForeground());
			graphics.setFont(this.getFont().deriveFont(Font.ITALIC | Font.BOLD));

			final FontMetrics metrics = graphics.getFontMetrics();
			graphics.drawString("Me", (int)(bounds.width - metrics.stringWidth("Me") - padding), (int)(bounds.height - metrics.getHeight() / 2));
		}
		else
		{
			graphics.setColor(markColor);
			graphics.fillOval(bounds.width - bounds.height + padding, padding, bounds.height - doublePadding, bounds.height - doublePadding);
		}

		graphics.setColor(borderColor);
		graphics.setStroke(new BasicStroke(borderWidth));
		graphics.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
	}
}
