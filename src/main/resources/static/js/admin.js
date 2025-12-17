document.addEventListener('DOMContentLoaded', () => {
    // 1. Seleccionamos todos los radios y todas las secciones
    const radios = document.querySelectorAll('input[name="options"]');
    const sections = document.querySelectorAll('.section-unit');
    const selectorDept = document.getElementById('selector-dept');
    const selectorDocente = document.getElementById('selector-docente');
    const table1Horario = document.getElementById('horario-tabla-mañana');
    const table2Horario = document.getElementById('horario-tabla-tarde');

    const departamentos = fetch('/api/departamentos').then(res => res.json()).then(data => {
        data.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.codigo;
            option.textContent = dept.codigo;
            selectorDept.appendChild(option);
        })
    })

    selectorDept.addEventListener('change', () => {
        const dept = selectorDept.value;
        limpiarTablasHorario()
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
        const docente = selectorDocente.value;
        const horario = fetch(`/api/horarios/docente/${docente}`).then(res => res.json()).then(data => {
            limpiarTablasHorario()
            const dat = Array.from(data).sort((a, b) => a.dia - b.dia || a.hora - b.hora)
            console.log(dat)
            dat.forEach(horario => {
                const filas = Array.from(document.querySelectorAll('#horario-tabla-mañana tbody tr')).concat(Array.from(document.querySelectorAll('#horario-tabla-tarde tbody tr')));
                filas.splice(10, 1)
                filas.splice(3, 1)
                console.log(filas)
                let contador = 1;
                filas.forEach(fila => {
                    const celdas = fila.querySelectorAll('td');
                    for (let i = 1; i < celdas.length; i++) {
                        if (horario.dia == i && horario.hora == contador) {
                            celdas[i].innerHTML = `
                            <div class="border-0! **:border-0 flex flex-col justify-between p-0! *:p-0! gap-1">
                                <span class="text-sm font-bold text-indigo-600 dark:text-indigo-400">
                                    ${horario.asignatura.nombre}
                                </span>
                                <span class="text-xs font-medium text-gray-500 dark:text-gray-400">
                                    📍Aula: ${horario.aula + ' ' + horario.asignatura.curso + horario.asignatura.ciclo.codigo.toUpperCase()}
                                </span>
                            </div>
                        `;
                        }
                    }
                    contador++;
                });
            })
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
