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

package com.tkmtwo.rest.exhandler.handlers;


import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;


import cz.jirutka.spring.exhandler.handlers.ErrorMessageRestExceptionHandler;
import cz.jirutka.spring.exhandler.messages.ErrorMessage;
import cz.jirutka.spring.exhandler.messages.ValidationErrorMessage;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;


/**
 *
 *
 */
public class BindExceptionHandler
  extends ErrorMessageRestExceptionHandler<BindException> {
  
  
  public BindExceptionHandler() {
    super(UNPROCESSABLE_ENTITY);
  }
  
  @Override
    public ValidationErrorMessage createBody(BindException ex, HttpServletRequest req) {
    
    ErrorMessage tmpl = super.createBody(ex, req);
    ValidationErrorMessage msg = new ValidationErrorMessage(tmpl);
    
    BindingResult result = ex.getBindingResult();
    
    for (ObjectError err : result.getGlobalErrors()) {
      msg.addError(err.getDefaultMessage());
    }
    for (FieldError err : result.getFieldErrors()) {
      msg.addError(err.getField(), err.getRejectedValue(), err.getDefaultMessage());
    }
    return msg;
  }
}
