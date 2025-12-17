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
                option.textContent = docente.siglas;
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
        const img = document.getElementById('edit-icon').cloneNode(true);
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

        // Eliminamos las filas de recreo según tu lógica (índices 10 y 3)
        // Nota: Es importante hacerlo en orden inverso o con cuidado si los índices cambian al borrar
        // Asumo que tu lógica original funcionaba para tu HTML específico.
        filas.splice(10, 1);
        filas.splice(3, 1);

        // 3. Recorremos TODAS las celdas para ponerles el listener de editar (incluso las vacías)
        filas.forEach((fila, indexHora) => {
            const horaReal = indexHora + 1;
            const celdas = fila.querySelectorAll('td');

            // Empezamos en i=1 porque la 0 es la columna de la hora
            for (let i = 1; i < celdas.length; i++) {
                const diaReal = i;
                const celda = celdas[i];

                // Buscamos si hay clase en este hueco
                const horario = datosOrdenados.find(h => h.dia == diaReal && h.hora == horaReal);

                // Configuración básica de la celda
                celda.classList.add('relative', 'group');

                // Si hay horario, pintamos el contenido
                if (horario) {
                    // Usamos ?. para evitar errores si asignatura es null
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
                                📍Aula: ${aula} ${cursoCiclo}
                            </span>
                        </div>
                    `;
                } else {celda.innerHTML = '';}

                const img = document.getElementById('edit-icon').cloneNode(true);
                img.classList.add(
                    'absolute', 'top-0', 'left-0', 'w-full', 'h-full',
                    'group-hover:block', 'cursor-pointer', 'backdrop-blur-[2px]'
                );

                img.addEventListener('click', () => {
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

// --- FUNCIÓN 2: GUARDAR LOS DATOS (Al hacer click en Guardar) ---
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
    const template = document.getElementById('alerta');
    if (!template || !container) return;
    const alerta = template.cloneNode(true);
    alerta.removeAttribute('id');
    const textoElement = alerta.querySelector('#alerta-texto');
    if (textoElement) {
        textoElement.textContent = mensaje;
    }
    container.appendChild(alerta);
    alerta.classList.add('flex');
    alerta.classList.remove('hidden');

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
    
    // Función auxiliar para envolver
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