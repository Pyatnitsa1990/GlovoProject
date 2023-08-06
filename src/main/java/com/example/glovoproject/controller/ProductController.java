package com.example.glovoproject.controller;

import com.example.glovoproject.dto.Product;
import com.example.glovoproject.exceptions.ProductException;
import com.example.glovoproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;


    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(productService.findById(id));
        } catch (ProductException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        try {
            productService.delete(id);
        } catch (ProductException ex) {
            ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Product product) {
        try {
            productService.save(product);
            return ResponseEntity
                    .status(HttpStatus.CREATED).build();
        } catch (ProductException ex) {
            return ResponseEntity.badRequest()
                    .body(ex.getLocalizedMessage());
        }
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestBody Product product) {
        try {
            productService.update(product);
            return ResponseEntity
                    .ok("Product with id:" + product.getId() + " was updated");
        } catch (ProductException ex) {
            return ResponseEntity.badRequest()
                    .body(ex.getLocalizedMessage());
        }
    }
}
