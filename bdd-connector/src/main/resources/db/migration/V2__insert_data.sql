-- Insert 10 customers
INSERT INTO customers (name, email, address) VALUES
('Luke Skywalker', 'luke.skywalker@rebellion.com', 'Tatooine'),
('Leia Organa', 'leia.organa@rebellion.com', 'Alderaan'),
('Han Solo', 'han.solo@rebellion.com', 'Corellia'),
('Darth Vader', 'darth.vader@empire.com', 'Death Star'),
('Yoda', 'yoda@jedi-temple.com', 'Dagobah'),
('Obi-Wan Kenobi', 'obi-wan.kenobi@jedi-temple.com', 'Tatooine'),
('Rey Skywalker', 'rey.skywalker@resistance.com', 'Jakku'),
('Kylo Ren', 'kylo.ren@firstorder.com', 'Starkiller Base'),
('Din Djarin', 'din.djarin@mandalore.com', 'Nevarro'),
('Grogu', 'grogu@mandalore.com', 'Nevarro');

-- Insert 30 products (6 categories x 5)

-- Lightsabers
INSERT INTO products (name, description, price, category) VALUES
('Sabre Laser Bleu de Luke Skywalker', 'Réplique du sabre laser bleu emblématique de Luke Skywalker, hérité de son père Anakin.', 29999.00, 'Lightsabers'),
('Sabre Laser Rouge de Darth Vader', 'Réplique du sabre laser rouge de Darth Vader, symbole de la puissance obscure.', 34999.00, 'Lightsabers'),
('Sabre Laser Vert de Yoda', 'Réplique du légendaire sabre laser vert du Maître Yoda, témoin de mille batailles.', 39999.00, 'Lightsabers'),
('Sabre Laser Violet de Mace Windu', 'Réplique du sabre laser violet unique de Mace Windu, membre éminent du Conseil Jedi.', 44999.00, 'Lightsabers'),
('Sabre Laser Croisé de Kylo Ren', 'Réplique du sabre laser croisé instable de Kylo Ren, à la puissance brute et sauvage.', 37999.00, 'Lightsabers');

-- Blasters
INSERT INTO products (name, description, price, category) VALUES
('DL-44 Heavy Blaster (Han Solo)', 'Réplique du blaster lourd DL-44 d''Han Solo, l''arme préférée du contrebandier de la galaxie.', 15000.00, 'Blasters'),
('E-11 Blaster Rifle (Stormtrooper)', 'Réplique du fusil blaster E-11 standard des Stormtroopers de l''Empire Galactique.', 12000.00, 'Blasters'),
('DC-15A Clone Blaster', 'Réplique du blaster de clone DC-15A utilisé par les soldats de la République lors des Guerres des Clones.', 14000.00, 'Blasters'),
('WESTAR-35 Blaster (Mandalorian)', 'Réplique du blaster WESTAR-35 du Mandalorien Din Djarin, forgé dans la tradition mandalore.', 16000.00, 'Blasters'),
('EE-3 Carbine (Boba Fett)', 'Réplique de la carabine EE-3 de Boba Fett, chasseur de primes légendaire.', 18000.00, 'Blasters');

-- Vaisseaux & Véhicules - Modèles réduits
INSERT INTO products (name, description, price, category) VALUES
('Millennium Falcon Model Kit', 'Maquette détaillée du Millennium Falcon, le vaisseau le plus rapide de la galaxie.', 8999.00, 'Modeles Reduits'),
('X-Wing Starfighter Model', 'Maquette du chasseur X-Wing de l''Alliance Rebelle, symbole de la résistance.', 6999.00, 'Modeles Reduits'),
('TIE Fighter Model', 'Maquette du chasseur TIE Fighter impérial, emblème de la puissance de l''Empire.', 5999.00, 'Modeles Reduits'),
('Death Star Model Kit', 'Maquette de l''Étoile de la Mort, l''arme ultime de destruction massive de l''Empire.', 12999.00, 'Modeles Reduits'),
('AT-AT Walker Model', 'Maquette du marcheur AT-AT impérial, redoutable engin de guerre terrestre.', 9999.00, 'Modeles Reduits');

