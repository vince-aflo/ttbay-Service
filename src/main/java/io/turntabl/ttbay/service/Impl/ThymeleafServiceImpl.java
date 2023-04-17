package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.service.ThymeleafService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@AllArgsConstructor
public class ThymeleafServiceImpl implements ThymeleafService{
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Override
    public Context setTemplateContext(Map<String, Object> templateModel){
        Context context = new Context();
        context.setVariables(templateModel);
        return context;
    }

    @Override
    public String createHtmlBody(Context templateContext, String templateName){
        return thymeleafTemplateEngine.process(templateName,templateContext);
    }
}
