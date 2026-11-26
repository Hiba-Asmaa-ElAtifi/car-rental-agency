CREATE DATABASE IF NOT EXISTS car_rental_v15 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE car_rental_v15;

CREATE TABLE IF NOT EXISTS `vehicle_categories` (
  `id` BIGINT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL UNIQUE,
  `description` TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT PRIMARY KEY,
  `fullName` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `passwordHash` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(80) NOT NULL,
  `role` ENUM('ADMINISTRATOR','AGENT','CUSTOMER') NOT NULL,
  `createdAt` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vehicles` (
  `id` BIGINT PRIMARY KEY,
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
  FOREIGN KEY (`category_id`) REFERENCES `vehicle_categories`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `equipment` (
  `id` BIGINT PRIMARY KEY,
  `name` VARCHAR(150) NOT NULL UNIQUE,
  `dailyPrice` DECIMAL(14,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `app_settings` (
  `id` BIGINT PRIMARY KEY,
  `vatRate` DECIMAL(10,6) NOT NULL,
  `lateFeeRate` DECIMAL(10,6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `reservations` (
  `id` BIGINT PRIMARY KEY,
  `pickupDate` DATE NOT NULL,
  `plannedReturnDate` DATE NOT NULL,
  `status` ENUM('PENDING','ACCEPTED','REJECTED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL,
  `createdAt` DATETIME NOT NULL,
  `customer_id` BIGINT NOT NULL,
  `vehicle_id` BIGINT NOT NULL,
  FOREIGN KEY (`customer_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `reservation_equipment` (
  `reservation_id` BIGINT NOT NULL,
  `equipment_id` BIGINT NOT NULL,
  PRIMARY KEY (reservation_id,equipment_id),
  FOREIGN KEY (`reservation_id`) REFERENCES `reservations`(`id`),
  FOREIGN KEY (`equipment_id`) REFERENCES `equipment`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `rentals` (
  `id` BIGINT PRIMARY KEY,
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
  FOREIGN KEY (`reservation_id`) REFERENCES `reservations`(`id`),
  FOREIGN KEY (`handledByAgent_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`inspectionValidatedBy_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `rental_extensions` (
  `id` BIGINT PRIMARY KEY,
  `requestDate` DATE NOT NULL,
  `previousReturnDate` DATE NOT NULL,
  `newReturnDate` DATE NOT NULL,
  `status` ENUM('PENDING','ACCEPTED','REJECTED') NOT NULL,
  `rental_id` BIGINT NOT NULL,
  `approvedBy_id` BIGINT NULL,
  FOREIGN KEY (`rental_id`) REFERENCES `rentals`(`id`),
  FOREIGN KEY (`approvedBy_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `invoices` (
  `id` BIGINT PRIMARY KEY,
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
  FOREIGN KEY (`rental_id`) REFERENCES `rentals`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `maintenances` (
  `id` BIGINT PRIMARY KEY,
  `operationDate` DATE NOT NULL,
  `type` VARCHAR(255) NOT NULL,
  `cost` DECIMAL(14,2) NOT NULL,
  `mileage` INT NOT NULL,
  `vehicle_id` BIGINT NOT NULL,
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `model_metrics` (
  `id` BIGINT PRIMARY KEY,
  `modelType` ENUM('MAINTENANCE_RISK','RENTAL_EXTENSION') NOT NULL,
  `algorithm` VARCHAR(255) NOT NULL,
  `trainedAt` DATETIME NOT NULL,
  `trainingSize` INT NOT NULL,
  `testSize` INT NOT NULL,
  `accuracy` DOUBLE NOT NULL,
  `precision` DOUBLE NOT NULL,
  `recall` DOUBLE NOT NULL,
  `f1Score` DOUBLE NOT NULL,
  `baselineF1` DOUBLE NOT NULL,
  `modelPath` VARCHAR(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
