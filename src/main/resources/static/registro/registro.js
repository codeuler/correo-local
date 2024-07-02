const nameUser = document.getElementById("name");
const lastname = document.getElementById("lastname");
const username = document.getElementById("username");
const password = document.getElementById("password");
const verificacion = document.getElementById("verificacion");

const validarFormulario = (event) => {
    event.preventDefault();
}

const verificarCampo = (input) => {
    if (input.validity.patternMismatch) {
        input.setCustomValidity("Debe tener únicamente letras y entre (3 - 16) caracteres");
    } else {
        input.setCustomValidity("")
    }
}

/*
    Verificar que el campo de nombre sea únicamente letras
*/
nameUser.addEventListener("input", function (event) { 
    if (nameUser.validity.patternMismatch) {
        nameUser.setCustomValidity("Debe tener únicamente letras y entre (3 - 16) caracteres");
    } else {
        nameUser.setCustomValidity("");
    }
});

/*
    Verificar que el campo de apellido sea únicamente letras
*/
lastname.addEventListener("input", function (event) { 
    if (lastname.validity.patternMismatch) {
        lastname.setCustomValidity("Debe tener únicamente letras y entre (3 - 16) caracteres");
    } else {
        lastname.setCustomValidity("");
    }
});

/*
    Verificar que el campo de username contenga solo letras y/o números
*/
username.addEventListener("input", function (event) { 
    if (username.validity.patternMismatch) {
        username.setCustomValidity("Debe tener únicamente letras y/o números");
    } else {
        username.setCustomValidity("");
    }
});

/*
    Verificar que el campo de constraseña contenga lo especificado
*/
password.addEventListener("input", function (event) {
    if (password.validity.patternMismatch) {
        password.setCustomValidity("Password debe contener mínimo 8 caracteres de los cuales deben haber al menos una letra en mayúscula, una en minuscula, un número y un caracter especial");
    } else {
        password.setCustomValidity("");
    }
});

/*
    Verificar que el campo de verificación sea exactamente igual al de contraseña
*/
verificacion.addEventListener("input", function (event) {
    if (!(password.value === verificacion.value)) {
        verificacion.setCustomValidity("Las contraseñas no coinciden");
    } else {
        verificacion.setCustomValidity("");
    }
});

document.querySelector(".card__form").addEventListener("submit", validarFormulario);

