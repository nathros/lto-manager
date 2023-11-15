package lto.manager.web.handlers.http.api;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.JSON;
import lto.manager.web.resource.JSON.APIStatus;

public class APIVirtualDir extends BaseHTTPHandler {
	public static final String PATH = "/api/virtualdir";

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		final BodyModel bm = BodyModel.of(he, null);
		final String newDirName = bm.getQueryNoNull("name");
		String response;
		if ((newDirName == null) || (newDirName.length() == 0)) {
			response = JSON.populateAPIResponse(APIStatus.error, "Directory name empty");
		} else {
			response = ""; // TODO add new dir here
		}
		requestHandleCompleteAPIJSON(he, response);
	}
}
