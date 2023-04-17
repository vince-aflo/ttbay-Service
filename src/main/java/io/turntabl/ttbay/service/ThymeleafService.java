package io.turntabl.ttbay.service;

import org.thymeleaf.context.Context;

import java.util.Map;

public interface ThymeleafService{
    Context setTemplateContext(Map<String, Object> templateModel);
    String createHtmlBody(Context templateContext, String templateName);
}
