package bharati.binita.job.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author binita.bharati@gmail.com
 * A sample test case to test the run time of this project.
 *
 */

public class SampleTC {
	
	private Properties configProperties;
	private List<String> jobIds;
	
	@Before
	public void setUp() throws Exception {

		configProperties = new Properties();
		jobIds = new ArrayList<>();
	    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("job_processor.properties");
	    configProperties.load(inputStream);
	    
	    FileOutputStream fos = new FileOutputStream(new File(configProperties.getProperty("job.execution.io.path")), true);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	    
		//Add some dummy job entries that will not be monitored by the Job Tracker. This could be as well the past completed jobs.
		for(int i = 0 ; i < 100 ; i++) {
			String jobId = UUID.randomUUID().toString();			
			bw.write("ID = "+jobId + " - Hello || ");

		}
			    
		for(int i = 0 ; i < Integer.parseInt(configProperties.get("job.total.count") + "") ; i++) {
			String jobId = UUID.randomUUID().toString();
			jobIds.add(jobId);
			
			bw.write("ID = "+jobId + " - Hello || ");

		}
		
		bw.close();
	    fos.close();
		
	    	    
	}
	
	@Test
	public void test() throws Exception {
		JobHandler jh = new JobHandler(Integer.parseInt(configProperties.get("job.execution.tpool.size") + ""));
		jh.getJt().startTracker();
		for(int i = 0 ; i < jobIds.size() ; i++) {
			Job job = new Job(jobIds.get(i), new Date().getTime());
			jh.submitJob(job);
			Thread.sleep(2000);

		}
	}
	
	
}
