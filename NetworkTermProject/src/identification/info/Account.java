package identification.info;

import java.io.*;
import java.security.*;
import java.util.*;

public class Account
{
	/**
	 * public static Methods
	 */
	public static Account fromFile(File file)
	{
		FileInputStream stream;
		Scanner in;
		try
		{
			stream = new FileInputStream(file);
			in = new Scanner(stream);
		}
		catch (FileNotFoundException e)
		{
			return null;
		}

		Account accnt = new Account();
		accnt.id = in.next();
		accnt.password = in.next();
		accnt.firstName = in.next();
		accnt.lastName = in.next();
		accnt.lastSignInTime = in.nextLong();
		accnt.winCount = in.nextInt();
		accnt.loseCount = in.nextInt();

		try
		{
			stream.close();
			in.close();
		}
		catch (IOException e)
		{
		}

		return accnt;
	}

	/**
	 * private Fields
	 */
	private String id = null;
	private String password = null;
	private String firstName = null;
	private String lastName = null;
	private Long lastSignInTime = null;
	private Integer winCount = null;
	private Integer loseCount = null;

	/**
	 * Constructor
	 */
	private Account()
	{
		// NOTHING
	}

	public Account(String id, String password, String firstName, String lastName)
	{
		this.id = id;
		this.password = encryptPassword(password);
		this.firstName = firstName;
		this.lastName = lastName;
		lastSignInTime = Long.valueOf(-1);
		winCount = 0;
		loseCount = 0;
	}

	/**
	 * Getter / Setter
	 */
	public String getId()
	{
		return id;
	}

	public boolean matchPassword(String password)
	{
		String enc = encryptPassword(password);
		return enc.equalsIgnoreCase(this.password);
	}

	public void resetPassword(String password)
	{
		this.password = encryptPassword(password);
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getFullName()
	{
		return firstName + ' ' + lastName;
	}

	public Date getLastSignInTime()
	{
		return new Date(lastSignInTime);
	}
	
	public long getLastSignInTimeAsLong()
	{
		return lastSignInTime;
	}

	public void updateSignInTime()
	{
		lastSignInTime = System.currentTimeMillis();
	}
	
	public int getWin()
	{
		return winCount;
	}
	
	public void setWin(int win)
	{
		winCount = win;
	}
	
	public void win()
	{
		winCount++;
	}
	
	public int getLose()
	{
		return loseCount;
	}
	
	public void setLose(int lose)
	{
		loseCount = lose;
	}
	
	public void lose()
	{
		loseCount++;
	}

	/**
	 * public Methods
	 */
	public void write(OutputStream out)
	{
		StringBuilder info = new StringBuilder();
		info.append(id + '\n');
		info.append(password + '\n');
		info.append(firstName + '\n');
		info.append(lastName + '\n');
		info.append(lastSignInTime + "\n");
		info.append(winCount + "\n");
		info.append(loseCount + "\n");

		PrintStream writer = new PrintStream(out);
		writer.println(info.toString());
	}

	/**
	 * private Methods
	 */
	private static String encryptPassword(String password)
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());

			StringBuilder sb = new StringBuilder();
			for (final byte bt : md.digest())
				sb.append(String.format("%02x", bt & 0xff));

			return sb.toString().toUpperCase();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
