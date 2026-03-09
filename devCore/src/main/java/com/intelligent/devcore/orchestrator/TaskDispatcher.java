package com.intelligent.devcore.orchestrator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.intelligent.devcore.agents.AgentContract;

public class TaskDispatcher {

    // A fixed thread pool to limit how many AI task can run simultaneously
    // help to prevent the JVM crashing
    // --########################################################   MOdify note
    // Add 10 as a dff
    private final ExecutorService aiThreadPool = Executors.newFixedThreadPool(10);
    private final JobTracker jobTracker;

    public TaskDispatcher(JobTracker jobTracker) {
        this.jobTracker = jobTracker;
    }
    public void dispatchTasks(String jobId, String secureFilePath, List<AgentContract> requiredAgents, Runnable onCompleteAction){
      for (AgentContract agent: requiredAgents) {
          // use completableFuture to push the heavy AI  processing to background thread,
          // frees up the main application thread to accept more user uploads.
          CompletableFuture.supplyAsync(()->{
              try {
                  return agent.executeAnalysis(secureFilePath);
              }catch(Exception e){
                  System.err.println("Agent " + agent.getAgentIdentifier() + "failed "+  e.getMessage());
                  return 0.0;
              }

          }, aiThreadPool)
                  // Once AI Finished the math, it automatically jump here
                  // to update the Redis-style scoreboard. If it is the last agent to finish,
                  // it triggers the final fusion action.
                  .thenAccept(score -> {
                    boolean isComplete = jobTracker.addResultandCheckCompletion(jobId, agent.getAgentIdentifier(), score);
                    if(isComplete){
                        onCompleteAction.run();
                    }
                  });
      }
    }
}

