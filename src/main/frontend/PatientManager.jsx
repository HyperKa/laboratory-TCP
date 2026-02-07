import React, { useState, useEffect } from 'react';

export default function PatientManager() {
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [userRole, setUserRole] = useState(''); // Добавили состояние роли

    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [isViewModalOpen, setIsViewModalOpen] = useState(false);
    const [selectedPatient, setSelectedPatient] = useState(null);
    const [editingPatient, setEditingPatient] = useState(null);

    useEffect(() => {
        const container = document.getElementById('react-patient-manager-container');
        const role = container ? container.dataset.userRole : '';
        setUserRole(role);

        fetch('/api/v1/clients')
            .then(res => {
                if (!res.ok) throw new Error('Сетевой ответ был не в порядке');
                return res.json();
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

    const handleOpenViewModal = (patient) => {
        setSelectedPatient(patient);
        setIsViewModalOpen(true);
    };

    const handleOpenEditModal = (patient) => {
        setSelectedPatient(patient);
        setEditingPatient({ ...patient });
        setIsEditModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsViewModalOpen(false);
        setIsEditModalOpen(false);
        setSelectedPatient(null);
        setEditingPatient(null);
    };

    const handleDelete = (patientId) => {
        if (window.confirm(`Вы уверены, что хотите удалить пациента с ID ${patientId}?`)) {
            fetch(`/api/v1/clients/${patientId}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        setPatients(patients.filter(p => p.id !== patientId));
                        alert('Клиент успешно удален');
                    } else {
                        alert('Ошибка при удалении: проверьте права доступа.');
                    }
                })
                .catch(error => console.error("Ошибка:", error));
        }
    };

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setEditingPatient({ ...editingPatient, [name]: value});
    };

    const handleUpdateSubmit = (event) => {
        event.preventDefault();
        fetch(`/api/v1/clients/${editingPatient.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
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
            <table className="data-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Имя</th>
                    <th>Логин</th>
                    <th className="actions-column">Действия</th>
                </tr>
                </thead>
                <tbody>
                {patients.map(p => (
                    <tr key={p.id}>
                        <td>{p.id}</td>
                        <td>{p.firstName} {p.lastName}</td>
                        <td>{p.login}</td>
                        <td className="col-actions">
                            <div className="actions-container">
                                <button className="action-button view" onClick={() => handleOpenViewModal(p)}>Просмотр</button>
                                <button className="action-button edit" onClick={() => handleOpenEditModal(p)}>Редактировать</button>
                                {userRole === 'ROLE_ADMIN' && (
                                    <button className="action-button delete" onClick={() => handleDelete(p.id)}>Удалить</button>
                                )}
                            </div>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {isViewModalOpen && (
                <div className="modal show">
                    <div className="modal-content">
                        <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                        <h2>Информация о клиенте</h2>
                        <div className="modal-body">
                            <p><strong>ID:</strong> {selectedPatient.id}</p>
                            <p><strong>Возраст:</strong> {selectedPatient.age}</p>
                            <p><strong>Пол:</strong> {selectedPatient.gender}</p>
                            <p><strong>Имя:</strong> {selectedPatient.firstName}</p>
                            <p><strong>Фамилия:</strong> {selectedPatient.lastName}</p>
                            <p><strong>Адрес:</strong> {selectedPatient.address}</p>
                            <p><strong>Паспорт:</strong> {selectedPatient.passport}</p>
                            <p><strong>Логин:</strong> {selectedPatient.login}</p>
                        </div>
                    </div>
                </div>
            )}

            {isEditModalOpen && (
                <div className="modal show">
                    <div className="modal-content">
                        <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                        <h2>Редактировать клиента</h2>
                        <form onSubmit={handleUpdateSubmit} className="modal-form">
                            <div className="form-group">
                                <label>Имя:</label>
                                <input name="firstName" type="text" value={editingPatient.firstName} onChange={handleInputChange} />
                            </div>
                            <div className="form-group">
                                <label>Фамилия:</label>
                                <input name="lastName" type="text" value={editingPatient.lastName} onChange={handleInputChange} />
                            </div>
                            <div className="form-group">
                                <label>Возраст:</label>
                                <input name="age" type="number" value={editingPatient.age} onChange={handleInputChange} />
                            </div>
                            <div className="form-group">
                                <label>Пол:</label>
                                <input name="gender" type="text" value={editingPatient.gender} onChange={handleInputChange} />
                            </div>
                            <div className="form-group">
                                <label>Адрес:</label>
                                <input name="address" type="text" value={editingPatient.address} onChange={handleInputChange} />
                            </div>
                            <div className="form-group">
                                <label>Паспорт:</label>
                                <input name="passport" type="text" value={editingPatient.passport} onChange={handleInputChange} />
                            </div>
                            <div className="form-group">
                                <label>Логин:</label>
                                <input name="login" type="text" value={editingPatient.login} onChange={handleInputChange} />
                            </div>
                            <div className="modal-actions">
                                <button type="submit" className="button">Сохранить</button>
                                <button type="button" className="button button-secondary" onClick={handleCloseModal}>Отмена</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}