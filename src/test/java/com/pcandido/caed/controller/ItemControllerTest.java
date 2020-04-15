package com.pcandido.caed.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static com.pcandido.caed.matcher.ErrorMatcher.isError;
import static com.pcandido.caed.matcher.JsonEqualMatcher.jsonEqualTo;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ItemControllerTest {

    private String res(String fileName) {
        return "expected-content/controller/item-controller-test/" + fileName;
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/D.sql"})
    public void deve_buscar_proximo_item() {
        given()
                .get("correcoes/proximo")
                .then()
                .log().body()
                .statusCode(HttpStatus.OK.value())
                .body(jsonEqualTo(res("deve_buscar_proximo_item.json")));
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/RR.sql"})
    public void deve_buscar_os_itens_reservados() {
        given()
                .get("correcoes/reservadas")
                .then()
                .log().body()
                .statusCode(HttpStatus.OK.value())
                .body(jsonEqualTo(res("deve_buscar_os_itens_reservados.json")));
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/D.sql"})
    public void deve_reservar_um_item_na_ordem() {
        given()
                .post("correcoes/reservadas/9859662")
                .then()
                .log().body()
                .statusCode(HttpStatus.OK.value())
                .body(jsonEqualTo(res("deve_reservar_um_item_na_ordem.json")));
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/D.sql"})
    public void deve_marcar_um_item_como_defeito_na_ordem() {
        given()
                .post("correcoes/defeito/9859662")
                .then()
                .log().body()
                .statusCode(HttpStatus.OK.value())
                .body(jsonEqualTo(res("deve_marcar_um_item_como_defeito_na_ordem.json")));
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/RD.sql"})
    public void deve_marcar_um_item_reservado_como_defeito() {
        given()
                .post("correcoes/defeito/9859662")
                .then()
                .log().body()
                .statusCode(HttpStatus.OK.value())
                .body(jsonEqualTo(res("deve_marcar_um_item_como_defeito_na_ordem.json")));
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/D.sql"})
    public void deve_corrigir_um_item_na_ordem() {
        given()
                .body("{\"chave\":[{\"id\":\"186\",\"valor\":\"0\"}]}")
                .contentType("application/json")
                .post("correcoes/9859662")
                .then()
                .log().body()
                .statusCode(HttpStatus.OK.value())
                .body(jsonEqualTo(res("deve_marcar_um_item_como_defeito_na_ordem.json")));
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/EC.sql"})
    public void deve_mostrar_erro_se_nao_tiver_proximo() {
        given()
                .get("correcoes/proximo")
                .then()
                .log().body()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(isError("SEM_CORRECAO", "Não existem mais correções disponíveis"));
    }


    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/DD.sql"})
    public void nao_deve_reservar_um_item_fora_de_ordem() {
        given()
                .post("correcoes/reservadas/9859663")
                .then()
                .log().body()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(isError("NAO_PERMITODO", "Não é permitido alterar um item fora de ordem"));
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/DD.sql"})
    public void nao_deve_marcar_como_defeito_fora_de_ordem() {
        given()
                .post("correcoes/reservadas/9859663")
                .then()
                .log().body()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(isError("NAO_PERMITODO", "Não é permitido alterar um item fora de ordem"));
    }


}