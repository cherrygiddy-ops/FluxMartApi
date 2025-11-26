package com.fluxmartApi.order;

import com.fluxmartApi.cart.CartEntity;
import com.fluxmartApi.payments.PaymentMethod;
import com.fluxmartApi.users.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "orders")
@Entity
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @JoinColumn(name = "customer_id")
    @ManyToOne
    private UserEntity customer;

    @Column(name = "order_date",insertable = false,updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "delivery_status")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Column(name = "comments")
    private String comments;

    @Column(name = "shipped_date")
    private Date shippedDate;

    @Column(name = "total_price")
    private BigDecimal totalPrice;


    @JoinColumn(name = "shipper_id")
    @OneToOne()
    private Shipper shipper;


    @JoinColumn(name = "payment_method")
    @OneToOne()
    private PaymentMethod paymentMethod;

    @JoinColumn(name = "cart_id")
    @ManyToOne
    private CartEntity cart;

    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.REMOVE} ,mappedBy = "order")
    private Set<OrderItemsEntity> orderItems = new LinkedHashSet<>();

    public void addItems(OrderItemsEntity item){
        orderItems.add(item);
        item.setOrder(this);
    }

    public static OrderEntity createOrder(CartEntity cart,UserEntity customer,String phoneNumber){
        var order = new OrderEntity();
        order.setCart(cart);
        order.setComments("order 1");
        order.setTotalPrice(cart.getTotalPrice());
        order.setCustomer(customer);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setDeliveryStatus(DeliveryStatus.PROCESSING);

        cart.getItems().stream().forEach(cartI -> {
                    var orderItem = new OrderItemsEntity(order,cartI.getProduct(),cartI.getQuantity());
                    order.addItems(orderItem);
                }
        );

        return order;
    }



}
