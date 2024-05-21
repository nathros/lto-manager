package lto.manager.web.handlers.http.api;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.JSON;
import lto.manager.web.resource.JSON.APIStatus;

public class APIVirtualDir extends BaseHTTPHandler {
	public static final String PATH = "/api/virtualdir/";

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		final BodyModel bm = BodyModel.of(he, null);
		final String operation = bm.getQueryNoNull("op");

		final String newDirName = bm.getQueryNoNull("name");
		if ((newDirName == null) || (newDirName.length() == 0)) {
			requestHandleCompleteAPIText(he, JSON.populateAPIResponse(APIStatus.error, "Directory name empty"), CONTENT_TYPE_JSON);
			return;
		} else {
			final String basePath = bm.getQueryNoNull("path");
			if ((basePath == null) || (basePath.length() == 0)) {
				requestHandleCompleteAPIText(he, JSON.populateAPIResponse(APIStatus.error, "Directory path empty"), CONTENT_TYPE_JSON);
				return;
			}
			try {
				switch (operation) {
				case "new":
					if (Database.addVirtualDir(basePath, newDirName)) {
						requestHandleCompleteAPIText(he,
								JSON.populateAPIResponse(APIStatus.ok, APIStatus.ok.toString()), CONTENT_TYPE_JSON);
						return;
					}
					throw new Exception("Failed to add new virtual directory: " + basePath + newDirName);

				case "del":
					if (Database.delVirtualDir(basePath)) {
						requestHandleCompleteAPIText(he,
								JSON.populateAPIResponse(APIStatus.ok, APIStatus.ok.toString()), CONTENT_TYPE_JSON);
						return;
					}
					throw new Exception("Failed to delete virtual directory: " + basePath + newDirName);

				case "rename":
					if (Database.renameVirtualDir(basePath, newDirName)) {
						requestHandleCompleteAPIText(he,
								JSON.populateAPIResponse(APIStatus.ok, APIStatus.ok.toString()), CONTENT_TYPE_JSON);
						return;
					}
					throw new Exception("Failed to rename virtual directory: " + basePath + newDirName);

				case "changeico":
					if (Database.chageVirtualDirIcon(basePath, newDirName)) {
						requestHandleCompleteAPIText(he,
								JSON.populateAPIResponse(APIStatus.ok, APIStatus.ok.toString()), CONTENT_TYPE_JSON);
						return;
					}
					throw new Exception("Failed to rename virtual directory: " + basePath + newDirName);

				default:
					throw new Exception("Invalid op code: " + operation);
				}

			} catch (Exception e) {
				requestHandleCompleteAPIText(he, JSON.populateAPIResponse(APIStatus.error, e.getMessage()), CONTENT_TYPE_JSON);
			}
		}

	}
}
