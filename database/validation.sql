USE car_rental_agency;
-- Les requetes de detection doivent renvoyer 0 lignes.
SELECT COUNT(*) AS total_vehicle_categories FROM `vehicle_categories`;
SELECT COUNT(*) AS total_users FROM `users`;
SELECT COUNT(*) AS total_vehicles FROM `vehicles`;
SELECT COUNT(*) AS total_equipment FROM `equipment`;
SELECT COUNT(*) AS total_app_settings FROM `app_settings`;
SELECT COUNT(*) AS total_reservations FROM `reservations`;
SELECT COUNT(*) AS total_reservation_equipment FROM `reservation_equipment`;
SELECT COUNT(*) AS total_rentals FROM `rentals`;
SELECT COUNT(*) AS total_rental_extensions FROM `rental_extensions`;
SELECT COUNT(*) AS total_invoices FROM `invoices`;
SELECT COUNT(*) AS total_maintenances FROM `maintenances`;
SELECT COUNT(*) AS total_model_metrics FROM `model_metrics`;
SELECT id FROM rentals WHERE status="ACTIVE" AND (actualReturnDate IS NOT NULL OR returnMileage IS NOT NULL OR inspectionValidatedAt IS NOT NULL);
SELECT m.id FROM maintenances m JOIN vehicles v ON v.id=m.vehicle_id WHERE m.operationDate<v.acquisitionDate;
SELECT r.id FROM rentals r LEFT JOIN reservations b ON b.id=r.reservation_id WHERE b.id IS NULL;
SELECT i.id FROM invoices i LEFT JOIN rentals r ON r.id=i.rental_id WHERE r.id IS NULL OR r.status<>"COMPLETED";
SELECT v.id FROM vehicles v LEFT JOIN vehicle_categories c ON c.id=v.category_id WHERE c.id IS NULL;
SELECT COUNT(*) AS completed_rentals FROM rentals WHERE status="COMPLETED";
-- Controles ajoutes en V16 (doivent aussi renvoyer 0 ligne)
SELECT id FROM reservations r WHERE r.status IN ('PENDING','ACCEPTED','IN_PROGRESS','COMPLETED') AND EXISTS (SELECT 1 FROM reservations o WHERE o.vehicle_id=r.vehicle_id AND o.id<>r.id AND o.status IN ('PENDING','ACCEPTED','IN_PROGRESS','COMPLETED') AND o.pickupDate < r.plannedReturnDate AND o.plannedReturnDate > r.pickupDate);
SELECT r.id FROM rentals r JOIN reservations b ON b.id=r.reservation_id WHERE (r.status='COMPLETED' AND b.status<>'COMPLETED') OR (r.status<>'COMPLETED' AND b.status<>'IN_PROGRESS');
SELECT v.id FROM vehicles v WHERE v.status='RENTED' AND NOT EXISTS (SELECT 1 FROM rentals r JOIN reservations b ON b.id=r.reservation_id WHERE b.vehicle_id=v.id AND r.status='ACTIVE');
-- Comptes de demo presents (3 lignes attendues apres demo_accounts.sql)
SELECT email, role FROM users WHERE email LIKE '%@carrental.ma' ORDER BY role;
