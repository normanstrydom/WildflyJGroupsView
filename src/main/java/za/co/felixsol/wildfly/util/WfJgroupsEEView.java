package za.co.felixsol.wildfly.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.cli.Util;
import org.jboss.as.cli.operation.OperationFormatException;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestBuilder;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

public class WfJgroupsEEView {

    public static void main(String[] args) throws Exception {

	String user = System.getenv("WILDFLY_USERNAME");
	String password = System.getenv("WILDFLY_PASSWORD");
	int port = 9990;
	String host = "workstation43-wf-20";
//	String host = "workstation43-wf-39";

	List<String> initialTcpHosts = new ArrayList<>();

	try (ModelControllerClient wildflyClient = ModelControllerClientFactory.getClient(host, port, user,
		password)) {

	    if (true) {

		List<String> hosts = getHosts(wildflyClient);
		for (String hostName : hosts) {
		    List<String> servers = getservers(wildflyClient, hostName);

		    for (String serverName : servers) {
			DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
			builder.addNode("host", hostName);
			builder.addNode("server", serverName);
			builder.addNode("subsystem", "jgroups");
			builder.addNode("channel", "ee");
			builder.addNode("protocol", "TCP");
			builder.setOperationName("query");
			ModelNode request = builder.buildRequest();
			ModelNode outcome = wildflyClient.execute(request);
			if (Util.isSuccess(outcome)) {

			    initialTcpHosts.add(outcome.get("result").get("bind_addr").asString().replaceAll("/", "")
				    + "[" + outcome.get("result").get("bind_port").asString() + "]");

			}
		    }

		}
	    }
	    
	    System.out.println("Initial TCP Hosts: ");
	    System.out.println();
	    
	    for (int idx=0; idx<initialTcpHosts.size(); idx++) {
		System.out.print(initialTcpHosts.get(idx));
		if (idx < initialTcpHosts.size() -1) {
		    System.out.print(",");
		}
	    }
	    
	    System.out.println();
	    System.out.println();

	    {

		DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
		builder.addNode("host", "*");
		builder.addNode("server", "*");
		builder.addNode("subsystem", "jgroups");
		builder.addNode("channel", "ee");
		builder.setOperationName("query");
		ModelNode request = builder.buildRequest();
		ModelNode outcome = wildflyClient.execute(request);

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

    private static List<String> getHosts(ModelControllerClient wildflyClient)
	    throws OperationFormatException, Exception {
	List<String> hosts = Util.getHosts(wildflyClient);
	return hosts;
    }

    private static List<String> getservers(ModelControllerClient wildflyClient, String hostName)
	    throws OperationFormatException, Exception {
	List<String> servers = new ArrayList<>();
	DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
	builder.addNode("host", hostName);
	builder.addNode("server", "*");
	builder.setOperationName("query");
	ModelNode request = builder.buildRequest();
	ModelNode outcome = wildflyClient.execute(request);
	if (Util.isSuccess(outcome)) {
	    outcome.get("result").asList().forEach(node -> {
		String serverName = node.get("address").asPropertyList().get(1).getValue().asString();
		servers.add(serverName);
	    });
	}
	return servers;
    }

}
