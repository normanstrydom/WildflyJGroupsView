package za.co.felixsol.wildfly.util;

import org.jboss.as.cli.Util;
import org.jboss.as.cli.operation.OperationFormatException;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestBuilder;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

public class WfJgroupsEEView {
    
    public static void main(String[] args) throws Exception {
	
	String user = "****";
	String password = "*************";
	int port = 9990;
//	String host = "workstation43-wf-20";
	String host = "workstation43-wf-39";
	
	try (ModelControllerClient wildflyClient = ModelControllerClientFactory.getClient(host, port, user,
		    password)) {
	    
	    DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
	    builder.addNode("host", "*");
	    builder.addNode("server", "*");
	    builder.addNode("subsystem", "jgroups");
	    builder.addNode("channel", "ee");
	    builder.setOperationName("query");
	    ModelNode request = builder.buildRequest();
            ModelNode outcome = wildflyClient.execute(request);
            
            System.out.println(outcome.toJSONString(false));
            
	}
	
    }

}
