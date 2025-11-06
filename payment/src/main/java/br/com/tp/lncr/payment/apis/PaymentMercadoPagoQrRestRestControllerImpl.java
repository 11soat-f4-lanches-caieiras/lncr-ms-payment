package br.com.tp.lncr.payment.apis;

import br.com.tp.lncr.commons.model.ResponseListModel;
import br.com.tp.lncr.commons.model.ResponseModel;
import br.com.tp.lncr.commons.utils.ResponseEntityModelUtil;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.dataproxy.PaymentMercadoPagoQrDataProxy;
import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import br.com.tp.lncr.core.interfaces.payment.PaymentController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments/mercadoPago")
public class PaymentMercadoPagoQrRestRestControllerImpl implements PaymentRestController<PaymentMercadopagoQrDTO> {


    public final PaymentController<PaymentMercadopagoQrDTO> paymentMercadoPagoQrController;
    public final PaymentMercadoPagoQrDataProxy paymentMercadoPagoQrDataProxy;
    public final MercadoPagoConfig mercadoPagoConfig;


    public PaymentMercadoPagoQrRestRestControllerImpl(PaymentController<PaymentMercadopagoQrDTO> paymentMercadoPagoQrController, PaymentMercadoPagoQrDataProxy paymentMercadoPagoQrDataProxy, MercadoPagoConfig mercadoPagoConfig) {
        this.paymentMercadoPagoQrController = paymentMercadoPagoQrController;
        this.paymentMercadoPagoQrDataProxy = paymentMercadoPagoQrDataProxy;
        this.mercadoPagoConfig = mercadoPagoConfig;
    }

    @Override
    @PostMapping("/charge")
    public ResponseEntity<ResponseModel<PaymentMercadopagoQrDTO>> createPaymentCharge(@RequestBody PaymentMercadopagoQrDTO paymentMercadopagoQrDTO) {
        paymentMercadopagoQrDTO = this.paymentMercadoPagoQrController.createPaymentCharge(this.paymentMercadoPagoQrDataProxy,paymentMercadopagoQrDTO);
        return ResponseEntityModelUtil.created(null, mercadoPagoConfig.getLocationPrefix() + "/" + paymentMercadopagoQrDTO.getId());
    }

    @Override
    @GetMapping("/{paymentId}")
    public ResponseEntity<ResponseModel<PaymentMercadopagoQrDTO>> getPaymentById(@PathVariable(name = "paymentId") Integer paymentId){
        PaymentMercadopagoQrDTO paymentMercadopagoQrDTO = this.paymentMercadoPagoQrController.getPaymentById(this.paymentMercadoPagoQrDataProxy,paymentId);
        return ResponseEntityModelUtil.ok(paymentMercadopagoQrDTO);
    }

    @Override
    @GetMapping("/customerOrder/{customerOrderId}/get")
    public ResponseEntity<ResponseModel<PaymentMercadopagoQrDTO>> getPaymentByCustomerOrderId(@PathVariable(name = "customerOrderId")Integer customerOrderId) {
        PaymentMercadopagoQrDTO paymentMercadopagoQrDTO = this.paymentMercadoPagoQrController.getPaymentByCustomerOrderId(this.paymentMercadoPagoQrDataProxy,customerOrderId);
        return ResponseEntityModelUtil.ok(paymentMercadopagoQrDTO);
    }

    @Override
    @PatchMapping("/customerOrder/{customerOrderId}/cancel")
    public ResponseEntity<ResponseModel<PaymentMercadopagoQrDTO>> cancelPaymentByCustomerOrderId(@PathVariable(name = "customerOrderId") Integer customerOrderId) {
        PaymentMercadopagoQrDTO paymentMercadopagoQrDTO = this.paymentMercadoPagoQrController.cancelPaymentByOrderId(this.paymentMercadoPagoQrDataProxy,customerOrderId);
        return ResponseEntityModelUtil.ok(paymentMercadopagoQrDTO);
    }

    @Override
    @PatchMapping("/paymentReceived")
    public ResponseEntity<ResponseModel<PaymentMercadopagoQrDTO>> processPaymentReceived(@RequestParam(name = "data_external_reference") String externalReference,
                                                                                         @RequestParam(name = "data_id") String dataId,
                                                                                         @RequestParam(name = "type", defaultValue = "order") String type,
                                                                                         @RequestBody Map<String, Object> body) {
        PaymentMercadopagoQrDTO paymentMercadopagoQrDTO = this.paymentMercadoPagoQrController.processPaymentReceived(this.paymentMercadoPagoQrDataProxy,externalReference,dataId,body);
        return ResponseEntityModelUtil.ok(paymentMercadopagoQrDTO);
    }

    @Override
    @GetMapping("paymentStatusList/{paymentStatusList}")
    public ResponseEntity<ResponseListModel<PaymentMercadopagoQrDTO>> getPaymentByStatusList(@PathVariable(name = "paymentStatusList") List<String> paymentStatusList) {
        List<PaymentMercadopagoQrDTO> paymentMercadopagoQrDTO = this.paymentMercadoPagoQrController.getPaymentByStatusList(this.paymentMercadoPagoQrDataProxy,paymentStatusList);
        return ResponseEntityModelUtil.listOK(paymentMercadopagoQrDTO);
    }

}
