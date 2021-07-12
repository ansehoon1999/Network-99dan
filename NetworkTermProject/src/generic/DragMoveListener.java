package generic;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

public final class DragMoveListener implements MouseListener, MouseMotionListener
{
	private JFrame frame = null;
	private boolean move = false;
	private Point startCurPos = new Point(0, 0);
	private Point startPos = new Point(0, 0);
	private int buttonId;
	
	public DragMoveListener(JFrame frame, int buttonId)
	{
		this.frame = frame;
		this.buttonId = buttonId;
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == buttonId)
		{
			startCurPos = e.getLocationOnScreen();
			startPos = frame.getLocation();
			move = true;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (move)
		{
			Point curPos = e.getLocationOnScreen();
			frame.setLocation(startPos.x + curPos.x - startCurPos.x, startPos.y + curPos.y - startCurPos.y);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (move)
		{
			if (e.getButton() == buttonId)
			{
				move = false;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// NOTHING
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// NOTHING
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// NOTHING
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// NOTHING
	}
}
