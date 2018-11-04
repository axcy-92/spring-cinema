package com.axcy.springcinema.web.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author Aleksei_Cherniavskii
 */
public abstract class AbstractITextPdfView extends AbstractView {

    AbstractITextPdfView() {
        setContentType("application/pdf");
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        ByteArrayOutputStream byteTemporaryOutputStream = createTemporaryOutputStream();
        Document document =  new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, byteTemporaryOutputStream);
        writer.setViewerPreferences(PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage);
        //buildPdfMetadata(model, document, request);

        document.open();
        buildPdfDocument(model, document, writer, request, response);
        document.close();

        writeToResponse(response, byteTemporaryOutputStream);
    }


    /*
    protected void buildPdfMetadata(Map<String, Object> model,
                                    Document document,
                                    HttpServletRequest request) {
    }
    */

    protected abstract void buildPdfDocument(Map<String, Object> model,
                                             Document document,
                                             PdfWriter writer,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception;
}
