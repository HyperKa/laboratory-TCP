package com.example.demo.service;

import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public List<Doctor> getAllClients() {
        return doctorRepository.findAll();
    }

    public Doctor getClientById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public Doctor saveClient(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void deleteClient(Long id) {
        doctorRepository.deleteById(id);
    }

    // получение списка столбцов
    public List<String> getDoctorColumns() {
        return Arrays.stream(Client.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    // создание клиента "в одну строку"
    public Doctor createDoctor(String lastName, String firstName, String specialization, String experience, String login, String password) {
        Doctor doctor = new Doctor();
        doctor.setLastName(lastName);
        doctor.setFirstName(firstName);
        doctor.setSpecialization(specialization);
        doctor.setExperience(experience);
        doctor.setLogin(login);
        doctor.setPassword(password);
        return doctorRepository.save(doctor);
    }
}
