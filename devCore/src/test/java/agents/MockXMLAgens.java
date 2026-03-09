package agents;

import com.intelligent.devcore.agents.AgentContract;

public class MockXMLAgens implements AgentContract {
    @Override
    public String getAgentIdentifier() {
        return "MOCK_XML_AGENT";
    }

    @Override
    public double executeAnalysis(String secureFilePath) throws Exception {
        Thread.sleep(1500);
        return 0.99;
    }
}
