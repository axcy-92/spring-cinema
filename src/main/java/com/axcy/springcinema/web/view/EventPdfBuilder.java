package com.axcy.springcinema.web.view;

import com.axcy.springcinema.entity.Event;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Aleksei_Cherniavskii
 */
public class EventPdfBuilder extends AbstractITextPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model,
                                    Document doc,
                                    PdfWriter writer,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        List<Event> eventList = (List<Event>) model.get("eventList");
        doc.add(new Paragraph("Events:"));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100.0f);
        table.setWidths(new float[] {1.0f, 2.0f, 2.0f, 2.0f, 2.0f, 3.0f});
        table.setSpacingBefore(10);

        Font font = FontFactory.getFont(FontFactory.COURIER);
        font.setColor(BaseColor.WHITE);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.BLACK);
        cell.setPadding(5);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Date", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Price", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Rating", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Auditorium", font));
        table.addCell(cell);

        for (Event event : eventList) {
            table.addCell(event.getId().toString());
            table.addCell(event.getName());
            table.addCell(event.getDateTime().toString());
            table.addCell(String.valueOf(event.getTicketPrice()));
            table.addCell(event.getRating().name());
            table.addCell(event.getAuditorium().getName());
        }

        doc.add(table);
    }
}