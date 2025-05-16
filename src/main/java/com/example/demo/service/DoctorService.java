package com.example.demo.service;

import com.example.demo.dto.ClientDTO;
import com.example.demo.dto.DoctorDTO;
import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.Role;
import com.example.demo.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
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


    public Doctor findByLogin(String login) {
        return  doctorRepository.findByLogin(login).orElseThrow(() ->
                new RuntimeException("Доктор не найден в сервисе: " + login));
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

    public DoctorDTO createDoctorAsDTO(DoctorDTO dto) {
        Doctor doctor = convertToEntity(dto);
        doctor.setRole(Role.DOCTOR);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return convertToDTO(savedDoctor);
    }

    // Обновление доктора
    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        // Находим существующего врача по ID
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        // Обновляем поля существующего врача данными из updatedDoctor
        if (updatedDoctor.getLastName() != null) {
            existingDoctor.setLastName(updatedDoctor.getLastName());
        }
        if (updatedDoctor.getFirstName() != null) {
            existingDoctor.setFirstName(updatedDoctor.getFirstName());
        }
        if (updatedDoctor.getSpecialization() != null) {
            existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        }
        if (updatedDoctor.getExperience() != null) {
            existingDoctor.setExperience(updatedDoctor.getExperience());
        }
        if (updatedDoctor.getLogin() != null) {
            existingDoctor.setLogin(updatedDoctor.getLogin());
        }
        if (updatedDoctor.getPassword() != null) {
            existingDoctor.setPassword(passwordEncoder.encode(updatedDoctor.getPassword()));
        }

        // Сохраняем обновленного врача в базу данных
        return doctorRepository.save(existingDoctor);
    }

    // Обновление доктора
    public Doctor updateDoctorAsDTO(Long id, DoctorDTO updatedDoctor) {
        // Находим существующего врача по ID
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        // Обновляем поля существующего врача данными из updatedDoctor
        if (updatedDoctor.getLastName() != null) {
            existingDoctor.setLastName(updatedDoctor.getLastName());
        }
        if (updatedDoctor.getFirstName() != null) {
            existingDoctor.setFirstName(updatedDoctor.getFirstName());
        }
        if (updatedDoctor.getSpecialization() != null) {
            existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        }
        if (updatedDoctor.getExperience() != null) {
            existingDoctor.setExperience(updatedDoctor.getExperience());
        }
        if (updatedDoctor.getLogin() != null) {
            existingDoctor.setLogin(updatedDoctor.getLogin());
        }
        if (updatedDoctor.getPassword() != null) {
            existingDoctor.setPassword(passwordEncoder.encode(updatedDoctor.getPassword()));
        }

        // Сохраняем обновленного врача в базу данных
        return doctorRepository.save(existingDoctor);
    }


    // Преобразование Entity -> DTO
    public DoctorDTO convertToDTO(Doctor doctor) {
        return new DoctorDTO(doctor);
    }

    // Преобразование DTO -> Entity
    public Doctor convertToEntity(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setId(dto.getId());                                  // Убрана сложная обработка получения ID доктора
        doctor.setLastName(dto.getLastName());
        doctor.setFirstName(dto.getFirstName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setExperience(dto.getExperience());
        doctor.setLogin(dto.getLogin());

        //dto.setPassword(doctor.getPassword()); // <-- Вот из-за этой хуйни пароль не сохранялся и он был null.
        // Я тут потрясающий ход свершил, беру пустую сущность доктора, из нее беру пароль null и сохраняю это в ответ dto
        //dto.setRole(dto.getRole());  // <-- Эта хрень по факту не важна, но пусть будет, вроде в JwtTokenService роль сама устанавливается. Напиздел, через UserDetailsService, ну наконец-то заработало)

        //doctor.setPassword(dto.getPassword());
        doctor.setPassword(passwordEncoder.encode(dto.getPassword()));
        doctor.setRole(dto.getRole());
        return doctor;
    }

    // Получение всех врачей в виде DTO
    public List<DoctorDTO> getAllDoctorsAsDTO() {
        return doctorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Получение врача по ID в виде DTO
    public Optional<DoctorDTO> getDoctorByIdAsDTO(Long id) {
        return doctorRepository.findById(id).map(this::convertToDTO);
    }

    // Создание врача из DTO
    public DoctorDTO createDoctorFromDTO(DoctorDTO dto) {
        Doctor doctor = convertToEntity(dto);
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        doctor.setPassword(passwordEncoder.encode(dto.getPassword()));
        doctor.setRole(Role.DOCTOR); // Автоматическая установка роли
        Doctor savedDoctor = doctorRepository.save(doctor);
        return convertToDTO(savedDoctor);
    }

    // Обновление врача из DTO
    public DoctorDTO updateDoctorFromDTO(Long id, DoctorDTO updatedDto) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));

        // Обновляем поля существующего врача
        if (updatedDto.getLastName() != null) {
            existingDoctor.setLastName(updatedDto.getLastName());
        }
        if (updatedDto.getFirstName() != null) {
            existingDoctor.setFirstName(updatedDto.getFirstName());
        }
        if (updatedDto.getSpecialization() != null) {
            existingDoctor.setSpecialization(updatedDto.getSpecialization());
        }
        if (updatedDto.getExperience() != null) {
            existingDoctor.setExperience(updatedDto.getExperience());
        }
        if (updatedDto.getLogin() != null) {
            existingDoctor.setLogin(updatedDto.getLogin());
        }
        if (updatedDto.getPassword() != null && !updatedDto.getPassword().isEmpty()) {
            existingDoctor.setPassword(passwordEncoder.encode(updatedDto.getPassword()));
        }

        Doctor savedDoctor = doctorRepository.save(existingDoctor);
        return convertToDTO(savedDoctor);
    }
}