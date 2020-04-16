package com.pcandido.caed.controller;

import io.restassured.specification.RequestSpecification;
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

    private void testSucessoGetApi(String urlRelativa, String fileName) {
        given() // obtem uma conexão com a API
                .get("correcoes/" + urlRelativa) // especifica qual a rota relativa
                .then() // ao fazer a requisição
                .log().body() // loga o corpo da resposta
                .statusCode(HttpStatus.OK.value()) // valida o status
                .body(jsonEqualTo("expected-content/controller/item-controller-test/" + fileName)); // valida o corpo recebido com o esperado
    }

    private void testSucessoPostApi(String urlRelativa, String body, String fileName) {
        RequestSpecification rs;
        if (body == null)
            rs = given();
        else
            rs = given().body(body).contentType("application/json");

        rs.post("correcoes/" + urlRelativa)
                .then()
                .log().body()
                .statusCode(HttpStatus.OK.value())
                .body(jsonEqualTo("expected-content/controller/item-controller-test/" + fileName));
    }

    private void testErroGetApi(String urlRelativa, HttpStatus httpStatus, String tipo, String descricao) {
        given()
                .get("correcoes/" + urlRelativa)
                .then()
                .log().body()
                .statusCode(httpStatus.value())
                .body(isError(tipo, descricao));
    }

    private void testErroPostApi(String urlRelativa, String body, HttpStatus httpStatus, String tipo, String descricao) {
        RequestSpecification rs;
        if (body == null)
            rs = given();
        else
            rs = given().body(body).contentType("application/json");

        rs.post("correcoes/" + urlRelativa)
                .then()
                .log().body()
                .statusCode(httpStatus.value())
                .body(isError(tipo, descricao));
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/D.sql"})
    // executa duas SQLs antes de executar o teste, um para limpar itens e outro para inserir os necessários para esse testes
    //padrão de nomenclatura para SQLs de povoamento: (D)isponivel, (R)eservado, (C)orrigido, com_d(E)feito
    public void deve_buscar_proximo_item() {
        testSucessoGetApi("proxima", "deve_buscar_proximo_item.json");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/RR.sql"})
    public void deve_buscar_os_itens_reservados() {
        testSucessoGetApi("reservadas", "deve_buscar_os_itens_reservados.json");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/D.sql"})
    public void deve_reservar_um_item_na_ordem() {
        testSucessoPostApi("reservadas/9859662", null, "deve_reservar_um_item_na_ordem.json");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/D.sql"})
    public void deve_marcar_um_item_como_defeito_na_ordem() {
        testSucessoPostApi("defeito/9859662", null, "deve_marcar_um_item_como_defeito_na_ordem.json");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/RD.sql"})
    public void deve_marcar_um_item_reservado_como_defeito() {
        testSucessoPostApi("defeito/9859662", null, "deve_marcar_um_item_reservado_como_defeito.json");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/D.sql"})
    public void deve_corrigir_um_item_na_ordem() {
        testSucessoPostApi("9859662", "{\"chave\":[{\"id\":\"186\",\"valor\":\"0\"}]}", "deve_corrigir_um_item_na_ordem.json");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/RD.sql"})
    public void deve_corrigir_um_item_reservado() {
        testSucessoPostApi("9859662", "{\"chave\":[{\"id\":\"186\",\"valor\":\"0\"}]}", "deve_corrigir_um_item_reservado.json");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/EC.sql"})
    public void deve_mostrar_erro_se_nao_tiver_proximo() {
        testErroGetApi(
                "proxima",
                HttpStatus.NOT_FOUND,
                "SEM_CORRECAO",
                "Não existem mais correções disponíveis");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/DD.sql"})
    public void nao_deve_reservar_um_item_fora_de_ordem() {
        testErroPostApi(
                "reservadas/9859663",
                null,
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item fora de ordem"
        );
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/RD.sql"})
    public void nao_deve_reservar_um_item_reservado() {
        testErroPostApi(
                "reservadas/9859662",
                null,
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item de RESERVADO para RESERVADO"
        );
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/C.sql"})
    public void nao_deve_reservar_um_item_corrigido() {
        testErroPostApi(
                "reservadas/9859662",
                null,
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item de CORRIGIDO para RESERVADO"
        );
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/E.sql"})
    public void nao_deve_reservar_um_item_com_defeito() {
        testErroPostApi(
                "reservadas/9859662",
                null,
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item de COM_DEFEITO para RESERVADO"
        );
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/DD.sql"})
    public void nao_deve_marcar_como_defeito_fora_de_ordem() {
        testErroPostApi(
                "defeito/9859663",
                null,
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item fora de ordem"
        );
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/C.sql"})
    public void nao_deve_marcar_como_defeito_um_item_corrigido() {
        testErroPostApi(
                "defeito/9859662",
                null,
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item de CORRIGIDO para COM_DEFEITO"
        );
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/E.sql"})
    public void nao_deve_marcar_como_defeito_um_item_com_defeito() {
        testErroPostApi(
                "defeito/9859662",
                null,
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item de COM_DEFEITO para COM_DEFEITO"
        );
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/DD.sql"})
    public void nao_deve_corrigir_item_fora_de_ordem() {
        testErroPostApi(
                "9859663",
                "{\"chave\":[{\"id\":\"186\",\"valor\":\"0\"}]}",
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item fora de ordem");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/E.sql"})
    public void nao_deve_corrigir_item_com_defeito() {
        testErroPostApi(
                "9859662",
                "{\"chave\":[{\"id\":\"186\",\"valor\":\"0\"}]}",
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item de COM_DEFEITO para CORRIGIDO");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/C.sql"})
    public void nao_deve_corrigir_item_corrigido() {
        testErroPostApi(
                "9859662",
                "{\"chave\":[{\"id\":\"186\",\"valor\":\"0\"}]}",
                HttpStatus.BAD_REQUEST,
                "NAO_PERMITODO",
                "Não é permitido alterar um item de CORRIGIDO para CORRIGIDO");
    }

    @Test
    @Sql({"classpath:sqls/clear.sql", "classpath:sqls/D.sql"})
    public void nao_deve_corrigir_item_com_opcao_invalida() {
        testErroPostApi(
                "9859662",
                "{\"chave\":[{\"id\":\"186\",\"valor\":\"99\"}]}",
                HttpStatus.BAD_REQUEST,
                "CHAVE_INCORRETA",
                "Chave de correção incorreta. Valor '99' não é válido para o item 186");
    }

}