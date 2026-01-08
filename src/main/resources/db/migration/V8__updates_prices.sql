-- Convert unit_price from USD to KES and remove decimals
UPDATE sql_store.products
SET unit_price = ROUND(unit_price / 160*4, 0);
