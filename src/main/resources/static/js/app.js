// archivo: static/js/app.js

let usuarioActualId = null;
let eventoActualId = null;
let isLoginMode = true;

// --- SISTEMA DE TOASTS (Notificaciones) ---
function showToast(mensaje, tipo = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${tipo}`;

    /*let icon = 'ℹ️';
    if (tipo === 'success') icon = '✅';
    if (tipo === 'error') icon = '❌';*/

    toast.innerHTML = `<span>${mensaje}</span>`;
    container.appendChild(toast);

    setTimeout(() => { toast.remove(); }, 3500);
}

// --- LÓGICA DEL MODAL UNIFICADO (CORRECCIÓN IMPORTANTE) ---
let funcionConfirmacionPendiente = null; // Aquí guardaremos "qué hacer" cuando digan SÍ

function mostrarModal(mensaje, accionConfirmada) {
    // 1. Ponemos el mensaje
    document.getElementById('modal-message').innerText = mensaje;

    // 2. Guardamos la función que se debe ejecutar si acepta
    funcionConfirmacionPendiente = accionConfirmada;

    // 3. Mostramos el modal
    document.getElementById('custom-modal').classList.remove('hidden');
}

function ocultarModal() {
    funcionConfirmacionPendiente = null; // Limpiamos la memoria
    document.getElementById('custom-modal').classList.add('hidden');
}

// Configuración inicial de botones (SE EJECUTA UNA SOLA VEZ AL CARGAR)
document.addEventListener('DOMContentLoaded', () => {
    // Botón Cancelar
    document.getElementById('modal-btn-cancel').addEventListener('click', ocultarModal);

    // Botón Confirmar (El mágico)
    document.getElementById('modal-btn-confirm').addEventListener('click', () => {
        if (funcionConfirmacionPendiente) {
            funcionConfirmacionPendiente(); // Ejecutamos la misión guardada
            ocultarModal();
        }
    });
});


// --- 1. LÓGICA LOGIN / REGISTER ---
function toggleLoginMode() {
    isLoginMode = !isLoginMode;

    const title = document.getElementById('loginTitle');
    const nameContainer = document.getElementById('nameContainer');
    const confirmPassContainer = document.getElementById('confirmPassContainer');
    const btn = document.getElementById('loginBtn');
    const toggle = document.getElementById('toggleText');

    document.getElementById('passwordInput').value = "";
    document.getElementById('confirmPasswordInput').value = "";

    if (isLoginMode) {
        title.innerText = "Iniciar Sesión";
        nameContainer.classList.add('hidden');
        confirmPassContainer.classList.add('hidden');
        btn.innerText = "ENTRAR";
        btn.className = "btn btn-primary";
        toggle.innerText = "¿No tienes cuenta? Regístrate aquí";
    } else {
        title.innerText = "Crear Cuenta";
        nameContainer.classList.remove('hidden');
        confirmPassContainer.classList.remove('hidden');
        btn.innerText = "REGISTRARSE";
        btn.className = "btn btn-success";
        toggle.innerText = "¿Ya tienes cuenta? Inicia Sesión";
    }
}

async function procesarAuth() {
    const email = document.getElementById('emailInput').value;
    const password = document.getElementById('passwordInput').value;

    if(!email || !password) {
        showToast("Falta email o contraseña", "error");
        return;
    }

    let nombre = "Usuario";

    if (!isLoginMode) {
        nombre = document.getElementById('usernameInput').value;
        const confirmPassword = document.getElementById('confirmPasswordInput').value;

        if(!nombre) {
            showToast("¡Para registrarte necesitas un nombre!", "error");
            return;
        }

        if(password !== confirmPassword) {
            showToast("Las contraseñas no coinciden", "error");
            return;
        }
    }

    try {
        const respuesta = await fetch('/api/usuarios', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre: nombre, email: email, password: password, fotoUrl: null })
        });

        if(respuesta.ok) {
            const usuario = await respuesta.json();
            usuarioActualId = usuario.id;
            showToast(`¡Bienvenido, ${usuario.nombre}!`, "success");
            cargarMisEventos();
        } else {
            showToast("Credenciales incorrectas", "error");
        }
    } catch (error) { console.error(error); showToast("Error de conexión", "error"); }
}

// --- 2. DASHBOARD ---
// Busca esta función en tu app.js y reemplázala por esta:

async function cargarMisEventos() {
    try {
        const resp = await fetch(`/api/usuarios/${usuarioActualId}/eventos`);
        const eventos = await resp.json();
        const contenedor = document.getElementById('lista-eventos-container');
        contenedor.innerHTML = "";

        if(eventos.length === 0) contenedor.innerHTML = "<p>No tienes eventos.</p>";

        eventos.forEach(evento => {
            const html = `
                <div class="event-item">
                    <div onclick="entrarEvento(${evento.id}, '${evento.nombre}')" style="flex:1;">
                        <div class="event-title">${evento.nombre}</div>
                        <div class="event-date">ID: ${evento.id}</div>
                    </div>

                    <button class="btn-delete" onclick="pedirBorrarEvento(${evento.id})" title="Eliminar Grupo">🗑️</button>
                </div>`;
            contenedor.innerHTML += html;
        });
        cambiarPantalla('screen-dashboard');
    } catch (e) { console.error(e); }
}

// --- 3. CREAR EVENTO ---
async function crearEvento() {
    const nombre = document.getElementById('eventNameInput').value;
    if(!nombre) {
        showToast("Ponle un nombre al evento", "error");
        return;
    }

    try {
        const resp = await fetch('/api/eventos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre: nombre, creadorId: usuarioActualId })
        });
        if(resp.ok) {
            showToast("¡Evento creado!", "success");
            document.getElementById('eventNameInput').value = "";
            cargarMisEventos();
        }
    } catch (error) { showToast("Error creando evento", "error"); }
}

// --- 4. ENTRAR A EVENTO ---
async function entrarEvento(id, nombre) {
    eventoActualId = id;
    document.getElementById('titulo-evento-detalle').innerText = nombre;

    cargarBalance(id);
    cargarSugerencias(id);
    cargarGastos(id);

    cambiarPantalla('screen-event-detail');
}

async function cargarBalance(eventoId) {
    const box = document.getElementById('balance-container');
    box.innerText = "Cargando...";
    box.className = "balance-box neutral";

    try {
        const resp = await fetch(`/api/eventos/${eventoId}/balance`);
        const balances = await resp.json();

        let miSaldo = 0;
        let encontreMiSaldo = false;
        let texto = "";

        balances.forEach(b => {
            texto += `${b.nombreParticipante}: $${b.saldo} \n`;
            if(b.nombreParticipante.toLowerCase() === document.getElementById('usernameInput').value.toLowerCase()) {
                miSaldo = b.saldo;
                encontreMiSaldo = true;
            }
        });

        box.innerText = texto || "Nadie debe nada aún.";

        if(encontreMiSaldo) {
            if(miSaldo > 0) box.className = "balance-box positive";
            else if(miSaldo < 0) box.className = "balance-box negative";
        }

    } catch(e) { console.error(e); box.innerText = "Error calculando balance"; }
}

async function cargarGastos(eventoId) {
    const cont = document.getElementById('lista-gastos-container');
    cont.innerHTML = "<p style='text-align:center'>Cargando...</p>";
    try {
        const resp = await fetch(`/api/gastos/evento/${eventoId}`);
        const gastos = await resp.json();

        cont.innerHTML = "";
        if(gastos.length === 0) cont.innerHTML = "<p>No hay gastos cargados.</p>";

        gastos.forEach(g => {
            const html = `
                <div class="gasto-item">
                    <div style="flex: 1;">
                        <div class="event-title">${g.titulo}</div>
                        <div class="event-date">Pagó: ${g.pagador.nombre}</div>
                    </div>
                    <div style="font-weight:bold; font-size: 16px;">$${g.montoTotal}</div>
                    <button class="btn-delete" onclick="pedirBorrarGasto(${g.id})" title="Eliminar">🗑️</button>
                </div>`;
            cont.innerHTML += html;
        });
    } catch(e) { console.error(e); cont.innerHTML = "Error cargando gastos"; }
}

// --- NUEVAS FUNCIONES DE BORRADO USANDO EL MODAL UNIFICADO ---

function pedirBorrarGasto(id) {
    mostrarModal(
        "¿Seguro que quieres borrar este gasto? Se recalcularán las deudas.",
        () => ejecutarBorrarGasto(id) // Esta es la misión
    );
}

async function ejecutarBorrarGasto(idGasto) {
    try {
        const resp = await fetch(`/api/gastos/${idGasto}`, { method: 'DELETE' });
        if(resp.ok) {
            showToast("Gasto eliminado", "success");
            entrarEvento(eventoActualId, document.getElementById('titulo-evento-detalle').innerText);
        } else { showToast("No se pudo borrar", "error"); }
    } catch(e) { console.error(e); showToast("Error de conexión", "error"); }
}

function pedirBorrarEvento(id) {
    mostrarModal(
        "¿Borrar este evento entero? Se perderán todos los datos.",
        () => ejecutarBorrarEvento(id) // Esta es la misión
    );
}

async function ejecutarBorrarEvento(id) {
    try {
        const resp = await fetch(`/api/eventos/${id}`, { method: 'DELETE' });
        if(resp.ok) {
            showToast("Evento eliminado", "success");
            cargarMisEventos();
        } else { showToast("Error al borrar evento", "error"); }
    } catch(e) { console.error(e); showToast("Error conexión", "error"); }
}


// --- 5. AGREGAR AMIGO / GESTIONAR ---
async function mostrarAgregarAmigo() {
    document.getElementById('friendNameInput').value = "";

    const contenedor = document.getElementById('lista-participantes-gestion');
    contenedor.innerHTML = "<p>Cargando...</p>";

    try {
        const resp = await fetch(`/api/participantes/evento/${eventoActualId}`);
        const participantes = await resp.json();

        contenedor.innerHTML = "";
        if(participantes.length === 0) contenedor.innerHTML = "<p>No hay nadie aún.</p>";

        participantes.forEach(p => {
            const div = document.createElement('div');
            div.style.display = "flex";
            div.style.justifyContent = "space-between";
            div.style.padding = "10px";
            div.style.borderBottom = "1px solid #f0f0f0";

            div.innerHTML = `
                <span>${p.nombre}</span>
                <button class="btn-delete" onclick="pedirBorrarParticipante(${p.id})" style="margin:0;">🗑️</button>
            `;
            contenedor.appendChild(div);
        });
    } catch(e) { console.error(e); }
    cambiarPantalla('screen-add-friend');
}

async function guardarAmigo() {
    const nombre = document.getElementById('friendNameInput').value;
    if(!nombre) { showToast("Pon un nombre", "error"); return; }

    try {
        const resp = await fetch('/api/participantes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre: nombre, eventoId: eventoActualId, usuarioId: null })
        });
        if(resp.ok) {
            showToast("¡Amigo agregado!", "success");
            mostrarAgregarAmigo(); // Recargamos la lista misma
        } else { showToast("Error al agregar", "error"); }
    } catch(e) { console.error(e); showToast("Error conexión", "error"); }
}

function pedirBorrarParticipante(id) {
    mostrarModal(
        "¿Sacar a esta persona? Si ya tiene gastos, NO se podrá borrar.",
        () => ejecutarBorrarParticipante(id)
    );
}

async function ejecutarBorrarParticipante(id) {
    try {
        const resp = await fetch(`/api/participantes/${id}`, { method: 'DELETE' });

        if(resp.ok) {
            showToast("Participante eliminado", "success");
            mostrarAgregarAmigo();
        } else if (resp.status === 409) {
            showToast("No se puede: Ya es parte de un gasto.", "error");
        } else { showToast("Error al borrar", "error"); }
    } catch(e) { console.error(e); showToast("Error conexión", "error"); }
}

async function cargarSugerencias(eventoId) {
    const contenedor = document.getElementById('lista-sugerencias-container');
    const botonSolucion = document.getElementById('btn-ver-solucion');
    contenedor.innerHTML = "";
    botonSolucion.classList.add('hidden');

    try {
        const resp = await fetch(`/api/eventos/${eventoId}/sugerencias`);
        const sugerencias = await resp.json();
        if (sugerencias.length === 0) return;
        botonSolucion.classList.remove('hidden');
        sugerencias.forEach(s => {
            const div = document.createElement('div');
            div.style.backgroundColor = "#fff3cd"; div.style.border = "1px solid #ffeeba"; div.style.color = "#856404";
            div.style.padding = "15px"; div.style.marginBottom = "10px"; div.style.borderRadius = "12px"; div.style.fontSize = "16px";
            div.innerHTML = `<strong>${s.deudor}</strong> debe pagarle <strong style="color:#26890C">$${s.monto}</strong> a <strong>${s.acreedor}</strong>`;
            contenedor.appendChild(div);
        });
    } catch (e) { console.error(e); }
}

function mostrarSugerencias() { cambiarPantalla('screen-sugerencias'); }
function volverDetalleEvento() { entrarEvento(eventoActualId, document.getElementById('titulo-evento-detalle').innerText); }

// --- 6. CARGAR GASTO ---
async function mostrarCargarGasto() {
    try {
        const resp = await fetch(`/api/participantes/evento/${eventoActualId}`);
        const participantes = await resp.json();
        const payerSelect = document.getElementById('payerSelect');
        payerSelect.innerHTML = "";
        const consumersContainer = document.getElementById('consumersContainer');
        consumersContainer.innerHTML = "";

        participantes.forEach(p => {
            const option = document.createElement('option');
            option.value = p.id;
            option.text = p.nombre;
            payerSelect.appendChild(option);
            const div = document.createElement('div');
            div.style.marginBottom = "8px";
            div.innerHTML = `<input type="checkbox" name="consumerCheckbox" value="${p.id}" checked style="width:auto; margin-right:10px;"><label>${p.nombre}</label>`;
            consumersContainer.appendChild(div);
        });
        cambiarPantalla('screen-add-expense');
    } catch(e) { console.error(e); showToast("Error cargando participantes", "error"); }
}

async function guardarGasto() {
    const titulo = document.getElementById('expenseTitle').value;
    const monto = document.getElementById('expenseAmount').value;
    const pagadorId = document.getElementById('payerSelect').value;
    const checkboxes = document.querySelectorAll('input[name="consumerCheckbox"]:checked');
    const consumidoresIds = Array.from(checkboxes).map(cb => parseInt(cb.value));

    if(!titulo || !monto || consumidoresIds.length === 0) { showToast("Completa todos los datos", "error"); return; }

    try {
        const resp = await fetch('/api/gastos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ titulo: titulo, monto: parseFloat(monto), pagadorId: parseInt(pagadorId), consumidoresIds: consumidoresIds, imagenUrl: null })
        });
        if(resp.ok) {
            showToast("¡Gasto guardado!", "success");
            document.getElementById('expenseTitle').value = ""; document.getElementById('expenseAmount').value = "";
            volverDetalleEvento();
        } else { showToast("Error guardando gasto", "error"); }
    } catch(e) { console.error(e); showToast("Error conexión", "error"); }
}

// --- UTILIDADES ---
function volverAtras() {
    if (!document.getElementById('screen-sugerencias').classList.contains('hidden')) { volverDetalleEvento(); }
    else if (!document.getElementById('screen-event-detail').classList.contains('hidden') || !document.getElementById('screen-add-friend').classList.contains('hidden') || !document.getElementById('screen-add-expense').classList.contains('hidden')) { volverDashboard(); }
}

function cambiarPantalla(idPantallaObjetivo) {
    const pantallas = ['screen-login', 'screen-dashboard', 'screen-create-event', 'screen-event-detail', 'screen-add-friend', 'screen-add-expense', 'screen-sugerencias'];
    pantallas.forEach(id => { const elemento = document.getElementById(id); if(elemento) elemento.classList.add('hidden'); });
    const objetivo = document.getElementById(idPantallaObjetivo);
    if(objetivo) objetivo.classList.remove('hidden');
    const btnBack = document.getElementById('btn-back');
    if(btnBack) {
        if(idPantallaObjetivo === 'screen-event-detail' || idPantallaObjetivo === 'screen-add-friend' || idPantallaObjetivo === 'screen-add-expense' || idPantallaObjetivo === 'screen-sugerencias') { btnBack.classList.remove('hidden'); } else { btnBack.classList.add('hidden'); }
    }
}

function mostrarCrearEvento() { cambiarPantalla('screen-create-event'); }
function volverDashboard() { cambiarPantalla('screen-dashboard'); }