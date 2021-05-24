/**
 * Name: Dharam Kathiriya (Individual project)
 * Course: CS 4350 - Lab 4
 */

import java.sql.*;
import java.util.*;

public class PomonaTransit 
{
	
	static Scanner kb = new Scanner(System.in); 
	static Connection con;
	static Statement st;
	static ResultSet rs;
	static ResultSetMetaData rsMD;
	static String theQuery;
	
	public static void main(String[] args) throws Exception
	{
		String url = "jdbc:mysql://localhost:3306/pts"; //mySQL url
		String userName = "root"; // userName
		String pass = "273790"; // password 
			
		// connect to database (mySQL)
		con = DriverManager.getConnection(url,userName,pass);
		
		
		int choice;
		do {
			// print menu
			displayMenu();
		
			// ask user to enter select the option
			System.out.print("Select the option (1-9): ");
			choice = kb.nextInt();
		
			// options
			switch(choice)
			{
			case 1: 
				displaySchedule();
				break;
			case 2: 
				editSchedule();
				break;
			case 3:
				displayStop();
				break;
			case 4:
				displayWeeklySchedule();
				break;
			case 5:
				addDriver();
				break;
			case 6:
				addBus();
				break;
			case 7:
				deleteBus();
				break;
			case 8:
				recordActualTripData();
				break;
			case 9:
				System.out.println("Goodbye!");
				break;
			default:
				System.out.println("Invalid option. Select aother option.\n");
			
			}
		}
		while(choice != 9); 
			
	}
	
	/**
	 * Displays the schedule based on the start location, destination and date of a trip
	 * @throws SQLException
	 */
	private static void displaySchedule() throws SQLException 
	{
		String startLocationName, destinationName, date;
		
		// take input from the user
		kb.nextLine();
		System.out.print("Enter start location name: ");
		startLocationName = kb.nextLine();
		
		System.out.print("Enter destination name: ");
		destinationName = kb.nextLine();
		
		System.out.print("Enter date as MM/DD/YYYY: ");
		date = kb.nextLine();
		
		// create a statement
		st = con.createStatement();

		// the SQL query to retrive the specified entries from table
		theQuery = "SELECT T.StartLocationName, T.DestinationName, TR.Date, TR.ScheduledStartTime, TR.ScheduledArrivalTime, TR.DriverName, TR.BusID " + 
				  		  "FROM Trip T, TripOffering TR " + 
				  		  "WHERE T.tripnumber = TR.tripnumber AND T.StartLocationName = '" + startLocationName + 
				  		   "' AND T.DestinationName = '" + destinationName + "' AND TR.Date = '" + date + "'";
		
		// execute query
		rs = st.executeQuery(theQuery);
		rsMD = rs.getMetaData();
		String colNames = "";
		int colCount = rsMD.getColumnCount();
		System.out.println();
		
		// print the table (columnNames and their values) based on the sql query
		for (int col=1;col<=colCount;col++)
		{
			colNames += String.format("| %-27s", rsMD.getColumnName(col));
		}
		System.out.println(colNames);
		for(int i=0; i<colNames.length();i++)
			System.out.print("-");
		System.out.println();
		while(rs.next())
		{
			for (int col=1;col<=colCount;col++)
			{
				System.out.printf("| %-27s",rs.getString(col));
			}
			System.out.println();
		}
		System.out.println();
		
		// clean up
		rs.close();
		st.close();
	}

