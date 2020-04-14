package com.pcandido.caed.controller;

import com.pcandido.caed.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ItemControllerTest {

    @Autowired
    private ItemController itemController;

    @MockBean
    private ItemService itemService;


}