import React, {useEffect, useState} from "react";

export default function DoctorManager() {
    const [doctors, setDoctors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [userRole, setUserRole] = useState('');

    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [isViewModalOpen, setIsViewModalOpen] = useState(false);

    const [selectedDoctor, setSelectedDoctor] = useState(null);
    const [editingDoctor, setEditingDoctor] = useState(null);

    useEffect(() => {
        const container = document.getElementById('react-doctor-manager-container');
        const role = container ? container.dataset.userRole : '';
        setUserRole(role);

        fetch('/api/v1/doctors')
            .then(res => {
                if (!res.ok) {
                    throw new Error('Сетевой ответ на doctors !ok');
                }
                else {
                    return res.json();
                }
            })
            .then(data => {
                setDoctors(data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Ошибка загрузки данных doctors');
                setLoading(false);
            });

    }, []);

    const handleOpenViewModal = (doctor) => {
        setSelectedDoctor(doctor);
        setIsViewModalOpen(true);
    }

    const handleOpenEditModal = (doctor) => {
        setSelectedDoctor(doctor);
        setEditingDoctor({ ...doctor });
        setIsEditModalOpen(true);
    }

    const handleCloseModal = () => {
        setIsViewModalOpen(false);
        setIsEditModalOpen(false);
        setSelectedDoctor(null);
        setEditingDoctor(null);
    }

    const handleInputChange = (event) => {
        const {name, value} = event.target;
        setEditingDoctor({ ...editingDoctor, [name]: value });
    }

    const handleDelete = (id) => {
        if (confirm(`Вы уверены что хотите удалить доктора с ID: ${id}?`)) {
            fetch(`/api/v1/doctors/${id}`, {
                method: 'DELETE'})
                .then(responce => {
                    if (!responce.ok) {
                        alert('Ошибка при удалении доктора, проверьте права доступа');
                    }
                    else {
                        setDoctors(doctors.filter(d => d.id !== id));
                        alert('Доктор удален');
                    }
                });
        }
    }

    const handleUpdate = (event) => {
        event.preventDefault();
            fetch(`/api/v1/doctors/${editingDoctor.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(editingDoctor)
        })
            .then(res => res.json())
            .then(updateDoctor => {
                setDoctors(doctors.map(d => (d.id === updateDoctor.id ? updateDoctor : d)));
                alert('Данные доктора обновлены');
                handleCloseModal();
            })
            .catch(error => console.error('ошибка обновления данных доктора: ', error));
    }

    const isAdminOrDoctor = userRole === 'ROLE_ADMIN' || userRole === 'ROLE_DOCTOR';

    if (loading) return <div>Загрузка истории болезней...</div>

    return (
        <table className='data-table'>
            <thead>
            <tr>
                <th>ID</th>
                <th>Фамилия</th>
                <th>Имя</th>
                <th>Специализация</th>
                <th>Опыт</th>
                <th>Логин</th>
                {isAdminOrDoctor && <th className='actions-column'>Действия</th>}
            </tr>
            </thead>
            <tbody>
            {doctors.map(doctor => (
                <tr key={doctor.id}>
                    <td>{doctor.id}</td>
                    <td>{doctor.lastName}</td>
                    <td>{doctor.firstName}</td>
                    <td>{doctor.specialization}</td>
                    <td>{doctor.experience}</td>
                    <td>{doctor.login}</td>
                    {isAdminOrDoctor && (<td className='col-actions'>
                        <div className="actions-container">
                            <button className='action-button view' onClick={() => handleOpenViewModal(doctor)}>Просмотр</button>
                            <button className='action-button edit' onClick={() => handleOpenEditModal(doctor)}>Редактировать</button>
                            {userRole === 'ROLE_ADMIN' && (
                                <button className="action-button delete" onClick={() => handleDelete(doctor.id)}>Удалить</button>
                            )}
                        </div>
                    </td>)}
                </tr>
            ))}
            </tbody>

            {isViewModalOpen && (
                <div className='modal show'>
                    <div className='modal-content'>
                        <span className='close-btn' onClick={handleCloseModal}>&times;</span>
                        <h2>Просмотр данных доктора</h2>
                        <p>ID: {selectedDoctor.id}</p>
                        <p>Фамилия: {selectedDoctor.lastName}</p>
                        <p>Имя: {selectedDoctor.firstName}</p>
                        <p>Специализация: {selectedDoctor.specialization}</p>
                        <p>Опыт: {selectedDoctor.experience}</p>
                        <p>Логин: {selectedDoctor.login}</p>
                    </div>
                </div>
            )}

            {isEditModalOpen && (
                <div className='modal show'>
                    <div className='modal-content'>
                        <span className='close-btn' onClick={handleCloseModal}>&times;</span>
                        <h2>Редактирование данных доктора</h2>
                        <form onSubmit={handleUpdate}>

                            <label>Фамилия:</label>
                            <input name="lastName" type="text" value={editingDoctor.lastName} onChange={handleInputChange} />

                            <label>Имя:</label>
                            <input name="firstName" type="text" value={editingDoctor.firstName} onChange={handleInputChange} />

                            <label>Специализация:</label>
                            <input name="specialization" type="text" value={editingDoctor.specialization} onChange={handleInputChange} />

                            <label>Опыт:</label>
                            <input name="experience" type="text" value={editingDoctor.experience} onChange={handleInputChange} />

                            <label>Логин:</label>
                            <input name="login" type="text" value={editingDoctor.login} onChange={handleInputChange} />

                            <div className="modal-actions">
                                <button type="submit">Сохранить</button>
                                <button type="submit" onClick={handleCloseModal}>Отмена</button>
                            </div>
                        </form>
                    </div>
                </div>

            )}
        </table>
    )
}