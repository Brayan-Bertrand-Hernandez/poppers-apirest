package com.project.springapirest.controller;

import com.project.springapirest.factory.UserFactory;
import com.project.springapirest.model.Product;
import com.project.springapirest.model.Ticket;
import com.project.springapirest.model.User;
import com.project.springapirest.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200", "https://poppers-app-angular.web.app", "https://poppers-app-angular.web.app/", "*", "**"})
@RestController
@RequestMapping("/api")
public class TicketRestController {
    @Autowired
    private UserFactory userFactory;

    @Autowired
    private EmailService emailService;

    @GetMapping("/tickets/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public Ticket show(@PathVariable Long id) {
        return userFactory.findTicketById(id);
    }

    @DeleteMapping("/tickets/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Ticket newTicket = userFactory.findTicketById(id);

        User user = userFactory.findById(newTicket.getUser().getId());

        String name = user.getName();
        String email = user.getEmail();

        String message = "\n\nEstimado "+name+" su compra ha sido cancelada!";

        emailService.sendMail(email,"Ticket cancelado",message);

        userFactory.deleteTicketById(id);
    }

    @GetMapping("/tickets/product-filter/{term}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Product> filteredProducts(@PathVariable String term){
        return userFactory.findProductByName(term);
    }

    @PostMapping("/tickets")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Ticket create(@RequestBody Ticket ticket) {
        ticket.setChecked(false);

        return userFactory.saveTicket(ticket);
    }

    @PutMapping("/tickets/{id}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Ticket update(@RequestBody Ticket ticket, @PathVariable Long id) {
        Ticket newTicket = userFactory.findTicketById(id);

        newTicket.setChecked(true);

        User user = userFactory.findById(newTicket.getUser().getId());

        String name = user.getName();
        String email = user.getEmail();
        String total = newTicket.getTotal().toString();

        String message = "\n\nEstimado "+name+" su ticket ha sido aceptado!\nTotal de compra: $" + total;

        emailService.sendMail(email,"Ticket aceptado con éxito",message);

        return userFactory.saveTicket(newTicket);
    }
}
