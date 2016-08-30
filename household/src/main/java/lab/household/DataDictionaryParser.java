package lab.household;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.MatchResult;


/**
 * @author Shan Krishnan
 *
 */
public final class DataDictionaryParser {
	
	// HashMap to hold the field name, size and location.
	// key is the field name and value is list of size and location.
	private HashMap<String, ArrayList<Integer>> DDHeader;
	
	private static DataDictionaryParser ddParser = null;

	/**************************************************************************
	 * Constructor DataDictionaryParser.
	 *************************************************************************/	
	private DataDictionaryParser() {
		
		ClassLoader classLoader = getClass().getClassLoader();
		
		// Read household record's  data dictionary.
		File f1 = new File(classLoader.getResource("asec2015early_pubuse.dd.txt").getFile());
		FileInputStream fileIS;
		BufferedReader buffReader;
		
		try {
			fileIS = new FileInputStream(f1);
			buffReader = new BufferedReader(new InputStreamReader(fileIS));
			DDHeader = new HashMap<String, ArrayList<Integer>>();
			
			String line;
			
			// Data dictionary file contains the field names, size, location
			// We will check for the lines with following format 
			// D HRECORD     1      1 
			// If a match is found add 2nd token as the key, 3rd & 4th token
			// as the list of values.
			while ((line = buffReader.readLine()) != null) {

				Scanner s = new Scanner(line);
				
				// Regular expression to match format D HRECORD     1      1 
				// check if the line read matches the re.
				if (s.findInLine("([D])\\s+\\w+\\s+\\d+\\s+\\d+") != null) {

					MatchResult match = s.match();

					StringTokenizer str = new StringTokenizer(match.group().substring(1));
					
					// Match is found, split the tokens and add populate the
					// DDHeader HashMap.
					while (str.hasMoreTokens()) {
						String header = str.nextToken();
						Integer headerFieldSize = Integer.parseInt(str.nextToken());
						Integer headerFieldPos = Integer.parseInt(str.nextToken());
						ArrayList<Integer> headerFieldValues = new ArrayList<Integer>();
						headerFieldValues.add(headerFieldPos);
						headerFieldValues.add(headerFieldSize);
						DDHeader.put(header, headerFieldValues);
					}

				}
				s.close();
			}

		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		
		} catch (NumberFormatException e) {

			e.printStackTrace();
		
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**************************************************************************
	 * getInstance to get the DataDictionary Parser singleton instance 
	 *************************************************************************/	
	public static DataDictionaryParser getInstance() {
		if (ddParser == null) {
			ddParser = new DataDictionaryParser();

		}

		return ddParser;
	}
	/**************************************************************************
	 * Getter method for Datadictionary header HashMap.
	 *************************************************************************/	
	public HashMap<String, ArrayList<Integer>> getDDHeader() {
		return DDHeader;
	}

	
	/**************************************************************************
	 * Getter method for Datadictionary header HashMap.
	 *************************************************************************/	
	public void setDDHeader(HashMap<String, ArrayList<Integer>> dDHeader) {
		DDHeader = dDHeader;
	}

}
