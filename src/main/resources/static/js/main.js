// Эти функции должны быть доступны глобально
window.openEditAnalysisModal = function(recordId) {
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

window.openEditDoctorModal = function(recordId) {
    if (!recordId) {
        console.error("recordId не указан");
        return;
    }

    // Загружаем данные через API-маршрут
    fetch(`/web/doctors/api/${recordId}`)
        .then(response => {
            if (!response.ok) throw new Error("Ошибка загрузки данных");
            return response.json();
        })
        .then(data => {
            document.getElementById('editDoctorId').value = data.id;
            document.getElementById('editLastName').value = data.lastName;
            document.getElementById('editFirstName').value = data.firstName;
            document.getElementById('editSpecialization').value = data.specialization;
            document.getElementById('editExperience').value = data.experience;
            document.getElementById('editLogin').value = data.login;
            document.getElementById('editPassword').value = data.password;

            const modal = document.getElementById('editDoctorModal');
            if (modal) modal.classList.add('show');
        })
        .catch(error => console.error('Ошибка загрузки данных:', error));
};



window.openAddAnalysisModal = function() {
    const modal = document.getElementById('addAnalysisModal');
    if (modal) modal.classList.add('show');
};

window.openAddDoctorModal = function() {
    const modal = document.getElementById('addDoctorModal');
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

window.viewDoctor = function(recordId) {
    if (!recordId) {
        console.error("recordId не указан");
        return;
    }

    window.location.href = `/web/doctors/${recordId}`;
};



// Функция для удаления записи

window.deleteRecord = function(recordId, type) {
    if (!recordId) {
        console.error("recordId не указан");
        return;
    }

    /*
    fetch(`/web/analysis-results/${recordId}/delete`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "_method=delete",
        credentials: 'same-origin'
    })
    */
    const url = type === "doctor"
        ? `/web/doctors/${recordId}/delete`
        : `/web/analysis-results/${recordId}/delete`;

    fetch(url, {
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
    const editAnalysisForm = document.getElementById("editAnalysisForm");
    if (editAnalysisForm) {
        editAnalysisForm.addEventListener("submit", function(event) {
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

    // Обработка формы редактирования доктора
    const editDoctorForm = document.getElementById("editDoctorForm");
    if (editDoctorForm) {
        editDoctorForm.addEventListener("submit", function(event) {
            event.preventDefault();

            const recordId = document.getElementById("editDoctorId").value || document.getElementById("editRecordId").value;
            const lastName = document.getElementById("editLastName").value;
            const firstName = document.getElementById("editFirstName").value;
            const specialization = document.getElementById("editSpecialization").value;
            const experience = document.getElementById("editExperience").value;
            const login = document.getElementById("editLogin").value;
            const password = document.getElementById("editPassword").value;

            const formData = new URLSearchParams();
            formData.append("lastName", lastName);
            formData.append("firstName", firstName);
            formData.append("specialization", specialization);
            formData.append("experience", experience);
            formData.append("login", login);
            formData.append("password", password);

            fetch(`/web/doctors/${recordId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: formData.toString(),
                credentials: 'same-origin'
            })
            .then(response => {
                if (!response.ok) throw new Error("Ошибка сохранения доктора");
                alert("Доктор успешно сохранен!");
                closeModal("editDoctorModal");
                location.reload();
            })
            .catch(err => {
                console.error("Ошибка:", err);
                alert("Не удалось сохранить данные доктора");
            });
        });
    }
});