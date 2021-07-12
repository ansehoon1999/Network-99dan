package javax.swing.custom;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class ShowPasswordCheckBox extends JLabel
{
	private static ImageIcon showImage = new ImageIcon(new ImageIcon("images/show.png").getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
	private static ImageIcon hideImage = new ImageIcon(new ImageIcon("images/hide.png").getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
	private boolean checked = false;

	private LinkedList<ChangeListener> changeListeners = new LinkedList<ChangeListener>();

	public ShowPasswordCheckBox()
	{
		super();

		addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					setChecked(!checked);
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				// NOTHING
			}

			@Override
			public void mouseReleased(MouseEvent e)
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

		});

		setIcon(hideImage);
	}

	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	public void removeChangeListener(ChangeListener listener)
	{
		int index = changeListeners.indexOf(listener);
		if (index != -1)
		{
			changeListeners.remove(index);
		}
	}

	public boolean getChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		if (this.checked != checked)
		{
			this.checked = checked;
			setIcon(checked ? showImage : hideImage);

			ListIterator<ChangeListener> iterator = changeListeners.listIterator();
			while (iterator.hasNext())
			{
				ChangeListener listener = iterator.next();
				listener.stateChanged(new ChangeEvent(checked));
			}
		}
	}
}
