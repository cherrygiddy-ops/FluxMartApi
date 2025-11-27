-- V2__insert_toys_games.sql
-- Seed 10 sample products under category_id = 7 (Toys & Games)

INSERT INTO products (name, descriptions, unit_price, category_id, image_url, quantity_in_stock) VALUES
('Lego Classic Bricks Set', 'Creative building blocks for kids ages 4+', 29.99, 7, '/images/lego-classic.jpg', 50),
('Monopoly Board Game', 'Classic family board game with updated tokens', 24.50, 7, '/images/monopoly.jpg', 40),
('Rubiks Cube 3x3', 'Original puzzle cube for brain training', 9.99, 7, '/images/rubiks-cube.jpg', 100),
('Hot Wheels Car Pack', 'Set of 5 die-cast cars for racing fun', 12.99, 7, '/images/hotwheels-pack.jpg', 75),
('UNO Card Game', 'Fast-paced card game for family and friends', 7.50, 7, '/images/uno.jpg', 120),
('Jenga Classic', 'Stacking wooden blocks game for all ages', 15.00, 7, '/images/jenga.jpg', 60),
('Nerf Elite Blaster', 'Foam dart blaster with 12 darts included', 19.99, 7, '/images/nerf-blaster.jpg', 35),
('Barbie Dream Doll', 'Fashion doll with accessories', 16.99, 7, '/images/barbie.jpg', 55),
('Chess Set Wooden', 'Portable wooden chess set with storage box', 22.00, 7, '/images/chess-set.jpg', 25),
('Play-Doh Starter Pack', 'Colorful modeling clay set for creativity', 10.99, 7, '/images/playdoh.jpg', 80);


-- V3__insert_food_products.sql
-- Seed 10 sample products under category_id = 8 (Food)

INSERT INTO products (name, descriptions, unit_price, category_id, image_url, quantity_in_stock) VALUES
('Organic Apples Pack', 'Fresh organic apples, 1kg pack', 3.99, 8, '/images/apples.jpg', 200),
('Whole Wheat Bread', 'Healthy whole wheat bread loaf', 2.50, 8, '/images/bread.jpg', 150),
('Premium Basmati Rice', 'Long grain aromatic basmati rice, 5kg bag', 12.99, 8, '/images/rice.jpg', 90),
('Olive Oil Extra Virgin', 'Cold-pressed extra virgin olive oil, 1L', 9.99, 8, '/images/olive-oil.jpg', 60),
('Fresh Milk 1L', 'Pasteurized whole milk, 1L carton', 1.20, 8, '/images/milk.jpg', 300),
('Free Range Eggs', 'Pack of 12 free range eggs', 4.50, 8, '/images/eggs.jpg', 180),
('Cheddar Cheese Block', 'Mature cheddar cheese, 500g block', 5.99, 8, '/images/cheese.jpg', 70),
('Spaghetti Pasta', 'Italian durum wheat spaghetti, 1kg pack', 2.80, 8, '/images/spaghetti.jpg', 110),
('Orange Juice', '100% pure squeezed orange juice, 1L bottle', 3.50, 8, '/images/orange-juice.jpg', 95),
('Dark Chocolate Bar', 'Premium dark chocolate, 70% cocoa, 100g bar', 2.20, 8, '/images/chocolate.jpg', 130);