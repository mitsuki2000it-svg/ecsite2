async function setCsrfToken() {

    const response = await fetch("/csrf");

    const csrf = await response.json();

    const input = document.createElement("input");

    input.type = "hidden";
    input.name = csrf.parameterName;
    input.value = csrf.token;

    document
        .getElementById("login-form")
        .appendChild(input);
}

setCsrfToken();


const params =
    new URLSearchParams(window.location.search);

if (params.has("error")) {
    document
        .getElementById("login-error")
        .textContent =
        "ユーザー名またはパスワードが違います";
}
if (params.has("logout")) {

    document
        .getElementById("login-error")
        .textContent =
        "ログアウトしました";
}
