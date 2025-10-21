CREATE TABLE `sql_store`.`cart` (
  `id` BINARY(16) NOT NULL DEFAULT (uuid_to_bin(uuid())),
  `createdAt` DATE NOT NULL DEFAULT (curdate()),
  PRIMARY KEY (`id`));