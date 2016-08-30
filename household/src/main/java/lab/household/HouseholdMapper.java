package lab.household;

import java.io.*;
import java.net.URI;
import java.util.*;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class HouseholdMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

	// hhRecordFieldValues: HashMap to hold the parsed household record values
	// hhRecordCompare : HashMap that holds query variables, conditions and
	// values
	HashMap<String, String> hhRecordFieldValues;
	HashMap<String, List<String>> hhRecordCompare;

	// HouseholdRecordParser object that parses the household record.
	HouseholdRecordParser hhParser;

	/**************************************************************************
	 * function interface to build the query.
	 **************************************************************************/
	public interface hashmapComparator {
		boolean compare(HashMap<String, List<String>> hashMapA, HashMap<String, String> hashMapB);
	}

	/**************************************************************************
	 * setup method will be called once at the start of the task
	 **************************************************************************/
	protected void setup(Context context) throws IOException, InterruptedException {

		// Create the householdRecordParser to be used in the map method.
		hhParser = new HouseholdRecordParser();

		// remoteFile holds the path of Query configuration file in HDFS
		String remoteFile = context.getConfiguration().get("configuration_file");

		// Client interface to HDFS file system to read the Query config file.
		FileSystem fis = FileSystem.get(URI.create(remoteFile), context.getConfiguration());

		InputStream isHandle = null;

		try {
			// Open the HDFS Query Configuration file
			isHandle = fis.open(new Path(remoteFile));
			BufferedReader bis = new BufferedReader(new InputStreamReader(isHandle));
			String line;
			
			// Create the HashMap to hold the query variables.
			hhRecordCompare = new HashMap<String, List<String>>();
			
			// Read the query variables into the HashMap
			while ((line = bis.readLine()) != null) {
				StringTokenizer str = new StringTokenizer(line);
				while (str.hasMoreTokens()) {
					hhRecordCompare.put(str.nextToken(), Arrays.asList(str.nextToken(), str.nextToken()));
				}

			}

		} finally {
			// Close the file handle
			IOUtils.closeStream(isHandle);
		}

	}
	
	
	/**************************************************************************
	 * Map method will be called once at the start of the task
	 **************************************************************************/
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		// textValue holds the value in the input split
		String textValue = value.toString();
		
		// Check whether the record is household record. Parse the content
		// only if it is household record, else skip
		if (textValue.charAt(0) == '1') {
			
			// Call the parse method to parse the record and store the value
			// in hhRecordFieldValues hashMap.
			hhRecordFieldValues = hhParser.parser(textValue);

			// Function definition for hashmapComparator which build the filter
			// by comparing the keys (header fields), values (list of operator 
			// and value). Loop through the hhRecordCompare and apply the query
			hashmapComparator hhComp = (hhmapA, hhmapB) -> {
				
				// Loop through the field names in hhRecordCompare
				for (String keys : hhmapA.keySet()) {
				
					// Check if the operation is equal. if yes, apply the condition
					// on hhRecordFieldValues
					if (hhmapA.get(keys).get(0).equals("=")) {
						if (!(Integer.parseInt(hhmapA.get(keys).get(1)) == Integer.parseInt(hhmapB.get(keys)))) {
							return false;
						}
					}
					
					// Check if the operation is less than. if yes, apply the condition
					// on hhRecordFieldValues
					if (hhmapA.get(keys).get(0).equals("<")) {
						if (!(Integer.parseInt(hhmapA.get(keys).get(1)) > Integer.parseInt(hhmapB.get(keys)))) {
							return false;
						}
					}
					
					// Check if the operation is greater than. if yes, apply the condition
					// on hhRecordFieldValues
					if (hhmapA.get(keys).get(0).equals(">")) {
						if (!(Integer.parseInt(hhmapA.get(keys).get(1)) < Integer.parseInt(hhmapB.get(keys)))) {
							return false;
						}
					}
					
					// Check if the operation is less than or equal to. if yes, apply 
					// the condition on hhRecordFieldValues
					if (hhmapA.get(keys).get(0).equals("<=")) {
						if (!(Integer.parseInt(hhmapA.get(keys).get(1)) >= Integer.parseInt(hhmapB.get(keys)))) {
							return false;
						}
					}
					
					// Check if the operation is greater than or equal to. if yes, apply 
					// the condition on hhRecordFieldValues
					if (hhmapA.get(keys).get(0).equals(">=")) {
						if (!(Integer.parseInt(hhmapA.get(keys).get(1)) <= Integer.parseInt(hhmapB.get(keys)))) {
							return false;
						}
					}
				}
				return true;
			};
			
			// Check the household record matches the query. If yes,
			// write the key(year),value(1).
			if (hhComp.compare(hhRecordCompare, hhRecordFieldValues)) {

				context.write(new IntWritable(Integer.parseInt(hhRecordFieldValues.get("H_YEAR"))), new IntWritable(1));

			}

		}

	}
}