	/**
	 * Edits the schedule (trip offering)- allows to delete, add, change the values of the table 'TripOffering'
	 * @throws SQLException if error occurred during the execution
	 */
	private static void editSchedule() throws SQLException 
	{
		char ch, addMore; 
		int tripNumber, entryRemoved, busID;
		String date, scheduledStartTime, scheduledArrivalTime, driverName;
		
		// shows choice to user for editing the schedule
		System.out.println("Edit the schedule:\n" + 
						   "\ta. Delete a trip offering\n" +
				   		   "\tb. Add a set of trip offerings\n" +
				   		   "\tc. Change the driver for a given trip offering\n" +
				   		   "\td. Chnage the bus (BusID) for a given trip offering\n");
		
		// ask user for the choice
		System.out.println("What would you like to edit? (Select the choice from a-d)");
		ch = kb.next().charAt(0);
		
		// Deletes the trip offering based specified by trip#, date, and scheduled start time
		if(ch == 'a')
		{
			// asks user to enter the specifed vlaues
			System.out.print("Enter the trip number: ");
			tripNumber = kb.nextInt();
			System.out.print("Enter the date (MM/DD/YYYY) of trip: ");
			date = kb.next();
			System.out.print("Enter the scheduled start time (like 05:23PM or 10:09AM) of trip: ");
			scheduledStartTime = kb.next();
			
			// execute the query to delete entry
			st = con.createStatement();
			theQuery = "DELETE FROM TripOffering " +
					   "WHERE TripNumber=" + tripNumber + " AND Date='" + date + 
			    	   "' AND ScheduledStartTime='" + scheduledStartTime + "'";
			entryRemoved = st.executeUpdate(theQuery);
			if (entryRemoved == 0)
			{
				System.out.println("Could not delete the specified entry.");
			}
			st.close();
			
			//display the changes (updated table)
			System.out.println("The updated table for the trip offering:");
			displayUpdatedTable("TripOffering");
		}
		
		// adds a set of trip offerings 
		else if(ch == 'b')
		{
			do
			{
				// ask user to enter the specified values
				System.out.print("Enter the trip number: ");
				tripNumber = kb.nextInt();
				System.out.print("Enter the date (MM/DD/YYYY) of trip: ");
				date = kb.next();
				System.out.print("Enter the scheduled start time (like 05:23PM or 10:09AM) of trip: ");
				scheduledStartTime = kb.next();
				System.out.print("Enter the scheduled arrival time for trip: ");
				scheduledArrivalTime = kb.next();
				kb.nextLine();
				System.out.print("Enter the driver's name: ");
				driverName = kb.nextLine();
				System.out.print("Enter the Bus ID: ");
				busID = kb.nextInt();
			
				// execute the query to insert/add entry for TripOffering
				st = con.createStatement();
				theQuery = "INSERT INTO TripOffering " +
						   "VALUES(" + tripNumber + ",'" + date + "','" + scheduledStartTime +
					       "','" + scheduledArrivalTime + "','" + driverName + "'," + busID + ")";
				st.executeUpdate(theQuery);
				st.close();
				
				// ask is user wants to add more entries
				System.out.print("\nDo you want to add more? (enter 'y' for yes or any other key for no) ");
				addMore = kb.next().charAt(0);
			}
			while(addMore == 'y');	
			
			// display the changes in the table 'TripOffering'
			System.out.println("The updated table for the trip offering:");
			displayUpdatedTable("TripOffering");
		}
		
		// Changes the driver for the given trip offering
		else if(ch == 'c')
		{
			// take user input
			System.out.print("Enter the trip number: ");
			tripNumber = kb.nextInt();
			System.out.print("Enter the date (MM/DD/YYYY) of trip: ");
			date = kb.next();
			System.out.print("Enter the scheduled start time (like 05:23PM or 10:09AM) of trip: ");
			scheduledStartTime = kb.next();
			kb.nextLine();
			System.out.print("Enter new driver name for the specifed entry: ");
			driverName = kb.nextLine();
			
			// execute the query
			st = con.createStatement();
			theQuery = "UPDATE TripOffering " +
			           "SET DriverName='" + driverName + "' " +
					   "WHERE TripNumber=" + tripNumber + " AND Date='" + date + "' AND ScheduledStartTime='" + scheduledStartTime + "'";
			st.executeUpdate(theQuery);
			st.close();
			
			// display the changes in the table 'TripOffering'
			System.out.println("The updated table for the trip offering:");
			displayUpdatedTable("TripOffering");
		}
		
		// Changes the bus for a given trip offering
		else if(ch == 'd')
		{
			// ask user for the input values
			System.out.print("Enter the trip number: ");
			tripNumber = kb.nextInt();
			System.out.print("Enter the date (MM/DD/YYYY) of trip: ");
			date = kb.next();
			System.out.print("Enter the scheduled start time (like 05:23PM or 10:09AM) of trip: ");
			scheduledStartTime = kb.next();
			System.out.print("Enter new Bus ID for the specified entry: ");
			busID = kb.nextInt();
			
			// execute the query
			st = con.createStatement();
			theQuery = "UPDATE TripOffering " +
			           "SET BusID='" + busID + "' " +
					   "WHERE TripNumber=" + tripNumber + " AND Date='" + date + "' AND ScheduledStartTime='" + scheduledStartTime + "'";
			st.executeUpdate(theQuery);
			st.close();
			
			// display the changes in the table 'TripOffering'
			System.out.println("The updated table for the trip offering:");
			displayUpdatedTable("TripOffering");
		}	
		
		// show error if invalid choice is selected for editing the schedule.
		else
			System.out.println("Invalid choice");
	}
	
