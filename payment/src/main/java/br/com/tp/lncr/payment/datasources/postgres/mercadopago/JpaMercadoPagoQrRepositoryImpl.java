package br.com.tp.lncr.payment.datasources.postgres.mercadopago;

import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaMercadoPagoQrRepositoryImpl {

    public final JpaMercadoPagoQrRepository jpaMercadoPagoQrPostgresRepository;
    public final JpaPaymentMercadopagoQRMapper jpamercadopagoQRMapper;

    public JpaMercadoPagoQrRepositoryImpl(@Lazy JpaMercadoPagoQrRepository jpaMercadoPagoQrPostgresRepository, JpaPaymentMercadopagoQRMapper jpamercadopagoQRMapper) {
        this.jpaMercadoPagoQrPostgresRepository = jpaMercadoPagoQrPostgresRepository;
        this.jpamercadopagoQRMapper = jpamercadopagoQRMapper;
    }

    public PaymentMercadopagoQrDTO save(PaymentMercadopagoQrDTO paymentDTO) {
        JpaMercadopagoQrEntity jpaMercadopagoQr = this.jpamercadopagoQRMapper.mercadopagoQrDtoToJpa(paymentDTO);
        jpaMercadopagoQr = this.jpaMercadoPagoQrPostgresRepository.save(jpaMercadopagoQr);
        return this.jpamercadopagoQRMapper.jpaMercadopagoQrToDTO(jpaMercadopagoQr);
    }

    public PaymentMercadopagoQrDTO findById(Integer paymentId) {
        return this.jpamercadopagoQRMapper.jpaMercadopagoQrToDTO(this.jpaMercadoPagoQrPostgresRepository.findById(paymentId).orElse(null));
    }

    public PaymentMercadopagoQrDTO findByCustomerOrderId(Integer customerOrderId) {
        return this.jpamercadopagoQRMapper.jpaMercadopagoQrToDTO(this.jpaMercadoPagoQrPostgresRepository.findByCustomerOrderId(customerOrderId).orElse(null));
    }

    public List<PaymentMercadopagoQrDTO> findByStatusList(List<Integer> paymentStatusIdList) {
        List<JpaMercadopagoQrEntity> jpaMercadopagoQrList = jpaMercadoPagoQrPostgresRepository.findByStatusList(paymentStatusIdList);
        return jpaMercadopagoQrList.stream().map(jpamercadopagoQRMapper::jpaMercadopagoQrToDTO).toList();
    }
}
