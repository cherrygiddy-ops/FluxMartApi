-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema sql_store
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema sql_store
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `sql_store` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `sql_store` ;

-- -----------------------------------------------------
-- Table `sql_store`.`b2c_c2b_entries`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`b2c_c2b_entries` (
  `internal_id` INT NOT NULL AUTO_INCREMENT,
  `transaction_type` VARCHAR(45) NULL DEFAULT NULL,
  `transaction_id` VARCHAR(45) NULL DEFAULT NULL,
  `bill_ref_number` VARCHAR(45) NULL DEFAULT NULL,
  `msisdn` VARCHAR(45) NULL DEFAULT NULL,
  `amount` DECIMAL(10,2) NULL DEFAULT NULL,
  `conversation_id` VARCHAR(45) NULL DEFAULT NULL,
  `originator_conversation_id` VARCHAR(45) NULL DEFAULT NULL,
  `entry_date` DATE NULL DEFAULT NULL,
  `result_code` VARCHAR(45) NULL DEFAULT NULL,
  `raw_callback_payload_response` JSON NULL DEFAULT NULL,
  PRIMARY KEY (`internal_id`),
  UNIQUE INDEX `transaction_id_UNIQUE` (`transaction_id` ASC) VISIBLE,
  UNIQUE INDEX `originator_conversation_id_UNIQUE` (`originator_conversation_id` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 63
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`cart`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`cart` (
  `id` BINARY(16) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`products`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`products` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `descriptions` VARCHAR(255) NULL DEFAULT NULL,
  `quantity_in_stock` INT NOT NULL,
  `unit_price` DECIMAL(9,2) NOT NULL,
  `category_id` TINYINT NULL DEFAULT NULL,
  `image_url` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_categoryId_idx` (`category_id` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 264
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`cart_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`cart_item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cart_id` BINARY(16) NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `cart_items_cart_product_unique` (`cart_id` ASC, `product_id` ASC) VISIBLE,
  INDEX `fk_product_cartItems_idx` (`product_id` ASC) VISIBLE,
  CONSTRAINT `fk_cartitems_cart`
    FOREIGN KEY (`cart_id`)
    REFERENCES `sql_store`.`cart` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_product_cartItems`
    FOREIGN KEY (`product_id`)
    REFERENCES `sql_store`.`products` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 309
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`categories` (
  `id` TINYINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`flyway_schema_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`flyway_schema_history` (
  `installed_rank` INT NOT NULL,
  `version` VARCHAR(50) NULL DEFAULT NULL,
  `description` VARCHAR(200) NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `script` VARCHAR(1000) NOT NULL,
  `checksum` INT NULL DEFAULT NULL,
  `installed_by` VARCHAR(100) NOT NULL,
  `installed_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` INT NOT NULL,
  `success` TINYINT(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  INDEX `flyway_schema_history_s_idx` (`success` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`order_item_notes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`order_item_notes` (
  `note_id` INT NOT NULL,
  `order_Id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `note` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`note_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`shippers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`shippers` (
  `shipper_id` SMALLINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`shipper_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(45) NOT NULL,
  `is_verified` TINYINT NULL DEFAULT '1',
  `verification_token` VARCHAR(45) NULL DEFAULT NULL,
  `token_expiry` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 13
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`payment_method`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`payment_method` (
  `payment_method_id` TINYINT NOT NULL,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`payment_method_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`orders` (
  `order_id` INT NOT NULL AUTO_INCREMENT,
  `customer_id` INT NOT NULL,
  `order_date` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `payment_status` VARCHAR(50) NULL DEFAULT NULL,
  `comments` VARCHAR(2000) NULL DEFAULT NULL,
  `shipped_date` DATE NULL DEFAULT NULL,
  `shipper_id` SMALLINT NULL DEFAULT NULL,
  `total_price` DECIMAL(10,2) NULL DEFAULT NULL,
  `payment_method` TINYINT NULL DEFAULT NULL,
  `cart_id` BINARY(16) NULL DEFAULT NULL,
  `delivery_status` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  INDEX `fk_orders_shippers_idx` (`shipper_id` ASC) VISIBLE,
  INDEX `fk_payment_method_idx` (`payment_method` ASC) VISIBLE,
  INDEX `fk_users_orders_idx` (`customer_id` ASC) VISIBLE,
  CONSTRAINT `fk_orders_shippers`
    FOREIGN KEY (`shipper_id`)
    REFERENCES `sql_store`.`shippers` (`shipper_id`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_orders_users`
    FOREIGN KEY (`customer_id`)
    REFERENCES `sql_store`.`users` (`id`)
    ON DELETE RESTRICT,
  CONSTRAINT `fk_payment_method`
    FOREIGN KEY (`payment_method`)
    REFERENCES `sql_store`.`payment_method` (`payment_method_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 98
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`order_items`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`order_items` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `order_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `order_item_notes` INT NOT NULL DEFAULT '1',
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_order_items_products_idx` (`product_id` ASC) VISIBLE,
  INDEX `fk_order_items_order_item_notes1_idx` (`order_item_notes` ASC) VISIBLE,
  INDEX `fk_order_items_orders_idx` (`order_id` ASC) VISIBLE,
  CONSTRAINT `fk_order_items_notes`
    FOREIGN KEY (`order_item_notes`)
    REFERENCES `sql_store`.`order_item_notes` (`note_id`),
  CONSTRAINT `fk_order_items_orders`
    FOREIGN KEY (`order_id`)
    REFERENCES `sql_store`.`orders` (`order_id`),
  CONSTRAINT `fk_order_items_products`
    FOREIGN KEY (`product_id`)
    REFERENCES `sql_store`.`products` (`id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 162
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`order_status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`order_status` (
  `order_status_id` TINYINT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`order_status_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`profile`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`profile` (
  `customer_id` INT NOT NULL,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `birth_date` DATE NULL DEFAULT NULL,
  `phone` VARCHAR(50) NULL DEFAULT NULL,
  `address` VARCHAR(50) NOT NULL,
  `city` VARCHAR(50) NOT NULL,
  `state` CHAR(2) NOT NULL,
  `points` INT NOT NULL DEFAULT '0',
  PRIMARY KEY (`customer_id`),
  INDEX `idx_state_points` (`state` ASC, `points` ASC) VISIBLE,
  INDEX `idx_state_last_name` (`state` ASC, `last_name` ASC) VISIBLE,
  INDEX `idx_points` (`points` ASC) VISIBLE,
  INDEX `fk_users_profile_idx` (`customer_id` ASC) VISIBLE,
  CONSTRAINT `fk_users_profile`
    FOREIGN KEY (`customer_id`)
    REFERENCES `sql_store`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`stk_push_entries`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`stk_push_entries` (
  `internal_id` INT NOT NULL AUTO_INCREMENT,
  `transaction_id` VARCHAR(45) NULL DEFAULT NULL,
  `transaction_type` VARCHAR(45) NULL DEFAULT NULL,
  `msisdn` VARCHAR(45) NULL DEFAULT NULL,
  `amount` DECIMAL(10,2) NULL DEFAULT NULL,
  `merchant_request_id` VARCHAR(45) NULL DEFAULT NULL,
  `checkout_request_id` VARCHAR(45) NULL DEFAULT NULL,
  `entry_date` DATE NULL DEFAULT NULL,
  `result_code` VARCHAR(45) NULL DEFAULT NULL,
  `result_desc` VARCHAR(255) NULL DEFAULT NULL,
  `mpesa_receipt_number` VARCHAR(45) NULL DEFAULT NULL,
  `raw_callback_payload_response` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`internal_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 23
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `sql_store`.`transactions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sql_store`.`transactions` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `transaction_id` VARCHAR(55) NULL DEFAULT NULL,
  `payment_type` VARCHAR(45) NULL DEFAULT NULL,
  `order_id` INT NOT NULL,
  `transaction_date` DATE NULL DEFAULT NULL,
  `amount` DECIMAL(10,2) NULL DEFAULT NULL,
  `total_amount` DECIMAL(10,2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `transaction_id_UNIQUE` (`transaction_id` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 15
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
