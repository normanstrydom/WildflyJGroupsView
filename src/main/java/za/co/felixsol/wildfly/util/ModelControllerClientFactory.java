package za.co.felixsol.wildfly.util;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.ModelControllerClientConfiguration;

public class ModelControllerClientFactory {

    public static ModelControllerClient getClient() throws UnknownHostException {
	return ModelControllerClient.Factory.create("localhost", 9990);
    }

    public static ModelControllerClient getClient(String host, int port, String username, String password)
	    throws UnknownHostException {
	ModelControllerClientConfiguration config = getClientConfiguration(host, port, username, password);
	return ModelControllerClient.Factory.create(config);
    }

    private static ModelControllerClientConfiguration getClientConfiguration(String host, int port, String username,
	    String password) {
	return new ModelControllerClientConfiguration.Builder()
		.setHostName(host)
		.setPort(port)
		.setConnectionTimeout(120000)
		.setHandler(
			new CallbackHandler() {

			    @Override
			    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
				for (Callback callback : callbacks) {
				    if (callback instanceof NameCallback) {
					((NameCallback) callback).setName(username);
				    } else if (callback instanceof PasswordCallback) {
					((PasswordCallback) callback).setPassword(password.toCharArray());
				    } else if (callback instanceof RealmCallback) {
					((RealmCallback) callback).setText("ManagementRealm");
				    }
				}
			    }
			    
			})
		.build();
    }

}
