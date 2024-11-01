package lto.manager.web.handlers.http.ajax.admin;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.Version;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class AJAXCheckUpdates extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "admin/update";
	public static final Permission PERMISSION = Permission.SYSTEM_SETTINGS_UPDATE;
	private static final String CHANGELOG_URL = "https://github.com/nathros/release-test/releases/latest/download/Changelog";

	static Void content(Div<?> view, BodyModel model) {
		final String changeLogFile = "/tmp/" + UUID.randomUUID().toString();

		// Download changelog to temp file
		try (BufferedInputStream in = new BufferedInputStream(new URL(CHANGELOG_URL).openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(changeLogFile)) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}

			final String changeLogContent = Files.readString(Path.of(changeLogFile), Charset.defaultCharset());
			final String latestVersion = changeLogContent.substring(changeLogContent.indexOf('[') + 1, changeLogContent.indexOf(']'));
			final boolean canUpdate = Arrays.asList(Version.VERSION, latestVersion).stream().sorted().findFirst().get().equals(Version.VERSION);
			view
				.div()
					.attrStyle("display:flex;gap:var(--padding)")
					.div()
						.attrClass(CSS.GROUP).attrStyle("flex:1").addAttr(CSS.GROUP_ATTRIBUTE, "Current Version")
						.text(Version.VERSION)
					.__()
					.div()
						.attrClass(CSS.GROUP).attrStyle("flex:1").addAttr(CSS.GROUP_ATTRIBUTE, "Latest Version")
						.text(latestVersion)
					.__()
				.__()
				.of(o -> {
					if (Version.VERSION.equals("UNDEFINED")) {
						o.div().of(innerD -> InlineMessage.contentGenericInfo(CSS.GROUP, innerD, "Cannot update development version")).__();
					} else if (canUpdate) {
						o.div().of(innerD -> InlineMessage.contentGenericOK(CSS.GROUP, innerD, "Update available")).__();
					} else if (Version.VERSION.equals(latestVersion)) {
						o.div().of(innerD -> InlineMessage.contentGenericInfo(CSS.GROUP, innerD, "Already using latest version")).__();
					} else {
						o.div().of(innerD -> InlineMessage.contentGenericInfo(CSS.GROUP, innerD, "Already newer than latest version")).__();
					}
				})
				.div()
					.attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "Change Log")
					.textarea()
						.attrStyle("width:calc(100% - var(--padding) * 2);height:20rem;resize:vertical;")
						.attrReadonly(true)
						.text(changeLogContent)
					.__()
				.__();
		} catch (IOException e) {
			view.of(div -> InlineMessage.contentGenericError(div, "Unable to fetch updates", e.toString()));
		}

		try { // Cleanup
			Files.deleteIfExists(Path.of(changeLogFile));
		} catch (IOException e) {}
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXCheckUpdates::content, bm));
	}

	@Override
	public Permission getHandlePermission() {
		return PERMISSION;
	}
}
