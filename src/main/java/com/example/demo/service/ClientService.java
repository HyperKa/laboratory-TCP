package com.example.demo.service;

/*
import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
*/

import com.example.demo.dto.ClientDTO;
import com.example.demo.entity.Client;
import com.example.demo.entity.Role;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder; // Добавляем PasswordEncoder

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

    // Преобразование Entity -> DTO
    public ClientDTO convertToDTO(Client client) {
        return new ClientDTO(client);
    }

    // Преобразование DTO -> Entity
    public Client convertToEntity(ClientDTO dto) {
        Client client = new Client();
        //client.setId(Math.toIntExact(dto.getId()));
        if (dto.getId() != null) {
            client.setId(Math.toIntExact(dto.getId())); // Устанавливаем id только если оно не null
        }
        client.setAge(dto.getAge());
        client.setGender(dto.getGender());
        client.setLastName(dto.getLastName());
        client.setFirstName(dto.getFirstName());
        client.setAddress(dto.getAddress());
        client.setPassport(dto.getPassport());

        client.setLogin(dto.getLogin());
        client.setPassword(passwordEncoder.encode(dto.getPassword())); // Хэшируем пароль
        client.setRole(dto.getRole()); // Преобразуем строку в Enum
        return client;
    }

    // Получение всех клиентов в виде DTO
    public List<ClientDTO> getAllClientsAsDTO() {
        return clientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Получение клиента по ID в виде DTO
    public Optional<ClientDTO> getClientByIdAsDTO(Long id) {
        return clientRepository.findById(id).map(this::convertToDTO);
    }

    // Создание клиента из DTO
    public ClientDTO createClientFromDTO(ClientDTO dto) {
        Client client = convertToEntity(dto);
        client.setRole(Role.CLIENT); // Автоматическая установка роли
        Client savedClient = clientRepository.save(client);
        return convertToDTO(savedClient);
    }

    // Обновление клиента из DTO
    public ClientDTO updateClientFromDTO(Long id, ClientDTO updatedDto) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));

        // Обновляем поля существующего клиента
        existingClient.setAge(updatedDto.getAge());
        existingClient.setGender(updatedDto.getGender());
        existingClient.setLastName(updatedDto.getLastName());
        existingClient.setFirstName(updatedDto.getFirstName());
        existingClient.setAddress(updatedDto.getAddress());
        existingClient.setPassport(updatedDto.getPassport());

        Client savedClient = clientRepository.save(existingClient);
        return convertToDTO(savedClient);
    }

    public Optional<Client> findByLogin(String login) {
        return clientRepository.findByLogin(login);
    }

}
