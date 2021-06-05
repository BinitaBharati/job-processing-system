package bharati.binita.job.processor;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bharati.binita.job.processor.JobDetails.JobStatus;

/**
 * 
 * @author binita.bharati@gmail.com
 * Responsible for job execution.
 *
 */

public class JobHandler {
	
	private int tpSize;
	private ScheduledExecutorService scheduler;
	private JobTracker jt;
	
	public JobHandler(int tpSize) throws Exception {
		scheduler = Executors.newScheduledThreadPool(tpSize);
		jt = new JobTracker();
	}
	
	

	public JobTracker getJt() {
		return jt;
	}



	public void setJt(JobTracker jt) {
		this.jt = jt;
	}



	public void submitJob(Job job) {
		job.setJt(jt);
		JobDetails jd = new JobDetails(job.getId());
		jd.setStatus(JobStatus.SCHEDULED);
		job.setJd(jd);
		scheduler.schedule(job, job.getExecutionTimeEpochMilliSecs() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);		
		jt.addJob(jd);
	}
	
	
}
