package com.example.glovoproject.service;

import com.example.glovoproject.dto.Order;
import com.example.glovoproject.dto.Product;
import com.example.glovoproject.entity.OrderEntity;
import com.example.glovoproject.entity.ProductEntity;
import com.example.glovoproject.exceptions.OrderException;
import com.example.glovoproject.repository.OrderRepository;
import com.example.glovoproject.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest extends BaseTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<List<ProductEntity>> productListArgumentCaptor;
    @Captor
    private ArgumentCaptor<OrderEntity> orderEntityArgumentCaptor;

    @Test
    public void findByIdExistingOrder() throws OrderException {
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(new OrderEntity()));

        Order result = orderService.findById(1L);

        assertNotNull(result);
    }

    @Test
    public void findByIdNotExistingOrder() {

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        long orderId = 1L;
        var exception = assertThrows(OrderException.class, () -> orderService.findById(orderId));
        assertEquals("Order with id:" + orderId + " not found", exception.getMessage());
    }

    @Test
    public void getAll() {
        OrderEntity orderEntity1 = new OrderEntity();
        orderEntity1.setId(1L);

        OrderEntity orderEntity2 = new OrderEntity();
        orderEntity2.setId(2L);

        List<OrderEntity> orderEntities = Arrays.asList(orderEntity1, orderEntity2);
        when(orderRepository.findAll()).thenReturn(orderEntities);

        List<Order> result = orderService.getAll();

        assertEquals(2, result.size());

    }

    @Test
    public void checkRemove() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new OrderEntity()));
        orderService.delete(orderId);
        verify(orderRepository).delete(any());

    }

    @Test
    public void checkUpdateWhenIdIsNull() {
        var exception = assertThrows(OrderException.class, () -> orderService.update(null, List.of()));
        assertEquals("You must set id order", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void checkUpdateWhenProductsIsNullOrEmpty(List<Product> products) {
        var exception = assertThrows(OrderException.class, () -> orderService.update(1L, products));
        assertEquals("You can't create order without products", exception.getMessage());
    }

    @Test
    public void checkUpdateForNotExistingOrder() {
        long orderId = 1L;
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(OrderException.class,
                () -> orderService.update(orderId, List.of(Product.builder().build())));

        assertAll(() -> assertEquals("Order with id:" + orderId + " doesn't exists", exception.getMessage()),
                () -> verify(orderRepository, never()).saveAll(anyList()),
                () -> verify(orderRepository, never()).save(any()));
    }

    @Test
    public void checkUpdateForExistingOrder() throws OrderException {
        long orderId = 1L;
        OrderEntity orderEntity = createOrderEntity(orderId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(orderEntity));

        orderService.update(orderId, createProducts());

        verify(productRepository).saveAll(productListArgumentCaptor.capture());
        verify(orderRepository).save(orderEntityArgumentCaptor.capture());

        List<ProductEntity> productEntityCaptor = productListArgumentCaptor.getValue();
        OrderEntity orderEntityCaptor = orderEntityArgumentCaptor.getValue();

        assertAll(
                () -> assertEquals(orderEntityCaptor.getCost(), 5.0),
                () -> assertEquals(orderEntityCaptor.getProducts().size(), 3),
                () -> assertEquals(productEntityCaptor.size(), 23));

    }

    private OrderEntity createOrderEntity(long orderId) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        ProductEntity productEntity1 = new ProductEntity();
        productEntity1.setCost(1D);
        ProductEntity productEntity2 = new ProductEntity();
        productEntity2.setCost(1.5D);
        ProductEntity productEntity3 = new ProductEntity();
        productEntity3.setCost(2.5D);
        orderEntity.setProducts(List.of(productEntity1, productEntity2, productEntity3));
        return orderEntity;
    }

    private List<Product> createProducts() {
        return List.of(Product.builder()
                        .name("cola")
                        .cost(1.1d).build(),
                Product.builder()
                        .name("fanta")
                        .cost(1.4d).build(),
                Product.builder()
                        .name("pepsi")
                        .cost(1.3d).build());
    }

    //========================================
    @Test
    void testSaveOrderWithProducts() throws OrderException {
        Order order = Order.builder().
                id(1L).
                date(LocalDateTime.now()).
                products(List.of(Product.builder()
                                .name("Cola")
                                .cost(3.4)
                                .build(),
                        Product.builder()
                                .name("Fanta")
                                .cost(1.4d)
                                .build())).build();

        orderService.save(order);

        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void checkSaveOrderWithoutProducts() {
        assertThrows(OrderException.class, () -> orderService.save(Order.builder().build()));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void checkDeleteForExistingOrder() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new OrderEntity()));
        orderService.delete(orderId);

        verify(orderRepository).delete(any(OrderEntity.class));
    }

    @Test
    void checkDeleteForNotExistentOrder() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        orderService.delete(orderId);
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void checkDeleteForNotExistentProductFromOrder() {
        Long orderId = 1L;
        Long productId = 1L;
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(new OrderEntity()));
        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        orderService.deleteProductFromOrder(orderId, productId);

        verify(orderRepository, never()).save(any());
    }

    @Test
    public void checkDeleteForExistentProductFromOrder() {
        Long orderId = 1L;
        Long productId = 1L;

        // create order
        var order = new OrderEntity();

        //create product 1
        var product1 = new ProductEntity();
        product1.setId(productId);
        product1.setCost(1d);

        //create product 2
        var product2 = new ProductEntity();
        product2.setId(2L);
        product2.setCost(2d);

        //set for order product1 & product2
        order.setId(orderId);
        order.setCost(3d);
        order.addProduct(product1);
        order.addProduct(product2);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product1));

        orderService.deleteProductFromOrder(orderId, productId);

        verify(orderRepository, times(1))
                .save(orderEntityArgumentCaptor.capture());

        var orderEntity = orderEntityArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(1, orderEntity.getProducts().size()),
                () -> assertEquals(2d, orderEntity.getProducts().get(0).getCost()),
                () -> assertEquals(2d, orderEntity.getCost()));


    }
}
