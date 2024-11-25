const nameUser = document.getElementById("name");
const lastname = document.getElementById("lastname");
const username = document.getElementById("username");
const password = document.getElementById("password");
const verificacion = document.getElementById("verificacion");
const errorCorreo = document.querySelector(".card__errorCorreo");
const cuentaCreada = document.querySelector(".card__cuentaCreada");
const errorExtra = document.querySelector(".card__errorExtra")

/*
    Verificar que el campo de nombre sea únicamente letras
*/
nameUser.addEventListener("input", () => {
    if (nameUser.validity.patternMismatch) {
        nameUser.setCustomValidity("Debe tener únicamente letras y entre (3 - 16) caracteres");
    } else {
        nameUser.setCustomValidity("");
    }
});

/*
    Verificar que el campo de apellido sea únicamente letras
*/
lastname.addEventListener("input", () => {
    if (lastname.validity.patternMismatch) {
        lastname.setCustomValidity("Debe tener únicamente letras y entre (3 - 16) caracteres");
    } else {
        lastname.setCustomValidity("");
    }
});

/*
    Verificar que el campo de username contenga solo letras y/o números
*/
username.addEventListener("input", () => {
    if (username.validity.patternMismatch) {
        username.setCustomValidity("Debe tener únicamente letras y/o números (min 8 caracteres)");
    } else {
        username.setCustomValidity("");
    }
});

/*
    Verificar que el campo de constraseña contenga lo especificado
*/
password.addEventListener("input", () => {
    if (password.validity.patternMismatch) {
        password.setCustomValidity("Password debe contener mínimo 8 caracteres de los cuales deben haber " +
            "al menos una letra en mayúscula, una en minuscula, un número y un caracter especial");
    } else {
        password.setCustomValidity("");
    }
});

/*
    Verificar que el campo de verificación sea exactamente igual al de contraseña
*/
verificacion.addEventListener("input", () => {
    if (!(password.value === verificacion.value)) {
        verificacion.setCustomValidity("Las contraseñas no coinciden");
    } else {
        verificacion.setCustomValidity("");
    }
});

document.querySelector(".card__form").addEventListener("submit",  event=> {
    event.preventDefault();
    errorCorreo.style.display = "none";
    cuentaCreada.style.display = "none";
    errorExtra.style.display = "none";
    fetch("/registro",{
        method: "POST",
        headers: {
            "Content-Type": "application/JSON",
        },
        body: JSON.stringify({
            nombre: nameUser.value,
            apellido: lastname.value,
            correo: username.value + "@learncode.local",
            password: password.value
        })
    })
        .then(response => {
            if (response.ok) {
                //La respuesta http tiene un codigo 2xx y es considerada existosa
                cuentaCreada.style.display = "block";
                nameUser.value = "";
                lastname.value = "";
                username.value = "";
                password.value = "";
                verificacion.value = "";
                response.json().then(data => {
                    const {token} = data;
                    //Guardar jwt
                    localStorage.setItem("token",token);
                });
            } else if (response.status === 409) {
                //El correo ya existe
                errorCorreo.style.display = "block";
            } else {
                /*
                 * En caso de presentarse algún error diferente del 409 se pondrá en pantalla
                 */
                response.json().then((data) => {
                    errorExtra.style.display = "block";
                    errorExtra.textContent = JSON.stringify(data, null, 2);
                });
            }
        })
});