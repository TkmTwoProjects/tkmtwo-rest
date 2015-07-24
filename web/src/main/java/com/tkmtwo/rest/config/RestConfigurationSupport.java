/*
 *
 * Copyright 2014 Tom Mahaffey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.tkmtwo.rest.config;

import com.tkmtwo.rest.exhandler.handlers.*;
import com.tkmtwo.timex.convert.IsoDateTimeConverter;
import com.tkmtwo.timex.convert.IsoPeriodConverter;
import com.tkmtwo.timex.jackson.JodaModule;
import cz.jirutka.spring.exhandler.RestHandlerExceptionResolver;
//import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
//import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.HandlerExceptionResolver;
//import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 *
 *
 */
public abstract class RestConfigurationSupport
  extends WebMvcConfigurationSupport {
  

  /* ***
  private List<HttpMessageConverter<?>> messageConverters;
  */
  
  @Autowired
  private Environment env;
  protected Environment getEnvironment() { return env; }
  
  
  /* ***  
  public RestConfig() throws Exception {
    super();
    getMessageConverters().add(newJacksonMessageConverter());
  }
  */
  
  
  @Bean
  public LocalValidatorFactoryBean validator() {
    LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
    //validatorFactoryBean.setValidationMessageSource(messageSource);
    return validatorFactoryBean;
  }
  
  @Override
  public Validator getValidator() {
    return validator();
  }
  
  /* ***  
  private List<HttpMessageConverter<?>> getMessageConverters() {
    if (messageConverters == null) {
      messageConverters = new ArrayList<HttpMessageConverter<?>>();
    }
    return messageConverters;
  }
  private void setMessageConverters(List<HttpMessageConverter<?>> l) {
    messageConverters = l;
  }
  */

  @Bean
  public MappingJackson2HttpMessageConverter jacksonMessageConverter() {
    MappingJackson2HttpMessageConverter jmc = new MappingJackson2HttpMessageConverter();
    jmc.setPrettyPrint(true);
    jmc.getObjectMapper().registerModule(new JodaModule());
    return jmc;
  }

  
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new IsoDateTimeConverter());
    registry.addConverter(new IsoPeriodConverter());
  }
  
  @Override
  public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
    converters.add(jacksonMessageConverter());
    addDefaultHttpMessageConverters(converters);
  }
  

  @Override
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    resolvers.add(exceptionHandlerExceptionResolver()); // resolves @ExceptionHandler
    resolvers.add(restExceptionResolver());
  }
  
  @Bean
  public RestHandlerExceptionResolver restExceptionResolver() {
    RestHandlerExceptionResolver rher = RestHandlerExceptionResolver.builder()
      .httpMessageConverters(getMessageConverters())
      .messageSource(httpErrorMessageSource())
      .defaultContentType(MediaType.APPLICATION_JSON)
      //.addErrorMessageHandler(EmptyResultDataAccessException.class, HttpStatus.NOT_FOUND)
      .addHandler(IllegalArgumentException.class, new IllegalArgumentExceptionHandler())
      .addHandler(BindException.class, new BindExceptionHandler())
      .addHandler(HttpMessageNotReadableException.class, new HttpMessageNotReadableExceptionHandler())
      .addHandler(AccessDeniedException.class, new AccessDeniedExceptionHandler())
      .build();
    
    return rher;
  }
  
  @Bean
  public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
    ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
    return resolver;
  }


  @Bean
  public MessageSource httpErrorMessageSource() {
    ReloadableResourceBundleMessageSource m = new ReloadableResourceBundleMessageSource();
    //m.setBasename("classpath:/aisp/exhandler/messages");
    m.setBasename(env.getProperty("messagesource.basename"));
    m.setDefaultEncoding("UTF-8");
    return m;
  }


}
