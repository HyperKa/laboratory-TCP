package com.example.demo.controllers;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.ClientDTO;
import com.example.demo.entity.Client;
import com.example.demo.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // CREATE
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO clientDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClientFromDTO(clientDTO));
    }

    // READ (все записи)
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClientsAsDTO());
    }

    @GetMapping
    public List<ClientDTO> getAllClientsAsDTO() {
        return clientService.getAllClientsAsDTO();
    }
    /*
    // READ (по ID)
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        return clientService.getClientByIdAsDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    */

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id, Authentication auth) {
        UserDetails user = (UserDetails) auth.getPrincipal();

        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT"))) {
            Optional<Client> current = clientService.findByLogin(user.getUsername());
            if (current.isEmpty() || !Long.valueOf(current.get().getId()).equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return clientService.getClientByIdAsDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody ClientDTO dto) {
        return ResponseEntity.of(
            clientService.updateClientById(id, dto)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
/*
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request,
            Authentication auth) {

        UserDetails user = (UserDetails) auth.getPrincipal();
        if (!user.getUsername().equals(clientService.getClientById(id).getLogin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        clientService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

 */
}