const radios = document.querySelectorAll('input[name="options"]');
const sections = document.querySelectorAll('.section-unit');
const selectorDept = document.getElementById('selector-dept');
const selectorDocente = document.getElementById('selector-docente');
const table1Horario = document.getElementById('horario-tabla-mañana');
const table2Horario = document.getElementById('horario-tabla-tarde');
const formHorario = document.getElementById('form-edit-horario');
const selectorCiclo = document.getElementById('selector-ciclo');
const selectorAsignatura = document.getElementById('selector-asignatura');
const selectorDia = document.getElementById('selector-dia');
const selectorHora = document.getElementById('selector-hora');
const inputAula = document.getElementById('aula');

document.addEventListener('DOMContentLoaded', () => {

    const departamentos = fetch('/api/departamentos').then(res => res.json()).then(data => {
        data.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.codigo;
            option.textContent = dept.codigo;
            selectorDept.appendChild(option);
        })
    })
    let clickDept = false;
    let clickDocente = false;
    selectorDept.addEventListener('mousedown', () => {
        if (document.activeElement == selectorDept) {
            clickDept = true;
        }
    })
    selectorDocente.addEventListener('mousedown', () => {
        if (document.activeElement == selectorDocente) {
            clickDocente = true;
        }
    })
    selectorDept.addEventListener('mouseup', () => {
        if (clickDept) {
            selectorDept.blur();
        }
        clickDept = false;
    })
    selectorDocente.addEventListener('mouseup', () => {
        if (clickDocente) {
            selectorDocente.blur();
        }
        clickDocente = false;
    })
    selectorDept.addEventListener('change', () => {
        selectorDept.blur();
        const dept = selectorDept.value;
        limpiarTablasHorario();
        const docentes = fetch(`/api/docentes/dept/${dept}`).then(res => res.json()).then(data => {
            selectorDocente.innerHTML = '<option value="docente">-Docente-</option>';
            data.forEach(docente => {
                const option = document.createElement('option');
                option.value = docente.id;
                option.textContent = docente.nombre + ' ' + docente.apellidos + ' - ' + docente.siglas;
                selectorDocente.appendChild(option);
            })
        })
    })

    selectorDocente.addEventListener('change', () => {
        selectorDocente.blur();
        limpiarTablasHorario();
        const docenteId = selectorDocente.value;
        cargarHorarioDocente(docenteId);
    })

    const formAusencia = document.getElementById('form-ausencia');
    if (formAusencia) {
        formAusencia.addEventListener('submit', procesarGuardarAusencia);
    }
    cargarAusencias();
    setupGuardias();
    cargarAsuntos();
})

radios.forEach(radio => {
    radio.addEventListener('change', (e) => {
        sections.forEach(section => section.classList.add('hidden'));
        sections.forEach(section => section.classList.remove('flex'));
        document.querySelector(`#${e.target.value}`).classList.remove('hidden')
        document.querySelector(`#${e.target.value}`).classList.add('flex')
    });
});

const filas = Array.from(document.querySelectorAll('#horario-tabla-mañana tbody tr')).concat(Array.from(document.querySelectorAll('#horario-tabla-tarde tbody tr')));
filas.splice(10, 1)
filas.splice(3, 1)
filas.forEach((fila, hora) => {
    const celdas = Array.from(fila.children);
    celdas.splice(0, 1);

    celdas.forEach((celda, dia) => {
        celda.classList.add('relative', 'group');
        const img = document.getElementById('template-edit-icon').content.cloneNode(true).firstElementChild;
        img.classList.add(
            'absolute', 'top-0', 'left-0', 'w-full', 'h-full',
            'group-hover:block', 'cursor-pointer'
        );
        const docente = selectorDocente.value;
        if (docente != null && docente != '' && docente != 'docente') {
            celda.appendChild(img);
        }
        img.addEventListener('click', () => {
            editarHorario(docente, hora + 1, dia + 1);
        })
    });
});

function swapTables() {
    const table1Horario = document.getElementById('horario-tabla-mañana');
    const table2Horario = document.getElementById('horario-tabla-tarde');
    const buttonSwapHorario = document.getElementById('horario-swap-button').querySelector('span');
    if (table1Horario.classList.contains('hidden')) {
        table1Horario.classList.remove('hidden');
        table2Horario.classList.add('hidden');
        buttonSwapHorario.textContent = 'Turno Mañana';
    } else {
        table1Horario.classList.add('hidden');
        table2Horario.classList.remove('hidden');
        buttonSwapHorario.textContent = 'Turno Tarde';
    }
}

