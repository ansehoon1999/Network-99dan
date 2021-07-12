package identification.info;

import java.io.*;
import java.util.*;

public class GameRecord
{
	/**
	 * private static Fields
	 */
	private static long lastId = 0;
	
	/**
	 * public static Methods
	 */
	public static GameRecord fromFile(File file)
	{
		GameRecord rec = new GameRecord();
		FileInputStream in;
		try
		{
			in = new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
		Scanner scanner = new Scanner(in);
		
		rec.id = scanner.nextLong();
		rec.winnerId = scanner.nextLine();
		rec.loserId = scanner.nextLine();
		
		lastId = Math.max(lastId,  rec.id);
		
		try
		{
			in.close();
		}
		catch (IOException e)
		{
		}
		
		return rec;
	}
	
	/**
	 * private Fields
	 */
	private Long id = null;
	private String winnerId = null;
	private String loserId = null;
	
	/**
	 * Constructor
	 */
	private GameRecord()
	{
		// NOTHING
	}
	public GameRecord(String winnerId, String loserId)
	{
		this.winnerId = winnerId;
		this.loserId = loserId;
		
		id = ++lastId;
	}
	
	/**
	 * Getter / Setter
	 */
	public long getId()
	{
		return id.longValue();
	}
	
	public String getWinnerId()
	{
		return winnerId;
	}
	
	public String getLoserId()
	{
		return loserId;
	}
	
	/**
	 * public Methods
	 */
	public void write(OutputStream out)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(id + '\n');
		builder.append(winnerId + '\n');
		builder.append(loserId + '\n');
		
		PrintStream writer = new PrintStream(out);
		writer.print(builder.toString());
	}
}