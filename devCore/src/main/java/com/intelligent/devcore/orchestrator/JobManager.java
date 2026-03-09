package com.intelligent.devcore.orchestrator;

import com.intelligent.devcore.agents.AgentContract;
import com.intelligent.devcore.gateway.SecureIngestionService;
import com.intelligent.devcore.strategy.WorkflowStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JobManager {

    private final SecureIngestionService ingestionService;
    private final JobTracker jobTracker;
    private final TaskDispatcher taskDispatcher;
    private final List<WorkflowStrategy> availableStatergies;
    private final List<AgentContract> availableAgents;

    public JobManager(SecureIngestionService ingestionService, JobTracker jobTracker, TaskDispatcher taskDispatcher, List<WorkflowStrategy> statergies, List<AgentContract> agents) {
        this.ingestionService = ingestionService;
        this.jobTracker = jobTracker;
        this.taskDispatcher = taskDispatcher;
        this.availableStatergies = statergies;
        this.availableAgents = agents;
    }

    public String processUploadv(MultipartFile file, String targetType) throws Exception {

        // pass the raw  upload to security part.
        // strip the filename, and stream it into storage, it returns the safe internally path
        String secureFilePath = ingestionService.processUpload(file);
        String jobId = java.util.UUID.randomUUID().toString();

        // STATERGY THAT MATCH THE FILE type
        // then filter our available AI agents, to only include the ones this strategy required
        WorkflowStrategy activeStrategy = availableStatergies.stream()
                .filter(s ->s.getTargetType().equals(targetType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for: " + targetType));

        List<AgentContract> requiredAgents = availableAgents.stream()
                .filter(a -> activeStrategy.getRequiredAgents().contains(a.getAgentIdentifier()))
                .collect(Collectors.toList());

        // register new job in thread-safe tracker, locking in extract
        //number of background agents waiting for
        jobTracker.createNewJob(jobId,requiredAgents.size());

        // scatter tasks to the background thread pool, also pass a callback function
        // that will only trigger when the very last agent report its score.
        taskDispatcher.dispatchTasks(jobId, secureFilePath,requiredAgents, ()->{

            // Fusion Phase -- triggered async
            Map<String, Double> finalScore = jobTracker.getJobResults(jobId);
            double humanPobability = activeStrategy.calculateFusionScore(finalScore);
            System.out.println("Job" + jobId + " Complete final Score: "+ humanPobability);

            jobTracker.cleanupJob(jobId);
        });
        // return the job id to the user so they can track progress
        return jobId;


    }

}



