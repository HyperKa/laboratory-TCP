// src/main/frontend/PatientManager.jsx
import React, { useState, useEffect } from 'react';
// Вам понадобятся компоненты для модальных окон, их можно создать отдельно
// import EditModal from './EditModal';
// import ViewModal from './ViewModal';

export default function PatientManager() {

    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);

    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [isViewModalOpen, setIsViewModalOpen] = useState(false)

    const [selectedPatient, setSelectedPatient] = useState(null);
    const [editingPatient, setEditingPatient] = useState(null);


    useEffect(() => {
        fetch('/api/v1/clients')
            .then(res => {
                if (!res.ok) {  // проверка на статус 200 OK
                    throw new Error('Сетевой ответ был не в порядке');
                }
                return res.json();  // парсинг JSON
            })
            .then(data => {
                setPatients(data);
                setLoading(false);
            })
            .catch(error => {
                console.error("Ошибка загрузки пациентов:", error);
                setLoading(false);
            });
    }, []);

    // Методы-обработчики модальных окон
    const handleOpenViewModal = (patient) => {
        setSelectedPatient(patient);
        setIsViewModalOpen(true);
    };

    const handleOpenEditModal = (patient) => {
        setSelectedPatient(patient);
        setEditingPatient({ ...patient });  // создание копии для редактирования
        setIsEditModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsViewModalOpen(false);
        setIsEditModalOpen(false);
        setSelectedPatient(null);
        setEditingPatient(null);
    };

    // CRUD-методы:
    const handleDelete = (patientId) => {
        if (confirm(`Вы уверены, что хотите удалить пациента с ID ${patientId}?`)) {
            fetch(`/api/v1/clients/${patientId}`, {
                method: 'DELETE' })
                .then(responce => {
                    if (responce.ok) {
                        setPatients(patients.filter(p => p.id != patientId));  // просто фильтровка всех пациентов, за исключением нужного
                        alert('Клиент удален');
                    } else {
                        alert('Ошибка при удалении клиента');
                    }
                });
        }
    };

    const handleInputChange = (event) => {
        const { name, value } = event.target;  // долбаное свойство value
        setEditingPatient({ ...editingPatient, [name]: value});
    };

    const handleUpdateSubmit = (event) => {
        event.preventDefault();
        fetch(`/api/v1/clients/${editingPatient.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(editingPatient)
        })
            .then(res => res.json())
            .then(updatedPatient => {
                setPatients(patients.map(p => (p.id === updatedPatient.id ? updatedPatient : p)));
                alert('Данные клиента обновлены');
                handleCloseModal();
            })
            .catch(error => console.error("Ошибка обновления: ", error));

    };

    if (loading) return <div>Загрузка...</div>;

    return (
        <div>
            {/* Таблица пациентов */}
            <table className="data-table">  {/* className из css */}
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Имя</th>
                    <th>Логин</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                {patients.map(p => (
                    <tr key={p.id}>
                        <td>{p.id}</td>
                        <td>{p.firstName} {p.lastName}</td>
                        <td>{p.login}</td>
                        <td>
                            <button className="action-button view" onClick={() => handleOpenViewModal(p)}>Просмотр</button>
                            <button className="action-button edit" onClick={() => handleOpenEditModal(p)}>Редактировать</button>
                            <button className="action-button delete" onClick={() => handleDelete(p.id)}>Удалить</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {/* Модальные окна (рендерим по условию) */}
            {isViewModalOpen && (
                <div className="modal show">
                    <div className="modal-content">
                        <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                        <h2>Информация о клиенте</h2>
                        <p>ID: {selectedPatient.id}</p>
                        <p>Возраст: {selectedPatient.firstName}</p>
                        <p>Пол: {selectedPatient.gender}</p>
                        <p>Имя: {selectedPatient.firstName}</p>
                        <p>Фамилия: {selectedPatient.lastName}</p>
                        <p>Адрес: {selectedPatient.gender}</p>
                        <p>Паспорт: {selectedPatient.passport}</p>
                        <p>Логин: {selectedPatient.login}</p>
                    </div>
                </div>
            )}

            {isEditModalOpen && (
                <div className="modal show">
                    <div className="modal-content">
                        <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                        <h2>Редактировать клиента</h2>
                        {/* Здесь будет форма редактирования, которая при отправке вызывает PUT-запрос */}
                        <form onSubmit={handleUpdateSubmit}>
                            <label>Имя:</label>
                            <input name="firstName" type="text" value={editingPatient.firstName} onChange={handleInputChange} />

                            <label>Фамилия:</label>
                            <input name="lastName" type="text" value={editingPatient.lastName} onChange={handleInputChange} />

                            <label>Возраст:</label>
                            <input name="age" type="number" value={editingPatient.age} onChange={handleInputChange} />

                            <label>Пол:</label>
                            <input name="gender" type="text" value={editingPatient.gender} onChange={handleInputChange} />

                            <label>Адрес:</label>
                            <input name="address" type="text" value={editingPatient.address} onChange={handleInputChange} />

                            <label>Паспорт:</label>
                            <input name="passport" type="text" value={editingPatient.passport} onChange={handleInputChange} />

                            <label>Логин:</label>
                            <input name="login" type="text" value={editingPatient.login} onChange={handleInputChange} />

                            <br/><br/>
                            <button type="submit">Сохранить</button>
                            <button type="button" onClick={handleCloseModal}>Отмена</button>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}