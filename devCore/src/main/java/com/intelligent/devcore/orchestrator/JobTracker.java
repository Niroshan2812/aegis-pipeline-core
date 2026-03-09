package com.intelligent.devcore.orchestrator;

import javax.print.attribute.standard.JobState;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JobTracker {
    // use concurrentHashMap to store al active job in memory
    // enable automatically handle multiple threads reading and writing safely
    private final Map<String, JobState> activeJobs = new ConcurrentHashMap<>();

    public void createNewJob(String jobId, int expectedTasks){
        // initialize a JobState object when a file is uploaded, looking in the exact
        //number of AI agents we expect to hear back from.
        activeJobs.put(jobId, new JobState(expectedTasks));
    }

    public boolean addResultandCheckCompletion(String jobId, String agentID, double score){
        JobState state = activeJobs.get(jobId);
        if(state == null){
            throw new IllegalArgumentException("Job ID not found or already completed: "+ jobId);
        }
        // save the specific agent score into the job's  internal map.
        state.results.put(agentID, score);

        // Use atomicInteger to increment the completed task count. So if the two
        //agents finished simultaneously. the count is always perfectly accurate.
        int completed = state.completedTasks.incrementAndGet();
        return completed == state.expectedTasks;
    }

    public Map<String, Double> getJobResults(String jobId){
        return activeJobs.get(jobId).results;
    }

    public void cleanupJob(String jobId) {
        // remove the job from memory once fusion is complete to prevent memory leeks
        activeJobs.remove(jobId);
    }
    // innner class to hold the state --
    private static class JobState{
        final int expectedTasks;
        final AtomicInteger completedTasks;
        final Map<String, Double>results;

        public JobState(int expectedTasks) {
            this.expectedTasks = expectedTasks;
            this.completedTasks = new AtomicInteger(0);
            this.results = new ConcurrentHashMap<>();

        }
    }
}
