import agents.MockXMLAgens;
import org.apache.tika.Tika;
import org.springframework.mock.web.MockMultipartFile;
import MockPorvider.MockStorageProvider;
import MockPorvider.MockVideoStatergy;
import MockPorvider.MockVisionAgent;
import com.intelligent.devcore.agents.AgentContract;
import com.intelligent.devcore.gateway.SecureIngestionService;
import com.intelligent.devcore.gateway.StorageProvider;
import com.intelligent.devcore.orchestrator.JobManager;
import com.intelligent.devcore.orchestrator.JobTracker;
import com.intelligent.devcore.orchestrator.TaskDispatcher;
import com.intelligent.devcore.strategy.WorkflowStrategy;
import org.junit.Test;
import strategy.MockXmlStatergy;

import java.util.List;



public class PiplelineIntergrationTest {
    @Test
    public void testPiplelineIntergration() throws Exception {
        StorageProvider mockStorage = new MockStorageProvider();
        List<AgentContract> allAgents = List.of(new MockVisionAgent(), new MockXMLAgens());
        List<WorkflowStrategy> allStatergies = List.of(new MockVideoStatergy(), new MockXmlStatergy());

        List<String> allowedTypes = List.of("text/plain", "application/xml");
        org.apache.tika.Tika tika = new Tika();
        SecureIngestionService ingestionService = new SecureIngestionService(mockStorage, tika, allowedTypes);

        JobTracker jobTracker = new JobTracker();
        TaskDispatcher taskDispatcher = new TaskDispatcher(jobTracker);

        JobManager jobManager = new JobManager(ingestionService, jobTracker, taskDispatcher, allStatergies, allAgents);

        System.out.println("Start multy piple test");

        MockMultipartFile textUpload = new MockMultipartFile("file", "test.txt", "text/plain", "test text".getBytes());
        System.out.println("Uploading Text file ");
        String textJobId = jobManager.processUploadv(textUpload,"text/plain");

        // XML text
        MockMultipartFile xmlUpload = new MockMultipartFile("file", "data.xml", "application/xml", "<?xml version=\\\"1.0\\\"?><test>Hello</test>".getBytes());
        System.out.println("Uploading XML file ");
        String xmlJobId = jobManager.processUploadv(xmlUpload,"application/xml");

        System.out.println("\nWaiting for background AI agents to finish...");

        Thread.sleep(5000);
        System.out.println("Finished background AI agents to finish...");
    }
}
