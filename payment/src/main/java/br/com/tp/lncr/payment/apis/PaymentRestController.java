package br.com.tp.lncr.payment.apis;

import br.com.tp.lncr.commons.model.ResponseListModel;
import br.com.tp.lncr.commons.model.ResponseModel;
import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface PaymentRestController<T> {

    ResponseEntity<ResponseModel<T>> createPaymentCharge(@RequestBody PaymentMercadopagoQrDTO paymentMercadopagoQrDTO);

    ResponseEntity<ResponseModel<T>> getPaymentById(@PathVariable(name = "paymentId") Integer paymentId);

    ResponseEntity<ResponseModel<T>> cancelPaymentByCustomerOrderId(@PathVariable(name = "customerOrderId") Integer customerOrderId);

    ResponseEntity<ResponseModel<T>> getPaymentByCustomerOrderId(@PathVariable(name = "customerOrderId") Integer customerOrderId);

    ResponseEntity<ResponseModel<PaymentMercadopagoQrDTO>> processPaymentReceived(@RequestParam(name = "data.external_reference") String externalReference,
                                                                                  @RequestParam(name = "data.id") String dataId,
                                                                                  @RequestParam(name = "type", defaultValue = "order") String type,
                                                                                  @RequestBody Map<String, Object> body);

    ResponseEntity<ResponseListModel<T>> getPaymentByStatusList(@PathVariable(name = "paymentStatusList") List<String> paymentStatus);

}
