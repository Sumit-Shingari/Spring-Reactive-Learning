package com.sumit.shingari.springreactivelearning.exception;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;

public class GlobalErrorAttributes extends DefaultErrorAttributes{
    
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, 
      ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(
          request, options);
        String message = "Exception Occured";
        if (request.path().equals("/fun/runtimeException"))
        {
        	message = "Runtime Exception Occured for " + request.path();
        }
        map.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        map.put("message", message);
        return map;
    }

}