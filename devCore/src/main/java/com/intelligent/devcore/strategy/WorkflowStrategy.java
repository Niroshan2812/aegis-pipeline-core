package com.intelligent.devcore.strategy;

import java.util.List;
import java.util.Map;

public interface WorkflowStrategy {

    // target trigger for strategy so the orchestrator knows when to use it
    // for example returning video/mp4 or reverse_engineering _task
    String getTargetType();

    // return list of specific agent identifiers that are required for workflow
    // enable communication to scatter phase how many tasks to create
    List<String> getRequiredAgents();

    // final map of scores gathered from all successful AI agents and
    // apply custom mathematical weight to produced one final probability score.
    double calculateFusionScore(Map<String, Double> agentResults);
}
