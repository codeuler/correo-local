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
        if (!response.ok) {
            window.alert("No se ha logrado marcar el mensaje como leído");
        }
    });
    divMain.querySelector(".main__volver").addEventListener("click", (e) => {
        agregarMensajesDiv(botonAsideSeleccionado);
    })

}

function llamarApiCambiarMensajeFolder(mensajeId, idFolderOrigen, idFolderDestino) {
    return fetch("mensajes/cambiarFolder", {
        method: "PUT",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        },
        body: JSON.stringify({
            idMesajeCambiar: mensajeId,
            idFolderOrigen: idFolderOrigen,
            idFolderDestino: idFolderDestino
        })
    });
}

function cambiarMensajeFolder(e) {
    const padre = e.target.closest(".main__mensaje");
    let mensajeId = padre.dataset.mensajeId;
    obtenerFolders()
        .then(data => {
            data = data.filter(folder => folder.nombre !== 'Entrada' && folder.nombre !== 'Enviados');
            let opciones = ``;
            for (const folder of data){
                if (!(folder.id == botonAsideSeleccionado.dataset.folderId)){
                    opciones += `<option value=${folder.id}>${folder.nombre}</option> data-folder-id=${folder.id}</option>`;
                }
            }
            if (data.length === 0 || opciones.length === 0) {
                alert("No existe alguna carpeta permitida con la cual se pueda realizar el cambio");
                return;
            }
            const mainGuardar = e.target.closest(".main__guardar");

            mainGuardar.innerHTML += `<form class="main__seleccion">
                                    <label for="carpetasElegir">Elige la carpeta destino: </label>
                                        <select class="main__select" name="carpetasElegir" id="carpetasElegir"> 
                                            <option>Selecciona una opcion</option>` +
                                            opciones + `</select></form>`;
            const selectFormulario = mainGuardar.querySelector(".main__select");
            // Para que no se ejecute el evento de click del padre
            selectFormulario.addEventListener("click", (eventoC) => {
                    eventoC.stopPropagation();
                }
            );
            selectFormulario.addEventListener("change", (evento) => {
                evento.stopPropagation();
                let eleccion = window.confirm("¿Estás seguro que quieres cambiar de ubicación el mensaje?");
                if (eleccion) {
                    const idFolderDestino = selectFormulario.value;
                    llamarApiCambiarMensajeFolder(mensajeId, botonAsideSeleccionado.dataset.folderId, idFolderDestino).then(respuesta => {
                        if(respuesta.ok) {
                            mainGuardar.querySelector(".main__select").remove();
                            agregarMensajesDiv(botonAsideSeleccionado);
                        } else {
                            respuesta.text().then(cuerpo => {
                                window.alert("No se ha podido cambiar el mensaje de ubicación: " + cuerpo)
                            })
                        }
                    });
                } else {
                    mainGuardar.querySelector(".main__select").remove();
                    agregarMensajesDiv(botonAsideSeleccionado);
                }
            })
        });

}

function verificarFolder(mensajeId) {
    return fetch(`mensajes/${mensajeId}/validacionFolder`, {
        method: "GET",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        }
    }).then(respuesta => respuesta.ok);
}
function retornarRespuestaModal(modal) {
    return new Promise(resolve => {
        modal.querySelector(".boton--carpeta").addEventListener("click",()=> {
            resolve(0); // Da como resuelta la promesa retornando un valor de 0
            modal.close(); // Cierra el modal
        });
        modal.querySelector(".boton--completamente").addEventListener("click",()=> {
            resolve(1);
            modal.close();
        });
        modal.querySelector(".boton--cancelar").addEventListener("click",()=> {
            resolve(2);
            modal.close();
        });
    })
}

function mostrarQuitarFormulario(e, elementoHtml) {
    e.stopPropagation();
    const formulario = elementoHtml.querySelector(".main__seleccion");
    const formulario2 = divMain.querySelector(".main__seleccion");
    if (formulario !== null) {
        formulario.remove();
        return;
    } else if (formulario2 !== null) {
        divMain.querySelector(".main__seleccion").remove();
    }
    cambiarMensajeFolder(e);
}

function vaciarMainMensajes() {
    document.querySelector(".main").innerHTML = "";
}

function eliminarMensajeCarpeta(mensajeId, folderId) {
    return fetch("mensajes/eliminar/folder",{
        method:"DELETE",
        headers:{
            "Content-Type": "application/json",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        },
        body: JSON.stringify({
            mensajeId: mensajeId,
            folderId: folderId
        })
    }).then(response => {
        switch (response.status) {
            case 409:
                response.text().then(data => {
                    alert(data)
                });
                break;
            case 404:
                alert("No se ha encontrado la información ");
                break;
            default:
                break;
        }
    });
}

