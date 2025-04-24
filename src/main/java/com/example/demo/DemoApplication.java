package com.example.demo;

import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.service.ClientService;
import com.example.demo.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableScheduling // Включение поддержки планирования задач
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private ClientService clientService;
    @Autowired
    private DoctorService doctorService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        // получение заголовков
        System.out.println("Столбцы таблицы clients: " + clientService.getClientColumns());

        // создание нового клиента
        var newClient = clientService.createClient(30, "Male", "Smith", "John", "123 Main St", "AB123456");
        System.out.println("Новый клиент добавлен: " + newClient);
        */

        // 1. Получение списка столбцов
        System.out.println("Столбцы таблицы clients: " + clientService.getClientColumns());

        // 2. Создание нового клиента
        //var newClient = clientService.createClient(30, "Male", "Smith", "John", "123 Main St", "AB123456");
        //System.out.println("Новый клиент добавлен: " + newClient);

        /*
        var newDoctor = doctorService.createDoctor("Ivanov", "Ivan", "p", "25 years",
                "hgsfa1726ivan", "hshsga6512Tr");
        System.out.println("Новый врач добавлен: " + newDoctor);
        */

        // 3. Получение всех клиентов
        List<Client> allClients = clientService.getAllClients();
        System.out.println("Список всех клиентов: " + allClients);

        // 4. Получение клиента по ID
        //Long clientId = (long) newClient.getId();
        //Optional<Client> retrievedClient = clientService.getClientById(clientId);
        //System.out.println("Получен клиент по ID: " + retrievedClient);

        /*
        // 5. Удаление клиента
        if (retrievedClient != null) {
            clientService.deleteClient(clientId);
            System.out.println("Клиент с ID " + clientId + " удален.");
        } else {
            System.out.println("Клиент с ID " + clientId + " не найден.");
        }

        // 6. Проверка, что клиент удален
        Client deletedClient = clientService.getClientById(clientId);
        if (deletedClient == null) {
            System.out.println("Клиент с ID " + clientId + " успешно удален.");
        } else {
            System.out.println("Ошибка: клиент с ID " + clientId + " все еще существует.");
        }
         */
    }
}