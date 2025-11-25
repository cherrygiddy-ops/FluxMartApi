package com.fluxmartApi.order;

import com.fluxmartApi.users.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity,Integer> {
    @EntityGraph(attributePaths = "orderItems.product")
    @Query("Select o from OrderEntity o where o.customer = :customer")
    List<OrderEntity> loadAllOrderForCustomerWithItems(@Param("customer") UserEntity customer);

    @Modifying
    @Transactional
    @Query("UPDATE OrderEntity o SET o.paymentStatus = :status WHERE o.orderId = :orderId")
    int updatePaymentStatus(@Param("orderId") Integer orderId,
                             @Param("status") PaymentStatus status);

}
