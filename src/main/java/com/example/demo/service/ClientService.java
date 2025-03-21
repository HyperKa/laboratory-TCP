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
import java.util.stream.Collectors;
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

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
}