	/**
	 * Displays the stop information for the specified trip.
	 * @throws SQLException if error occurred during the execution
	 */
	private static void displayStop() throws SQLException
	{
		int tripNumber, colCount;
		System.out.print("Enter the trip number: "); //ask user for trip number
		tripNumber = kb.nextInt();
		
		// execute the query
		st = con.createStatement();
		theQuery = "SELECT * " +
				   "FROM TripStopInfo " +
				   "WHERE TripNumber = " + tripNumber; 
		
		rs = st.executeQuery(theQuery);
		rsMD = rs.getMetaData();
		
		// print the table displaying the stop information of a given trip
		String colNames = "";
		colCount = rsMD.getColumnCount();
		System.out.println();
		for (int col=1;col<=colCount;col++)
		{
			colNames += String.format("| %-20s", rsMD.getColumnName(col));
		}
		System.out.println(colNames);
		for(int i=0; i<colNames.length();i++)
			System.out.print("-");
		System.out.println();
		while(rs.next())
		{
			for (int col=1;col<=colCount;col++)
			{
				System.out.printf("| %-20s",rs.getString(col));
			}
			System.out.println();
		}
		System.out.println();

		// clean up
		rs.close();
		st.close();
		
	}
	
	/**
	 * Displays the weekly schedule for a given driver and date
	 * @throws SQLException if error occurred during the execution
	 */
	private static void displayWeeklySchedule() throws SQLException 
	{
		String driverName, date, theDate;
		String[] token;
		int colCount = 0, days;
	
		// take user input
		kb.nextLine();
		System.out.print("Enter the driver's name: ");
		driverName = kb.nextLine();
		System.out.print("Enter the date: "); // start date (first day) of week
		date = kb.nextLine();
		
		// for the seven days in week, search for the specified entry
		for(int i=0; i<7;i++) 
		{
			// split the date and determine the week from the provided date
			token = date.split("/"); 
			days = Integer.parseInt(token[1]) + i;
			if(days < 10)
				token[1] = "0" + Integer.toString(days);
			else
				token[1] = Integer.toString(days);
			theDate = String.join("/", token);
		
			// execute the query
			st = con.createStatement();
			theQuery = "SELECT * " +
				       "FROM TripOffering T, Driver D " +
				       "WHERE T.DriverName=D.DriverName AND T.DriverName='" + driverName + "' AND T.Date='" + theDate + "'";
		
			rs = st.executeQuery(theQuery);
			rsMD = rs.getMetaData();
		
			// print the table displaying the weekly schedule
			if(i==0)
			{
				String colNames = "";
				colCount = rsMD.getColumnCount();
				System.out.println();
				for (int col=1;col<=colCount;col++)
				{
					colNames += String.format("| %-20s", rsMD.getColumnName(col));
				}
				System.out.println(colNames);
				for(int j=0; j<colNames.length();j++)
					System.out.print("-");
				System.out.println();
			}

			while(rs.next())
			{
				for (int col=1;col<=colCount;col++)
				{
					System.out.printf("| %-20s",rs.getString(col));
				}
				System.out.println();
			}
		
			// clean up
			rs.close();
			st.close();
		}
		System.out.println();
	}
	
	/**
	 * Adds a new driver to the table 'Driver'
	 * @throws SQLException when the error occurred in the SQL query or the execution
	 */
	private static void addDriver() throws SQLException 
	{
		String driverName, telephoneNumber;
		
		//ask user for driver info
		kb.nextLine();
		System.out.print("Enter the name of a driver: ");
		driverName = kb.nextLine();
		System.out.print("Enter the telephone number of a driver: ");
		telephoneNumber = kb.nextLine();
		
		//execute the query
		st = con.createStatement();
		theQuery = "INSERT INTO Driver " + 
		           "VALUES('" + driverName + "','" + telephoneNumber + "')";
		st.executeUpdate(theQuery);
		st.close();
		
		// display the changes in the table 'Driver'
		System.out.println("The updated 'Driver' Table:");
		displayUpdatedTable("Driver");
	}
	
	/** 
	 * Adds a new bus to the table 'Bus'
	 * @throws SQLException if error occurred
	 */
	private static void addBus() throws SQLException 
	{
		int busID, year;
		String model;
		
		//ask user for the bus info
		System.out.print("Enter the Bus ID: ");
		busID = kb.nextInt();
		kb.nextLine();
		System.out.print("Enter the bus model: ");
		model = kb.nextLine();
		System.out.print("Enter the year: ");
		year = kb.nextInt();
		
		// execute the query
		st = con.createStatement();
		theQuery = "INSERT INTO Bus " + 
		           "VALUES(" + busID + ",'" + model + "','" + year + "')";
		st.executeUpdate(theQuery);
		st.close();	
		
		// display the changes in the table 'Bus'
		System.out.println("The updated 'Bus' Table:");
		displayUpdatedTable("Bus");
	}
	
