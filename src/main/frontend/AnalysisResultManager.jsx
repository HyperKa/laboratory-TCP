import React, {useState, useEffect} from "react";

export default function AnalysisResultManager() {
    const [result, setResult] = useState([]);
    const [loading, setLoading] = useState(true);

    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [isViewModalOpen, setIsViewModalOpen] = useState(false);

    const [selectedResult, setSelectedResult] = useState(null);
    const [editingResult, setEditingResult] = useState(null);

    useEffect(() => {
        const apiUrl = '/api/v1/analysis-results';

        fetch(apiUrl)
            .then(res => {
                if (!res.ok) throw new Error('Сетевая ошибка');
                return res.json();
            })
            .then(data => {
                setResult(data);
                setLoading(false);
            })
            .catch(error => {
                console.error("Ошибка загрузки результатов анализов:", error);
                setLoading(false);
            });
    }, []); // Пустой массив - выполнить один раз

    const handleOpenViewModal = (result) => {
        setSelectedResult(result);
        setIsViewModalOpen(true);
    };

    const handleOpenEditModal = (result) => {
        setSelectedResult(result);
        setEditingResult({ ...result });
        setIsEditModalOpen(true);
    }

    const handleCloseModal = () => {
        setIsViewModalOpen(false);
        setIsEditModalOpen(false);
        setSelectedResult(null);
        setEditingResult(null);
    }

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setEditingResult({ ...editingResult, [name]: value })
    }

    const handleDelete = (recordId) => {
        if(confirm(`Вы уверены, что хотите удалить результат анализа с ID: ${recordId}?`)) {
            fetch(`/api/v1/analysis-results/${recordId}`, {
                method: 'DELETE'})
                .then(responce => {
                    if (responce.ok) {
                        setResult(result.filter(r => r.recordId !== recordId));
                        alert('Клиент удален');
                    } else {
                        alert('Ошибка при удалении клиента');
                    }
                });
        }
    }

    const handleUpdateSubmit = (event) => {
        event.preventDefault();
        fetch(`/api/v1/analysis-results/${editingResult.recordId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(editingResult)
        })
            .then(res => res.json())
            .then(updatedResult => {
                setResult(result.map(r => (r.recordId === updatedResult.recordId ? updatedResult : r)));
                alert('Данные анализа обновлены');
                handleCloseModal();
            })
            .catch(error => console.error("Ошибка обновления: ", error));
    }

    if (loading) return <div>Загрузка записей на прием...</div>

    return (
        <div>
            <table className="data-table">
                <thead>
                <tr>
                    <th>Файл исследования</th>
                    <th>Дата исследования</th>
                    <th>ID клиента</th>
                    <th className="actions-column">Действия</th>
                </tr>
                </thead>

                <tbody>
                {result.map(result => (
                    <tr key={result.recordId}>
                        <td>{result.researchFile}</td>
                        <td>{result.analysisDate}</td>
                        <td>{result.clientId}</td>
                        <td className="col-actions">
                            <div className="actions-container">
                                <button className="action-button view" onClick={() => handleOpenViewModal(result)}>Просмотр</button>
                                <button className="action-button edit" onClick={() => handleOpenEditModal(result)}>Редактировать</button>
                                <button className="action-button delete" onClick={() => handleDelete(result.recordId)}>Удалить</button>
                            </div>
                        </td>
                    </tr>
                ))}
                </tbody>

                {isViewModalOpen  && (
                    <div className="modal show">
                        <div className="modal-content">
                            <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                            <h2>Просмотр анализа</h2>
                            <p>ID: {selectedResult.recordId}</p>
                            <p>Дата: {selectedResult.researchFile}</p>
                            <p>Время: {selectedResult.analysisDate}</p>
                            <p>Услуга: {selectedResult.clientId}</p>
                        </div>
                    </div>
                )}

                {isEditModalOpen && (
                    <div className="modal show">
                        <div className="modal-content">
                            <span className="close-btn" onClick={handleCloseModal}>&times;</span>
                            <h2>Редактировать анализ</h2>
                            <form onSubmit={handleUpdateSubmit}>

                                <label>Файл анализа:</label>
                                <input name="researchFile" type="text" value={editingResult.researchFile} onChange={handleInputChange} />

                                <label>Дата анализа:</label>
                                <input name="analysisDate" type="date" value={editingResult.analysisDate} onChange={handleInputChange} />

                                <div className="modal-actions">
                                    <button type="submit">Сохранить</button>
                                    <button type="submit" onClick={handleCloseModal}>Отмена</button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </table>
        </div>
    )
}