-- Objets de la Force
INSERT INTO products (name, description, price, category) VALUES
('Holocron Jedi', 'Réplique d''un Holocron Jedi contenant les enseignements des anciens Maîtres de la Force.', 22000.00, 'Objets Force'),
('Holocron Sith', 'Réplique d''un Holocron Sith renfermant les secrets du côté obscur de la Force.', 25000.00, 'Objets Force'),
('Cristal Kyber Bleu', 'Réplique d''un cristal Kyber bleu, cœur énergétique d''un sabre laser Jedi.', 5000.00, 'Objets Force'),
('Cristal Kyber Rouge', 'Réplique d''un cristal Kyber rouge, saigné par un Sith pour canaliser le côté obscur.', 5500.00, 'Objets Force'),
('Droïde d''Entraînement Jedi Remote', 'Réplique du droïde d''entraînement utilisé par les Jedi pour perfectionner leur maîtrise de la Force.', 3500.00, 'Objets Force');

-- Vêtements
INSERT INTO products (name, description, price, category) VALUES
('Tenue de Jedi complète', 'Tenue complète de Jedi incluant tunique, ceinture et cape, fidèle aux films.', 7500.00, 'Vetements'),
('Cape Sith Noire', 'Cape noire de Sith Lord, symbole d''autorité et de puissance du côté obscur.', 6500.00, 'Vetements'),
('Veste Alliance Rebelle', 'Veste officielle des pilotes de l''Alliance Rebelle, portée lors de la Bataille de Yavin.', 4500.00, 'Vetements'),
('Uniforme d''Officier Impérial', 'Uniforme complet d''officier de l''Empire Galactique, avec grades et insignes.', 5000.00, 'Vetements'),
('Réplique Armure Mandalorienne', 'Réplique de l''armure beskar du Mandalorien, forgée dans l''acier mandalorien le plus pur.', 19999.00, 'Vetements');

-- Figurines & Peluches
INSERT INTO products (name, description, price, category) VALUES
('Figurine Luke Skywalker', 'Figurine articulée haute qualité de Luke Skywalker avec son sabre laser et accessoires.', 3500.00, 'Figurines'),
('Figurine Darth Vader', 'Figurine articulée de Darth Vader avec cape et sabre laser rouge, très détaillée.', 3500.00, 'Figurines'),
('Peluche Grogu (Baby Yoda)', 'Adorable peluche de Grogu, alias Baby Yoda, ultra-douce et câline.', 4500.00, 'Figurines'),
('Figurine Yoda', 'Figurine du Maître Yoda en position méditative, avec son sabre laser vert.', 3000.00, 'Figurines'),
('Droïde R2-D2 Interactif', 'Droïde R2-D2 interactif avec sons, lumières et mouvements, télécommandable.', 12500.00, 'Figurines');

-- Generate 50 orders with random data
DO $$
DECLARE
    i INTEGER;
    customer_id INTEGER;
    order_id INTEGER;
    order_date TIMESTAMP;
    status VARCHAR(20);
    num_lines INTEGER;
    j INTEGER;
    product_id INTEGER;
    quantity INTEGER;
    total DECIMAL(10,2);
    statuses VARCHAR(20)[] := ARRAY['IN_PROGRESS', 'DELIVERED', 'CANCELLED'];
BEGIN
    FOR i IN 1..50 LOOP
        -- Random customer from existing customers
        SELECT id INTO customer_id FROM customers ORDER BY random() LIMIT 1;

        -- Random date in the last 2 years
        order_date := NOW() - (random() * 730 || ' days')::INTERVAL;

        -- Random status
        status := statuses[floor(random() * 3 + 1)::INTEGER];

        -- Insert order
        INSERT INTO orders (order_date, customer_id, status, total_amount)
        VALUES (order_date, customer_id, status, 0)
        RETURNING id INTO order_id;

        -- Random number of lines (1-5)
        num_lines := floor(random() * 5 + 1)::INTEGER;
        total := 0;

        FOR j IN 1..num_lines LOOP
            -- Random product from existing products
            SELECT id INTO product_id FROM products ORDER BY random() LIMIT 1;

            -- Random quantity (1-5)
            quantity := floor(random() * 5 + 1)::INTEGER;

            -- Insert order line
            INSERT INTO order_lines (order_id, product_id, quantity, unit_price)
            SELECT order_id, product_id, quantity, p.price
            FROM products p WHERE p.id = product_id;

            -- Update total
            SELECT total + (p.price * quantity) INTO total
            FROM products p WHERE p.id = product_id;
        END LOOP;

        -- Update order total
        UPDATE orders SET total_amount = total WHERE id = order_id;
    END LOOP;
END $$;
