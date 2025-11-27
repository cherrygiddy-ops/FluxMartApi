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