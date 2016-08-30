package lab.household;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HouseholdJob extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Job job = Job.getInstance();
		job.setJarByClass(getClass());

		job.setJobName("Household Job");

		Path localPath = new Path(args[2]);
		Path remotePath = new Path(args[3]);

		Configuration conf = job.getConfiguration();


		InputStream in = new BufferedInputStream(new FileInputStream(localPath.toString()));
		FileSystem fs = FileSystem.get(URI.create(remotePath.toString()), conf);
		OutputStream out = fs.create(remotePath);
		IOUtils.copyBytes(in, out, 4096, true);
		conf.set("configuration_file", remotePath.toString());

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(HouseholdMapper.class);
		job.setReducerClass(HouseholdReducer.class);

		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(IntWritable.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int exitcode = ToolRunner.run(new HouseholdJob(), args);
		System.exit(exitcode);

	}

}
