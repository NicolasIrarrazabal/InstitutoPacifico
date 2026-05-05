CASO 10  ·  📚  Industria: Educación Técnica Profesional — Puerto Montt
InstitutoPacifico
Sistema de Microservicios — Evaluación Parcial 2 · DSY1103

📌  CONTEXTO DEL NEGOCIO
Instituto Pacífico es un centro de formación técnica acreditado con sede principal en Puerto Montt y dos sedes en Puerto Varas y Osorno. Imparte 12 carreras técnicas de nivel superior con foco en las industrias de la región: acuicultura, mecánica industrial, electricidad, logística, turismo y gastronomía. Con 2.200 estudiantes matriculados, 95 docentes y 40 administrativos, es uno de los institutos técnicos más grandes de la región.
La institución opera bajo el sistema de Financiamiento Estudiantil Gratuito (FES) del MINEDUC para una parte de sus estudiantes, más becas propias y créditos del Banco Estado. El calendario académico está dividido en dos semestres y las carreras tienen entre 4 y 6 semestres de duración. El instituto tiene convenios con más de 80 empresas de la región para prácticas profesionales de sus estudiantes.
Los desafíos actuales incluyen: el sistema de notas y asistencia está en planillas separadas por docente, la gestión de prácticas se hace por email, el cobro de aranceles tiene errores frecuentes y el proceso de titulación es completamente manual y tarda hasta 6 meses. La nueva dirección exige digitalización total para el próximo semestre.

💡  El MINEDUC auditó al instituto y encontró que los registros de asistencia de 3 secciones no cuadraban con las actas de notas. El rector exige un sistema donde la asistencia y las notas sean registradas digitalmente en el momento y no puedan ser alteradas retroactivamente sin trazabilidad de cambios.

🗃️  ENTIDADES PRINCIPALES DEL DOMINIO
El dominio incluye, como mínimo, las siguientes entidades que deben ser modeladas con JPA:

•	01. Estudiante
•	02. Docente
•	03. Carrera
•	04. Asignatura
•	05. Sección / Grupo
•	06. Matrícula
•	07. Nota / Calificación
•	08. Asistencia
•	09. Práctica profesional
•	10. Empresa (convenio)

⚙️  MICROSERVICIOS SUGERIDOS
La siguiente tabla propone los microservicios base. El equipo puede agregar más si la arquitectura lo justifica, pero no puede implementar menos de 10.

#	Microservicio	Responsabilidad	Entidades Clave / Relaciones
1	ms-estudiantes	Registro y perfil de estudiantes	Estudiante, Contacto, Estado
2	ms-docentes	Docentes, especialidades y contratos	Docente, Especialidad, Contrato
3	ms-carreras	Carreras, mallas curriculares y sedes	Carrera, Malla, Sede
4	ms-asignaturas	Asignaturas, prerrequisitos y créditos	Asignatura, Prerequisito, Creditos
5	ms-matriculas	Proceso de matrícula y secciones inscritas	Matricula, Seccion, Estudiante
6	ms-notas	Registro de evaluaciones y calificaciones	Nota, Evaluacion, Seccion
7	ms-asistencia	Control de asistencia por sesión y módulo	Asistencia, Sesion, Estudiante
8	ms-aranceles	Cobro de aranceles, becas y convenios de pago	Arancel, Beca, Cuota
9	ms-practicas	Prácticas profesionales y supervisión	Practica, Empresa, Supervisor
10	ms-empresas	Empresas con convenio y contactos de práctica	Empresa, Convenio, Contacto
11	ms-titulacion	Proceso de titulación y emisión de título	Titulacion, Estudiante, Estado

Actualmente se usa el microservicio 1,2,3 y 5  


📋  REGLAS DE NEGOCIO OBLIGATORIAS
Estas reglas deben estar implementadas en la capa de servicio (Service) con validaciones correctas y respuestas HTTP apropiadas. El docente verificará cada una durante la defensa técnica.