	/**
	 * Deletes the specified bus entry from the table 'Bus'.
	 * @throws SQLException if error occurred 
	 */
	private static void deleteBus() throws SQLException 
	{
		// take user input
		int busID;
		System.out.print("Enter the busID: ");
		busID = kb.nextInt();
		
		// execute the query
		st = con.createStatement();
		theQuery = "DELETE FROM Bus " + 
				   "Where BusID=" + busID;
		st.executeUpdate(theQuery);
		st.close();
		
		// display the changes in the table 'Bus'
		System.out.println("The updated 'Bus' Table:");
		displayUpdatedTable("Bus");	
	}
	
	/**
	 * Records (inserts) the actual trip data into the table 'ActualTripStopInfo'
	 * @throws SQLException if there is an error in the SQL execution
	 */
	private static void recordActualTripData() throws SQLException 
	{
		int tripNumber, stopNumber, numberOfPassengersIn, numberOfPassengersOut;
		String date, scheduledStartTime, scheduledArrivalTime, actualStartTime, actualArrivalTime;
		
		// ask user to enter the attribute values for the actual trip
		System.out.print("Enter the trip number: ");
		tripNumber = kb.nextInt();
		kb.nextLine();
		System.out.print("Enter the date: ");
		date = kb.nextLine();
		System.out.print("Enter scheduled start time: ");
		scheduledStartTime = kb.nextLine();
		System.out.print("Enter the stop number: ");
		stopNumber = kb.nextInt();
		kb.nextLine();
		System.out.print("Enter scheduled arrival time: ");
		scheduledArrivalTime = kb.nextLine();
		System.out.print("Enter actual start time: ");
		actualStartTime = kb.nextLine();
		System.out.print("Enter actual arrival time: ");
		actualArrivalTime = kb.nextLine();
		System.out.print("Enter number of passenger in: ");
		numberOfPassengersIn = kb.nextInt();
		System.out.print("Enter number of passengeres out: ");
		numberOfPassengersOut = kb.nextInt();
		
		// execute the query
		st = con.createStatement();
		theQuery = "INSERT INTO ActualTripStopInfo " +
		           "VALUES(" + tripNumber +",'" + date + "','" + scheduledStartTime + "'," + stopNumber + ",'" + 
		             	     scheduledArrivalTime + "','" + actualStartTime + "','" + actualArrivalTime + "'," +
		              		 numberOfPassengersIn + "," + numberOfPassengersOut + ")";
		st.close ();
		
		// display the changes in the table 'ActualTripStopInfo'
		System.out.println("The updated table for the actual trip stop info:");
		displayUpdatedTable("ActualTripStopInfo");
	}
	
	/**
	 * This method displays the resulting table after a particular table is modified.
	 * @param tableName the name of the table
	 * @throws SQLException throws error if the query is incorrect or the data is not found
	 */
	private static void displayUpdatedTable(String tableName) throws SQLException
	{	
		// the query to retrieve the specified table
		theQuery = "SELECT * " +
	               "FROM " + tableName;
		// create the table and execute the query
		int colCount;
		st = con.createStatement();
		rs = st.executeQuery(theQuery);
		rsMD = rs.getMetaData();
		
		// print the table 
		String colNames = "";
		colCount = rsMD.getColumnCount();
		System.out.println();
		//column values
		for (int col=1;col<=colCount;col++)
		{
			colNames += String.format("| %-20s", rsMD.getColumnName(col));
		}
		System.out.println(colNames);
		
		for(int i=0; i<colNames.length();i++)
			System.out.print("-");
		System.out.println();
		
		// the actual values of each row
		while(rs.next())
		{
			for (int col=1;col<=colCount;col++)
			{
				System.out.printf("| %-20s",rs.getString(col));
			}
			System.out.println();
		}
		System.out.println();

		// clean up
		rs.close();
		st.close();
	}
	
	// print menu for user
	private static void displayMenu() 
	{
		System.out.print("1. Display the schedule of all trips\n" +
	                     "2. Edit the schedule\n" +
				         "3. Display the stops of a given trip\n" +
	                     "4. Display the weekly schedule of a given driver and date\n" +
				         "5. Add a driver\n" +
	                     "6. Add a bus\n" +
				         "7. Delete a bus\n" +
	                     "8. Record (insert) actual data of a given trip offering\n" +
				         "9. Exit\n\n");
		
	}
}
