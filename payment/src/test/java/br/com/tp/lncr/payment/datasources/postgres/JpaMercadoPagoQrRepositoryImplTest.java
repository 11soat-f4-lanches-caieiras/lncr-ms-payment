package br.com.tp.lncr.payment.datasources.postgres;

import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaMercadoPagoQrRepository;
import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaMercadoPagoQrRepositoryImpl;
import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaMercadopagoQrEntity;
import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaPaymentMercadopagoQRMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaMercadoPagoQrRepositoryImplTest {

    private final JpaMercadoPagoQrRepository jpaMercadoPagoQrRepository = mock(JpaMercadoPagoQrRepository.class);
    private final JpaPaymentMercadopagoQRMapper jpaMapper = mock(JpaPaymentMercadopagoQRMapper.class);
    private final JpaMercadoPagoQrRepositoryImpl repository = new JpaMercadoPagoQrRepositoryImpl(jpaMercadoPagoQrRepository, jpaMapper);

    @Test
    void savePaymentCallsRepositoryAndMapper() {
        PaymentMercadopagoQrDTO dto = createPaymentDTO();
        JpaMercadopagoQrEntity entity = createEntity();
        JpaMercadopagoQrEntity savedEntity = createEntity();
        savedEntity.setId(1);
        PaymentMercadopagoQrDTO savedDTO = createPaymentDTO();

        when(jpaMapper.mercadopagoQrDtoToJpa(dto)).thenReturn(entity);
        when(jpaMercadoPagoQrRepository.save(entity)).thenReturn(savedEntity);
        when(jpaMapper.jpaMercadopagoQrToDTO(savedEntity)).thenReturn(savedDTO);

        PaymentMercadopagoQrDTO result = repository.save(dto);

        assertEquals(savedDTO, result);
        verify(jpaMapper).mercadopagoQrDtoToJpa(dto);
        verify(jpaMercadoPagoQrRepository).save(entity);
        verify(jpaMapper).jpaMercadopagoQrToDTO(savedEntity);
    }

    @Test
    void savePaymentWithNullDto() {
        when(jpaMapper.mercadopagoQrDtoToJpa(null)).thenReturn(null);
        when(jpaMercadoPagoQrRepository.save(null)).thenReturn(null);
        when(jpaMapper.jpaMercadopagoQrToDTO(null)).thenReturn(null);

        PaymentMercadopagoQrDTO result = repository.save(null);

        assertNull(result);
        verify(jpaMapper).mercadopagoQrDtoToJpa(null);
        verify(jpaMercadoPagoQrRepository).save(null);
        verify(jpaMapper).jpaMercadopagoQrToDTO(null);
    }

    @Test
    void findByIdReturnsPaymentWhenExists() {
        Integer paymentId = 1;
        JpaMercadopagoQrEntity entity = createEntity();
        PaymentMercadopagoQrDTO expectedDTO = createPaymentDTO();

        when(jpaMercadoPagoQrRepository.findById(paymentId)).thenReturn(Optional.of(entity));
        when(jpaMapper.jpaMercadopagoQrToDTO(entity)).thenReturn(expectedDTO);

        PaymentMercadopagoQrDTO result = repository.findById(paymentId);

        assertEquals(expectedDTO, result);
        verify(jpaMercadoPagoQrRepository).findById(paymentId);
        verify(jpaMapper).jpaMercadopagoQrToDTO(entity);
    }

    @Test
    void findByIdReturnsNullWhenNotExists() {
        Integer paymentId = 999;

        when(jpaMercadoPagoQrRepository.findById(paymentId)).thenReturn(Optional.empty());
        when(jpaMapper.jpaMercadopagoQrToDTO(null)).thenReturn(null);

        PaymentMercadopagoQrDTO result = repository.findById(paymentId);

        assertNull(result);
        verify(jpaMercadoPagoQrRepository).findById(paymentId);
        verify(jpaMapper).jpaMercadopagoQrToDTO(null);
    }

    @Test
    void findByCustomerOrderIdReturnsPaymentWhenExists() {
        Integer customerOrderId = 100;
        JpaMercadopagoQrEntity entity = createEntity();
        PaymentMercadopagoQrDTO expectedDTO = createPaymentDTO();

        when(jpaMercadoPagoQrRepository.findByCustomerOrderId(customerOrderId)).thenReturn(Optional.of(entity));
        when(jpaMapper.jpaMercadopagoQrToDTO(entity)).thenReturn(expectedDTO);

        PaymentMercadopagoQrDTO result = repository.findByCustomerOrderId(customerOrderId);

        assertEquals(expectedDTO, result);
        verify(jpaMercadoPagoQrRepository).findByCustomerOrderId(customerOrderId);
        verify(jpaMapper).jpaMercadopagoQrToDTO(entity);
    }

    @Test
    void findByCustomerOrderIdReturnsNullWhenNotExists() {
        Integer customerOrderId = 999;

        when(jpaMercadoPagoQrRepository.findByCustomerOrderId(customerOrderId)).thenReturn(Optional.empty());
        when(jpaMapper.jpaMercadopagoQrToDTO(null)).thenReturn(null);

        PaymentMercadopagoQrDTO result = repository.findByCustomerOrderId(customerOrderId);

        assertNull(result);
        verify(jpaMercadoPagoQrRepository).findByCustomerOrderId(customerOrderId);
        verify(jpaMapper).jpaMercadopagoQrToDTO(null);
    }

    @Test
    void findByStatusListReturnsPaymentListWhenExists() {
        List<Integer> statusIds = Arrays.asList(1, 2, 3);
        List<JpaMercadopagoQrEntity> entities = Arrays.asList(createEntity(), createEntity());
        PaymentMercadopagoQrDTO dto1 = createPaymentDTO();
        PaymentMercadopagoQrDTO dto2 = createPaymentDTO();

        when(jpaMercadoPagoQrRepository.findByStatusList(statusIds)).thenReturn(entities);
        when(jpaMapper.jpaMercadopagoQrToDTO(entities.get(0))).thenReturn(dto1);
        when(jpaMapper.jpaMercadopagoQrToDTO(entities.get(1))).thenReturn(dto2);

        List<PaymentMercadopagoQrDTO> result = repository.findByStatusList(statusIds);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(jpaMercadoPagoQrRepository).findByStatusList(statusIds);
        verify(jpaMapper, times(2)).jpaMercadopagoQrToDTO(any(JpaMercadopagoQrEntity.class));
    }

    @Test
    void findByStatusListReturnsEmptyListWhenNoResults() {
        List<Integer> statusIds = List.of(999);
        List<JpaMercadopagoQrEntity> emptyList = Collections.emptyList();

        when(jpaMercadoPagoQrRepository.findByStatusList(statusIds)).thenReturn(emptyList);

        List<PaymentMercadopagoQrDTO> result = repository.findByStatusList(statusIds);

        assertTrue(result.isEmpty());
        verify(jpaMercadoPagoQrRepository).findByStatusList(statusIds);
        verify(jpaMapper, never()).jpaMercadopagoQrToDTO(any());
    }

    @Test
    void findByStatusListWithNullList() {
        when(jpaMercadoPagoQrRepository.findByStatusList(null)).thenReturn(Collections.emptyList());

        List<PaymentMercadopagoQrDTO> result = repository.findByStatusList(null);

        assertTrue(result.isEmpty());
        verify(jpaMercadoPagoQrRepository).findByStatusList(null);
    }

    @Test
    void findByStatusListWithEmptyList() {
        List<Integer> emptyStatusIds = Collections.emptyList();
        when(jpaMercadoPagoQrRepository.findByStatusList(emptyStatusIds)).thenReturn(Collections.emptyList());

        List<PaymentMercadopagoQrDTO> result = repository.findByStatusList(emptyStatusIds);

        assertTrue(result.isEmpty());
        verify(jpaMercadoPagoQrRepository).findByStatusList(emptyStatusIds);
    }

    private PaymentMercadopagoQrDTO createPaymentDTO() {
        return new PaymentMercadopagoQrDTO.Builder()
                .id(1)
                .orderId(100)
                .status("Aprovado")
                .amount(50.0)
                .paymentProvider("mercadopago")
                .paymentMethod("qrcode")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .externalPaymentId("ext123")
                .qrData("qrdata123")
                .meliId("meli123")
                .build();
    }

    private JpaMercadopagoQrEntity createEntity() {
        return new JpaMercadopagoQrEntity(1, 100, 2, 50.0, "mercadopago", "qrcode",
                "ext123", LocalDateTime.now(), LocalDateTime.now(), "meli123", "qrdata123");
    }
}
