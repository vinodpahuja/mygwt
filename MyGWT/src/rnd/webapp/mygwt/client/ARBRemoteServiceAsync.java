package rnd.webapp.mygwt.client;

import rnd.mywt.client.rpc.ApplicationRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ARBRemoteServiceAsync {

	void executeRequest(ApplicationRequest req, AsyncCallback callback);

}
