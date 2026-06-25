package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.*;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OrderService
{
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ShoppingCartService shoppingCartService;
    private final ProfileService profileService;

    public OrderService(OrderRepository orderRepository,
                        OrderLineItemRepository orderLineItemRepository,
                        ShoppingCartService shoppingCartService,
                        ProfileService profileService)
    {
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.shoppingCartService = shoppingCartService;
        this.profileService = profileService;
    }

    public Order checkout(int userId)
    {
        // get the user's cart
        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        // get the user's profile for address info
        Profile profile = profileService.getByUserId(userId);

        // create and save the order
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        order.setShippingAmount(0);

        if (profile != null)
        {
            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());
        }
        else
        {
            order.setAddress("");
            order.setCity("");
            order.setState("");
            order.setZip("");
        }

        Order savedOrder = orderRepository.save(order);

        // create one line item per product in the cart
        Map<Integer, ShoppingCartItem> items = cart.getItems();

        for (ShoppingCartItem cartItem : items.values())
        {
            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrderId(savedOrder.getOrderId());
            lineItem.setProductId(cartItem.getProductId());
            lineItem.setSalesPrice(cartItem.getProduct().getPrice());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setDiscount(cartItem.getDiscountPercent());
            orderLineItemRepository.save(lineItem);
        }

        // clear the cart after checkout
        shoppingCartService.clearCart(userId);

        return savedOrder;
    }
}