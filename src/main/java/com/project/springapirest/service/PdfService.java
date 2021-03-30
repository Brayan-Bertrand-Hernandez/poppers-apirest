package com.project.springapirest.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

import com.project.springapirest.model.Ticket;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

@Service
public class PdfService {
    public ByteArrayInputStream ticketPDFReport(Ticket ticket) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
            Paragraph para = new Paragraph( "Tabla de pago", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);

            Stream.of("ID", "Descripción", "Observaciones", "Fecha", "Total", "Revisado")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setBorderWidth(1);
                        header.setPhrase(new Phrase(headerTitle, headFont));
                        table.addCell(header);
                    });

            PdfPCell idCell = new PdfPCell(new Phrase(ticket.getId().toString()));
            idCell.setPaddingLeft(3);
            idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(idCell);

            PdfPCell descriptionCell = new PdfPCell(new Phrase(ticket.getDescription()));
            descriptionCell.setPaddingLeft(3);
            descriptionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            descriptionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(descriptionCell);

            PdfPCell commentCell = new PdfPCell(new Phrase(ticket.getComment()));
            commentCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            commentCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            commentCell.setPaddingRight(3);
            table.addCell(commentCell);

            PdfPCell dateCell = new PdfPCell(new Phrase(String.valueOf(ticket.getDate())));
            dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            dateCell.setPaddingRight(3);
            table.addCell(dateCell);

            PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(ticket.getTotal())));
            quantityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            quantityCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            quantityCell.setPaddingRight(3);
            table.addCell(quantityCell);

            PdfPCell checkedCell;

            if(ticket.getChecked()) {
                checkedCell = new PdfPCell(new Phrase("Revisado"));
            } else {
                checkedCell = new PdfPCell(new Phrase("En espera"));
            }

            checkedCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            checkedCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            checkedCell.setPaddingRight(4);
            table.addCell(checkedCell);
            document.add(table);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream ticketsPDFReport(List<Ticket> tickets) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
            Paragraph para = new Paragraph( "Tabla de pagos", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);

            Stream.of("ID", "Descripción", "Observaciones", "Fecha", "Total", "Revisado")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setBorderWidth(1);
                        header.setPhrase(new Phrase(headerTitle, headFont));
                        table.addCell(header);
                    });

            for(Ticket ticket: tickets) {
                PdfPCell idCell = new PdfPCell(new Phrase(ticket.getId().toString()));
                idCell.setPaddingLeft(3);
                idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                PdfPCell descriptionCell = new PdfPCell(new Phrase(ticket.getDescription()));
                descriptionCell.setPaddingLeft(3);
                descriptionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                descriptionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(descriptionCell);

                PdfPCell commentCell = new PdfPCell(new Phrase(ticket.getComment()));
                commentCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                commentCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                commentCell.setPaddingRight(3);
                table.addCell(commentCell);

                PdfPCell dateCell = new PdfPCell(new Phrase(String.valueOf(ticket.getDate())));
                dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                dateCell.setPaddingRight(3);
                table.addCell(dateCell);

                PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(ticket.getTotal())));
                quantityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                quantityCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                quantityCell.setPaddingRight(3);
                table.addCell(quantityCell);

                PdfPCell checkedCell;

                if(ticket.getChecked()) {
                    checkedCell = new PdfPCell(new Phrase("Revisado"));
                } else {
                    checkedCell = new PdfPCell(new Phrase("En espera"));
                }

                checkedCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                checkedCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                checkedCell.setPaddingRight(4);
                table.addCell(checkedCell);
            }
            document.add(table);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
