package lto.manager.web.handlers.http.pages.sandpit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class EchoPostHandler extends BaseHTTPHandler {

	public static final String PATH = "/echopost";

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException {
		// parse request
		Map<String, Object> parameters = new HashMap<String, Object>();
		//InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
		//BufferedReader br = new BufferedReader(isr);
		//String query = br.readLine();

		// send response
		String response = "";
		for (String key : parameters.keySet())
			response += key + " = " + parameters.get(key) + "\n";
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();

	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
