
  ALTER TABLE `sql_store`.`cart_items`
 ADD constraint cart_items_cart_product_unique
      unique (cart_id, product_id),
  ADD CONSTRAINT fk_cartitems_cart
    FOREIGN KEY (`cart_id`)
    REFERENCES `sql_store`.`cart` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;