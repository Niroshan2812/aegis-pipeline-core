package MockPorvider;

import com.intelligent.devcore.strategy.WorkflowStrategy;

import java.util.List;
import java.util.Map;

public class MockVideoStatergy implements WorkflowStrategy {
    @Override
    public String getTargetType() {
        return "text/plain";
    }

    @Override
    public List<String> getRequiredAgents() {
        return List.of("MOCK_VISION");
    }

    @Override
    public double calculateFusionScore(Map<String, Double> agentResults) {
        return agentResults.getOrDefault("MOCK_VISION", 0.0);
    }
}
