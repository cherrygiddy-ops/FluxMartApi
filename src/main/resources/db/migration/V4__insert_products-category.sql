INSERT INTO sql_store.categories (id, name) VALUES
(1, 'Electronics'),
(2, 'Fashion'),
(3, 'Home & Kitchen'),
(4, 'Sports & Outdoors'),
(5, 'Books'),
(6, 'Beauty & Personal Care'),
(7, 'Toys & Games');

INSERT INTO sql_store.products (name, descriptions, quantity_in_stock, unit_price, category_id, image_url) VALUES
('Smartphone X1', '6.5-inch display, 128GB storage', 50, 699.99, 1, 'images/electronics/smartphone_x1.jpg'),
('Laptop Pro 15', '15-inch, 16GB RAM, 512GB SSD', 30, 1199.00, 1, 'images/electronics/laptop_pro15.jpg'),
('Wireless Earbuds', 'Noise cancelling, Bluetooth 5.2', 100, 129.99, 1, 'images/electronics/earbuds.jpg'),
('Smartwatch Z', 'Fitness tracking, heart rate monitor', 75, 199.99, 1, 'images/electronics/smartwatch_z.jpg'),
('Gaming Console', 'Next-gen console with 1TB storage', 40, 499.99, 1, 'images/electronics/console.jpg'),
('Bluetooth Speaker', 'Portable, waterproof', 120, 89.99, 1, 'images/electronics/speaker.jpg'),
('4K TV 55"', 'Ultra HD Smart TV', 25, 699.00, 1, 'images/electronics/tv55.jpg'),
('Digital Camera', '24MP DSLR with lens kit', 15, 899.00, 1, 'images/electronics/camera.jpg'),
('External SSD 1TB', 'High-speed portable drive', 60, 149.99, 1, 'images/electronics/ssd1tb.jpg'),
('Wireless Keyboard', 'Ergonomic, rechargeable', 80, 59.99, 1, 'images/electronics/keyboard.jpg');

 INSERT INTO sql_store.products (name, descriptions, quantity_in_stock, unit_price, category_id, image_url) VALUES
 ('Men T-Shirt', 'Cotton, slim fit', 200, 19.99, 2, 'images/fashion/tshirt_men.jpg'),
 ('Women Dress', 'Floral summer dress', 150, 49.99, 2, 'images/fashion/dress_women.jpg'),
 ('Sneakers', 'Unisex running shoes', 100, 79.99, 2, 'images/fashion/sneakers.jpg'),
 ('Leather Jacket', 'Classic biker style', 40, 149.99, 2, 'images/fashion/jacket.jpg'),
 ('Jeans', 'Denim slim fit', 120, 59.99, 2, 'images/fashion/jeans.jpg'),
 ('Handbag', 'Leather tote bag', 60, 129.99, 2, 'images/fashion/handbag.jpg'),
 ('Cap', 'Adjustable baseball cap', 180, 15.99, 2, 'images/fashion/cap.jpg'),
 ('Scarf', 'Wool winter scarf', 90, 29.99, 2, 'images/fashion/scarf.jpg'),
 ('Sunglasses', 'UV protection stylish shades', 70, 89.99, 2, 'images/fashion/sunglasses.jpg'),
 ('Watch', 'Analog wristwatch', 50, 199.99, 2, 'images/fashion/watch.jpg');

 INSERT INTO sql_store.products (name, descriptions, quantity_in_stock, unit_price, category_id, image_url) VALUES
 ('Blender', 'High-speed smoothie blender', 60, 99.99, 3, 'images/home/blender.jpg'),
 ('Microwave Oven', '1000W digital microwave', 40, 149.99, 3, 'images/home/microwave.jpg'),
 ('Coffee Maker', '12-cup programmable machine', 70, 89.99, 3, 'images/home/coffeemaker.jpg'),
 ('Vacuum Cleaner', 'Bagless, powerful suction', 50, 199.99, 3, 'images/home/vacuum.jpg'),
 ('Air Fryer', 'Oil-free cooking', 80, 129.99, 3, 'images/home/airfryer.jpg'),
 ('Cookware Set', '10-piece non-stick set', 30, 179.99, 3, 'images/home/cookware.jpg'),
 ('Electric Kettle', '1.7L stainless steel', 100, 39.99, 3, 'images/home/kettle.jpg'),
 ('Rice Cooker', 'Multi-function cooker', 60, 59.99, 3, 'images/home/ricecooker.jpg'),
 ('Iron', 'Steam iron with ceramic soleplate', 90, 49.99, 3, 'images/home/iron.jpg'),
 ('Toaster', '4-slice toaster', 70, 69.99, 3, 'images/home/toaster.jpg');

 INSERT INTO sql_store.products (name, descriptions, quantity_in_stock, unit_price, category_id, image_url) VALUES
 ('Football', 'Official size 5', 100, 29.99, 4, 'images/sports/football.jpg'),
 ('Basketball', 'Indoor/outdoor use', 80, 34.99, 4, 'images/sports/basketball.jpg'),
 ('Tennis Racket', 'Lightweight graphite', 40, 89.99, 4, 'images/sports/racket.jpg'),
 ('Yoga Mat', 'Non-slip, 6mm thick', 120, 25.99, 4, 'images/sports/yogamat.jpg'),
 ('Camping Tent', '4-person waterproof tent', 30, 199.99, 4, 'images/sports/tent.jpg'),
 ('Hiking Backpack', '50L capacity', 50, 129.99, 4, 'images/sports/backpack.jpg'),
 ('Running Shoes', 'Breathable, cushioned sole', 70, 99.99, 4, 'images/sports/runningshoes.jpg'),
 ('Dumbbell Set', 'Adjustable weights', 60, 149.99, 4, 'images/sports/dumbbells.jpg'),
 ('Bicycle', 'Mountain bike, 21-speed', 25, 499.99, 4, 'images/sports/bicycle.jpg'),
 ('Water Bottle', 'Insulated stainless steel', 150, 19.99, 4, 'images/sports/bottle.jpg');

 INSERT INTO sql_store.products (name, descriptions, quantity_in_stock, unit_price, category_id, image_url) VALUES
 ('Novel A', 'Bestselling fiction', 200, 14.99, 5, 'images/books/novel_a.jpg'),
 ('Novel B', 'Romantic drama', 150, 12.99, 5, 'images/books/novel_b.jpg'),
 ('Science Book', 'Popular science title', 100, 24.99, 5, 'images/books/science.jpg'),
 ('History Book', 'World history overview', 80, 19.99, 5, 'images/books/history.jpg'),
 ('Children Storybook', 'Illustrated kids book', 120, 9.99, 5, 'images/books/children.jpg'),
 ('Cookbook', '100 easy recipes', 90, 29.99, 5, 'images/books/cookbook.jpg'),
 ('Business Book', 'Entrepreneurship guide', 70, 21.99, 5, 'images/books/business.jpg'),
 ('Self-help Book', 'Motivational bestseller', 110, 18.99, 5, 'images/books/selfhelp.jpg'),
 ('Fantasy Novel', 'Epic adventure series', 60, 22.99, 5, 'images/books/fantasy.jpg'),
 ('Biography', 'Life story of famous figure', 50, 25.99, 5, 'images/books/biography.jpg');

 INSERT INTO sql_store.products (name, descriptions, quantity_in_stock, unit_price, category_id, image_url) VALUES
 ('Shampoo', 'Moisturizing formula', 150, 12.99, 6, 'images/beauty/shampoo.jpg'),
 ('Conditioner', 'Strengthening hair care', 140, 13.99, 6, 'images/beauty/conditioner.jpg'),
 ('Face Cream', 'Hydrating moisturizer', 100, 29.99, 6, 'images/beauty/facecream.jpg'),
 ('Perfume', 'Floral fragrance', 80, 59.99, 6, 'images/beauty/perfume.jpg'),
 ('Lipstick', 'Matte red shade', 120, 19.99, 6, 'images/