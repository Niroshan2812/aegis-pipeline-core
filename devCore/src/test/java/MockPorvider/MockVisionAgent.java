package MockPorvider;

import com.intelligent.devcore.agents.AgentContract;

public class MockVisionAgent implements AgentContract {
    @Override
    public String getAgentIdentifier() {
        return "MOCK_VISION";
    }

    @Override
    public double executeAnalysis(String secureFilePath) throws Exception {

        Thread.sleep(2000);
        return 0.85;
    }
}
