package com.pcandido.caed.controller;

import com.pcandido.caed.service.CorrecaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class CorrecaoControllerTest {

    @Autowired
    private CorrecaoController correcaoController;

    @MockBean
    private CorrecaoService correcaoService;


}