function limpiarTablasHorario() {
    const table1Horario = document.getElementById('horario-tabla-mañana');
    const table2Horario = document.getElementById('horario-tabla-tarde');
    const filas1 = document.querySelectorAll('#horario-tabla-mañana tbody tr');
    const filas2 = document.querySelectorAll('#horario-tabla-tarde tbody tr');
    let contador = 1;
    filas1.forEach(fila => {
        if (contador != 4) {
            const celdas = fila.querySelectorAll('td');
            for (let i = 1; i < celdas.length; i++) {
                celdas[i].innerHTML = '';
                var clone = celdas[i].cloneNode(true);
                celdas[i].parentNode.replaceChild(clone, celdas[i]);
            }
        }
        contador++;
    });
    contador = 1;
    filas2.forEach(fila => {
        if (contador != 4) {
            const celdas = fila.querySelectorAll('td');
            for (let i = 1; i < celdas.length; i++) {
                celdas[i].innerHTML = '';
                var clone = celdas[i].cloneNode(true);
                celdas[i].parentNode.replaceChild(clone, celdas[i]);
            }
        }
        contador++;
    });
}

function closeDialog(id) {
    document.getElementById(id).close();
}

async function editarHorario(docenteId, hora, dia) {
    const dialog = document.getElementById('dialog-edit-horario');

    // 1. Guardamos el contexto en los inputs ocultos
    document.getElementById('hidden-docente-id').value = docenteId;

    // 2. Pre-seleccionamos Día y Hora en el formulario
    selectorDia.value = dia;
    selectorHora.value = hora;
    inputAula.value = ''; // Limpiar aula por defecto

    // 3. Lógica visual (la que me pasaste)
    // Usamos 'onmousedown' en lugar de addEventListener para evitar duplicar eventos si abres el modal 20 veces
    selectorCiclo.onmousedown = () => { if (document.activeElement == selectorCiclo) clickCiclo = true; };
    selectorAsignatura.onmousedown = () => { if (document.activeElement == selectorAsignatura) clickAsignatura = true; };

    // Variables auxiliares para tu lógica de foco
    let clickCiclo = false;
    let clickAsignatura = false;

    selectorCiclo.onmouseup = () => {
        if (clickCiclo) selectorCiclo.blur();
        clickCiclo = false;
    };
    selectorAsignatura.onmouseup = () => {
        if (clickAsignatura) selectorAsignatura.blur();
        clickAsignatura = false;
    };

    // 4. Determinar si ya existe un horario (Para saber si es PUT o POST)
    // Buscamos si este profe ya tiene clase ese día a esa hora
    document.getElementById('hidden-horario-id').value = ""; // Reseteamos ID
    try {
        const res = await fetch(`/api/horarios/docente/${docenteId}`);
        if (res.ok) {
            const horarios = await res.json();
            const coincidencia = horarios.find(h => h.dia == dia && h.hora == hora);

            if (coincidencia) {
                // Si existe, guardamos su ID para hacer PUT luego
                document.getElementById('hidden-horario-id').value = coincidencia.id;
                inputAula.value = coincidencia.aula || '';
                // NOTA: Aquí deberías lógica para pre-seleccionar el Ciclo y Asignatura guardados si quieres
            }
        }
    } catch (e) { console.error("Error buscando horario existente", e); }


    // 5. Cargar Ciclos
    fetch('/api/ciclos')
        .then(res => res.json())
        .then(data => {
            selectorCiclo.innerHTML = '<option value="">-Ciclo-</option>';
            data.forEach(c => {
                const option = document.createElement('option');
                option.value = c.codigo; // O c.id, depende de tu backend
                option.textContent = c.codigo;
                selectorCiclo.appendChild(option);
            });

            dialog.showModal();
            selectorCiclo.blur();
        });

    // 6. Configurar el cambio de ciclo (Cargar asignaturas)
    selectorCiclo.onchange = () => {
        selectorCiclo.blur();
        const codigoCiclo = selectorCiclo.value;
        if (!codigoCiclo) return;

        // Ojo: Asegúrate de que esta URL es la correcta con tu @Query nuevo
        fetch(`/api/asignaturas/ciclo/${codigoCiclo}`)
            .then(res => res.json())
            .then(data => {
                selectorAsignatura.innerHTML = '<option value="">-Asignatura-</option>';
                data.forEach(asignatura => {
                    const option = document.createElement('option');
                    option.value = asignatura.id;
                    option.textContent = asignatura.nombre;
                    selectorAsignatura.appendChild(option);
                });
            });
    };
}

