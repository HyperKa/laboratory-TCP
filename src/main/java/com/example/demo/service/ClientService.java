package com.example.demo.service;

/*
import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
*/

import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }
    /*
    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }
    */
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    // получение списка столбцов
    public List<String> getClientColumns() {
        return Arrays.stream(Client.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    // создание клиента "в одну строку"

    public Client createClient(int age, String gender, String lastName, String firstName, String address, String passport) {
        Client client = new Client();
        client.setAge(age);
        client.setGender(gender);
        client.setLastName(lastName);
        client.setFirstName(firstName);
        client.setAddress(address);
        client.setPassport(passport);
        return clientRepository.save(client);
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client updatedClient) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));

        // Обновляем поля существующего клиента
        existingClient.setAge(updatedClient.getAge());
        existingClient.setGender(updatedClient.getGender());
        existingClient.setLastName(updatedClient.getLastName());
        existingClient.setFirstName(updatedClient.getFirstName());
        existingClient.setAddress(updatedClient.getAddress());
        existingClient.setPassport(updatedClient.getPassport());

        return clientRepository.save(existingClient);
    }
}
