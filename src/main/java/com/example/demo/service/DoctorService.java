package com.example.demo.service;

import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Получение всех докторов
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // Получение доктора по ID
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    // Сохранение доктора
    public Doctor saveDoctor(Doctor doctor) {
        if (doctor.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        return doctorRepository.save(doctor);
    }

    // Удаление доктора по ID
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    // Получение списка столбцов сущности Doctor
    public List<String> getDoctorColumns() {
        return Arrays.stream(Doctor.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    // Создание доктора "в одну строку"
    public Doctor createDoctor(String lastName, String firstName, String specialization, String experience, String login, String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        Doctor doctor = new Doctor();
        doctor.setLastName(lastName);
        doctor.setFirstName(firstName);
        doctor.setSpecialization(specialization);
        doctor.setExperience(experience);
        doctor.setLogin(login);
        doctor.setPassword(passwordEncoder.encode(password)); // Хешируем переданный пароль
       // assertTrue(passwordEncoder.matches("hshsga6512Tr",passwordEncoder.encode(password)));
        return doctorRepository.save(doctor);
    }
}