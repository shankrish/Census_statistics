package lab.household;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class HouseholdReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

	/**************************************************************************
	 * Reduce method will be called once for each key.
	 **************************************************************************/
	@Override
	public void reduce(IntWritable key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		
		// Initialize the totalRecords counter
		int totalRecords = 0;

		// Loop through the list of values and increment the counter
		for (IntWritable text : values) {

			totalRecords += text.get();
		}
		
		// Write the key(year) and value(no of records) to output file.
		context.write(key, new IntWritable(totalRecords));

	}

}
