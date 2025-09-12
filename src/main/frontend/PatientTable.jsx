import React, { useState, useEffect } from 'react';

export default function PatientTable() {
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Загружаем данные при первом рендере компонента
        fetch('/api/v1/clients')
            .then(response => response.json())
            .then(data => {
                setPatients(data);
                setLoading(false);
            })
            .catch(error => console.error("Ошибка загрузки пациентов:", error));
    }, []); // пустой массив - выполнить только 1 раз

    let handleEdit = (id) => alert(`Редактировать пациента с ID: ${id}`);
    const handleDelete = (id) => alert(`Удалить пациента с ID: ${id}`);

    if (loading) {
        return <div>Загрузка пациентов...</div>;
    }

    return (
        <table border="1">
            <thead>
            <tr>
                <th>ID</th>
                <th>Возраст</th>
                <th>Пол</th>
                <th>Фамилия</th>
                <th>Имя</th>
                <th>Адрес</th>
                <th>Паспорт</th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            {patients.map(patient => (
                <tr key={patient.id}>
                    <td>{patient.id}</td>
                    <td>{patient.age}</td>
                    <td>{patient.gender}</td>
                    <td>{patient.lastName}</td>
                    <td>{patient.firstName}</td>
                    <td>{patient.address}</td>
                    <td>{patient.passport}</td>
                    <td>
                        <button onClick={() => handleEdit(patient.id)}>Редактировать</button>
                        <button onClick={() => handleDelete(patient.id)}>Удалить</button>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}