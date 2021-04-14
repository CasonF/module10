package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JTextField;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class Main extends Application {
	
	static Connection connection = null;
	static String databaseName = "";
	static String url = "jdbc:mysql://localhost:3306/" + databaseName;
	
	static String username = "root";
	static String password = "#g&C02232019";
	
	static TextField txt = new TextField();
	
	Stage window;
	Scene scene1, scene2;
	final int W = 900;
	final int H = 200;
	
	static String output;
	//static int outputLength;
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{	
		Word word = null;
		
		System.out.println("Connecting to database...");
		connection = DriverManager.getConnection(url, username, password);
		System.out.println("Connected!");
		
		//**Map<String, Integer> wordMap = new HashMap<>();
		
		try {
			String user = System.getProperty("user.home");
			//Simply using user + \\Desktop\\TheRaven.txt didn't work for me,
			//but if you have a Desktop folder in your main user folder,
			//feel free to remove the \\OneDrive part of this code
			File raven = new File(user + "\\OneDrive\\Desktop\\TheRaven.txt");
			Scanner reader = new Scanner(raven);
		
			while (reader.hasNextLine())
			{
				//This separates words that are separated by spaces. Also accounts for additional symbols between words.
				String words[] = reader.next().toLowerCase().split("[\\s*,.!?\"\'-+\\–\\—\\;]");
				for (String w : words)
				{
					word = new Word(w);
					if (checkWord(word, connection))
					{
						updateWord(word);
					}
					else
					{
						insertWord(word);
					}
					
					//Here I use the conditional operator to check if a word has
					//already been counted, then I add 1 to the count (number of
					//that word present in the text file).
					//**Integer q = wordMap.get(w);
					//**q = (q == null) ? 1 : ++q; //if q doesn't exist for this var, q = 1, otherwise ++q
					//**wordMap.put(w, q);
				}
			}
			reader.close();
		}
		catch(FileNotFoundException e)
		{
			//If you have TheRaven.txt in another directory, you can ignore this if you have edited the
			//file searcher to locate your .txt
			System.out.println("An error occurred. Please make sure TheRaven.txt is on your desktop.");
			e.printStackTrace();
		}
		
		//LinkedHashMap is used to maintain the order of the variables
		//**LinkedHashMap<String, Integer> freqMap = new LinkedHashMap<String, Integer>();
		//entryset().stream() takes the wordMap's entry set and streams its values
		//sorted(...reverseOrder()) sorts the streamed values from highest value to lowest
		//limit(20) will produce only 20 results from the data set/stream (the top 20 words)
		//forEachOrdered(...) puts the ordered elements into freqMap in order from highest value to lowest
		//**wordMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(20).forEachOrdered(x -> freqMap.put(x.getKey(), x.getValue()));
		
		//**output = "The 20 most common words in the text file, with their frequency (word=# in the text), are:\n" + freqMap; <- Used in original TheRaven app to display frequency map
		//outputLength = output.length();
		//System.out.println("Output length before freqMap is: " + outputLength); //The result was 91
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try
		{
			window = primaryStage;
			window.setTitle("TheRavenApp");
			
			//Start window
			
			Button button1 = new Button();
			Button button2 = new Button();
			button1.setText("Let's do this!");
			button2.setText("Go back!");
			Label t = new Label("This is the GUI for TheRavenProject.java.");
			Label prompt = new Label("Click the button below after inputting the word you would like to test the occurrences of in TheRaven.txt.");
			
			VBox layout1 = new VBox(20);
			layout1.getChildren().addAll(t, prompt, txt, button1);
			
			scene1 = new Scene(layout1, W, H);
			
			
			//Run window
			
			Label result = new Label();
			
			VBox layout2 = new VBox(20);
			layout2.getChildren().addAll(result, button2);
			
			scene2 = new Scene(layout2, W, H);
			
			//Implement
			
			primaryStage.setScene(scene1);
			primaryStage.show();
			
			
			//// Button logic ////
			
			button1.setOnAction(e ->
	        {
	        	String text = txt.getText();
	        	try {
					selectWord(text);
					result.setText(output);
					System.out.println(output);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            window.setScene(scene2);
	        });
			
			button2.setOnAction(e ->
			{
				window.setScene(scene1);
			});
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void insertWord(Word word) throws SQLException 
	{
		PreparedStatement ps = connection.prepareStatement("INSERT INTO `wordoccurrences`.`word` (`word`, `occurrence`) VALUES ('" + word.word + "', '" + 1 + "');");
		int status = ps.executeUpdate();
		
		if (status != 0)
		{
			//System.out.println(word.word + "'s record was inserted!"); //this is used to check that the information is reaching the database
		}
	}
	
	public static boolean checkWord(Word word, Connection connection) throws SQLException
	{
		boolean exists = false;
		Statement stmt = connection.createStatement();
		String sql = "SELECT word FROM wordoccurrences.word WHERE (`word` = '" + word.word + "');";
		ResultSet rs = stmt.executeQuery(sql);
		Word thisEntry = new Word("");
		while(rs.next())
		{
			thisEntry.word = rs.getString("word");
		}
		System.out.println("This is to test that checkWord is working. Word: " + word.word);
		if (word.word.equals(thisEntry.word))
		{
			exists = true;
			System.out.println(word.word + " already exists in this table. Return true.");
		}
		else
		{
			exists = false;
		}
		return exists;
	}
	
	public static void updateWord(Word word) throws SQLException
	{
		Statement stmt = connection.createStatement();
		String sql = "SELECT word, occurrence FROM wordoccurrences.word WHERE (`word` = '" + word + "');";
		ResultSet rs = stmt.executeQuery(sql);
		Word thisEntry = new Word("");
		int occurrence = 0;
		while(rs.next())
		{
			thisEntry.word = rs.getString("word");
			occurrence = rs.getInt("occurrence");
		}
		occurrence += 1;
		PreparedStatement ps = connection.prepareStatement("UPDATE 'wordoccurrences'.'word' SET occurrence = ('" + occurrence + "') WHERE (`word` = '" + thisEntry.word + "');");
		System.out.println("Set " + thisEntry.word + " to " + occurrence + " occurrences.");
		/*int status = ps.executeUpdate();
		
		if (status != 0)
		{
			//System.out.println(word.word + "'s record was updated!"); //this is used to check that the information is reaching the database
		}*/
	}
	
	public static void selectWord(String word) throws SQLException
	{
		connection = DriverManager.getConnection(url, username, password);
		Statement stmt = connection.createStatement();
		String sql = "SELECT word, occurrence FROM wordoccurrences.word WHERE (`word` = '" + word + "');";
		ResultSet rs = stmt.executeQuery(sql);
		Word thisEntry = new Word("");
		int occurrence = 0;
		while(rs.next())
		{
			thisEntry.word = rs.getString("word");
			occurrence = rs.getInt("occurrence");
		}
		
		output = thisEntry.word + " appears " + occurrence + " times within The Raven. ";
		
	}

}
