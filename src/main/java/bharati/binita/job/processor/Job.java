package bharati.binita.job.processor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.EnumSet;
import java.util.Properties;
import java.util.concurrent.Callable;

import bharati.binita.job.processor.JobDetails.JobStatus;

/**
 * 
 * @author binita.bharati@gmail.com
 * Represents a Job. 
 *
 */

public class Job implements Runnable{
	
	private String id;
	private long executionTimeEpochMilliSecs;
	
	private JobTracker jt;
	private JobDetails jd;
	private Properties configProperties;
	
	public Job(String id, long executionTimeEpochMilliSecs) throws Exception {
		
		this.id = id;
		this.executionTimeEpochMilliSecs = executionTimeEpochMilliSecs;
		configProperties = new Properties();
	    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("job_processor.properties");
	    configProperties.load(inputStream);
	}
	
	@Override
	public void run() {
		long runTime = new Date().getTime();
	
		jd.setStartTimeEpochMilliSecs(new Date().getTime());
		jd.setStatus(JobStatus.PROCESSING);
		jt.updateJob(jd);
		
		try {
			doSomeIO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		jd.setEndTimeEpochMilliSecs(new Date().getTime());
		jd.setStatus(JobStatus.COMPLETED);
		jt.updateJob(getJd());
	}

	/**
	 * All job execution thread read and write to a common file.
	 * @throws Exception 
	 */
	private void doSomeIO() throws Exception {
	      Path path = Paths.get(configProperties.getProperty("job.execution.io.path"));

	      FileChannel readFileChannel = FileChannel.open(path);
	      ByteBuffer readBuffer = ByteBuffer.allocate(48);
	      while(readFileChannel.read(readBuffer) > 0) {
	         readBuffer.flip();
	         String fileEntry = new String(readBuffer.array());
	         readBuffer.clear();
	         if(fileEntry.indexOf(id) != -1) {
	        	 String updatedEntry = fileEntry.replaceFirst("ID = "+id , "ID = "+id + " random junk ");
	        	 FileChannel writeFileChannel = FileChannel.open(path, 
	        	         EnumSet.of(StandardOpenOption.CREATE, 
	        	            StandardOpenOption.TRUNCATE_EXISTING,
	        	            StandardOpenOption.WRITE)
	        	         );  
	        	 ByteBuffer writeBuffer = ByteBuffer.allocate(48);
	       	     writeBuffer.flip();
	       	     writeFileChannel.write(ByteBuffer.wrap(updatedEntry.getBytes()));
	       	     writeBuffer.clear();
	       	     writeFileChannel.close();
	         }
	      }
	      readBuffer.clear();
	      readFileChannel.close();
		
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public long getExecutionTimeEpochMilliSecs() {
		return executionTimeEpochMilliSecs;
	}

	public void setExecutionTimeEpochMilliSecs(long executionTimeEpochMilliSecs) {
		this.executionTimeEpochMilliSecs = executionTimeEpochMilliSecs;
	}

	public JobTracker getJt() {
		return jt;
	}

	public void setJt(JobTracker jt) {
		this.jt = jt;
	}
	
	
	
	public JobDetails getJd() {
		return jd;
	}

	public void setJd(JobDetails jd) {
		this.jd = jd;
	}

	@Override
	public String toString() {
		return "Job --> id = "+id + ", details = "+jd;
	}
	

}