function eliminarMensajeCompletamente(mensajeId) {
    return fetch("mensajes/eliminar",{
        method:"DELETE",
        headers:{
            "Content-Type": "application/json",
            'Authorization': `Bearer ${localStorage.getItem("token")}`
        },
        body: JSON.stringify({
            mensajeId: mensajeId
        })
    }).then(response => {
        if (!response.ok) {
            response.text().then(
                data => console.log(data)
            )
        } else {
            alert("Mensaje eliminado exitosamente");
        }
    })
}

function eliminarMensaje(e) {
    e.stopPropagation();
    const mainMensaje = e.target.closest(".main__mensaje")
    const mensajeId = mainMensaje.dataset.mensajeId;
    verificarFolder(mensajeId).then(boleano => {
        if (boleano) {
            const anuncio = "¿Estas seguro de que quieres eliminar completamente este mensaje?";
            if (window.confirm(anuncio)) {
                eliminarMensajeCompletamente(mensajeId).then( () =>obtenerMensajes(botonAsideSeleccionado.dataset.folderId));
            }
            return;
        }
        const ventanaModal = document.querySelector(".dialog__eliminar");
        ventanaModal.showModal();
        retornarRespuestaModal(ventanaModal).then(eleccion => {
            switch (eleccion) {
                case 0:
                    eliminarMensajeCarpeta(mensajeId, botonAsideSeleccionado.dataset.folderId).then(
                        () => obtenerMensajes(botonAsideSeleccionado.dataset.folderId)
                    )
                    break;
                case 1:
                    eliminarMensajeCompletamente(mensajeId).then( () => obtenerMensajes(botonAsideSeleccionado.dataset.folderId));
                    break;
                default:
                    break;
            }
        })
    });
}

