import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;


class HighScoreObject {
	int score;
	String name;
}


public class HighScoreArrayObject {
	ArrayList<HighScoreObject> list = new ArrayList<HighScoreObject>();
	
	public void add(String name, int score)
	{
		HighScoreObject obj = new HighScoreObject();
		obj.name = name;
		obj.score = score;
		
		list.add(obj);
		sort();
		if (list.size() == 10) list.remove(10);
	}
	
	public String getHighScoreString()
	{
		sort();
		String hsString = "";
		for (int i=0; i <list.size(); i++)
		{
			hsString += list.get(i).name; 

			for (int j = 0 ; j < 5 - list.get(i).name.length() ; j++) hsString += "_";
			hsString += "          " +  list.get(i).score + "\n";
		}
		return hsString;
	}
	
	private void sort()
	{
		for (int i = 0 ; i < list.size() ; i++)
		{
			for (int j = 0 ; j < i ; j++)
			{
				if (list.get(i).score > list.get(j).score)
				{
					int tempScore = list.get(j).score;
					list.get(j).score = list.get(i).score;
					list.get(i).score = tempScore;
					
					String tempName = list.get(j).name;
					list.get(j).name = list.get(i).name;
					list.get(i).name = tempName;
				}
			}	
		}
	}
	
	public void writeHighScore(int num, int playerNum)
	{
		//Find the index to place
		int i = 0;
		for (i = 0; i < list.size() ; i++)
		{
			if (num > list.get(i).score) break;
		}
		
		
		
		if (i < 10)
		{
			for (int j = i ; j < list.size() ; j++)
			{
				//Shift everything down
				if (j+1 == list.size()) break;
				
				String tempString = list.get(j + 1).name;
				int tempScore = list.get(j+1).score;
				
				list.get(j + 1).name = list.get(j).name;
				list.get(j + 1).score = list.get(j).score;
				
				list.get(j).name = tempString;
				list.get(j).score = tempScore;
			}
			
			String userInput = JOptionPane.showInputDialog(null, "Player " + playerNum + " scored a highscore!\n" + "Enter 5 Letter Name:", "Name", 1);
			
			if (userInput == null) userInput = "#" + playerNum;
			
			userInput = userInput.replaceAll("\\s", "");
			
			list.get(i).name = userInput.substring(0, (userInput.length() > 5) ? 5 : userInput.length());
			list.get(i).score = num;
			
			writeToFile();
		}
	}	
	
	public void showScoreBoard()
	{
		JOptionPane.showMessageDialog(null, getHighScoreString() ,"High Score", 1);
	}
	
	private void writeToFile()
	{
		try
		{
			//File fileObject = new File(Asteroids.HIGHSCORE_FILENAME);
			FileWriter fileWriter = new FileWriter(Asteroids.HIGHSCORE_FILENAME);
			//BufferedWriter writer = new BufferedWriter(fileWriter);
			
			for (int i = 0 ; i < list.size() ; i++)
			{
				fileWriter.write(list.get(i).name + " " + list.get(i).score + "\n");
				//writer.write(list.get(i).name + " " + list.get(i).score + "\n");
			}
					
			
			fileWriter.close(); 			
		}
		catch (Exception e) {
			System.out.println("Exception in write");
		}
	}
	
	public void reset()
	{
		for (int i = 0 ; i < list.size() ; i++)
		{
			if (i==10) break;
			list.get(i).name = "p"+i;
			list.get(i).score = 0;
			//writer.write(list.get(i).name + " " + list.get(i).score + "\n");
			
		}
		writeToFile();
	}
}
