package bharati.binita.job.processor;

import java.util.List;
import java.util.Properties;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bharati.binita.job.processor.JobDetails.JobStatus;

/**
 * 
 * @author binita.bharati@gmail.com
 * Single-threaded scheduled tracker for all submitted jobs; it will run periodically.
 *
 */

public class JobTracker {
	
    private static Logger logger = LoggerFactory.getLogger(JobTracker.class);

	
	private ConcurrentHashMap<String, JobDetails> jobIdToJobMap;
	private ScheduledExecutorService scheduler;
	private File file;
	private Properties configProperties;

	
	public JobTracker() throws Exception {
		jobIdToJobMap = new ConcurrentHashMap<>();
		scheduler = Executors.newScheduledThreadPool(1);
		
		configProperties = new Properties();
	    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("job_processor.properties");
	    configProperties.load(inputStream);
	    
		file = new File(configProperties.getProperty("job.tracker.output.path"));
		if(!file.exists())
			file.createNewFile();
        		
	}
	
	public void addJob(JobDetails job) {
		this.jobIdToJobMap.putIfAbsent(job.getJobId(), job);
	}
	
	public void updateJob(JobDetails jd) {
		JobDetails old = this.jobIdToJobMap.get(jd.getJobId());
		if (jd.getEndTimeEpochMilliSecs() != 0L)
			old.setEndTimeEpochMilliSecs(jd.getEndTimeEpochMilliSecs());
		if(jd.getStartTimeEpochMilliSecs() != 0L)
			old.setStartTimeEpochMilliSecs(jd.getStartTimeEpochMilliSecs());
		if(jd.getStatus() != null)
			old.setStatus(jd.getStatus());
		
		jobIdToJobMap.computeIfPresent(jd.getJobId(), (k, v) -> old);
	}
	
	public void generateReport() throws Exception {
		
		String numJobsSubmittedStr = null;
		String avgProcessingTimeStr = "N.A";
		String successRateStr = null;
		String failureRateStr = null;
		
		int numJobs = 0;
		double avgProcessingTime = 0.0d;
		double processingTime = 0.0d;
		int completedCount = 0;
		int failedJobCount = 0;
		int successJobCount = 0;
		List<String> includedJobIds = new ArrayList<>();



		
		FileOutputStream fos = new FileOutputStream(file, true);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		 
		Date curTime = new Date();
		Collection<JobDetails> jd = jobIdToJobMap.values();
		
		numJobs = jd.size();
		Iterator<JobDetails> itr = jd.iterator();
		boolean writeFlag = false;
		while (itr.hasNext()) {			
			JobDetails temp = itr.next();
			logger.info("JT: temp = "+temp);
			includedJobIds.add(temp.getJobId());
			if(temp.getStatus() == JobStatus.COMPLETED || temp.getStatus() == JobStatus.FAILED ) {
				processingTime = processingTime + (temp.getEndTimeEpochMilliSecs() - temp.getStartTimeEpochMilliSecs());
				completedCount++;
				if(temp.getStatus() == JobStatus.COMPLETED ) {
					successJobCount++;
				} else if(temp.getStatus() == JobStatus.FAILED ) {
					failedJobCount++;
				}
				itr.remove();
				logger.info("JT: after removing entry = "+temp);


			}
		}
		if(completedCount != 0) {
			
			avgProcessingTime = processingTime/completedCount;
			DecimalFormat df = new DecimalFormat("#.##");
			avgProcessingTime = Double.parseDouble(df.format(avgProcessingTime));
			avgProcessingTimeStr = avgProcessingTime +"";

			
		}
		successRateStr = successJobCount+"/"+numJobs;
		failureRateStr = failedJobCount+"/"+numJobs;
		
		numJobsSubmittedStr = numJobs + "";
		StringBuilder sb = new StringBuilder();
		if(avgProcessingTimeStr.equals("N.A") ) {
			bw.write("time = " + curTime + " ;numJobsSubmitted = "+numJobsSubmittedStr + " ;avgProcessingTime = "+avgProcessingTimeStr +
					" ;successRate = "+successRateStr + " ;failureRate = "+failureRateStr);
		} else {
		
			bw.write("time = " + curTime + " ;numJobsSubmitted = "+numJobsSubmittedStr + " ;avgProcessingTime = "+avgProcessingTimeStr + " ms" +
					" ;successRate = "+successRateStr + " ;failureRate = "+failureRateStr);
		}
		
		bw.write("\n");	 
		bw.close();
	
		
	}
	
	public void startTracker( ) {
		scheduler.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					generateReport();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, 0, 30000, TimeUnit.MILLISECONDS);
	}

}
