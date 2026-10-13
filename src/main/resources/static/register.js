async function registerUser() {

    const username =
        document.getElementById("username").value;

    const password =
        document.getElementById("password").value;

    const csrf = await getCsrfToken();

    const response = await fetch("/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrf.headerName]: csrf.token
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    });

    const message =
        document.getElementById("message");

    if (response.ok) {

        const result = await response.text();

        message.textContent = result;

        setTimeout(() => {
            window.location.href = "/login";
        }, 1000);

    } else {

        const contentType =
            response.headers.get("content-type");

        if (contentType &&
            contentType.includes("application/json")) {

            const errors = await response.json();

            message.textContent =
                Object.values(errors).join(" / ");


        } else {

            const result = await response.text();

            message.textContent = result;
        }
    }

    async function getCsrfToken() {
        const response = await fetch("/csrf");
        const csrf = await response.json();

        return csrf
    }
}
