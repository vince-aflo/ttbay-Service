package io.turntabl.ttbay.service;

import org.springframework.security.core.Authentication;

public interface TokenAttributesExtractor{
    String extractEmailFromToken (Authentication authentication);
}