function obtenerMensajes(folderId) {
    fetch(`mensajes/complejos/obtener/${folderId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        }
    }).then(response => {
        if (!response.ok) {
            window.alert("No se han podido cargar los mensajes desde el servidor");
        }
        return response.json();
    }).then(
        data => {
            vaciarMainMensajes();
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
                if (revisado) {
                    nuevoElemento.classList.add("mensaje--leido");
                }
                divMain.appendChild(nuevoElemento);
                nuevoElemento.addEventListener("click", (event) => agregarFuncionAbrir(event));
                nuevoElemento.querySelector(".main__guardar").addEventListener("click", (e) => mostrarQuitarFormulario(e,nuevoElemento))
                nuevoElemento.querySelector(".main__eliminar").addEventListener("click", (e) => eliminarMensaje(e));
                });
            });
}

function agregarMensajesDiv(boton) {
    divMain.innerHTML = "" // Se vacía el espacio en el que se muestran los mensajes de las carpetas
    //Quitar la clase de seleccionado a el botón que lo posea
    botonAsideSeleccionado.classList.toggle('boton__aside__seleccionado');
    //Ahora el boton será el que se selecciono en pantalla
    botonAsideSeleccionado = boton;
    //Se agrega la clase de boton seleccionado
    botonAsideSeleccionado.classList.toggle('boton__aside__seleccionado');
    obtenerMensajes(boton.dataset.folderId)
}

function crearOption(contenido, value) {
    const elemento = document.createElement("option");
    elemento.value = value;
    elemento.textContent = contenido;
    return elemento;
}

function eliminarCarpeta(folderId) {
    fetch("/folders/eliminar",{
        method: "DELETE",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        },
        body: JSON.stringify({
            folderId: folderId
        })
    }).then((respuesta)=>{
        if (!respuesta.ok) {
            window.alert("El folder no pudo ser eliminado: ");
        } else {
            window.alert("El folder ha sido eliminado exitosamente");
        }
        divAsideCarpetas.closest(".aside__carpetas").innerHTML = "";
        cargarFolders();
    })
}

function actualizarNombreCarpeta(folderId, nuevoNombre) {
    fetch(`/folders/${folderId}/actualizar`,{
        method: "PUT",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        },
        body: JSON.stringify({
            nombre: nuevoNombre
        })
    }).then(response => {
        if(response.ok) {
            window.alert("Se ha cambiado el nombre de la carpeta con éxito");
        } else if (response.status === 409) {
            window.alert("Ya existe una carpeta con dicho nombre");
        } else {
            response.json().then(data => {
                window.alert("No se ha podido cambiar el nombre de la carpeta debido a un problema: " + (data.hasOwnProperty("nombre"))?data.nombre:"Problema interno");
            })
        }
        divAsideCarpetas.closest(".aside__carpetas").innerHTML = "";
        cargarFolders();
    })
}

function eventosSelectorCarpetas(evento) {
    const asideBoton = evento.target.closest(".aside__boton");
    const valor = evento.target.value;
    if(valor === "1") {
        const nuevoNombre = window.prompt("Escribe el nuevo nombre que tendrá la carpeta");
        actualizarNombreCarpeta(asideBoton.dataset.folderId, nuevoNombre);
    } else if(valor === "2") {
        const deseaCambiar = window.confirm(`¿Estás seguro de que quieres eliminar la carpeta? \nRecuerda que los mensajes dentro no se eliminaran sino que volveran a su carpeta de origen`);
        if (deseaCambiar) {
            eliminarCarpeta(asideBoton.dataset.folderId);
        }
    }
}

function crearBotonFolder(folder) {
    const nuevoCarpeta = divAsideCarpetas.appendChild(document.createElement('div'));
    nuevoCarpeta.dataset.folderId = folder.id;
    //nuevoBoton.textContent = folder.nombre;
    nuevoCarpeta.classList.add('aside__boton');
    const botonTexto = nuevoCarpeta.appendChild(document.createElement('p'));
    botonTexto.classList.add('aside__botonTexto');
    botonTexto.textContent = folder.nombre;
    nuevoCarpeta.addEventListener('click', (event) => {
        agregarMensajesDiv(event.currentTarget);
        const formulario = divAsideCarpetas.querySelector(".main__seleccion");
        if (formulario !== null) {
            formulario.remove();
        }
    })
    if ((folder.nombre === "Entrada" || folder.nombre === "Enviados")) {
        return;
    }
    const botonMenu = nuevoCarpeta.appendChild(document.createElement('div'));
    botonMenu.classList.add('aside__botonMenu');
    const imagenMenu = botonMenu.appendChild(document.createElement('i'));
    imagenMenu.classList.add('fa-solid','fa-ellipsis-vertical');
    botonMenu.addEventListener("click", (event) => {
        event.stopPropagation();
        let formulario2 = divAsideCarpetas.querySelector(".main__seleccion");
        let formulario = nuevoCarpeta.querySelector(".main__seleccion");
        if (formulario != null) {
            formulario.remove();
        } else {
            if (formulario2 != null) {
                formulario2.remove();
            }
            const formulario = nuevoCarpeta.appendChild(document.createElement('form'));
            formulario.classList.add('main__seleccion');
            const etiqueta = formulario.appendChild(document.createElement('label'));
            etiqueta.textContent = "¿Qué deseas hacer?";
            etiqueta.htmlFor = "opcionElegir";
            const selector = formulario.appendChild(document.createElement('select'));
            selector.classList.add('main__select');
            selector.name = "seleccion";
            selector.id = "opcionElegir";
            selector.options.add(crearOption("Selecciona una opcion", "0"));
            selector.options.add(crearOption("Cambiar nombre", "1"));
            selector.options.add(crearOption("Eliminar carpeta", "2"));
            selector.addEventListener("change", ev => eventosSelectorCarpetas(ev));
            formulario.addEventListener("click", evento1 => evento1.stopPropagation());
        }
    });

}

function crearFolder(folder) {
    return fetch(`/folders/crear`, {
        method: "POST",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        },
        body: JSON.stringify({
            nombre: folder
        })
    }).then(
        response => {
            if (response.status === 409) {
                alert("Ya existe un folder con dicho nombre");
                return false;
            } else if (!response.ok) {
                response.json().then((data) => {
                    alert(data.nombre);
                })
            } else {
                alert("Folder creado con exito");
                return true;
            }
        }
    );
}

function obtenerFolders() {
    return fetch("folders/obtener/todos", {
        method: "GET",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        }
    }).then(response => {
        if(!response.ok) {
            alert("No se han podido recuperar las carpetas");
        }
        return response.json();
    });
}

function cambiarStyleDisplay(queryElemento, styleDisplay) {
    asideCrearCarpetas.querySelector(queryElemento).style.display = styleDisplay;
}

function cargarFolders() {
    obtenerFolders()
        .then(
            data => {
                const entrada = data.find((elemento) => elemento.nombre === "Entrada");
                const enviados = data.find((elemento) => elemento.nombre === "Enviados");
                data = data.filter(folder =>
                    folder.nombre !== 'Entrada' && folder.nombre !== 'Enviados');
                data.unshift(enviados);
                data.unshift(entrada);
                divAsideCarpetas.innerHTML = "";
                data.forEach(folder => {
                    crearBotonFolder(folder)
                });
                botonAsideSeleccionado = divAsideCarpetas.querySelector(".aside__boton");
                botonAsideSeleccionado.classList.toggle("boton__aside__seleccionado");
                agregarMensajesDiv(botonAsideSeleccionado);
            }
        ).catch((error) => {
            console.log(error.message);
        }
    );
}

function buscarCarpeta(nombreCarpeta) {
    return fetch(`/folders/${nombreCarpeta}/id`, {
        method: "GET",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}`
        }
    });
}

