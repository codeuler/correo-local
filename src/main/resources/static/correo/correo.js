const divAsideCarpetas = document.querySelector(".aside__carpetas");
const divMain = document.querySelector(".main");
let botonAsideSeleccionado = null;
let asideCrearCarpetas = document.querySelector(".aside__crearCarpetas");
let asideCrearCarpetasContenedor = document.querySelector(".crearCarpetas--contenedor");
function agregarFuncionAbrir(event) {
    let divMensaje = event.target;
    if(!divMensaje.classList.contains("main__mensaje")) {
        divMensaje = event.target.closest(".main__mensaje");
    }
    const remitente = divMensaje.querySelector(".main__remitente").textContent;
    const asunto = divMensaje.querySelector(".main__asunto").textContent.replace(/:$/,"");
    const fecha = divMensaje.querySelector(".main__fecha").textContent;
    const contenido = divMensaje.querySelector(".main__contenido").textContent.replace(/^.*: /,"");
    const mensajeId = divMensaje.dataset.mensajeId;
    divMain.innerHTML = `<header class="main__headerMensajeAbierto">
                            <div class="main__volver"><i class="fa-solid fa-arrow-left fa-2xs"></i></div>
                            <p class="main__fechaMensajeAbierto">${fecha}</p>
                        </header>
                        <div class="main__correoAbierto">
                            <h2 class="main__tituloCorreo">${asunto}</h2>
                            <p class="main__remitenteCorreoAbierto">Remitente: ${remitente}</p>
                            <p class="main__contenidoCorreoAbierto">${contenido}</p>
                        </div>`;
    fetch(`mensajes/complejos/revisar`,  {
        method: "POST",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        },
        body: JSON.stringify({
            mensajeId: mensajeId
        })
    }).then(response => {
        if (response.ok) {
            console.log("Mensaje marcado como leído");
        } else {
            console.log("El mensaje no ha logrado ser marcado como leído");
        }
    });
    divMain.querySelector(".main__volver").addEventListener("click", (e) => {
        agregarMensajesDiv(botonAsideSeleccionado);
    })

}
function obtenerMensajes(nombreCarpeta) {
    fetch(`mensajes/complejos/obtener/${nombreCarpeta}`,  {
        method: "GET",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        }
    }).then(response => {
        return response.json();
    }).then(
        data => {
            data.forEach(el => {
                const nuevoElemento = document.createElement("div");
                const {mensajeId, asunto, cuerpo, fechaEnvio, emailRemitente, revisado} = el;
                nuevoElemento.innerHTML = `<p class="main__remitente">${emailRemitente}</p>
                    <p class="main__contenido"><span class="main__asunto">${asunto}:</span> ${cuerpo}</p>
                    <div class="main__guardar">
                        <i class="fa-solid fa-folder-open fa-lg" style="color: #1e1e1e;"></i>
                    </div>
                    <div class="main__eliminar">
                        <i class="fa-solid fa-trash-can fa-lg" style="color: #1e1e1e;"></i>
                    </div>
                    <p class="main__fecha">${new Date(fechaEnvio).toLocaleDateString('en-us')}</p>`;
                nuevoElemento.dataset.mensajeId = mensajeId;
                nuevoElemento.classList.add("main__mensaje");
                if(revisado) {
                    nuevoElemento.classList.add("mensaje--leido");
                }
                divMain.appendChild(nuevoElemento);
                nuevoElemento.addEventListener("click",(event) => agregarFuncionAbrir(event))
            })
        }
    );

}

function agregarMensajesDiv(boton) {
    divMain.innerHTML = "" // Se vacía el espacio en el que se muestran los mensajes de las carpetas
    //Quitar la clase de seleccionado a el botón que lo posea
    botonAsideSeleccionado.classList.toggle('boton__aside__seleccionado');
    //Ahora el boton será el que se selecciono en pantalla
    botonAsideSeleccionado = boton;
    //Se agrega la clase de boton seleccionado
    botonAsideSeleccionado.classList.toggle('boton__aside__seleccionado');
    obtenerMensajes(boton.textContent)
}

function crearBotonFolder(folder) {
    let nuevoBoton = divAsideCarpetas.appendChild(document.createElement('button'));
    nuevoBoton.textContent = folder.nombre;
    nuevoBoton.classList.add('aside__boton');
    nuevoBoton.addEventListener('click', (event) => {
        //history.pushState(`/${folder.name}`);
        agregarMensajesDiv(event.target);
    })
}
function crearFolder(folder) {
    return fetch(`/folders/crear`,  {
        method: "POST",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        },
        body: JSON.stringify({
            nombre:folder
        })
    }).then(
        response => {
            if(!response.ok) {
                response.json().then((data) => {
                  alert(data.nombre);
                })
                return false;
            } else {
                alert("Folder creado con exito");
                return true;
            }
        }
    );
}