async function cargarHorarioDocente(docenteId) {
    if (!docenteId || docenteId === 'docente') return;

    // 1. Limpiamos la tabla visualmente antes de cargar nada
    limpiarTablasHorario();

    try {
        const response = await fetch(`/api/horarios/docente/${docenteId}`);
        if (!response.ok) throw new Error('Error al cargar horarios');

        const data = await response.json();

        // Ordenamos los datos
        const datosOrdenados = Array.from(data).sort((a, b) => a.dia - b.dia || a.hora - b.hora);

        // 2. Preparamos las filas (igual que en tu código original)
        const filas = Array.from(document.querySelectorAll('#horario-tabla-mañana tbody tr'))
            .concat(Array.from(document.querySelectorAll('#horario-tabla-tarde tbody tr')));

        filas.splice(10, 1);
        filas.splice(3, 1);


        filas.forEach((fila, indexHora) => {
            const horaReal = indexHora + 1;
            const celdas = fila.querySelectorAll('td');

            for (let i = 1; i < celdas.length; i++) {
                const diaReal = i;
                const celda = celdas[i];

                const horario = datosOrdenados.find(h => h.dia == diaReal && h.hora == horaReal);

                celda.classList.add('relative', 'group');

                if (horario) {
                    const nombreAsig = horario.asignatura?.nombre || '---';
                    const aula = horario.aula || '';
                    const cursoCiclo = horario.asignatura?.ciclo?.codigo
                        ? (horario.asignatura.curso + horario.asignatura.ciclo.codigo.toUpperCase())
                        : '';

                    celda.innerHTML = `
                        <div class="border-0! **:border-0 flex flex-col justify-between p-0! *:p-0! gap-1">
                            <span class="text-sm font-bold text-indigo-600 dark:text-indigo-400">
                                ${nombreAsig}
                            </span>
                            <span class="text-xs font-medium text-gray-500 dark:text-gray-400">
                                Aula: ${aula}
                            </span>
                            <span class="text-xs font-medium text-gray-500 dark:text-gray-400">
                                Curso: ${cursoCiclo}
                            </span>
                        </div>
                    `;
                } else { celda.innerHTML = ''; }

                const img = document.getElementById('template-edit-icon').content.cloneNode(true).firstElementChild;
                img.classList.add(
                    'absolute', 'top-0', 'left-0', 'w-full', 'h-full',
                    'group-hover:block', 'cursor-pointer', 'backdrop-blur-[2px]'
                );

                img.addEventListener('click', () => {
                    editarHorario(docenteId, horaReal, diaReal);
                });
                celda.addEventListener('touchstart', () => {
                    editarHorario(docenteId, horaReal, diaReal);
                });
                if (docenteId != null && docenteId != '') {
                    celda.appendChild(img);
                } else {
                    limpiarTablasHorario();
                }
            }
        });

    } catch (error) {
        console.error("Error pintando tabla:", error);
    }
}