cargarFolders();
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
                        buscarCarpeta("Enviados").then(
                            response => {
                                if (!response.ok) {
                                    alert("No existe la carpeta de enviados");
                                    return;
                                }
                                response.json().then(
                                    data => {
                                        const {id} = data;
                                        let myArray = Array.from(divAsideCarpetas.children);
                                        let myArray2 = myArray.find(hijo => hijo.dataset.folderId == id);
                                        agregarMensajesDiv(myArray2);
                                    }
                                )
                            }
                        )
                    } else if(response.status === 409) {
                        alert("No se ha podido enviar el mensaje dado que ninguno de los correos detinatarios existe");
                    }
            })
    })
})

function getInfoUsuario() {
    return fetch("/usuarios/informacion", {
        method: "GET",
        headers: {
            "Content-Type": "application/JSON",
            'Authorization': `Bearer ${localStorage.getItem("token")}` // Incluir el token en el encabezado Authorization
        }
    }).then(data => {
        if(data.ok){
            return data.json();
        } else {
            alert("No se ha podido recuperar la información del usuario");
            throw new Error();
        }
    });
}

function ocultarOpcionesNuevaCarpeta() {
    cambiarStyleDisplay(".crearCarpetas--nuevaCarpeta","block");
    cambiarStyleDisplay(".crearCarpetas--contenedor","flex");
    cambiarStyleDisplay(".crearCarpetas--input","none");
    cambiarStyleDisplay(".crearCarpetas--contenedor2","none");
    cambiarStyleDisplay(".crearCarpetas--contenedorCancelar","none");
}

asideCrearCarpetasContenedor.addEventListener("click", () => {
    cambiarStyleDisplay(".crearCarpetas--nuevaCarpeta","none");
    cambiarStyleDisplay(".crearCarpetas--contenedor","none");
    cambiarStyleDisplay(".crearCarpetas--input","block");
    cambiarStyleDisplay(".crearCarpetas--contenedor2","flex");
    cambiarStyleDisplay(".crearCarpetas--contenedorCancelar","flex");
})

document.querySelector(".asideDerecho__logout").addEventListener("click", (evento) => {
  window.localStorage.removeItem("token");
  window.location.replace("/login");
  console.log("Logout existoso");
})
const contenedorInfoUsuario = document.querySelector(".contenedor--infoUsuario");
const dialogInfoUsuario = document.querySelector(".dialog__informacionUsuario");
contenedorInfoUsuario.addEventListener("click", evento => {
    const nombre = dialogInfoUsuario.querySelector(".itemLista--nombre");
    const apellido = dialogInfoUsuario.querySelector(".itemLista--apellido");
    const correo = dialogInfoUsuario.querySelector(".itemLista--correo");
    getInfoUsuario().then(data => {
        dialogInfoUsuario.showModal();
        nombre.textContent = data.nombre;
        apellido.textContent = data.apellido;
        correo.textContent = data.email;
    });

});
document.querySelector(".boton--aceptar").addEventListener("click", evento => {
    dialogInfoUsuario.close();
});

asideCrearCarpetas.querySelector(".crearCarpetas--contenedor2").addEventListener("click", event => {
    crearFolder(asideCrearCarpetas.querySelector(".crearCarpetas--input").value).then(boleano => {
        if (boleano) {
            ocultarOpcionesNuevaCarpeta();
            divAsideCarpetas.innerHTML = "";
            cargarFolders();
        }
    });
});

asideCrearCarpetas.querySelector(".crearCarpetas--contenedorCancelar").addEventListener("click",evento => {
    ocultarOpcionesNuevaCarpeta()
});
