package br.com.tp.lncr.payment.datasources.postgres.mercadopago;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface JpaMercadoPagoQrRepository extends JpaRepository<JpaMercadopagoQrEntity, Integer> {

    @Query(value = "select * from payment p, payment_mercadopago mp where p.id = mp.id and p.order_id = :customerOrderId", nativeQuery = true)
    Optional<JpaMercadopagoQrEntity> findByCustomerOrderId(@Param("customerOrderId") Integer customerOrderId);

    @Query(value = "select * from payment p, payment_mercadopago mp where p.id = mp.id and p.status_id in(:paymentStatusIdList)", nativeQuery = true)
    List<JpaMercadopagoQrEntity> findByStatusList(@Param("paymentStatusIdList") List<Integer> paymentStatusIdList);
}
