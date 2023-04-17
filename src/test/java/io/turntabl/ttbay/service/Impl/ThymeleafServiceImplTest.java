package io.turntabl.ttbay.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ThymeleafServiceImplTest{
    @Mock
    private SpringTemplateEngine thymeleafTemplateEngine;
    @InjectMocks
    private ThymeleafServiceImpl thymeleafServiceImpl;
    private Context context;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createHtmlBody_givenContext_shouldCreateString(){
        thymeleafServiceImpl.createHtmlBody(context, "bid-was-made.html");
        verify(thymeleafTemplateEngine, times(1)).process("bid-was-made.html", context);
    }
}