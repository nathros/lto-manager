package lto.manager.web.handlers.http.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.resource.JSON;
import lto.manager.web.resource.JSON.APIStatus;

public class APISystemInfo extends BaseHTTPHandler {
	public static final String PATH = "/api/systeminfo";

	private static final Long startTime = System.currentTimeMillis(); // Program start time

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		try {
			HashMap<String, Object> message = new HashMap<String, Object>();
			message.put("startTime", startTime); // Set program start time
			message.put("jvmMaxMemory", Util.getJVMMaxMemory());
			message.put("jvmAllocatedMemory", Util.getJVMAllocatedMemory());
			message.put("jvmUsedMemory", Util.getUsedMemory());

			requestHandleCompleteAPIText(he, JSON.populateAPIResponse(APIStatus.ok, message), CONTENT_TYPE_JSON);

		} catch (Exception e) {
			requestHandleCompleteAPIText(he, JSON.populateAPIResponse(APIStatus.error, e.getMessage()),
					CONTENT_TYPE_JSON);
		}
	}
}
