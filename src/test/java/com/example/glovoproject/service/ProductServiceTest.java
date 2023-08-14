package com.example.glovoproject.service;

import com.example.glovoproject.dto.Product;
import com.example.glovoproject.entity.OrderEntity;
import com.example.glovoproject.entity.ProductEntity;
import com.example.glovoproject.exceptions.ProductException;
import com.example.glovoproject.repository.OrderRepository;
import com.example.glovoproject.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest extends BaseTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;


    @Test
    public void checkGetAll() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);

        ProductEntity productEntity2 = new ProductEntity();
        productEntity.setId(2L);

        List<ProductEntity> productEntities = Arrays.asList(productEntity, productEntity2);
        when(productRepository.findAll()).thenReturn(productEntities);

        List<Product> result = productService.getAll();

        assertEquals(2, result.size());

    }

    @Test
    public void checkFindById() throws ProductException {
        Long productId = 13L;
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        Product result = productService.findById(productId);

        assertEquals(productId, result.getId());
    }

    @Test
    public void checkFindByIdNotExistingProduct() {

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        long productId = 1L;
        var exception = assertThrows(ProductException.class, () -> productService.findById(productId));
        assertEquals("Product with id:" + productId + " not found", exception.getMessage());
    }

    @Test
    public void checkDeleteExistingProduct() throws ProductException {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(new ProductEntity()));
        productService.delete(productId);
        verify(productRepository).delete(any());
    }

    @Test
    public void checkDeleteForNotExistingProduct() {

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        long productId = 1L;
        var exception = assertThrows(ProductException.class, () -> productService.delete(productId));
        assertEquals("Product with id:" + productId + " not found", exception.getMessage());
    }

    @Test
    void checkSaveProducts() throws ProductException {
        Product product = Product.builder().
                id(1L).
                name("Pepsi").
                cost(2.0D).
                build();
        productService.save(product);

        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    public void checkUpdateWhenIdIsNull() {
        var exception = assertThrows(ProductException.class, () -> productService.update(null));
        assertEquals("You must set product", exception.getMessage());
    }

    @Test
    public void checkUpdateWithNullId() {

        assertThrows(ProductException.class, () -> productService.update(Product.builder().build()));
    }

    @Test
    public void checkUpdateExistingProduct() throws ProductException {
        Product product = Product.builder().build();
        Long productId = 1L;
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(new ProductEntity()));

        productService.update(product);

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    public void checkUpdateOrderCostIfExists() throws ProductException {
        var product = Product.builder()
                .id(1l)
                .build();

        var productEntity = new ProductEntity();
        var orderEntity = new OrderEntity();
        orderEntity.setId(1L);

        productEntity.setOrder(orderEntity);

        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(productEntity));

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(new OrderEntity()));

        productService.update(product);

        verify(orderRepository, times(1)).save(any());

    }
    @Test
    public void checkUpdateNotExistingProduct() {
        var product = Product.builder()
                .id(1l)
                .build();

        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.empty());

        var exception =
                assertThrows(ProductException.class, () -> productService.update(product));

        assertTrue(exception.getMessage().contains("Product with id:"));
    }

}





