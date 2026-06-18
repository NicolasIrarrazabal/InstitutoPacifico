package com.proyecto.ms_empresas;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requiere base de datos PostgreSQL activa — se omite en CI sin BD")
class MsEmpresasApplicationTests {

    @Test
    void contextLoads() {
    }
}
