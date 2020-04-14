package com.pcandido.caed.controller;

import com.pcandido.caed.exception.NoAvailableItems;
import com.pcandido.caed.model.Item;
import com.pcandido.caed.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("correcoes")
public class ItemController {

    @Autowired
    private ItemService service;

    @GetMapping("proxima")
    public ResponseEntity<Item> getProxima() throws NoAvailableItems {
        return ResponseEntity.ok().body(service.getProxima());
    }

}
