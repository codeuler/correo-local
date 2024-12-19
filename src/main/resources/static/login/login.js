const email = document.getElementById("email");
const password = document.getElementById("password");
const form = document.querySelector(".card__form");
const errorOutput = document.querySelector(".card__error");

email.addEventListener("input", () => {
    if (email.validity.patternMismatch) {
        email.setCustomValidity("El correo debe contener un username de entre 8 y 30 caracteres (letras y numeros)" +
            " seguido de '@learncode.local'");
    } else {
        email.setCustomValidity("");
    }
});

form.addEventListener("submit", e => {
    e.preventDefault();
    errorOutput.style.display = "none";
    errorOutput.textContent = "";
    fetch("/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/JSON"
        },
        body: JSON.stringify({
            username: email.value,
            password: password.value
        })
    }).then(response => {
        if (response.ok) {
            response.json().then((data) => {
                //Guardar jwt
                const {token} = data;
                localStorage.setItem("token", token);
                window.location.replace("/bandeja")
            })
        } else if (response.status === 401) {
            errorOutput.textContent = "Error de credenciales";
            errorOutput.style.display = "block";
        } else {
            console.log(response.json());
        }
    });

})

