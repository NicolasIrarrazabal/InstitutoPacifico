#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE db_asignaturas;
    CREATE DATABASE db_aranceles;
    CREATE DATABASE db_docente;
    CREATE DATABASE db_matriculas;
    CREATE DATABASE db_carreras;
    CREATE DATABASE db_estudiante;
    CREATE DATABASE db_notas;
    CREATE DATABASE db_practicas;
    CREATE DATABASE db_asistencia;
    CREATE DATABASE db_empresas;
EOSQL