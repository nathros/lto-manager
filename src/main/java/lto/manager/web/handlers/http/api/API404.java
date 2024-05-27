package lto.manager.web.handlers.http.api;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.JSON;
import lto.manager.web.resource.JSON.APIStatus;

public class API404 extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_API_BASE;

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		requestHandleCompleteAPIText404(he, JSON.populateAPIResponse(APIStatus.error, "404 - API request not found"), CONTENT_TYPE_JSON);
	}
}
