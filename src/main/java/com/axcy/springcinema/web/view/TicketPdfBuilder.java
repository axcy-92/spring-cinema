package com.axcy.springcinema.web.view;

import com.axcy.springcinema.entity.Ticket;
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
public class TicketPdfBuilder extends AbstractITextPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model,
                                    Document doc,
                                    PdfWriter writer,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        List<Ticket> ticketList = (List<Ticket>) model.get("ticketList");
        doc.add(new Paragraph("Book tickets for event: " + ticketList.get(0).getEvent().getName()));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100.0f);
        table.setWidths(new float[] {1.0f, 2.0f, 2.0f, 2.0f, 2.0f});
        table.setSpacingBefore(10);

        Font font = FontFactory.getFont(FontFactory.COURIER);
        font.setColor(BaseColor.WHITE);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.BLACK);
        cell.setPadding(5);

        cell.setPhrase(new Phrase("Event", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Date", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Auditorium", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Seat", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Price", font));
        table.addCell(cell);

        for (Ticket ticket : ticketList) {
            table.addCell(ticket.getEvent().getName());
            table.addCell(ticket.getEvent().getDateTime().toString());
            table.addCell(ticket.getEvent().getAuditorium().getName());
            table.addCell(ticket.getSeat().getNumber() + (ticket.getSeat().isVip() ? "(VIP)" : ""));
            table.addCell(String.valueOf(ticket.getPrice()));
        }

        doc.add(table);
    }
}
