package lto.manager.web.handlers.http.api;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.JSON;
import lto.manager.web.resource.JSON.APIStatus;
import lto.manager.web.resource.JSON.JSONMap;

public class APISystemInfo extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_API_BASE + "systeminfo/";

	public static final Long startTime = System.currentTimeMillis(); // Program start time

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		try {
			JSONMap json = new JSONMap();
			json.set(startTime, "startTime");
			json.set(System.currentTimeMillis(), "nowTime");
			json.set(Util.getJVMMaxMemory(), "jvmMaxMemory");
			json.set(Util.getJVMAllocatedMemory(), "jvmAllocatedMemory");
			json.set(Util.getUsedMemory(), "jvmUsedMemory");

			requestHandleCompleteAPIText(he, JSON.populateAPIResponse(APIStatus.ok, json), CONTENT_TYPE_JSON);

		} catch (Exception e) {
			requestHandleCompleteAPIText(he,
					JSON.populateAPIResponse(APIStatus.error, e.getMessage().replaceAll("\"", "'")), CONTENT_TYPE_JSON);
		}
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}
}
