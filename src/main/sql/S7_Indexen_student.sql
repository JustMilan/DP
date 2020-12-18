-- ------------------------------------------------------------------------
-- Data & Persistency
-- Opdracht S7: Indexen
--
-- (c) 2020 Hogeschool Utrecht
-- Tijmen Muller (tijmen.muller@hu.nl)
-- André Donk (andre.donk@hu.nl)
-- ------------------------------------------------------------------------
-- LET OP, zoals in de opdracht op Canvas ook gezegd kun je informatie over
-- het query plan vinden op: https://www.postgresql.org/docs/current/using-explain.html


-- S7.1.
--
-- Je maakt alle opdrachten in de 'sales' database die je hebt aangemaakt en gevuld met
-- de aangeleverde data (zie de opdracht op Canvas).
--
-- Voer het voorbeeld uit wat in de les behandeld is:
-- 1. Voer het volgende EXPLAIN statement uit:
EXPLAIN
SELECT *
FROM order_lines
WHERE stock_item_id = 9;
--    Bekijk of je het resultaat begrijpt. Kopieer het explain plan onderaan de opdracht


-- Gather  (cost=1000.00..6150.97 rows=997 width=96)
--   Workers Planned: 2
--   ->  Parallel Seq Scan on order_lines  (cost=0.00..5051.27 rows=415 width=96)
--         Filter: (stock_item_id = 9)


-- 2. Voeg een index op stock_item_id toe:
CREATE INDEX ord_lines_si_id_idx ON order_lines (stock_item_id);
-- 3. Analyseer opnieuw met EXPLAIN hoe de query nu uitgevoerd wordt
--    Kopieer het explain plan onderaan de opdracht


-- Bitmap Heap Scan on order_lines  (cost=12.02..2287.20 rows=997 width=96)
--   Recheck Cond: (stock_item_id = 9)
--   ->  Bitmap Index Scan on ord_lines_si_id_idx  (cost=0.00..11.77 rows=997 width=0)
--         Index Cond: (stock_item_id = 9)


-- 4. Verklaar de verschillen. Schrijf deze hieronder op.

-- Hij hoeft doordat er een index is aangemaakt niet meer sequentieel door de alle rows heen te gaan totdat hij hem gevonden heeft.
-- Hij kijkt nu door de index gegenereerde tabel heen met directe referenties.


-- S7.2.
--
-- 1. Maak de volgende twee query’s:
-- 	  A. Toon uit de order tabel de order met order_id = 73590
-- 	  B. Toon uit de order tabel de order met customer_id = 1028
-- 2. Analyseer met EXPLAIN hoe de query’s uitgevoerd worden en kopieer het explain plan onderaan de opdracht
-- 3. Verklaar de verschillen en schrijf deze op
-- 4. Voeg een index toe, waarmee query B versneld kan worden
-- 5. Analyseer met EXPLAIN en kopieer het explain plan onder de opdracht
-- 6. Verklaar de verschillen en schrijf hieronder op

EXPLAIN
SELECT *
FROM orders
WHERE order_id = 73590;

-- Index Scan using pk_sales_orders on orders  (cost=0.29..8.31 rows=1 width=155)
--   Index Cond: (order_id = 73590)


EXPLAIN
SELECT *
FROM orders
WHERE customer_id = 1028;

-- Seq Scan on orders  (cost=0.00..1819.94 rows=107 width=155)
--   Filter: (customer_id = 1028)


CREATE INDEX query_b_accelerator ON orders (customer_id);
EXPLAIN
SELECT *
FROM orders
WHERE customer_id = 1028;
DROP INDEX query_b_accelerator;

-- Bitmap Heap Scan on orders  (cost=5.12..308.96 rows=107 width=155)
--   Recheck Cond: (customer_id = 1028)
--   ->  Bitmap Index Scan on query_b_accelerator  (cost=0.00..5.10 rows=107 width=0)
--         Index Cond: (customer_id = 1028)


-- Er zijn veel minder verschillende customer_id's dus de database kan hier sneller doorheen gaan dan door de order id's.
-- Hierdoor is zowel de opstart tijd als de totale tijd langer bij het zoeken op een order id.
-- Bij sequentieel opzoeken van customer id 1028 is er geen opstart tijd nodig, maar kost het vervolgens wel 1819.94 aan kosten
-- om de query te completen. De opstart tijd voor de bitmap heap scan op de orders van de index is 5.12 en kost in totaal 308.96, vervolgens
-- kost het daarna nog maar 5.10 om de gewenste customer id te vinden. Ook zien we dat het aantal rows hetzelfde blijft, maar dat de width van 155 bytes naar 0 is gegaan.


