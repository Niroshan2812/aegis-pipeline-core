package strategy;

import com.intelligent.devcore.strategy.WorkflowStrategy;

import java.util.List;
import java.util.Map;

public class MockXmlStatergy implements WorkflowStrategy {
    @Override
    public String getTargetType() {
        return "application/xml";
    }

    @Override
    public List<String> getRequiredAgents() {
        return List.of("MOCK_XML_AGENT");
    }

    @Override
    public double calculateFusionScore(Map<String, Double> agentResults) {
        return agentResults.getOrDefault("MOCK_XML_AGENT", 0.0);
    }
}
