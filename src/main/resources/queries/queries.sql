SELECT d.id, d.name, d.producer_fk, bd.id, bd.edition_fk, bd.service_method, s.id, style_fk
FROM   drink d
           LEFT OUTER JOIN bought_drink bd on d.id = bd.drink_fk
           LEFT OUTER JOIN service s on bd.id = s.bought_drink_fk
WHERE s.id IS NULL
  AND   d.style_fk <> 1576
ORDER BY producer_fk;

INSERT INTO bought_drink (id, drink_fk, edition_fk, availability, returnable, service_method, buying_price)
SELECT nextval('hibernate_sequence'), d.id, 1737, 'SOON', false, 'BOTTLE', 0
FROM   drink d
           LEFT OUTER JOIN bought_drink bd on d.id = bd.drink_fk
           LEFT OUTER JOIN service s on bd.id = s.bought_drink_fk
WHERE d.id IN (2199, 2205, 2195, 2198, 2202, 2192)
ORDER BY producer_fk;

INSERT INTO service (id, bought_drink_fk, volume_in_cl)
SELECT nextval('hibernate_sequence'), bd.id, 0
FROM   drink d
           LEFT OUTER JOIN bought_drink bd on d.id = bd.drink_fk
           LEFT OUTER JOIN service s on bd.id = s.bought_drink_fk
WHERE s.id IS NULL
  AND   d.style_fk <> 1576;