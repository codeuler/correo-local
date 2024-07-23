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
    fetch("/login", {
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
            console.log("Ya se pudo loguear");
        } else if (response.status === 401) {
            console.log("Credenciales no validas");
            console.log(response.json());
        } else {
            console.log("Sabrá dios que pasó");
            console.log(response.json());
        }
    });

})