async function guardarHorario() {
    const form = document.getElementById('form-edit-horario');
    const dialog = document.getElementById('dialog-edit-horario');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const docenteId = document.getElementById('hidden-docente-id').value;
    const horarioId = document.getElementById('hidden-horario-id').value;

    const datosHorario = {
        cicloId: selectorCiclo.value,
        asignatura: { id: selectorAsignatura.value },
        docente: { id: docenteId },
        dia: parseInt(selectorDia.value),
        hora: parseInt(selectorHora.value),
        aula: inputAula.value
    };

    console.log("Enviando:", datosHorario);

    try {
        let response;
        if (horarioId) {
            response = await fetch(`/api/horarios/${horarioId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosHorario)
            });
        } else {
            response = await fetch('/api/horarios', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosHorario)
            });
        }

        if (response.ok) {
            mostrarAlerta('Horario guardado correctamente');
            form.reset();
            closeDialog('dialog-edit-horario');
            const docenteId = document.getElementById('hidden-docente-id').value;
            await cargarHorarioDocente(docenteId);
        } else {
            const errorText = await response.text();
            mostrarAlerta('Error al guardar: ' + errorText);
        }
    } catch (error) {
        console.error(error);
        mostrarAlerta('Error de conexión');
    }
}

async function borrarHorario() {
    const horarioId = document.getElementById('hidden-horario-id').value;
    const docenteId = document.getElementById('hidden-docente-id').value;

    if (!horarioId) {
        mostrarAlerta("No hay horario seleccionado para borrar.");
        return;
    }

    try {
        const response = await fetch(`/api/horarios/${horarioId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            mostrarAlerta("Horario eliminado correctamente.");
            closeDialog('dialog-edit-horario');
            await cargarHorarioDocente(docenteId);
        } else {
            mostrarAlerta("Error al eliminar el horario.");
        }
    } catch (error) {
        console.error("Error de red:", error);
        mostrarAlerta("Error de conexión al intentar borrar.");
    }
}

function mostrarAlerta(mensaje) {
    const container = document.getElementById('alerta-cuerpo');
    const alerta = document.getElementById('template-alerta').content.cloneNode(true).firstElementChild;
    const textoElement = alerta.querySelector('#alerta-texto');
    textoElement.textContent = mensaje;
    container.appendChild(alerta);

    setTimeout(() => {
        alerta.classList.remove('animate-fade-in-left');
        alerta.classList.add('animate-fade-out-right');
        setTimeout(() => {
            alerta.classList.remove('flex');
            alerta.classList.add('hidden');
            alerta.remove();
        }, 300);
    }, 4000);
}

function printHorarios() {
    const printContainer = document.createElement('div');
    printContainer.id = 'print-container';
    printContainer.style.display = 'none';

    const original1 = document.getElementById('horario-tabla-mañana');
    const original2 = document.getElementById('horario-tabla-tarde');

    if (!original1 || !original2) return;

    const table1 = original1.cloneNode(true);
    const table2 = original2.cloneNode(true);

    table1.classList.remove('hidden');
    table2.classList.remove('hidden');

    function createPageWrapper(table, isFirst) {
        const wrapper = document.createElement('div');
        wrapper.className = 'print-page-wrapper';
        wrapper.appendChild(table);
        if (isFirst) {
            wrapper.style.pageBreakAfter = 'always';
        }
        return wrapper;
    }

    printContainer.appendChild(createPageWrapper(table1, true));
    printContainer.appendChild(createPageWrapper(table2, false));

    document.body.appendChild(printContainer);

    const style = document.createElement('style');
    style.textContent = `
        @media print {
            body > *:not(#print-container) {
                display: none !important;
            }
            
            #print-container {
                display: block !important;
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
            }

            /* Wrapper para simular los márgenes de página */
            .print-page-wrapper {
                width: 100%;
                padding: 10mm; /* Margen real para el contenido */
                box-sizing: border-box;
            }

            #print-container table {
                width: 100% !important;
                border-collapse: collapse;
                /* Sin márgenes en la tabla, los pone el wrapper */
            }
            
            .group-hover\\:block, .cursor-pointer {
                display: none !important;
            }
        }

        @page { 
            size: landscape; 
            margin: 0; 
        }
    `;
    document.head.appendChild(style);

    window.print();

    setTimeout(() => {
        if (document.body.contains(printContainer)) {
            document.body.removeChild(printContainer);
        }
        if (document.head.contains(style)) {
            document.head.removeChild(style);
        }
    }, 100);
}

async function cargarAusencias() {
    const ausencias = document.getElementById('ausencias-body');
    const selectorDocente = document.getElementById('form-ausencia').querySelector('#selectorDocente');
    var horas = Array.from(document.querySelectorAll('#horario-tabla-mañana tbody tr')).concat(Array.from(document.querySelectorAll('#horario-tabla-tarde tbody tr')));
    horas.splice(10, 1)
    horas.splice(3, 1)
    horas = horas.map(hora => hora.children[0].textContent)
    ausencias.innerHTML = '';
    fetch(`/api/guardias/hoy-adelante`)
        .then(response => response.json())
        .then(data => {
            data.forEach(ausencia => {
                const ausenciaDiv = document.getElementById('template-ausencia').content.cloneNode(true).firstElementChild;
                const ausenciaFecha = ausenciaDiv.querySelector('.fecha-ausencia');
                const ausenciaHora = ausenciaDiv.querySelector('.hora-ausencia');
                const ausenciaPersona = ausenciaDiv.querySelector('.persona-ausencia');
                const obsAusencia = ausenciaDiv.querySelector('.observacion-ausencia');
                ausenciaFecha.textContent = parseFecha(ausencia.fecha);
                ausenciaFecha.dataset.value = ausencia.fecha;
                ausenciaHora.textContent = horas[parseInt(ausencia.horario.hora) - 1];
                ausenciaHora.dataset.value = ausencia.horario.hora;
                ausenciaPersona.textContent = ausencia.docenteAusente.nombre + ' ' + ausencia.docenteAusente.apellidos;
                ausenciaPersona.dataset.value = ausencia.docenteAusente.id;
                obsAusencia.textContent = ausencia.anotacion;
                ausenciaDiv.id = '';
                ausencias.appendChild(ausenciaDiv);
                ausenciaDiv.querySelector('.borrar-ausencia').addEventListener('click', () => {
                    borrarAusencia(ausenciaDiv);
                });
            });
            if (data.length == 0) {
                document.getElementById('sin-ausencias').classList.remove('hidden');
                document.getElementById('sin-ausencias').classList.add('flex');
            } else {
                document.getElementById('sin-ausencias').classList.remove('flex');
                document.getElementById('sin-ausencias').classList.add('hidden');
            }
        })
        .catch(error => console.error(error));

    const docentes = fetch(`/api/docentes`).then(res => res.json()).then(data => {
        selectorDocente.innerHTML = '<option value="">-Docente-</option>';
        data.sort((a, b) => a.nombre.localeCompare(b.nombre) || a.apellidos.localeCompare(b.apellidos));
        data.forEach(docente => {
            const option = document.createElement('option');
            option.value = docente.id;
            option.textContent = docente.nombre + ' ' + docente.apellidos + ' - ' + docente.siglas;
            selectorDocente.appendChild(option);
        })
    })
    const input = document.getElementById('fecha-input-ausencia');
    const hoy = new Date();
    const dia = String(hoy.getDate()).padStart(2, '0');
    const mes = String(hoy.getMonth() + 1).padStart(2, '0');
    const anio = hoy.getFullYear();
    const fechaMinima = `${anio}-${mes}-${dia}`;
    input.min = fechaMinima;

    const selectorHora = document.getElementById('selectorHora');
    selectorHora.innerHTML = '<option value="">-Hora-</option>';
    horas.forEach((hora, index) => {
        const option = document.createElement('option');
        option.value = index + 1;
        option.textContent = hora;
        selectorHora.appendChild(option);
    })
}

function cancelarAusencia() {
    const formAusencia = document.getElementById('form-ausencia');
    formAusencia.classList.toggle('hidden');
    formAusencia.classList.toggle('flex');
    formAusencia.reset();
}

function parseFecha(fechaInput) {
    const [anio, mes, dia] = fechaInput.split('-');
    const fecha = new Date(anio, mes - 1, dia);

    const opciones = {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    };

    const resultado = fecha.toLocaleDateString('es-ES', opciones);
    return resultado;
}

function nuevaAusencia() {
    const formAusencia = document.getElementById('form-ausencia');
    formAusencia.reset();
    formAusencia.classList.remove('hidden');
    formAusencia.classList.add('flex');
}

async function procesarGuardarAusencia(e) {
    e.preventDefault();
    const formAusencia = e.target;
    try {
        const fecha = document.getElementById('fecha-input-ausencia').value;
        const docenteId = document.getElementById('selectorDocente').value;
        const hora = document.getElementById('selectorHora').value;
        const diaSemana = (new Date(fecha).getDay() || 7);
        const horarioId = await fetch(`/api/horarios/docente/${docenteId}`).then(res => res.json()).then(data => {
            const horario = Array.from(data).find(h => h.hora == hora && h.dia == diaSemana);
            if (!horario) {
                throw new Error('El docente no tiene clase en esa hora')
            }
            return horario.id;
        });

        const formData = new FormData();
        formData.append('fecha', fecha);
        formData.append('horarioId', horarioId);
        formData.append('anotacion', document.getElementById('anotacion-text-ausencia').value);

        const fileInput = document.getElementById('material-file-ausencia');
        if (fileInput && fileInput.files[0]) {
            formData.append('archivo', fileInput.files[0]);
        }

        const response = await fetch(`/api/guardias/generar`, {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            mostrarAlerta('Ausencia guardada correctamente');
            formAusencia.classList.toggle('hidden');
            formAusencia.classList.toggle('flex');
            if (typeof cargarGuardias === 'function') cargarGuardias();
        } else {
            const textoError = await response.text();
            mostrarAlerta('Error al crear: ' + textoError);
        }
    } catch (error) {
        mostrarAlerta(error.message);
    }
    cargarAusencias();
}

async function borrarAusencia(el) {
    await fetch(`/api/guardias/docente-ausente/${el.querySelector('.persona-ausencia').dataset.value}`).then(res => res.json()).then(data => {
        const guardiaId = Array.from(data).find(guardia => guardia.fecha == el.querySelector('.fecha-ausencia').dataset.value && guardia.horario.hora == el.querySelector('.hora-ausencia').dataset.value).id
        fetch(`/api/guardias/${guardiaId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    mostrarAlerta('Ausencia eliminada correctamente');
                    el.remove();
                } else {
                    mostrarAlerta('Error al eliminar la ausencia');
                }
            })
            .catch(error => mostrarAlerta(error.message));
    })
        .catch(error => mostrarAlerta(error.message));
    cargarGuardias();
}

function setupGuardias() {
    const selectorFecha = document.getElementById('selector-fecha-guardia');
    selectorFecha.addEventListener('change', cargarGuardias);
    cargarGuardias();
}

async function createGuardia(guardia) {
    try {
        const guardias = document.getElementById('guardias-body');
        const guardiaDiv = document.getElementById('template-guardia').content.cloneNode(true).firstElementChild;
        const material = guardiaDiv.querySelector('.material-guardia');
        var horas = Array.from(document.querySelectorAll('#horario-tabla-mañana tbody tr')).concat(Array.from(document.querySelectorAll('#horario-tabla-tarde tbody tr')));
        horas.splice(10, 1)
        horas.splice(3, 1)
        horas = horas.map(hora => hora.children[0].textContent)
        guardiaDiv.dataset.value = guardia.id;
        guardiaDiv.querySelector('.hora-guardia').textContent = horas[guardia.horario.hora - 1];
        guardiaDiv.querySelector('.hora-guardia').dataset.value = guardia.horario.hora;
        guardiaDiv.querySelector('.fecha-guardia').textContent = parseFecha(guardia.fecha);
        guardiaDiv.querySelector('.fecha-guardia').dataset.value = guardia.fecha;
        guardiaDiv.querySelector('.docente-ausente').textContent = guardia.docenteAusente.nombre + ' ' + guardia.docenteAusente.apellidos;
        guardiaDiv.querySelector('.docente-ausente').dataset.value = guardia.docenteAusente.id;
        guardiaDiv.querySelector('.docente-cubriendo').textContent = guardia.docenteCubriendo.nombre + ' ' + guardia.docenteCubriendo.apellidos;
        guardiaDiv.querySelector('.docente-cubriendo').dataset.value = guardia.docenteCubriendo.id;
        if (guardia.material != null && guardia.material != '') {
            const link = document.createElement('a');
            link.textContent = guardia.material.split('/')[guardia.material.split('/').length - 1];
            link.href = `/api/guardias/${guardia.id}/material`;
            link.target = '_blank';
            link.className = 'underline hover:text-indigo-600';
            material.innerHTML = '';
            material.appendChild(link);
        }
        pintarGuardia(guardiaDiv, guardia);

        guardiaDiv.addEventListener('click', (e) => {
            if (e.target.classList.contains('guardia-boton-realizada') || e.target.classList.contains('guardia-boton-pendiente')) {
                alternarGuardia(guardiaDiv)
            }
        })

        return guardiaDiv;
    } catch (error) {
        mostrarAlerta(`Error al cargar la guardia id + ${guardia.id}\n${error.message}`);
    }
}

async function cargarGuardias() {
    const fecha = document.getElementById('selector-fecha-guardia').value;
    const guardiasBody = document.getElementById('guardias-body');
    guardiasBody.innerHTML = ''
    if (fecha == '') {
        fetch(`/api/guardias/hoy-adelante`).then(res => res.json()).then(data => {
            const guardias = Array.from(data).sort((a, b) => a.fecha.localeCompare(b.fecha) || a.horario.hora - b.horario.hora)
            Promise.all(guardias.map(guardia => createGuardia(guardia))).then(guardias => {
                guardias.forEach(g => guardiasBody.appendChild(g))
            })
            document.getElementById('sin-guardias').classList.add('hidden')
            document.getElementById('sin-guardias').classList.remove('flex')
            if (data.length == 0) {
                document.getElementById('sin-guardias').classList.remove('hidden')
                document.getElementById('sin-guardias').classList.add('flex')
            }
        })
    } else {
        fetch(`/api/guardias/fecha/${fecha}`).then(res => res.json()).then(data => {
            Array.from(data).sort((a, b) => a.fecha.localeCompare(b.fecha) || a.horario.hora - b.horario.hora).forEach(guardia => {
                createGuardia(guardia);
            })
            document.getElementById('sin-guardias').classList.add('hidden')
            document.getElementById('sin-guardias').classList.remove('flex')
            if (data.length == 0) {
                document.getElementById('sin-guardias').classList.remove('hidden')
                document.getElementById('sin-guardias').classList.add('flex')
            }
        })
    }
}

async function alternarGuardia(guardia) {
    const guardiaId = guardia.dataset.value;
    const guardiaData = await fetch(`/api/guardias/id/${guardiaId}`).then(res => res.json());
    guardiaData.realizada = !guardiaData.realizada;
    fetch(`/api/guardias/${guardiaId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(guardiaData)
    })
        .then(response => {
            if (response.ok) {
                mostrarAlerta('Guardia marcada como ' + (guardiaData.realizada ? 'realizada' : 'pendiente'));
                pintarGuardia(guardia, guardiaData);
            } else {
                mostrarAlerta('Error al alternar la guardia');
            }
        })
        .catch(error => mostrarAlerta(error.message));
}

function pintarGuardia(guardiaDiv, guardia) {
    if (guardia.realizada) {
        guardiaDiv.querySelector('.guardia-boton-realizada').classList.add('hidden')
        guardiaDiv.querySelector('.guardia-boton-realizada').classList.remove('flex')
        guardiaDiv.querySelector('.span-realizada').classList.remove('hidden')
        guardiaDiv.querySelector('.span-realizada').classList.add('flex')
        guardiaDiv.querySelector('.span-pendiente').classList.add('hidden')
        guardiaDiv.querySelector('.span-pendiente').classList.remove('flex')
        guardiaDiv.querySelector('.guardia-boton-pendiente').classList.add('flex')
        guardiaDiv.querySelector('.guardia-boton-pendiente').classList.remove('hidden')
    } else {
        guardiaDiv.querySelector('.guardia-boton-realizada').classList.remove('hidden')
        guardiaDiv.querySelector('.guardia-boton-realizada').classList.add('flex')
        guardiaDiv.querySelector('.span-realizada').classList.add('hidden')
        guardiaDiv.querySelector('.span-realizada').classList.remove('flex')
        guardiaDiv.querySelector('.span-pendiente').classList.remove('hidden')
        guardiaDiv.querySelector('.span-pendiente').classList.add('flex')
        guardiaDiv.querySelector('.guardia-boton-pendiente').classList.remove('flex')
        guardiaDiv.querySelector('.guardia-boton-pendiente').classList.add('hidden')
    }
}

async function pintarAsunto(asuntoDiv, asunto) {
    const spanPendiente = asuntoDiv.querySelector('.span-pendiente');
    const spanAprobado = asuntoDiv.querySelector('.span-aprobado');
    const spanRechazado = asuntoDiv.querySelector('.span-rechazado');
    spanPendiente.classList.add('hidden');
    spanPendiente.classList.remove('flex');
    spanAprobado.classList.add('hidden');
    spanAprobado.classList.remove('flex');
    spanRechazado.classList.add('hidden');
    spanRechazado.classList.remove('flex');
    asuntoDiv.querySelector('.asunto-btns').classList.add('hidden');
    asuntoDiv.querySelector('.asunto-btns').classList.remove('flex');
    asuntoDiv.querySelector('.mail-aprobado').classList.add('hidden');
    asuntoDiv.querySelector('.mail-aprobado').classList.remove('flex');
    asuntoDiv.querySelector('.mail-denegado').classList.add('hidden');
    asuntoDiv.querySelector('.mail-denegado').classList.remove('flex');
    asuntoDiv.querySelector('.anotacion-asunto').classList.add('hidden');
    asuntoDiv.querySelector('.anotacion-asunto').classList.remove('flex');
    switch (asunto.estado.toLowerCase()) {
        case 'pendiente':
            spanPendiente.classList.remove('hidden');
            spanPendiente.classList.add('flex');
            asuntoDiv.querySelector('.asunto-btns').classList.remove('hidden');
            asuntoDiv.querySelector('.asunto-btns').classList.add('flex');
            if (asuntoDiv.querySelector('.anotacion-asunto').value != null && asuntoDiv.querySelector('.anotacion-asunto').value != '') {
                asuntoDiv.querySelector('.anotacion-asunto').classList.remove('hidden');
                asuntoDiv.querySelector('.anotacion-asunto').classList.add('flex');
            }
            break;
        case 'aceptado':
            spanAprobado.classList.remove('hidden');
            spanAprobado.classList.add('flex');
            asuntoDiv.querySelector('.mail-aprobado').classList.remove('hidden');
            asuntoDiv.querySelector('.mail-aprobado').classList.add('flex');
            await fetch(`/api/guardias/docente-ausente/${asunto.docente.id}`).then(res => res.json()).then(data => {
                if (Array.from(data).filter(g => g.fecha == asunto.diaSolicitado && g.material != null && g.material != '').length != 0) {
                    asuntoDiv.querySelector('.asunto-materiales').classList.remove('hidden');
                    asuntoDiv.querySelector('.asunto-materiales').classList.add('flex');
                }
            })
            asuntoDiv.querySelector('.text-prioridad').firstChild.nodeValue = 'Sin Prioridad';
            asuntoDiv.querySelector('.num-prioridad').textContent = '';
            break;
        case 'denegado':
            spanRechazado.classList.remove('hidden');
            spanRechazado.classList.add('flex');
            asuntoDiv.querySelector('.mail-denegado').classList.remove('hidden');
            asuntoDiv.querySelector('.mail-denegado').classList.add('flex');
            asuntoDiv.querySelector('.text-prioridad').firstChild.nodeValue = 'Sin Prioridad';
            asuntoDiv.querySelector('.num-prioridad').textContent = '';
            break;
    }

    const rolMap = {
        'interino': 'Interino',
        'practicas': 'Funcionario en Prácticas',
        'carrera': 'Funcionario de Carrera'
    };
    asuntoDiv.querySelector('.rol-docente').textContent = rolMap[asunto.docente.rol.nombre.toLowerCase()] || asunto.docente.rol.nombre;
    asuntoDiv.querySelector('.antiguedad-docente').textContent = new Date(Date.now() - new Date(asunto.docente.fechaAntiguedad).getTime()).getFullYear() - 1970 + ' Años';
    asuntoDiv.querySelector('.posicion-docente').textContent = asunto.docente.posicion;
    asuntoDiv.querySelector('.anotacion-asunto-text').value = await fetch(`/api/dias/id/${asunto.id}`).then(res => res.json()).then(data => data.observaciones);

    if (asunto.estado.toLowerCase() != 'pendiente') {
        asuntoDiv.querySelector('.asunto-btns').classList.add('hidden');
        asuntoDiv.querySelector('.asunto-btns').classList.remove('flex');
    }

    if (asuntoDiv.querySelector('.anotacion-asunto-text').value.length != 0) {
        asuntoDiv.querySelector('.anotacion-asunto').classList.remove('hidden');
        asuntoDiv.querySelector('.anotacion-asunto').classList.add('flex');
    }
    asuntoDiv.querySelector('.anotacion-asunto-text').disabled = true;
}

async function cargarAsuntos() {
    const asuntosBody = document.getElementById('asuntos-body');
    const sinAsuntos = document.getElementById('sin-asuntos');
    asuntosBody.innerHTML = '';
    sinAsuntos.classList.add('hidden');
    sinAsuntos.classList.remove('flex');
    const response = await fetch(`/api/dias`);
    const data = await response.json();
    if (!data || data.length === 0) {
        sinAsuntos.classList.remove('hidden');
        sinAsuntos.classList.add('flex');
        return;
    }

    const asuntosSorted = data.sort((a, b) =>
        (
            a.estado.toLowerCase() == 'pendiente' ? -1 : 1 -
                b.estado.toLowerCase() == 'pendiente' ? -1 : 1
        ) ||
        (a.docente.rol.prioridad - b.docente.rol.prioridad) ||
        (a.docente.fechaAntiguedad.localeCompare(b.docente.fechaAntiguedad)) ||
        (a.docente.posicion - b.docente.posicion)
    );

    const divsListos = await Promise.all(asuntosSorted.map((asunto, index) => createAsunto(asunto, index)));
    divsListos.forEach(div => {
        asuntosBody.appendChild(div);
    })
}

async function createAsunto(asunto, p) {
    const asuntosBody = document.getElementById('asuntos-body');
    const asuntoDiv = document.getElementById('template-asunto').content.cloneNode(true).firstElementChild;
    asuntoDiv.dataset.value = asunto.id;
    const fecha = asuntoDiv.querySelector('.fecha-asunto');
    asuntoDiv.querySelector('.trimestre-asunto').textContent = (await fetch(`/api/config/trimestre-index/${asunto.diaSolicitado}`).then(res => res.json()) + 1) + 'º Trimestre';
    asuntoDiv.querySelector('.num-prioridad').textContent = p + 1;
    asuntoDiv.querySelector('.nombre-docente').textContent = asunto.docente.nombre + ' ' + asunto.docente.apellidos;
    asuntoDiv.querySelector('.nombre-docente').dataset.value = asunto.docente.id;
    fecha.textContent = asunto.diaSolicitado;
    fecha.dataset.value = asunto.diaSolicitado;

    asuntoDiv.querySelector('.guardias-realizadas-asunto').textContent = await fetch(`/api/guardias/realizadas/${asunto.docente.id}`).then(res => res.json()).then(data => data.length);

    asuntoDiv.querySelector('.asunto-btns').addEventListener('click', (e) => {
        if (e.target.closest('.btn-aceptar')) {
            aprobarAsunto(asuntoDiv);
            return;
        }
        if (e.target.closest('.btn-rechazar')) {
            denegarAsunto(asuntoDiv);
            return;
        }
        if (e.target.closest('.btn-notas')) {
            anotacionAsunto(asuntoDiv);
            return;
        }
    })

    pintarAsunto(asuntoDiv, asunto);

    return asuntoDiv;
}

async function aprobarAsunto(asuntoDiv) {
    const asuntoId = asuntoDiv.dataset.value;
    const fecha = asuntoDiv.querySelector('.fecha-asunto').dataset.value;
    const docenteId = asuntoDiv.querySelector('.nombre-docente').dataset.value;
    try {
        const response = await fetch(`/api/guardias/generar-para-asunto/${asuntoId}`, {
            method: 'POST',
        })

        if (!response.ok) {
            const data = await response.json();
            mostrarAlerta('Error al aprobar el asunto\n' + data.error);
            return;
        }

        const response2 = await fetch(`/api/dias/validar/${fecha}/${docenteId}/aceptado`, {
            method: 'PUT',
        })

        if (!response2.ok) {
            const data = await response2.json();
            mostrarAlerta('Error al aprobar el asunto\n' + data.error);
            return;
        }

        mostrarAlerta('Asunto aprobado');
        const asunto = await fetch(`/api/dias/id/${asuntoId}`).then(res => res.json());
        await pintarAsunto(asuntoDiv, asunto);
    } catch (error) {
        console.error(error);
        mostrarAlerta('Error inesperado\nConsulte la consola');
    }
}

async function denegarAsunto(asuntoDiv) {
    const asuntoId = asuntoDiv.dataset.value;
    const fecha = asuntoDiv.querySelector('.fecha-asunto').dataset.value;
    const docenteId = asuntoDiv.querySelector('.nombre-docente').dataset.value;
    const response = await fetch(`/api/dias/validar/${fecha}/${docenteId}/denegado`, {
        method: 'PUT',
    }).catch((error) => {
        mostrarAlerta(error.message);
        return;
    })

    if (!response.ok) {
        const data = await response.json();
        mostrarAlerta('Error al denegar el asunto\n' + data.error);
        return;
    }

    const asunto = await fetch(`/api/dias/id/${asuntoId}`).then(res => res.json());
    await pintarAsunto(asuntoDiv, asunto);
}

function anotacionAsunto(asuntoDiv) {
    asuntoDiv.querySelector('.anotacion-asunto').classList.remove('hidden');
    asuntoDiv.querySelector('.anotacion-asunto').classList.add('flex');
    asuntoDiv.querySelector('.btn-notas').querySelector('span').textContent = 'Guardar Anotación';
    asuntoDiv.querySelector('.anotacion-asunto-text').disabled = false;

    const funcEditar = async (e) => {
        e.stopPropagation();
        const nota = asuntoDiv.querySelector('.anotacion-asunto-text').value;
        const asunto = await fetch(`/api/dias/id/${asuntoDiv.dataset.value}`).then(res => res.json());
        asunto.observaciones = nota;
        const response = await fetch(`/api/dias/${asunto.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(asunto),
        }).catch((error) => {
            mostrarAlerta(error.message);
            return;
        })

        if (!response.ok) {
            const data = response.json();
            mostrarAlerta('Error al anotar el asunto\n' + data.error);
            return;
        }

        mostrarAlerta('Anotación guardada');
        if (nota.length == 0) {
            asuntoDiv.querySelector('.anotacion-asunto').classList.add('hidden');
            asuntoDiv.querySelector('.anotacion-asunto').classList.remove('flex');
        }
        asuntoDiv.querySelector('.anotacion-asunto-text').disabled = true;
        asuntoDiv.querySelector('.btn-notas').querySelector('span').textContent = 'Añadir anotación';
    }

    asuntoDiv.querySelector('.btn-notas').addEventListener('click', funcEditar, { once: true });
}