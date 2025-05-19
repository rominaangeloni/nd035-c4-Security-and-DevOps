package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private Item createItem(Long id, String name, BigDecimal price, String desc) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setDescription(desc);
        return item;
    }

    @Test
    public void get_all_items_successfully() {
        List<Item> items = Arrays.asList(
                createItem(1L, "Item1", BigDecimal.valueOf(10.0), "Description1"),
                createItem(2L, "Item2", BigDecimal.valueOf(20.0), "Description2")
        );

        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void get_item_by_id_successfully() {
        Item item = createItem(1L, "Item1", BigDecimal.valueOf(10.0), "Description");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(item, response.getBody());
    }

    @Test
    public void get_item_by_id_not_found() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(99L);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_items_by_name_successfully() {
        List<Item> items = Arrays.asList(
                createItem(1L, "ItemX", BigDecimal.valueOf(15.0), "Desc")
        );

        when(itemRepository.findByName("ItemX")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("ItemX");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void get_items_by_name_not_found() {
        when(itemRepository.findByName("GhostItem")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Item>> response = itemController.getItemsByName("GhostItem");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}

