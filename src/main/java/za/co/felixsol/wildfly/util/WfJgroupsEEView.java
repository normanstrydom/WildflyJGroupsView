package za.co.felixsol.wildfly.util;

import org.jboss.as.cli.Util;
import org.jboss.as.cli.operation.OperationFormatException;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestBuilder;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

public class WfJgroupsEEView {

    public static void main(String[] args) throws Exception {

	String user = "*****";
	String password = "************";
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

	    StringBuilder sb = new StringBuilder();
	    outcome.get("result").asList().forEach(node -> {
		String hostName = node.get("address").asPropertyList().get(0).getValue().asString();
		String serverName = node.get("address").asPropertyList().get(1).getValue().asString();
		String view = node.get("result").get("view").asString();
		sb.append(String.format("%s/%s\n", hostName, serverName));
		sb.append(String.format("\t%s\n", view));
	    });
	    System.out.println(sb.toString());

	}

    }

}