N.º	Regla de Negocio	Descripción Técnica
R1	Prerrequisito de asignatura	Un estudiante no puede matricularse en una asignatura si no ha aprobado sus prerrequisitos. El sistema verifica automáticamente el historial académico y bloquea la inscripción indicando qué prerrequisitos faltan.
R2	Reprobación por inasistencia	Un estudiante que acumule más del 25% de inasistencias en una asignatura queda automáticamente reprobado por asistencia, independientemente de sus notas. El sistema registra el estado y notifica al estudiante y al docente.
R3	Nota mínima de aprobación	La nota mínima de aprobación es 4.0. Si el promedio de un estudiante en una asignatura queda entre 3.5 y 3.9, el sistema lo registra como PENDIENTE DE EXAMEN DE RECUPERACIÓN. Bajo 3.5, reprobado directo sin recuperación.
R4	Arancel al día para rendir exámenes	Un estudiante con aranceles vencidos por más de 45 días no puede rendir exámenes finales ni acceder a su historial de notas hasta regularizar su situación. La deuda debe verificarse antes de cada período de evaluaciones.
R5	Práctica: requisitos previos	Para inscribir la práctica profesional, el estudiante debe haber aprobado el 80% de los créditos de la carrera y no tener deuda de arancel. La empresa donde realizará la práctica debe tener un convenio vigente con el instituto.

🛠️  REQUISITOS TÉCNICOS ESPECÍFICOS PARA ESTE CASO
Además de los requisitos generales de la evaluación, este caso exige lo siguiente:

•	Cada microservicio debe tener su propia base de datos independiente (no compartir tablas).
•	Al menos 2 microservicios deben consumir datos de otro microservicio usando WebClient o Feign Client.
•	El ms de trazabilidad o reportes debe hacer llamadas a al menos 3 otros microservicios para consolidar información.
•	Las validaciones con Bean Validation (@NotNull, @Size, @Min, @Max, @Pattern, etc.) deben aplicarse en los DTOs de entrada.
•	Los errores de validación y de negocio deben retornar JSON estructurado con código HTTP adecuado (400, 404, 409, 422 según corresponda).
•	El @ControllerAdvice debe manejar al menos: EntityNotFoundException, ConstraintViolationException y excepciones de negocio propias.
•	Cada microservicio debe tener logs SLF4J en las operaciones de creación, modificación, error de validación y operaciones críticas de negocio.

🔗  EJEMPLOS DE ENDPOINTS ESPERADOS
Para cada microservicio se espera implementar, como mínimo, los siguientes tipos de endpoints:

◦	GET /api/v1/estudiantes/  →  Listar todos los registros (con paginación opcional)
◦	GET /api/v1/estudiantes/{id}  →  Obtener registro por ID
◦	POST /api/v1/estudiantes/  →  Crear nuevo registro (con validación de DTO)
◦	PUT /api/v1/estudiantes/{id}  →  Actualizar registro existente
◦	DELETE /api/v1/estudiantes/{id}  →  Eliminar (o desactivar lógicamente)
◦	GET /api/v1/estudiantes/{id}/detalle  →  Endpoint enriquecido con datos de otro microservicio

📊  CRITERIOS DE EVALUACIÓN ESPECÍFICOS DE ESTE CASO
Criterio	Lo que el docente verificará en la defensa técnica
Contexto de negocio	¿Puede el estudiante explicar qué hace la empresa, su problema y cómo el sistema lo resuelve?
Modelado de datos	¿Las entidades JPA reflejan fielmente el dominio descrito? ¿Las relaciones son coherentes?
Reglas de negocio	¿Están las 5 reglas implementadas y probables mediante Postman en tiempo real?
Comunicación inter-ms	¿Puede demostrar una llamada entre microservicios y explicar el flujo?
Manejo de errores	¿Retorna el código HTTP correcto y un JSON estructurado para cada caso de error?
Logs	¿Hay mensajes de log claros en las operaciones críticas de este dominio?