-- S7.3.A
--
-- Het blijkt dat customers regelmatig klagen over trage bezorging van hun bestelling.
-- Het idee is dat verkopers misschien te lang wachten met het invoeren van de bestelling in het systeem.
-- Daar willen we meer inzicht in krijgen.
-- We willen alle orders (order_id, order_date, salesperson_person_id (als verkoper),
--    het verschil tussen expected_delivery_date en order_date (als levertijd),  
--    en de bestelde hoeveelheid van een product zien (quantity uit order_lines).
-- Dit willen we alleen zien voor een bestelde hoeveelheid van een product > 250
--   (we zijn nl. als eerste geïnteresseerd in grote aantallen want daar lijkt het vaker mis te gaan)
-- En verder willen we ons focussen op verkopers wiens bestellingen er gemiddeld langer over doen.
-- De meeste bestellingen kunnen binnen een dag bezorgd worden, sommige binnen 2-3 dagen.
-- Het hele bestelproces is er op gericht dat de gemiddelde bestelling binnen 1.45 dagen kan worden bezorgd.
-- We willen in onze query dan ook alleen de verkopers zien wiens gemiddelde levertijd 
--  (expected_delivery_date - order_date) over al zijn/haar bestellingen groter is dan 1.45 dagen.
-- Maak om dit te bereiken een subquery in je WHERE clause.
-- Sorteer het resultaat van de hele geheel op levertijd (desc) en verkoper.
-- 1. Maak hieronder deze query (als je het goed doet zouden er 377 rijen uit moeten komen, en het kan best even duren...)

DROP VIEW IF EXISTS verkoop;
CREATE OR REPLACE VIEW verkoop AS
SELECT o.order_id,
       o.order_date,
       o.salesperson_person_id                      verkoper,
       abs(o.expected_delivery_date - o.order_date) levertijd,
       ol.quantity
FROM orders o
         JOIN order_lines ol on o.order_id = ol.order_id
         JOIN customers c on o.salesperson_person_id = c.customer_id
WHERE quantity > 250
  AND (SELECT avg(expected_delivery_date - order_date)
       FROM orders o
                JOIN customers c2 on o.salesperson_person_id = c2.customer_id
       WHERE c.customer_id = c2.customer_id
       GROUP BY c.customer_id) > 1.45
ORDER BY levertijd DESC, verkoper;


-- S7.3.B
--
-- 1. Vraag het EXPLAIN plan op van je query (kopieer hier, onder de opdracht)
-- 2. Kijk of je met 1 of meer indexen de query zou kunnen versnellen
-- 3. Maak de index(en) aan en run nogmaals het EXPLAIN plan (kopieer weer onder de opdracht) 
-- 4. Wat voor verschillen zie je? Verklaar hieronder.

EXPLAIN ANALYSE
SELECT *
FROM verkoop;

-- Sort  (cost=48829.04..48829.84 rows=321 width=20) (actual time=3136.347..3136.413 rows=377 loops=1)
-- "  Sort Key: (abs((o.expected_delivery_date - o.order_date))) DESC, o.salesperson_person_id"
--   Sort Method: quicksort  Memory: 54kB
--   ->  Merge Join  (cost=7706.50..48815.67 rows=321 width=20) (actual time=43.982..3136.327 rows=377 loops=1)
--         Merge Cond: (c.customer_id = o.salesperson_person_id)
--         ->  Index Only Scan using pk_sales_customers on customers c  (cost=0.28..1297571.05 rows=221 width=4) (actual time=17.889..3110.049 rows=4 loops=1)
--               Filter: ((SubPlan 1) > 1.45)
--               Rows Removed by Filter: 659
--               Heap Fetches: 663
--               SubPlan 1
--                 ->  GroupAggregate  (cost=0.28..1957.04 rows=1 width=36) (actual time=4.689..4.689 rows=0 loops=663)
--                       Group Key: c.customer_id
--                       ->  Nested Loop  (cost=0.28..1901.83 rows=7360 width=12) (actual time=4.596..4.681 rows=111 loops=663)
--                             ->  Index Only Scan using pk_sales_customers on customers c2  (cost=0.28..8.29 rows=1 width=4) (actual time=0.002..0.003 rows=1 loops=663)
--                                   Index Cond: (customer_id = c.customer_id)
--                                   Heap Fetches: 663
--                             ->  Seq Scan on orders o_1  (cost=0.00..1819.94 rows=7360 width=12) (actual time=4.587..4.662 rows=111 loops=663)
--                                   Filter: (salesperson_person_id = c.customer_id)
--                                   Rows Removed by Filter: 73484
--         ->  Sort  (cost=7706.22..7708.63 rows=964 width=20) (actual time=26.073..26.191 rows=770 loops=1)
--               Sort Key: o.salesperson_person_id
--               Sort Method: quicksort  Memory: 98kB
--               ->  Gather  (cost=1000.29..7658.44 rows=964 width=20) (actual time=0.988..25.942 rows=941 loops=1)
--                     Workers Planned: 2
--                     Workers Launched: 2
--                     ->  Nested Loop  (cost=0.29..6562.04 rows=402 width=20) (actual time=0.072..8.758 rows=314 loops=3)
--                           ->  Parallel Seq Scan on order_lines ol  (cost=0.00..5051.27 rows=402 width=8) (actual time=0.048..8.185 rows=314 loops=3)
--                                 Filter: (quantity > 250)
--                                 Rows Removed by Filter: 76824
--                           ->  Index Scan using pk_sales_orders on orders o  (cost=0.29..3.76 rows=1 width=16) (actual time=0.002..0.002 rows=1 loops=941)
--                                 Index Cond: (order_id = ol.order_id)
-- Planning Time: 0.319 ms
-- Execution Time: 3136.518 ms

CREATE INDEX s7_index ON orders (order_id);
CREATE INDEX s7_index1 ON orders (salesperson_person_id);

DROP INDEX s7_index1;

EXPLAIN ANALYSE
SELECT *
FROM verkoop;

-- Sort  (cost=33234.33..33235.14 rows=321 width=20) (actual time=53.979..54.042 rows=377 loops=1)
-- "  Sort Key: (abs((o.expected_delivery_date - o.order_date))) DESC, o.salesperson_person_id"
--   Sort Method: quicksort  Memory: 54kB
--   ->  Merge Join  (cost=7706.50..33220.97 rows=321 width=20) (actual time=30.627..53.958 rows=377 loops=1)
--         Merge Cond: (c.customer_id = o.salesperson_person_id)
--         ->  Index Only Scan using pk_sales_customers on customers c  (cost=0.28..805223.94 rows=221 width=4) (actual time=5.568..28.731 rows=4 loops=1)
--               Filter: ((SubPlan 1) > 1.45)
--               Rows Removed by Filter: 659
--               Heap Fetches: 663
--               SubPlan 1
--                 ->  GroupAggregate  (cost=85.61..1214.44 rows=1 width=36) (actual time=0.043..0.043 rows=0 loops=663)
--                       Group Key: c.customer_id
--                       ->  Nested Loop  (cost=85.61..1159.23 rows=7360 width=12) (actual time=0.007..0.035 rows=111 loops=663)
--                             ->  Index Only Scan using pk_sales_customers on customers c2  (cost=0.28..8.29 rows=1 width=4) (actual time=0.001..0.001 rows=1 loops=663)
--                                   Index Cond: (customer_id = c.customer_id)
--                                   Heap Fetches: 663
--                             ->  Bitmap Heap Scan on orders o_1  (cost=85.33..1077.33 rows=7360 width=12) (actual time=0.004..0.020 rows=111 loops=663)
--                                   Recheck Cond: (salesperson_person_id = c.customer_id)
--                                   Heap Blocks: exact=8998
--                                   ->  Bitmap Index Scan on s7_index1  (cost=0.00..83.49 rows=7360 width=0) (actual time=0.003..0.003 rows=111 loops=663)
--                                         Index Cond: (salesperson_person_id = c.customer_id)
--         ->  Sort  (cost=7706.22..7708.63 rows=964 width=20) (actual time=25.037..25.143 rows=770 loops=1)
--               Sort Key: o.salesperson_person_id
--               Sort Method: quicksort  Memory: 98kB
--               ->  Gather  (cost=1000.29..7658.44 rows=964 width=20) (actual time=0.385..24.909 rows=941 loops=1)
--                     Workers Planned: 2
--                     Workers Launched: 2
--                     ->  Nested Loop  (cost=0.29..6562.04 rows=402 width=20) (actual time=0.086..9.444 rows=314 loops=3)
--                           ->  Parallel Seq Scan on order_lines ol  (cost=0.00..5051.27 rows=402 width=8) (actual time=0.064..8.755 rows=314 loops=3)
--                                 Filter: (quantity > 250)
--                                 Rows Removed by Filter: 76824
--                           ->  Index Scan using pk_sales_orders on orders o  (cost=0.29..3.76 rows=1 width=16) (actual time=0.002..0.002 rows=1 loops=941)
--                                 Index Cond: (order_id = ol.order_id)
-- Planning Time: 0.352 ms
-- Execution Time: 54.155 ms


-- Er zijn heel veel orderid's en door ook de sales persons te indesen wordt er al een soort tabel gemaakt waar beide snel te vinden zijn.

-- S7.3.C
--
-- Zou je de query ook heel anders kunnen schrijven om hem te versnellen?
CREATE VIEW s7_3_c AS
SELECT o.order_id,
       o.order_date,
       o.salesperson_person_id                      verkoper,
       abs(o.expected_delivery_date - o.order_date) levertijd,
       ol.quantity
FROM orders o
         JOIN order_lines ol on o.order_id = ol.order_id
         JOIN customers c on o.salesperson_person_id = c.customer_id
WHERE quantity > 250
  AND (SELECT avg(expected_delivery_date - order_date)
       FROM orders o
                JOIN customers c2 on o.salesperson_person_id = c2.customer_id
       WHERE c.customer_id = c2.customer_id
       GROUP BY c.customer_id) > 1.45
ORDER BY levertijd DESC, verkoper;
