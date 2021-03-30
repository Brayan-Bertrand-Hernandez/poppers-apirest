package com.project.springapirest.repository;

import com.project.springapirest.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByUserId(Long id);
    Ticket findByPdf(String pdfName);
}
