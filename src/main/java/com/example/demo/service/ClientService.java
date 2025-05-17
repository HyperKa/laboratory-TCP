package com.example.demo.service;

/*
import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
*/

import com.example.demo.dto.AppointmentRecordDTO;
import com.example.demo.dto.ClientDTO;
import com.example.demo.entity.AppointmentRecord;
import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.Role;
import com.example.demo.repository.AppointmentRecordRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRecordRepository appointmentRecordRepository;

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

    public Optional<ClientDTO> updateClientById(Long id, ClientDTO dto) {
        return clientRepository.findById(id).map(client -> {
            client.setFirstName(dto.getFirstName());
            client.setLastName(dto.getLastName());
            client.setAge(dto.getAge());
            client.setGender(dto.getGender());
            client.setAddress(dto.getAddress());
            client.setPassport(dto.getPassport());
            client.setLogin(dto.getLogin());

            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                client.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            Client savedClient = clientRepository.save(client);
            return convertToDTO(savedClient);
        });
    }

    /*
    //для смены пароля

    public void changePassword(Long clientId, String currentPassword, String newPassword) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Проверяем, что текущий пароль верный
        if (!passwordEncoder.matches(currentPassword, client.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Шифруем новый пароль
        String encryptedPassword = passwordEncoder.encode(newPassword);
        client.setPassword(encryptedPassword);

        // Сохраняем обновленные данные
        clientRepository.save(client);
    }


     */

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

        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
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

    public void updateClientByLogin(String username, ClientDTO clientDTO) {
        Client client = clientRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        client.setAge(clientDTO.getAge());
        client.setGender(clientDTO.getGender());
        client.setLastName(clientDTO.getLastName());
        client.setFirstName(clientDTO.getFirstName());
        client.setAddress(clientDTO.getAddress());
        client.setPassport(clientDTO.getPassport());

        clientRepository.save(client);
    }

    public List<ClientDTO> getClientsForDoctor(String username) {
        Doctor doctor = doctorRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Доктор не найден"));

        List<AppointmentRecord> records = appointmentRecordRepository.findByDoctor(doctor);

        return records.stream()
                .map(record -> {
                    Client client = record.getClient();
                    if (client == null) {
                        return null;
                    }

                    ClientDTO dto = new ClientDTO();
                    dto.setId(Long.valueOf(client.getId()));
                    dto.setFirstName(client.getFirstName());
                    dto.setLastName(client.getLastName());
                    dto.setLogin(client.getLogin());
                    dto.setAge(client.getAge());
                    dto.setAddress(client.getAddress());
                    dto.setPassport(client.getPassport());
                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        ClientDTO::getId,
                        dto -> dto,
                        (existing, replacement) -> existing // если дубликаты, берём первый
                ))
                .values()
                .stream()
                .toList();
    }

}
