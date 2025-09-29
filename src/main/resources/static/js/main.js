/**
 * Открывает модальное окно по его ID.
 * Используется кнопками "Добавить доктора" и "Сменить пароль".
 * @param {string} modalId - ID модального окна, которое нужно открыть.
 */
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        // Используем 'display: flex', так как он задан в вашем CSS для .modal.show
        modal.style.display = 'flex';
    }
}

/**
 * Закрывает модальное окно по его ID.
 * Используется кнопками "Отмена" и крестиками (×) в модальных окнах Thymeleaf.
 * @param {string} modalId - ID модального окна, которое нужно закрыть.
 */
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
    }
}


// Этот блок кода выполнится один раз, когда страница полностью загрузится.
document.addEventListener("DOMContentLoaded", function () {

    // --- Логика для всплывающих уведомлений (Alerts) ---
    // Находит на странице блок с классом .alert, показывает его на 3 секунды, а затем плавно скрывает.
    const alertBox = document.querySelector(".alert");
    if (alertBox) {
        // Показываем alert (предполагается, что в CSS есть анимация)
        alertBox.classList.add("show");

        // Устанавливаем таймер на его скрытие через 3 секунды
        setTimeout(() => {
            alertBox.classList.remove("show");
            alertBox.classList.add("hide");
            // Полностью удаляем элемент из DOM после завершения анимации скрытия
            setTimeout(() => alertBox.remove(), 500);
        }, 3000);
    }

    // --- Логика для закрытия модальных окон по клику на фон ---
    // Добавляет обработчик событий на все модальные окна, которые остались от Thymeleaf.
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(event) {
            // Закрываем окно, только если клик был на сам фон (event.target), а не на его содержимое.
            if (event.target === modal) {
                closeModal(modal.id);
            }
        });
    });

    // --- Логика для оставшихся Thymeleaf-форм (если они есть) ---
    // ПРИМЕЧАНИЕ: Формы добавления/редактирования докторов и анализов теперь в React.
    // Этот код может быть полезен для других простых форм, например, для формы обновления профиля клиента.
    const forms = document.querySelectorAll("form"); // Простая валидация для ВСЕХ форм
    forms.forEach(form => {
        form.addEventListener("submit", function (event) {
            // Этот код не будет влиять на React-формы, так как у них есть event.preventDefault()
            if (!form.checkValidity()) {
                event.preventDefault();
                alert("Пожалуйста, заполните все обязательные поля.");
            }
        });
    });

    // --- ОБРАБОТКА ФОРМЫ ДОБАВЛЕНИЯ ДОКТОРА (Thymeleaf) ---
    // Так как модальное окно добавления доктора у вас осталось в Thymeleaf, его обработчик тоже нужно оставить.
    const addDoctorForm = document.getElementById("addDoctorForm");
    if (addDoctorForm) {
        addDoctorForm.addEventListener("submit", function(event) {
            // Эта форма отправляется стандартным способом, без fetch,
            // поэтому event.preventDefault() здесь не нужен.
            // Этот блок кода остается на случай, если вы захотите добавить AJAX-отправку в будущем.
        });
    }

});