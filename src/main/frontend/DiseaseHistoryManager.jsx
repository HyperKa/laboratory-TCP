import React, {useEffect, useState} from "react";

export default function DiseaseHistoryManager() {
    const [histories, setHistories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [userRole, setUserRole] = useState('');

    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [isViewModalOpen, setIsViewModalOpen] = useState(false);

    const [selectedRecord, setSelectedRecord] = useState(null);
    const [editingRecord, setEditingRecord] = useState(null);

    useEffect(() => {
        const container = document.getElementById('react-disease-history-manager-container');
        const role = container ? container.dataset.userRole : '';
        setUserRole(role);

        fetch('/api/v1/disease-history')
            .then(res => res.json())
            .then(data => setHistories(data))
            .finally(() => setLoading(false));
    }, []);

    const handleOpenViewModal = (history) => {
        setSelectedRecord(history);
        setIsViewModalOpen(true);
    };

    const handleOpenEditModal = (history) => {
        setSelectedRecord(history);
        setEditingRecord({ ...history });
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
        if(confirm(`Вы уверены, что хотите удалить историю болезни с ID: ${recordId}?`)) {
            fetch(`/api/v1/disease-history/${recordId}`, {
                method: 'DELETE'})
                .then(responce => {
                    if (responce.ok) {
                        setHistories(histories.filter(h => h.recordId !== recordId));
                        alert('История удалена');
                    } else {
                        alert('Ошибка при удалении истории');
                    }
                });
        }
    }

    const handleUpdateSubmit = (event) => {
        event.preventDefault();
        fetch(`/api/v1/disease-history/${editingRecord.recordId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(editingRecord)
        })
            .then(res => res.json())
            .then(updatedRecord => {
                setHistories(histories.map(h => (h.recordId === updatedRecord.recordId ? updatedRecord : h)));
                alert('Данные истории обновлены');
                handleCloseModal();
            })
            .catch(error => console.error("Ошибка обновления: ", error));
    }

    // Определение на роль доктора/админа
    const isAdminOrDoctor = userRole === 'ROLE_ADMIN' || userRole === 'ROLE_DOCTOR';

    if (loading) return <div>Загрузка истории болезней...</div>


    return (
        <table className="data-table">
            <thead>
            <tr>
                <th>Начало</th>
                <th>Окончание</th>
                <th>Диагноз</th>
                <th>Врач</th>
                {/* УСЛОВНЫЙ РЕНДЕРИНГ: */}
                {isAdminOrDoctor && <th>ID доктора</th>}
                {isAdminOrDoctor && <th>ID клиента</th>}
                {isAdminOrDoctor && <th className="actions-column">Действия</th>}
            </tr>
            </thead>
            <tbody>
            {histories.map(history => (
                <tr key={history.recordId}>
                    <td>{histories.find(recordId)}</td>  {/* Эксперименты */}
                    <td>{history.startDate}</td>
                    <td>{history.endDate}</td>
                    <td>{history.disease}</td>
                    <td>{history.firstNameDoctor} {history.lastNameDoctor}</td>
                    {/* УСЛОВНЫЙ РЕНДЕРИНГ: */}
                    {isAdminOrDoctor && <td>{history.doctorId}</td>}
                    {isAdminOrDoctor && <td>{history.clientId}</td>}
                    {isAdminOrDoctor && (
                        <td className="actions-column">
                            <button className="action-button view" onClick={() => handleOpenViewModal(history)}>Просмотр</button>
                            <button className="action-button edit" onClick={() => handleOpenEditModal(history)}>Редактировать</button>
                            <button className="action-button delete" onClick={() => handleDelete(history.recordId)}>Удалить</button>
                        </td>
                    )}
                </tr>
            ))}
            </tbody>

            {isViewModalOpen  && (
                <div className="modal show">
                    <div className="modal-content">
                        <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                        <h2>Просмотр истории болезни</h2>
                        <p>ID: {selectedRecord.recordId}</p>
                        <p>начало: {selectedRecord.startDate}</p>
                        <p>Окончание: {selectedRecord.endDate}</p>
                        <p>Диагноз: {selectedRecord.disease}</p>
                        <p>Врач: {selectedRecord.firstNameDoctor}  {selectedRecord.lastNameDoctor}</p>
                        <p>ID врача: {isAdminOrDoctor && selectedRecord.doctorId}</p>
                        <p>ID клиента: {isAdminOrDoctor && selectedRecord.clientId}</p>
                    </div>
                </div>
            )}

            {isEditModalOpen && (
                <div className="modal show">
                    <div className="modal-content">
                        <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                        <h2>Редактировать анализ</h2>
                        <form onSubmit={handleUpdateSubmit}>

                            <label>Начало:</label>
                            <input name="startDate" type="datetime-local" value={editingRecord.startDate.substring(0, 16)} onChange={handleInputChange} />

                            <label>Окончание:</label>
                            <input name="endDate" type="datetime-local" value={editingRecord.endDate.substring(0, 16)} onChange={handleInputChange} />

                            <label>Диагноз:</label>
                            <input name="disease" type="text" value={editingRecord.disease} onChange={handleInputChange} />

                            <label>Фамилия врача:</label>
                            <input name="lastNameDoctor" type="text" value={editingRecord.lastNameDoctor} onChange={handleInputChange} />

                            <label>Имя врача:</label>
                            <input name="firstNameDoctor" type="text" value={editingRecord.firstNameDoctor} onChange={handleInputChange} />

                            <label>Специальность</label>
                            <input name="profession" type="text" value={editingRecord.profession} onChange={handleInputChange} />

                            <label>ID врача:</label>
                            <input name="doctorId" type="number" value={editingRecord.doctorId} onChange={handleInputChange} />

                            <label>ID клиента:</label>
                            <input name="clientId" type="number" value={editingRecord.clientId} onChange={handleInputChange} />

                            <button type="submit">Сохранить</button>
                            <button type="button" onClick={handleCloseModal}>Отмена</button>
                        </form>
                    </div>
                </div>
            )}
        </table>
    )
}