package com.example.glovoproject.service;

import com.example.glovoproject.dto.Product;
import com.example.glovoproject.entity.OrderEntity;
import com.example.glovoproject.entity.ProductEntity;
import com.example.glovoproject.exceptions.ProductException;
import com.example.glovoproject.mapper.ProductMapper;
import com.example.glovoproject.repository.OrderRepository;
import com.example.glovoproject.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.glovoproject.utils.CostUtils.calculateOrderCost;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }


    public List<Product> getAll() {
        return productRepository.findAll().stream()
                .map(ProductMapper::mapToDto)
                .toList();
    }

    public Product findById(Long id) throws ProductException {
        Optional<ProductEntity> product = productRepository.findById(id);

        if (product.isPresent()) {
            return ProductMapper.mapToDto(product.get());
        } else {
            throw new ProductException(String.format("Product with id:%s not found", id));
        }

    }

    public void delete(Long id) throws ProductException {
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            product.ifPresent(productRepository::delete);
        } else {
            throw new ProductException(String.format("Product with id:%s not found", id));
        }

    }

    public void save(Product product) throws ProductException {
        ProductEntity productEntity = ProductMapper.mapToEntity(product);
        productRepository.save(productEntity);
    }
    public void update(Product product) throws ProductException {
        if (product == null) {
            throw new ProductException("You must set product");
        }
        if (product.getId() == null) {
            throw new ProductException("You must set id product");
        }
        Optional<ProductEntity> productEntity = productRepository.findById(product.getId());
        if (productEntity.isPresent()) {
            ProductEntity entity = new ProductEntity();
            BeanUtils.copyProperties(product, entity);

            OrderEntity order = productEntity.get().getOrder();
            entity.setOrder(order);
            productRepository.save(entity);


            if (order != null) {
                orderRepository.findById(order.getId()).ifPresent(orderEntity -> {
                    orderEntity.setCost(calculateOrderCost(orderEntity.getProducts()));
                    orderRepository.save(orderEntity);
                });
            }
        } else {
            throw new ProductException(String.format("Product with id:%s doesn't exists", product.getId()));
        }

    }

}
