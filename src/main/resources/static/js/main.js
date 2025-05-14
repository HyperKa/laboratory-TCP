// Эти функции должны быть доступны глобально
window.openEditModal = function(recordId) {
    if (!recordId) {
        console.error("recordId не указан");
        return;
    }

    // Загружаем данные через API-маршрут
    fetch(`/web/analysis-results/api/${recordId}`)
        .then(response => {
            if (!response.ok) throw new Error("Ошибка загрузки данных");
            return response.json();
        })
        .then(data => {
            document.getElementById('editRecordId').value = data.recordId;
            document.getElementById('editResearchFile').value = data.researchFile;
            document.getElementById('editAnalysisDate').value = data.analysisDate;

            const modal = document.getElementById('editAnalysisModal');
            if (modal) modal.classList.add('show');
        })
        .catch(error => console.error('Ошибка загрузки данных:', error));
};

window.openAddModal = function() {
    const modal = document.getElementById('addAnalysisModal');
    if (modal) modal.classList.add('show');
};

window.closeModal = function(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('show');
};

// Функция для просмотра записи
window.viewRecord = function(recordId) {
    if (!recordId) {
        console.error("recordId не указан");
        return;
    }

    window.location.href = `/web/analysis-results/${recordId}`;
};

// Функция для удаления записи
window.deleteRecord = function(recordId) {
    if (!recordId) {
        console.error("recordId не указан");
        return;
    }

    fetch(`/web/analysis-results/${recordId}/delete`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "_method=delete",
        credentials: 'same-origin'
    })
    .then(response => {
        if (!response.ok) throw new Error("Ошибка удаления");
        return response;
    })
    .then(() => {
        alert("Запись успешно удалена!");
        location.reload();
    })
    .catch(err => {
        console.error("Ошибка:", err);
        alert("Не удалось удалить запись");
    });
};

// Остальная логика выполняется при загрузке DOM
document.addEventListener("DOMContentLoaded", function () {
    // Валидация форм
    const forms = document.querySelectorAll(".form");
    forms.forEach(form => {
        form.addEventListener("submit", function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                alert("Пожалуйста, заполните все поля.");
            }
        });
    });

    // Уведомления
    const alertBox = document.querySelector(".alert");
    if (alertBox) {
        alertBox.classList.add("show");

        setTimeout(() => {
            alertBox.classList.remove("show");
            alertBox.classList.add("hide");

            setTimeout(() => alertBox.remove(), 500);
        }, 3000);
    }

    // Закрытие модального окна по клику вне области
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(event) {
            if (event.target === modal) {
                modal.classList.remove('show');
            }
        });
    });

    // Обработка формы редактирования
    const editForm = document.getElementById("editAnalysisForm");
    if (editForm) {
        editForm.addEventListener("submit", function(event) {
            event.preventDefault();

            const recordId = document.getElementById("editRecordId").value;
            const researchFile = document.getElementById("editResearchFile").value;
            const analysisDate = document.getElementById("editAnalysisDate").value;

            console.log("Отправляем recordId:", recordId);
            console.log("Файл исследования:", researchFile);
            console.log("Дата анализа:", analysisDate);

            // Отправка данных как application/x-www-form-urlencoded
            const formData = new URLSearchParams();
            formData.append("researchFile", researchFile);
            formData.append("analysisDate", analysisDate);

            fetch(`/web/analysis-results/${recordId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: formData.toString(),
                credentials: 'same-origin'
            })
            .then(response => {
                if (!response.ok) throw new Error("Ошибка сохранения");
                return response;
            })
            .then(() => {
                alert("Изменения успешно сохранены!");
                closeModal("editAnalysisModal");
                location.reload();
            })
            .catch(err => {
                console.error("Ошибка:", err);
                alert("Не удалось сохранить изменения");
            });
        });
    }
});