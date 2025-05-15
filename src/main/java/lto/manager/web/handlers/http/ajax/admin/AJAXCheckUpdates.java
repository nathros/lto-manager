package lto.manager.web.handlers.http.ajax.admin;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.Version;
import lto.manager.common.Util;
import lto.manager.common.Util.ContainerType;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.Link;

public class AJAXCheckUpdates extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "admin/update";
	public static final Permission PERMISSION = Permission.SYSTEM_SETTINGS_UPDATE;

	static Void content(Div<?> view, BodyModel model) {
		final String changeLogFile = "/tmp/" + UUID.randomUUID().toString();

		//f.getAbsolutePath()

		// Download changelog to temp file

		try (BufferedInputStream in = new BufferedInputStream(URI.create(Link.LINK_GITHUB_CHANGELOG).toURL().openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(changeLogFile)) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}

			final String changeLogContent = Files.readString(Path.of(changeLogFile), Charset.defaultCharset());
			final String latestVersion = changeLogContent.substring(changeLogContent.indexOf('[') + 1, changeLogContent.indexOf(']'));
			final ContainerType containerType = Util.runningInContainer();
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
					if (containerType != ContainerType.None) {
						o.div().of(innerD -> InlineMessage.contentGenericWarning(CSS.GROUP, innerD, "Update unavailable when running in " + containerType)).__();
					} else if (Version.VERSION.equals("UNDEFINED")) {
						o.div().of(innerD -> InlineMessage.contentGenericInfo(CSS.GROUP, innerD, "Cannot update development version")).__();
					} else if (canUpdate) {
						o.div().attrId("progress-inline").of(innerD -> InlineMessage.contentGenericOK(CSS.GROUP, innerD, "Update available")).__();
						o.div()
							.attrStyle("display:flex;gap:var(--padding)")
							.div()
								.attrClass(CSS.GROUP).attrStyle("flex:1").addAttr(CSS.GROUP_ATTRIBUTE, "Upgrade" )
								.button()
									.attrClass(CSS.BUTTON)
									.attrOnclick("startUpdate()")
									.text("Upgrade to " + latestVersion)
								.__()
								.textarea()
									.attrId("progress")
									.attrStyle("display:none")
									.attrClass(CSS.WIDTH_AVAILABLE)
									.attrStyle("height:10rem")
									.attrReadonly(true)
								.__()
							.__()
						.__();

					} else if (Version.VERSION.equals(latestVersion)) {
						o.div().of(innerD -> InlineMessage.contentGenericInfo(CSS.GROUP, innerD, "Already using latest version")).__();
					} else {
						o.div().of(innerD -> InlineMessage.contentGenericInfo(CSS.GROUP, innerD, "Already newer than latest version")).__();
					}
				})
				.div()
					.attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "Change Log")
					.textarea()
						.attrClass(CSS.WIDTH_AVAILABLE)
						.attrStyle("height:20rem")
						.attrReadonly(true)
						.text(changeLogContent)
					.__()
				.__();
		} catch (IOException e) {
			view.of(div -> InlineMessage.contentGenericError(div, "Failure in fetching updates", e.toString()));
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
