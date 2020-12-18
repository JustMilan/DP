-- ------------------------------------------------------------------------
-- Data & Persistency
-- Opdracht S9: Aanvullende herkansingsopdracht
--
-- (c) 2020 Hogeschool Utrecht
-- Tijmen Muller (tijmen.muller@hu.nl)
-- André Donk (andre.donk@hu.nl)
--
--
-- Opdracht: schrijf SQL-queries om onderstaande resultaten op te vragen,
-- aan te maken, verwijderen of aan te passen in de database van de
-- bedrijfscasus.
--
-- Codeer je uitwerking onder de regel 'DROP VIEW ...' (bij een SELECT)
-- of boven de regel 'ON CONFLICT DO NOTHING;' (bij een INSERT)
-- Je kunt deze eigen query selecteren en los uitvoeren, en wijzigen tot
-- je tevreden bent.
--
-- Vervolgens kun je je uitwerkingen testen door de testregels
-- (met [TEST] erachter) te activeren (haal hiervoor de commentaartekens
-- weg) en vervolgens het hele bestand uit te voeren. Hiervoor moet je de
-- testsuite in de database hebben geladen (bedrijf_postgresql_test.sql).
-- NB: niet alle opdrachten hebben testregels.
--
-- Lever je werk pas in op Canvas als alle tests slagen.
-- ------------------------------------------------------------------------


-- S9.1  Overstap
--
-- Jan-Jaap den Draaier is per 1 oktober 2010 manager van personeelszaken.
-- Hij komt direct onder de directeur te vallen en gaat 2100 euro per
-- maand verdienen.
-- Voer alle queries uit om deze wijziging door te voeren.
UPDATE medewerkers
SET chef     = 7839,
    functie  = 'MANAGER',
    maandsal = 2100,
    comm     = null,
    afd      = 40
WHERE mnr = 7844;

UPDATE afdelingen
SET hoofd = 7844
WHERE anr = 40;

-- UPDATE historie
-- SET einddatum   = date('2020-10-01'),
--     opmerkingen = 'Gepromoveerd tot manager van personeelszaken'
-- WHERE mnr = 7844
--   AND begindatum = (SELECT begindatum FROM historie WHERE mnr = 7844 ORDER BY begindatum DESC LIMIT 1);

INSERT INTO historie (mnr, beginjaar, begindatum, einddatum, afd, maandsal, opmerkingen)
VALUES (7844, EXTRACT(YEAR FROM date('2020-10-01')), date('2020-10-01'), null, 40, 2100, null)
ON CONFLICT DO NOTHING;
-- [TEST]


-- S9.2  Beginjaar
--
-- Voeg een beperkingsregel `h_beginjaar_chk` toe aan de historietabel
-- die controleert of een ingevoerde waarde in de kolom `beginjaar` een
-- correcte waarde heeft, met andere woorden: dat het om het huidige jaar
-- gaat of een jaar dat in het verleden ligt.
-- Test je beperkingsregel daarna met een INSERT die deze regel schendt.
ALTER TABLE historie
    ADD CONSTRAINT h_beginjaar_chk CHECK ( begindatum <= current_date );

INSERT INTO historie (mnr, beginjaar, begindatum, einddatum, afd, maandsal, opmerkingen)
VALUES (7387, EXTRACT(YEAR FROM now()), current_date + 1, null, 10, 2100, null)
ON CONFLICT DO NOTHING;


-- S9.3  Opmerkingen
--
-- Geef uit de historietabel alle niet-lege opmerkingen bij de huidige posities
-- van medewerkers binnen het bedrijf. Geef ter referentie ook het medewerkersnummer
-- bij de resultaten.
-- https://stackoverflow.com/a/59074975
-- https://stackoverflow.com/a/28085614
-- https://dba.stackexchange.com/q/190815
DROP VIEW IF EXISTS s9_3;
CREATE OR REPLACE VIEW s9_3 AS -- [TEST]
SELECT h.opmerkingen, h1.mnr
FROM historie h
         JOIN (SELECT DISTINCT mnr, max(begindatum) begindatum FROM historie GROUP BY mnr) h1 on h1.mnr = h.mnr AND h1.begindatum = h.begindatum
WHERE opmerkingen IS NOT NULL
  AND trim(opmerkingen) != '';


-- S9.4  Carrièrepad
--
-- Toon van alle medewerkers die nú op het hoofdkantoor werken hun historie
-- binnen het bedrijf: geef van elke positie die ze bekleed hebben de
-- de naam van de medewerker, de begindatum, de naam van hun afdeling op dat
-- moment (`afdeling`) en hun toenmalige salarisschaal (`schaal`).
-- Sorteer eerst op naam en dan op ingangsdatum.
DROP VIEW IF EXISTS s9_4;
CREATE OR REPLACE VIEW s9_4 AS
SELECT m.naam, h.begindatum, a.naam afdeling, s.snr schaal
FROM historie h
         JOIN afdelingen a on a.anr = h.afd
         JOIN medewerkers m on m.mnr = h.mnr
         JOIN schalen s on h.maandsal BETWEEN ondergrens AND bovengrens
WHERE m.afd = 10;
-- [TEST]


-- S9.5 Aanloop
--
-- Toon voor elke medewerker de naam en hoelang zij in andere functies hebben
-- gewerkt voordat zij op hun huidige positie kwamen (`tijdsduur`).
-- Rond naar beneden af op gehele jaren.
DROP VIEW IF EXISTS s9_5;
CREATE OR REPLACE VIEW s9_5 AS -- [TEST]
SELECT m.naam, tijdsduur
FROM medewerkers m
         JOIN (SELECT (max(begindatum) - min(begindatum)) / 365 tijdsduur, mnr
               FROM bedrijf.public.historie
               GROUP BY historie.mnr) tijd_per_mnr on m.mnr = tijd_per_mnr.mnr;


-- S9.6 Index
--
-- Maak een index `historie_afd_idx` op afdelingsnummer in de historietabel.
EXPLAIN ANALYSE
SELECT *
FROM historie
WHERE afd = 30;

-- Seq Scan on historie  (cost=0.00..2.00 rows=31 width=39) (actual time=0.009..0.019 rows=31 loops=1)
--   Filter: (afd = '30'::numeric)
--   Rows Removed by Filter: 49
-- Planning Time: 0.056 ms
-- Execution Time: 0.025 ms

CREATE INDEX historie_afd_idx ON historie (afd);

EXPLAIN ANALYSE
SELECT *
FROM historie
WHERE afd = 30;
-- Seq Scan on historie  (cost=0.00..2.00 rows=31 width=39) (actual time=0.008..0.017 rows=31 loops=1)
--   Filter: (afd = '30'::numeric)
--   Rows Removed by Filter: 49
-- Planning Time: 0.727 ms
-- Execution Time: 0.024 ms

DROP INDEX historie_afd_idx;

-- -------------------------[ HU TESTRAAMWERK ]--------------------------------
-- Met onderstaande query kun je je code testen. Zie bovenaan dit bestand
-- voor uitleg.

SELECT *
FROM test_exists('S9.1', 1) AS resultaat
UNION
SELECT 'S9.2 wordt niet getest: zelf handmatig testen.' AS resultaat
UNION
SELECT *
FROM test_select('S9.3') AS resultaat
UNION
SELECT *
FROM test_select('S9.4') AS resultaat
UNION
SELECT *
FROM test_select('S9.5') AS resultaat
UNION
SELECT 'S9.6 wordt niet getest: geen test mogelijk.' AS resultaat
ORDER BY resultaat;
