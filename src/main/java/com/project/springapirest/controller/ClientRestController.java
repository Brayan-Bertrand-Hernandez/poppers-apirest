package com.project.springapirest.controller;

import com.project.springapirest.model.Ticket;
import com.project.springapirest.model.User;
import com.project.springapirest.model.Country;
import com.project.springapirest.model.Role;
import com.project.springapirest.repository.RoleRepository;
import com.project.springapirest.service.EmailService;
import com.project.springapirest.service.PdfService;
import com.project.springapirest.service.UserService;
import com.project.springapirest.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200", "https://poppers-app-angular.web.app", "https://poppers-app-angular.web.app/", "*", "**"})
@RestController
@RequestMapping("/api")
public class ClientRestController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String MESSAGE = "Mensaje";
    private static final String ERROR = "Error";
    private static final String OBJ = "Cliente";
    private static final String OBJ_SOCIAL= "Social";
    private Map<String, Object> response;

    @Secured({"ROLE_ADMIN", "ROLE_SELLER"})
    @GetMapping("/clients")
    public List<User> index() {
        return userService.findAll();
    }

    @Secured({"ROLE_ADMIN", "ROLE_SELLER"})
    @GetMapping("/clients/page/{page}")
    public Page<User> index(@PathVariable Integer page) {
        return userService.findAll(PageRequest.of(page, 5 ));
    }

    @Secured({"ROLE_ADMIN", "ROLE_SELLER"})
    @GetMapping("/clients/role/page/{page}")
    public Page<User> indexClients(@PathVariable Integer page) {
        return userService.findAllClients(PageRequest.of(page, 5 ));
    }

    @Secured({"ROLE_ADMIN", "ROLE_SELLER"})
    @GetMapping("/clients/role/enabled/page/{page}")
    public Page<User> indexEnabledClients(@PathVariable Integer page) {
        return userService.findAllEnabledClients(PageRequest.of(page, 5 ));
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/clients/administration/page/{page}")
    public Page<User> indexAdministration(@PathVariable Integer page) {
        return userService.findAllAdministration(PageRequest.of(page, 5 ));
    }

    @GetMapping("/clients/email/{email}")
    public ResponseEntity<?> showEmail(@PathVariable String email) {
        User currentUser;
        response = new HashMap<>();

        try {
            currentUser = userService.findByEmail(email);
        } catch (DataAccessException e) {
            String problem_1 = "Error al realizar la consulta en la base de datos.";
            String problem_2 = "Dato invalido.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(currentUser == null) {
            String header = "El cliente email: ";
            String problem = " no existe en la base de datos!";

            response.put(MESSAGE, header.concat(email.toString().concat(problem)));

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }


    @GetMapping("/clients/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        User currentUser;
        response = new HashMap<>();

        try {
            currentUser = userService.findById(id);
        } catch (DataAccessException e) {
            String problem_1 = "Error al realizar la consulta en la base de datos.";
            String problem_2 = "Dato invalido.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(currentUser == null) {
            String header = "El cliente ID: ";
            String problem = " no existe en la base de datos!";

            response.put(MESSAGE, header.concat(id.toString().concat(problem)));

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @GetMapping("/clients/enabled/{email}")
    public ResponseEntity<?> enabled(@PathVariable String email) {
        User currentUser;
        User enabledUser;
        response = new HashMap<>();

        try {
            currentUser = userService.findByEmail(email);
            currentUser.setEnabled(true);

            enabledUser = userService.save(currentUser);
        } catch (DataAccessException e) {
            String problem_1 = "Error al realizar la consulta en la base de datos.";
            String problem_2 = "Dato invalido.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(enabledUser == null) {
            String header = "El cliente ID: ";
            String problem = " no existe en la base de datos!";

            response.put(MESSAGE, header.concat(email.concat(problem)));

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(enabledUser, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/clients")
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult bindingResult) {
        User newUser;
        response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            return binding(bindingResult);
        }

        try {
            String password = user.getPassword();

            if(password.length() <= 8) {
                String passwordEncoded = bCryptPasswordEncoder.encode(password);

                user.setPassword(passwordEncoded);
            }

            user.setEnabled(true);

            String message = "\n\nEstimado "+user.getUsername()+" se le ha registrado con éxito en nuestra plataforma, disfrutelo!";

            emailService.sendMail(user.getEmail(),"Bienvenido",message);

            newUser = userService.save(user);
        } catch (DataAccessException e) {
            String problem_1 = "Error al realizar la inserción en la base de datos.";
            String problem_2 = "Email en uso.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String success = "El cliente ha sido creado con éxito!";

        response.put(MESSAGE, success);
        response.put(OBJ, newUser);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/clients/sign-up")
    public ResponseEntity<?> createClient(@Valid @RequestBody User user, BindingResult bindingResult) {
        User newClient;
        response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            return binding(bindingResult);
        }

        try {
            String passwordEncoded = bCryptPasswordEncoder.encode(user.getPassword());

            user.setPassword(passwordEncoded);

            user.setEnabled(true);

            List<Role> roles = roleRepository.findAllRoles();

            user.setRole(roles.get(roles.size()-2));

            newClient = userService.save(user);
        } catch (DataAccessException e) {
            String problem_1 = "Error al realizar la inserción en la base de datos.";
            String problem_2 = "Email en uso.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String success = "El cliente ha sido creado con éxito!";

        String message = "\n\nEstimado "+user.getUsername()+" se ha registrado con éxito en nuestra plataforma!\nComo último paso verifique su cuente en: \n http://localhost:4200/clients/enabled/"+user.getEmail();

        emailService.sendMail(user.getEmail(),"Confirmación de creación",message);

        response.put(MESSAGE, success);
        response.put(OBJ, newClient);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/clients/social")
    public ResponseEntity<?> createSocial(@Valid @RequestBody User user, BindingResult bindingResult) {
        User newSocial;
        response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            return binding(bindingResult);
        }

        try {
            String passwordEncoded = bCryptPasswordEncoder.encode(user.getPassword());

            user.setPassword(passwordEncoded);

            List<Role> roles = roleRepository.findAllRoles();

            user.setRole(roles.get(roles.size()-2));
            user.setEnabled(true);

            String message = "\n\nEstimado "+user.getUsername()+" se ha registrado con éxito en nuestra plataforma, disfrutelo!";

            emailService.sendMail(user.getEmail(),"Confirmación de creación",message);

            newSocial = userService.save(user);
        } catch (DataAccessException e) {
            String problem_1 = "Error al realizar la inserción en la base de datos.";
            String problem_2 = "Email o Username en uso.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String success = "El usuario ha sido creado con éxito!";

        response.put(MESSAGE, success);
        response.put(OBJ_SOCIAL, newSocial);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/clients/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult bindingResult, @PathVariable Long id) {
        User currentUser = userService.findById(id);
        User updatedUser;

        response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            return binding(bindingResult);
        }

        try {
            if (currentUser == null) {
                String header = "Error: no se pudo editar, el cliente ID: ";
                String problem = " no existe en la base de datos!";

                response.put(MESSAGE, header.concat(id.toString().concat(problem)));

                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            currentUser.setName(user.getName());
            currentUser.setAddress(user.getAddress());
            currentUser.setCellphone(user.getCellphone());
            currentUser.setEmail(user.getEmail());
            currentUser.setCountry(user.getCountry());
            currentUser.setRole(user.getRole());

            String password = user.getPassword();
            String name = user.getName();
            String email = user.getEmail();
            String message;

            if(password.length() <= 8) {
                String passwordEncoded = bCryptPasswordEncoder.encode(password);

                user.setPassword(passwordEncoded);
                message = "\n\nEstimado " + name + " los datos de de su cuenta han sido cambiados y son los siguientes: \nEmail: " + email + "\nUsuario: " + user.getUsername() + "\nContraseña: " + password;
            } else {
                message = "\n\nEstimado "+name+" los datos de de su cuenta han sido cambiados y son los siguientes: \nEmail: "+email+"\nUsuario: "+user.getUsername();
            }

            emailService.sendMail(email,"Actualización de datos de cuenta",message);

            currentUser.setPassword(user.getPassword());

            updatedUser = userService.save(currentUser);
        }  catch (DataAccessException e) {
            String problem_1 = "Error al actualizar el cliente en la base de datos.";
            String problem_2 = "Email en uso.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String success = "El cliente ha sido actualizado con éxito!";

        response.put(MESSAGE, success);
        response.put(OBJ, updatedUser);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/clients/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        response = new HashMap<>();

        try {
            userService.delete(id);
        } catch (DataAccessException e) {
            String problem_1 = "Error al eliminar el cliente en la base de datos.";
            String problem_2 = "El cliente está ligado a otro campo.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String success = "El cliente ha sido eliminado con éxito!";

        response.put(MESSAGE, success);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/clients/recoverAccount")
    public ResponseEntity<?> recoverAccount(@RequestBody User user) {
        User recoveryUser;
        String message, name, email, password;
        response = new HashMap<>();

        try {
            recoveryUser = userService.findByEmail(user.getEmail());
        } catch (DataAccessException e) {
            String problem_1 = "Error al realizar la consulta en la base de datos.";
            String problem_2 = "Dato invalido.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(recoveryUser == null) {
            String header = "El cliente con correo: ";
            String problem = " no existe en la base de datos!";

            response.put(MESSAGE, header.concat(user.getEmail().concat(problem)));

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        password = user.getPassword();
        name = user.getName();
        email = user.getEmail();

        if(!password.isEmpty() && password.length() <= 8) {
            String passwordEncoded = bCryptPasswordEncoder.encode(password);

            recoveryUser.setPassword(passwordEncoded);

            message = "\n\nEstimado "+name+" los datos de recuperacion de su cuenta son: \nEmail: "+email+"\nUsuario: "+recoveryUser.getUsername()+"\nContraseña: "+password;
        } else {
            message = "\n\nEstimado "+name+" los datos de recuperacion de su cuenta son: \nEmail: "+email+"\nUsuario: "+recoveryUser.getUsername();
        }

        recoveryUser = userService.save(recoveryUser);

        emailService.sendMail(email,"Recuperacion de datos de cuenta",message);

        return new ResponseEntity<>(recoveryUser, HttpStatus.OK);
    }

    @PostMapping("/clients/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile multipartFile, @RequestParam("id") Long id) throws IOException {
        response = new HashMap<>();

        User currentUser = userService.findById(id);

        if(!multipartFile.isEmpty()) {
            MultipartFile fileName;

            try {
                fileName = uploadFileService.copy(multipartFile);
            } catch (IOException e) {
                String problem_1 = "Error al actualizar el cliente en la base de datos.";
                String problem_2 = "Al subir la imagen del cliente";

                response.put(MESSAGE, problem_1);
                response.put(ERROR, problem_2);

                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            currentUser.setPhotoBits(uploadFileService.compressBytes(fileName.getBytes()));
            currentUser.setPhotoType(fileName.getContentType());
            currentUser.setPhoto(fileName.getName());
            userService.save(currentUser);

            String success = "Has subido correctamente la imagen: " + fileName;

            response.put(OBJ, currentUser);
            response.put(MESSAGE, success);
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/clients/upload/ticket")
    public ResponseEntity<?> uploadTicket(@RequestParam("file") MultipartFile multipartFile, @RequestParam("id") Long id) throws IOException {
        response = new HashMap<>();

        Ticket currentTicket = userService.findTicketById(id);

        if(!multipartFile.isEmpty()) {
            MultipartFile fileName;

            try {
                fileName = uploadFileService.copy(multipartFile);
            } catch (IOException e) {
                String problem_1 = "Error al actualizar el ticket en la base de datos.";
                String problem_2 = "Al subir la imagen del pago";

                response.put(MESSAGE, problem_1);
                response.put(ERROR, problem_2);

                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            currentTicket.setPdfBits(uploadFileService.compressBytes(fileName.getBytes()));
            currentTicket.setPdfType(fileName.getContentType());
            currentTicket.setPdf(fileName.getName());
            userService.saveTicket(currentTicket);

            User user = userService.findById(currentTicket.getUser().getId());

            String name = user.getName();
            String email = user.getEmail();

            String message = "\n\nEstimado "+name+" su ticket se ha subido con éxito, su compra está ene stado pendiente!";

            emailService.sendMail(email,"Ticket subido con éxito",message);

            String success = "Has subido correctamente la imagen: " + fileName;

            response.put(OBJ, currentTicket);
            response.put(MESSAGE, success);
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/uploads/img/{fileName:.+}")
    public ResponseEntity<?> photoView(@PathVariable String fileName) {
        User user = userService.findByPhoto(fileName);

        HttpHeaders httpHeaders = new HttpHeaders();

        String fName = user.getPhoto();

        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fName + "\"");

        return new ResponseEntity<>(uploadFileService.decompressBytes(user.getPhotoBits()), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/uploads/ticket/{fileName:.+}")
    public ResponseEntity<?> ticketView(@PathVariable String fileName) {
        Ticket ticket = userService.findTicketByPdf(fileName);

        HttpHeaders httpHeaders = new HttpHeaders();

        String fName = ticket.getPdf();

        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fName + "\"");

        return new ResponseEntity<>(uploadFileService.decompressBytes(ticket.getPdfBits()), httpHeaders, HttpStatus.OK);
    }

    @GetMapping(value = "/tickets/pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> exportTicketPdf(@PathVariable Long id) {
        Ticket ticket;

        try {
            ticket = userService.findTicketById(id);
        } catch (DataAccessException e) {
            String problem_1 = "Error al realizar la consulta en la base de datos.";
            String problem_2 = "Dato invalido.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(ticket == null) {
            String header = "El ticket ID: ";
            String problem = " no existe en la base de datos!";

            response.put(MESSAGE, header.concat(id.toString().concat(problem)));

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ByteArrayInputStream bis = pdfService.ticketPDFReport(ticket);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=ticket.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    @GetMapping(value = "/tickets/pdf-per-id/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> exportTicketsPdf(@PathVariable Long id) {
        List<Ticket> tickets;

        try {
            tickets = userService.findTicketsById(id);
        } catch (DataAccessException e) {
            String problem_1 = "Error al realizar la consulta en la base de datos.";
            String problem_2 = "Dato invalido.";

            response.put(MESSAGE, problem_1);
            response.put(ERROR, problem_2);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(tickets == null) {
            String header = "El ticket ID: ";
            String problem = " no existe en la base de datos!";

            response.put(MESSAGE, header.concat(id.toString().concat(problem)));

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ByteArrayInputStream bis = pdfService.ticketsPDFReport(tickets);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=tickets.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    @GetMapping(value = "/tickets/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> exportAllTicketsPdf() {
        List<Ticket> tickets = userService.findAllTickets();

        if(tickets == null) {
            String header = "No hay tickets en la base de datos!";

            response.put(MESSAGE, header);

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ByteArrayInputStream bis = pdfService.ticketsPDFReport(tickets);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=allTickets.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    @GetMapping("/clients/countries")
    public List<Country> countryList() {
        return userService.findAllCountries();
    }

    @GetMapping("/clients/roles")
    public List<Role> roleList() {
        return roleRepository.findAllRoles();
    }

    @GetMapping("/clients/role")
    public List<User> clientsList() {
        return userService.findAllByRole();
    }

    public ResponseEntity<?> binding(BindingResult bindingResult) {
        String errs = "Errores";
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                .collect(Collectors.toList());

        response.put(errs, errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}