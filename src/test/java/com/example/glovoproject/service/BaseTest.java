package com.example.glovoproject.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public class BaseTest {

    private AutoCloseable closeable;

    @BeforeEach
    protected void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    protected void releaseMocks() throws Exception {
        closeable.close();
    }

}