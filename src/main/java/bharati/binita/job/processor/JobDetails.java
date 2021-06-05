package bharati.binita.job.processor;

import java.util.Date;

/**
 * 
 * @author binita.bharati@gmail.com
 * Additional details of a Job. 
 * This class will be used to generate the job execution statistics.
 *
 */

public class JobDetails {
	
	private String jobId;
	
	public static enum JobStatus {
		SCHEDULED,
		PROCESSING,
		COMPLETED,
		FAILED
	}
	
	private long startTimeEpochMilliSecs;
	private long endTimeEpochMilliSecs;
	private JobStatus status;
	
	private JobTracker jt;
	
	public JobDetails(String id) {
		jobId = id;
	}

	public String getJobId() {
		return jobId;
	}


	public void setJobId(String jobId) {
		this.jobId = jobId;
	}


	public long getStartTimeEpochMilliSecs() {
		return startTimeEpochMilliSecs;
	}


	public void setStartTimeEpochMilliSecs(long startTimeEpochMilliSecs) {
		this.startTimeEpochMilliSecs = startTimeEpochMilliSecs;
	}


	public long getEndTimeEpochMilliSecs() {
		return endTimeEpochMilliSecs;
	}


	public void setEndTimeEpochMilliSecs(long endTimeEpochMilliSecs) {
		this.endTimeEpochMilliSecs = endTimeEpochMilliSecs;
	}


	public JobStatus getStatus() {
		return status;
	}


	public void setStatus(JobStatus status) {
		this.status = status;
	}


	@Override
	public String toString() {
		return ("JobDetails:  status = " + status +
				", startTimeEpochMilliSecs = " +startTimeEpochMilliSecs+
				", endTimeEpochMilliSecs = "+endTimeEpochMilliSecs);
	}
	

}
