document.addEventListener("DOMContentLoaded", function () {
    const logoutBtn = document.getElementById("logoutBtn");

    if (logoutBtn) {
        logoutBtn.addEventListener("click", async function () {
            try {
                const response = await fetch("/auth/logout", {
                    method: "POST",
                    credentials: "same-origin", // для передачи cookies
                    headers: {
                        "Content-Type": "application/json"
                    }
                });

                if (response.ok) {
                    // редирект на страницу входа или главную
                    window.location.href = "/auth/login";
                } else {
                    alert("Ошибка при выходе из системы");
                }
            } catch (error) {
                console.error("Ошибка выхода:", error);
            }
        });
    }
});
