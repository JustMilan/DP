-- ------------------------------------------------------------------------
-- Data & Persistency
-- Opdracht S6: Views
--
-- (c) 2020 Hogeschool Utrecht
-- Tijmen Muller (tijmen.muller@hu.nl)
-- André Donk (andre.donk@hu.nl)
-- ------------------------------------------------------------------------


-- S6.1.
--
-- 1. Maak een view met de naam "deelnemers" waarmee je de volgende gegevens uit de tabellen inschrijvingen en uitvoering combineert:
--    inschrijvingen.cursist, inschrijvingen.cursus, inschrijvingen.begindatum, uitvoeringen.docent, uitvoeringen.locatie
-- 2. Gebruik de view in een query waarbij je de "deelnemers" view combineert met de "personeels" view (behandeld in de les):
CREATE OR REPLACE VIEW personeel AS
SELECT mnr, voorl, naam as medewerker, afd, functie
FROM medewerkers;
-- 3. Is de view "deelnemers" updatable ? Waarom ?

CREATE OR REPLACE VIEW deelnemers AS -- [TEST]
SELECT i.cursist, i.cursus, i.begindatum, u.docent, u.locatie
FROM inschrijvingen i
         INNER JOIN uitvoeringen u on i.cursus = u.cursus and i.begindatum = u.begindatum;

SELECT mnr FROM personeel WHERE mnr IN (SELECT cursist FROM deelnemers);

-- Nee want het is een select query.

-- S6.2.
--
-- 1. Maak een view met de naam "dagcursussen". Deze view dient de gegevens op te halen: 
--      code, omschrijving en type uit de tabel curssussen met als voorwaarde dat de lengte = 1. Toon aan dat de view werkt. 
-- 2. Maak een tweede view met de naam "daguitvoeringen". 
--    Deze view dient de uitvoeringsgegevens op te halen voor de "dagcurssussen" (gebruik ook de view "dagcursussen"). Toon aan dat de view werkt
-- 3. Verwijder de views en laat zien wat de verschillen zijn bij DROP view <viewnaam> CASCADE en bij DROP view <viewnaam> RESTRICT

CREATE OR REPLACE VIEW dagcursussen AS
    SELECT code, omschrijving, type FROM cursussen WHERE lengte = 1;

CREATE OR REPLACE VIEW daguitvoeringen AS
    SELECT begindatum FROM uitvoeringen WHERE cursus IN (SELECT code FROM dagcursussen);

SELECT * FROM dagcursussen;
SELECT * FROM daguitvoeringen;

DROP VIEW dagcursussen CASCADE;
DROP VIEW daguitvoeringen RESTRICT;

SELECT * FROM dagcursussen;
SELECT * FROM daguitvoeringen;

-- Bij RESTRICT wordt alleen die view gedropt en bij CASCADE wordt ook de gereferencde view gedropt.