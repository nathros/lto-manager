package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ImgTemplate;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;

public class AJAXGenerateLTOLabelPDF extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "generate/lto/label/pdf/";
	private static final int PAGE_TOP_Y = 750;
	private static final int X_OFFSET_FIRST = 70;
	private static final int X_OFFSET_SECOND = 300;

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception { // FIXME finish PDF scale is wrong
		List<String> labelsSVGs = GenerateLTOLabelSVG.generate(LTOLabelOptions.of(bm));

		Document document = new Document();
		var stream = new ByteArrayOutputStream();
		try {
			var pdfwriter = PdfWriter.getInstance(document, stream);

			document.open();

			SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
			int position = PAGE_TOP_Y;
			int count = 0;

			for (final String svg : labelsSVGs) {
				// final StringReader reader = new StringReader(
				// AssetHandler.getResourceAsString(Asset.IMG_LTO_LABEL + "test.svg"));
				final StringReader reader = new StringReader(svg);
				var SVGDocument = factory.createSVGDocument("", reader);

				UserAgent userAgent = new UserAgentAdapter();
				DocumentLoader loader = new DocumentLoader(userAgent);

				BridgeContext context = new BridgeContext(userAgent, loader);
				context.setDynamicState(BridgeContext.DYNAMIC);

				GVTBuilder builder = new GVTBuilder();
				GraphicsNode rootGraphicsNode = builder.build(context, SVGDocument);

				PdfTemplate template = PdfTemplate.createTemplate(pdfwriter, 310, 100);

				Graphics2D g2d = template.createGraphics(template.getWidth(), template.getHeight(), null);
				var newBarcode = new ImgTemplate(template);
				// Not sure why the size is wrong, the SVG size is correct. width="80.5mm" height="18.5mm"
				newBarcode.scalePercent(75);

				if (count % 32 == 0 && count != 0) {
					document.newPage();
					count = 0;
					position = PAGE_TOP_Y;
					newBarcode.setAbsolutePosition(X_OFFSET_FIRST, position);
				} else if (count % 2 == 0) {
					newBarcode.setAbsolutePosition(X_OFFSET_FIRST, position);
				} else {
					newBarcode.setAbsolutePosition(X_OFFSET_SECOND, position);
					position -= 50;
				}

				document.add(newBarcode);

				try {
					rootGraphicsNode.paint(g2d);
				} finally {
					g2d.dispose();
				}

				count++;
			}

		} catch (DocumentException | IOException de) {
			System.err.println(de.getMessage());
		}

		document.close();

		requestHandleCompleteAPIBinary(he, stream, BaseHTTPHandler.CONTENT_TYPE_PDF);
	}

	@Override
	public Permission getHandlePermission() {
		return null;
	}
}
