import React, {useState, useEffect } from "react";

export default function AppointmentRecordManager() {
    const [records, setRecords] = useState([]);
    const [loading, setLoading] = useState(true);

    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [isViewModalOpen, setIsViewModalOpen] = useState(false);

    const [selectedRecord, setSelectedRecord] = useState(null);
    const [editingRecord, setEditingRecord] = useState(null);

    useEffect(() => {
        fetch('/api/v1/appointment-records')
            .then(res => res.json())
            .then(data => {
                setRecords(data);
                setLoading(false);
            })
            .catch(error => console.error("Ошибка загрузки записей: ", error));
    }, []);

    const handleOpenViewModal = (record) => {
        setSelectedRecord(record);
        setIsViewModalOpen(true);
    };

    const handleOpenEditModal = (record) => {
        setSelectedRecord(record);
        setEditingRecord({ ...record });
        setIsEditModalOpen(true);
    }

    const handleCloseModal = () => {
        setIsViewModalOpen(false);
        setIsEditModalOpen(false);
        setSelectedRecord(null);
        setEditingRecord(null);
    }

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setEditingRecord({ ...editingRecord, [name]: value })
    }

    const handleDelete = (recordId) => {
        if (confirm(`Вы уверены, что хотите удалить запись с ID ${recordId}?`)) {
            fetch(`/api/v1/appointment-records/${recordId}`, {
                method: 'DELETE' })
                .then(responce => {
                    if (responce.ok) {
                        setRecords(records.filter(r => r.recordId !== recordId));
                        alert('Клиент удален');
                    } else {
                        alert('Ошибка при удалении клиента');
                    }
                });
        }
    };


    const handleUpdateSubmit = (event) => {
        event.preventDefault();
        fetch(`/api/v1/appointment-records/${editingRecord.recordId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(editingRecord)
        })
            .then(res => res.json())
            .then(updatedRecord => {
                setRecords(records.map(p => (p.recordId === updatedRecord.recordId ? updatedRecord : p)));
                alert('Данные записи обновлены');
                handleCloseModal();
            })
            .catch(error => console.error("Ошибка обновления: ", error));

    };

    if (loading) return <div>Загрузка записей на прием...</div>

    return (
        <div>
            <table className="data-table">
                <thead>
                <tr>
                    <th>Дата</th>
                    <th>Время</th>
                    <th>Услуга</th>
                    <th>ID пациента</th>
                    <th>ID Врача</th>
                    <th className="actions-column">Действия</th>
                </tr>
                </thead>
                <tbody>
                {records.map(record => (
                    <tr key={record.recordId}>
                        <td>{record.appointmentDate}</td>
                        <td>{record.appointmentTime}</td>
                        <td>{record.serviceName}</td>
                        <td>{record.clientId}</td>
                        <td>{record.doctorId}</td>
                        <td className="actions-column">
                            <button className="action-button view" onClick={() => handleOpenViewModal(record)}>Просмотр</button>
                            <button className="action-button edit" onClick={() => handleOpenEditModal(record)}>Редактировать</button>
                            <button className="action-button delete" onClick={() => handleDelete(record.recordId)}>Удалить</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {/* Модальное окно просмотра */}
            {isViewModalOpen && (
                <div className="modal show">
                    <div className="modal-content">
                        <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                        <h2>Просмотр записи</h2>
                        <p>ID: {selectedRecord.recordId}</p>
                        <p>Дата: {selectedRecord.appointmentDate}</p>
                        <p>Время: {selectedRecord.appointmentTime}</p>
                        <p>Услуга: {selectedRecord.serviceName}</p>
                    </div>
                </div>
            )}

            {/* Модальное окно редактирования */}
            {isEditModalOpen && (
                <div className="modal show">
                    <div className="modal-content">
                        <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                        <h2>Редактировать запись</h2>
                        <form onSubmit={handleUpdateSubmit}>

                            <label>ID доктора:</label>
                            <input name="doctorId" type="text" value={editingRecord.doctorId} onChange={handleInputChange} />

                            <label>День приема:</label>
                            <input name="appointmentDate" type="date" value={editingRecord.appointmentDate} onChange={handleInputChange} />

                            <label>Время приема:</label>
                            <input name="appointmentTime" type="time" value={editingRecord.appointmentTime} onChange={handleInputChange} />

                            <label>Услуга:</label>
                            <input name="serviceName" type="text" value={editingRecord.serviceName} onChange={handleInputChange} />

                            <button type="submit">Сохранить</button>
                            <button type="button" onClick={handleCloseModal}>Отмена</button>
                        </form>
                    </div>
                </div>
            )}
        </div>
    )
}