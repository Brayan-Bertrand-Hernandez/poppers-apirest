package com.project.springapirest.service;

import com.project.springapirest.factory.UserFactory;
import com.project.springapirest.model.*;
import com.project.springapirest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserFactory, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllClients(Pageable pageable) {
        List<Role> roles = roleRepository.findAllRoles();

        return userRepository.findAllByRole(roles.get(roles.size()-2), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllEnabledClients(Pageable pageable) {
        List<Role> roles = roleRepository.findAllRoles();

        return userRepository.findAllByRoleAndEnabled(roles.get(roles.size()-2), true, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllAdministration(Pageable pageable) {
        List<Role> roles = roleRepository.findAllRoles();

        return userRepository.findAllByRoleOrRole(roles.get(roles.size()-3), roles.get(roles.size()-1), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if(user != null) {
            user.setEnabled(false);

            userRepository.save(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Country> findAllCountries() {
        return countryRepository.findAllCountries();
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User findByPhoto(String photoName) {
        return userRepository.findByPhoto(photoName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllByRole() {
        List<Role> roles = roleRepository.findAllRoles();

        return userRepository.getAllByRole(roles.get(roles.size()-2));
    }

    @Override
    @Transactional(readOnly = true)
    public Ticket findTicketById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket findTicketByPdf(String pdfName) {
        return ticketRepository.findByPdf(pdfName);
    }

    @Override
    @Transactional
    public List<Ticket> findTicketsById(Long id) {
        return ticketRepository.findAllByUserId(id);
    }

    @Override
    @Transactional
    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteTicketById(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findProductByName(String term) {
        return productRepository.findByNameContainingIgnoreCase(term);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User currentUsername = userRepository.findByUsername(username);

        List<Role> roles = new ArrayList<>();

        roles.add(currentUsername.getRole());

        currentUsername.setRoles(roles);

        List<GrantedAuthority> authorities = currentUsername.getRoles().stream().map(
                role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(currentUsername.getUsername(),
                currentUsername.getPassword(), currentUsername.getEnabled(), true, true, true, authorities);
    }
}
