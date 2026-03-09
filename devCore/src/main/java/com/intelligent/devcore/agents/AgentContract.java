package com.intelligent.devcore.agents;

public interface AgentContract {
   String getAgentIdentifier();
   double executeAnalysis(String secureFilePath) throws Exception;
}