fetch("folders/obtener/todos", {
    method: "GET",
    headers: {
        "Content-Type": "application/JSON",
        'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
    }
})
    .then(response =>  response.json())
    .then(
        data => {
            data = data.filter(folder =>
                folder.nombre !== 'Entrada' && folder.nombre !== 'Enviados');
            data.unshift({nombre:"Enviados"});
            data.unshift({nombre:"Entrada"});
            data.forEach(folder => {
                crearBotonFolder(folder)
            });
            botonAsideSeleccionado = divAsideCarpetas.querySelector(".aside__boton");
            botonAsideSeleccionado.classList.toggle("boton__aside__seleccionado");
            agregarMensajesDiv(botonAsideSeleccionado);
        }
    ).catch( (error) => {console.log(error.message);}
);

const botonRedactar = document.querySelector(".redactar--boton");
botonRedactar.addEventListener("click", () => {
    for (const nodo of divMain.children) {
        if (nodo.classList.contains('main__redaccion')) {
            return;
        }
    }
    divMain.innerHTML += `<form class="main__redaccion">
                                <header class="main__header">
                                    <h2 class="main__tituloNuevoMensaje">Mensaje nuevo</h2>
                                    <div class="main__cerrarNuevoMensaje"><i class="fa-solid fa-x fa-2xs"></i></div>
                                </header>
                                <div class="main__redactarNuevoMensaje">
                                        <input class="main__input main__destinatarios"
                                               placeholder="ejemplo@learncode.local, ejemplo2@learncode.local"
                                               pattern="^[A-Za-z0-9]{8,30}@learncode\.local(, [A-Za-z0-9]{8,30}@learncode\.local)*$"
                                               required>
                                        <input class="main__input main__asuntoRedactar"
                                               placeholder="Asunto del correo"
                                               required>
                                        <textarea rows="15"
                                               class="main__inputText main__input"
                                               placeholder="Cuerpo del correo"
                                               required></textarea>
                                        <button class="main__enviar"
                                                type="submit">Enviar</button>
                                </div>
                            </form>`;
    divMain.querySelector(".main__cerrarNuevoMensaje").addEventListener("click",
        ()=> {
        //Retirar el panel para redactar el correo
        divMain.removeChild(divMain.lastElementChild);
        }
    );

    const formRedaccion = divMain.querySelector(".main__redaccion");
    const correosDestinatarios = formRedaccion.querySelector(".main__destinatarios");
    correosDestinatarios.addEventListener("input", () => {
        if (correosDestinatarios.validity.patternMismatch) {
            correosDestinatarios.setCustomValidity("Los correos deben ser válidos y separados por una coma y espacio (, )" );
        } else {
            correosDestinatarios.setCustomValidity("");
        }
    })
    formRedaccion.addEventListener("submit", event => {
        event.preventDefault();
        const correos = divMain.querySelector(".main__destinatarios").value.split(", ");
        fetch(`mensajes/crear`,  {
            method: "POST",
            headers: {
                "Content-Type": "application/JSON",
                'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
            },
            body: JSON.stringify({
                correoDestinatarios: correos,
                asunto: divMain.querySelector(".main__asuntoRedactar").value,
                cuerpo: divMain.querySelector(".main__inputText").value,
                fecha: new Date()
            })
        })
            .then(
                response => {
                    if(response.ok) {
                        divMain.removeChild(divMain.lastElementChild);
                        alert("Mensaje enviado con exito");
                    } else if(response.status === 409) {
                        alert("No se ha podido enviar el mensaje dado que ninguno de los correos detinatarios existe")
                    }
            })
    })
})

asideCrearCarpetasContenedor.addEventListener("click", event => {
    asideCrearCarpetas.querySelector(".crearCarpetas--nuevaCarpeta").style.display = "none";
    asideCrearCarpetas.querySelector(".crearCarpetas--contenedor").style.display = "none";
    asideCrearCarpetas.querySelector(".crearCarpetas--input").style.display = 'block';
    asideCrearCarpetas.querySelector(".crearCarpetas--contenedor2").style.display = 'flex';
    asideCrearCarpetas.querySelector(".crearCarpetas--contenedor2").addEventListener("click", event => {
        crearFolder(asideCrearCarpetas.querySelector(".crearCarpetas--input").value).then(boleano => {
            if (boleano) {
                asideCrearCarpetas.querySelector(".crearCarpetas--nuevaCarpeta").style.display = "block";
                asideCrearCarpetas.querySelector(".crearCarpetas--contenedor").style.display = "flex";
                asideCrearCarpetas.querySelector(".crearCarpetas--input").style.display = 'none';
                asideCrearCarpetas.querySelector(".crearCarpetas--contenedor2").style.display = 'none';
            }
        })
    })
})
