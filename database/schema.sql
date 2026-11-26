-- =====================================================================
--  Car Rental Agency Management - schema MySQL / MariaDB (version V16)
--  Advanced Web Development (JEE) - UIR - 2026/2027
--
--  Ordre d'import : schema.sql, puis seed.sql, puis demo_accounts.sql.
--  Ce script est re-executable : il SUPPRIME puis recree la base.
--
--  Changements par rapport a la V15 (voir 01_Decisions/Decisions_phase0.md) :
--   1. AUTO_INCREMENT sur tous les id : necessaire pour
--      @GeneratedValue(strategy = GenerationType.IDENTITY) (Lab 7).
--   2. model_metrics : `precision` (mot reserve MySQL) devient precisionScore,
--      ajout de baselineAccuracy (comparaison avec la baseline).
--   3. rental_extensions : ajout de decidedAt (date de la decision de l'agent).
--   4. Contraintes CHECK sur les valeurs (MariaDB les applique depuis 10.2).
--   5. Index sur les requetes frequentes (chevauchements, historiques).
--   6. Nouveau nom de base : car_rental_agency.
-- =====================================================================

DROP DATABASE IF EXISTS car_rental_agency;
CREATE DATABASE car_rental_agency CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE car_rental_agency;

-- ---------------------------------------------------------------------
-- Categories de vehicules
-- ---------------------------------------------------------------------
CREATE TABLE `vehicle_categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL UNIQUE,
  `description` TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Utilisateurs : administrateur, agent ou client (mot de passe hache BCrypt)
-- ---------------------------------------------------------------------
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `fullName` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `passwordHash` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(80) NOT NULL,
  `role` ENUM('ADMINISTRATOR','AGENT','CUSTOMER') NOT NULL,
  `createdAt` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Vehicules (statut automatique, sauf UNDER_MAINTENANCE pose par l'agent)
-- ---------------------------------------------------------------------
CREATE TABLE `vehicles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `brand` VARCHAR(100) NOT NULL,
  `model` VARCHAR(100) NOT NULL,
  `licencePlate` VARCHAR(40) NOT NULL UNIQUE,
  `fuelType` ENUM('PETROL','DIESEL','HYBRID','ELECTRIC') NOT NULL,
  `gearbox` ENUM('MANUAL','AUTOMATIC') NOT NULL,
  `numberOfSeats` INT NOT NULL,
  `dailyRate` DECIMAL(14,2) NOT NULL,
  `currentMileage` INT NOT NULL,
  `status` ENUM('AVAILABLE','RENTED','PENDING_INSPECTION','UNDER_MAINTENANCE') NOT NULL,
  `photo` VARCHAR(512) NOT NULL,
  `acquisitionDate` DATE NOT NULL,
  `category_id` BIGINT NOT NULL,
  CONSTRAINT `ck_vehicles_seats`   CHECK (`numberOfSeats` > 0),
  CONSTRAINT `ck_vehicles_rate`    CHECK (`dailyRate` >= 0),
  CONSTRAINT `ck_vehicles_mileage` CHECK (`currentMileage` >= 0),
  FOREIGN KEY (`category_id`) REFERENCES `vehicle_categories`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Equipements optionnels (GPS, siege enfant...)
-- ---------------------------------------------------------------------
CREATE TABLE `equipment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(150) NOT NULL UNIQUE,
  `dailyPrice` DECIMAL(14,2) NOT NULL,
  CONSTRAINT `ck_equipment_price` CHECK (`dailyPrice` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Parametres globaux (une seule ligne) : TVA et taux de penalite
-- lateFeeRate = 0.15 signifie 15 % du tarif journalier par jour de retard
-- ---------------------------------------------------------------------
CREATE TABLE `app_settings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `vatRate` DECIMAL(10,6) NOT NULL,
  `lateFeeRate` DECIMAL(10,6) NOT NULL,
  CONSTRAINT `ck_settings_rates` CHECK (`vatRate` >= 0 AND `lateFeeRate` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Reservations (une demande PENDING bloque deja les dates : decision n° 6)
-- ---------------------------------------------------------------------
CREATE TABLE `reservations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `pickupDate` DATE NOT NULL,
  `plannedReturnDate` DATE NOT NULL,
  `status` ENUM('PENDING','ACCEPTED','REJECTED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL,
  `createdAt` DATETIME NOT NULL,
  `customer_id` BIGINT NOT NULL,
  `vehicle_id` BIGINT NOT NULL,
  CONSTRAINT `ck_reservations_dates` CHECK (`plannedReturnDate` >= `pickupDate`),
  FOREIGN KEY (`customer_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`),
  INDEX `idx_reservations_overlap` (`vehicle_id`, `pickupDate`, `plannedReturnDate`, `status`),
  INDEX `idx_reservations_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Table de liaison many-to-many : reservation <-> equipement
-- ---------------------------------------------------------------------
CREATE TABLE `reservation_equipment` (
  `reservation_id` BIGINT NOT NULL,
  `equipment_id` BIGINT NOT NULL,
  PRIMARY KEY (`reservation_id`, `equipment_id`),
  FOREIGN KEY (`reservation_id`) REFERENCES `reservations`(`id`),
  FOREIGN KEY (`equipment_id`) REFERENCES `equipment`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Locations : une par reservation ; pas de lien direct vers le vehicule
-- (on passe par la reservation). Les champs de retour et d'inspection
-- restent NULL tant que le vehicule n'est pas rendu / inspecte.
-- Carburant : echelle de 0 a 100.
-- ---------------------------------------------------------------------
CREATE TABLE `rentals` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `actualPickupDate` DATE NOT NULL,
  `departureMileage` INT NOT NULL,
  `departureFuelLevel` DOUBLE NOT NULL,
  `actualReturnDate` DATE NULL,
  `returnMileage` INT NULL,
  `returnFuelLevel` DOUBLE NULL,
  `status` ENUM('ACTIVE','RETURN_PENDING_INSPECTION','COMPLETED') NOT NULL,
  `plannedReturnDate` DATE NOT NULL,
  `inspectionConform` BOOLEAN NULL,
  `inspectionNotes` TEXT NULL,
  `inspectionValidatedAt` DATETIME NULL,
  `reservation_id` BIGINT NOT NULL UNIQUE,
  `handledByAgent_id` BIGINT NOT NULL,
  `inspectionValidatedBy_id` BIGINT NULL,
  CONSTRAINT `ck_rentals_departure_mileage` CHECK (`departureMileage` >= 0),
  CONSTRAINT `ck_rentals_departure_fuel`    CHECK (`departureFuelLevel` BETWEEN 0 AND 100),
  CONSTRAINT `ck_rentals_return_fuel`       CHECK (`returnFuelLevel` IS NULL OR `returnFuelLevel` BETWEEN 0 AND 100),
  CONSTRAINT `ck_rentals_return_mileage`    CHECK (`returnMileage` IS NULL OR `returnMileage` >= `departureMileage`),
  CONSTRAINT `ck_rentals_dates`             CHECK (`plannedReturnDate` >= `actualPickupDate`
                                                   AND (`actualReturnDate` IS NULL OR `actualReturnDate` >= `actualPickupDate`)),
  FOREIGN KEY (`reservation_id`) REFERENCES `reservations`(`id`),
  FOREIGN KEY (`handledByAgent_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`inspectionValidatedBy_id`) REFERENCES `users`(`id`),
  INDEX `idx_rentals_agent` (`handledByAgent_id`),
  INDEX `idx_rentals_status_return` (`status`, `plannedReturnDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Demandes de prolongation (le client demande, l'agent decide).
-- approvedBy_id = l'agent qui a decide (accepte OU refuse) : decidedBy en Java.
-- ---------------------------------------------------------------------
CREATE TABLE `rental_extensions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `requestDate` DATE NOT NULL,
  `previousReturnDate` DATE NOT NULL,
  `newReturnDate` DATE NOT NULL,
  `status` ENUM('PENDING','ACCEPTED','REJECTED') NOT NULL,
  `rental_id` BIGINT NOT NULL,
  `approvedBy_id` BIGINT NULL,
  `decidedAt` DATETIME NULL,
  CONSTRAINT `ck_extensions_dates` CHECK (`newReturnDate` > `previousReturnDate`),
  FOREIGN KEY (`rental_id`) REFERENCES `rentals`(`id`),
  FOREIGN KEY (`approvedBy_id`) REFERENCES `users`(`id`),
  INDEX `idx_extensions_rental_status` (`rental_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Factures : une par location ; tarif et taux figes au moment de l'emission
-- ---------------------------------------------------------------------
CREATE TABLE `invoices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `invoiceNumber` VARCHAR(60) NOT NULL UNIQUE,
  `rentalDays` INT NOT NULL,
  `lateDays` INT NOT NULL,
  `dailyRateApplied` DECIMAL(14,2) NOT NULL,
  `rentalAmount` DECIMAL(14,2) NOT NULL,
  `equipmentAmount` DECIMAL(14,2) NOT NULL,
  `lateFees` DECIMAL(14,2) NOT NULL,
  `damageFees` DECIMAL(14,2) NOT NULL,
  `vatAmount` DECIMAL(14,2) NOT NULL,
  `totalAmount` DECIMAL(14,2) NOT NULL,
  `vatRateApplied` DECIMAL(10,6) NOT NULL,
  `lateFeeRateApplied` DECIMAL(10,6) NOT NULL,
  `issueDate` DATE NOT NULL,
  `rental_id` BIGINT NOT NULL UNIQUE,
  CONSTRAINT `ck_invoices_days`    CHECK (`rentalDays` >= 1 AND `lateDays` >= 0),
  CONSTRAINT `ck_invoices_amounts` CHECK (`rentalAmount` >= 0 AND `equipmentAmount` >= 0 AND `lateFees` >= 0
                                          AND `damageFees` >= 0 AND `vatAmount` >= 0 AND `totalAmount` >= 0),
  FOREIGN KEY (`rental_id`) REFERENCES `rentals`(`id`),
  INDEX `idx_invoices_issue_date` (`issueDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Journal de maintenance
-- ---------------------------------------------------------------------
CREATE TABLE `maintenances` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `operationDate` DATE NOT NULL,
  `type` VARCHAR(255) NOT NULL,
  `cost` DECIMAL(14,2) NOT NULL,
  `mileage` INT NOT NULL,
  `vehicle_id` BIGINT NOT NULL,
  CONSTRAINT `ck_maintenances_values` CHECK (`cost` >= 0 AND `mileage` >= 0),
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`),
  INDEX `idx_maintenances_vehicle_date` (`vehicle_id`, `operationDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Metriques des modeles ML (une ligne par entrainement)
-- ---------------------------------------------------------------------
CREATE TABLE `model_metrics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `modelType` ENUM('MAINTENANCE_RISK','RENTAL_EXTENSION') NOT NULL,
  `algorithm` VARCHAR(255) NOT NULL,
  `trainedAt` DATETIME NOT NULL,
  `trainingSize` INT NOT NULL,
  `testSize` INT NOT NULL,
  `accuracy` DOUBLE NOT NULL,
  `precisionScore` DOUBLE NOT NULL,
  `recall` DOUBLE NOT NULL,
  `f1Score` DOUBLE NOT NULL,
  `baselineAccuracy` DOUBLE NOT NULL,
  `baselineF1` DOUBLE NOT NULL,
  `modelPath` VARCHAR(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
