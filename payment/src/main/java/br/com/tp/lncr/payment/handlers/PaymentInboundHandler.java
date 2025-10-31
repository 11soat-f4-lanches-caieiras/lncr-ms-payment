package br.com.tp.lncr.payment.handlers;

import br.com.tp.lncr.commons.utils.ExceptionHandlerUtil;
import br.com.tp.lncr.core.exceptions.PaymentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentInboundHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Object> handleKitchenOrderException(PaymentException ex) {
        return ExceptionHandlerUtil.handleException(ex.getMessage(), ex.getCode(), ex);
    }
}
