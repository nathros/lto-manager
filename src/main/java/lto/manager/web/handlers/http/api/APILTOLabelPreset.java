package lto.manager.web.handlers.http.api;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.JSON;
import lto.manager.web.resource.JSON.APIStatus;
import lto.manager.web.resource.JSON.JSONMap;

public class APILTOLabelPreset extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_API_BASE + "ltolabelpreset/";

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		try {
			final BodyModel bm = BodyModel.of(he, null);
			final String operation = bm.getQueryNoNull("op");
			final String id = bm.getQuery("id");
			if (id == null)
			{
				throw new Exception("User id is missing");
			}
			// final int userId = Integer.parseInt(id);

			JSONMap json = new JSONMap();
			json.set("startTime", "startTime");

			if (operation.equals("add")) { // FIXME finish

			} else if (operation.equals("update")) {

			} else if (operation.equals("delete")) {

			} else {
				throw new Exception("Unknown operation: " + operation);
			}

			requestHandleCompleteAPIText(he, JSON.populateAPIResponse(APIStatus.ok, json), CONTENT_TYPE_JSON);

		} catch (Exception e) {
			requestHandleCompleteAPIText(he,
					JSON.populateAPIResponse(APIStatus.error, e.getMessage().replaceAll("\"", "'")), CONTENT_TYPE_JSON);
		}
	}
}
