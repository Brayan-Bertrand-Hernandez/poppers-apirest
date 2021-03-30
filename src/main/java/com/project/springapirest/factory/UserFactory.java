package com.project.springapirest.factory;

import com.project.springapirest.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserFactory {
    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Page<User> findAllClients(Pageable pageable);

    Page<User> findAllEnabledClients(Pageable pageable);

    Page<User> findAllAdministration(Pageable pageable);

    User findById(Long id);

    User save(User user);

    void delete(Long id);

    List<Country> findAllCountries();

    User findByUsername(String username);

    User findByEmail(String email);

    User findByPhoto(String photoName);

    List<User> findAllByRole();

    Ticket findTicketById(Long id);

    Ticket saveTicket(Ticket ticket);

    Ticket findTicketByPdf(String pdfName);

    List<Ticket> findTicketsById(Long id);

    List<Ticket> findAllTickets();

    void deleteTicketById(Long id);

    List<Product> findProductByName(String term);
}