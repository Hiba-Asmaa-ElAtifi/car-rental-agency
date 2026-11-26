-- =====================================================================
--  Comptes de demonstration (a importer APRES schema.sql et seed.sql)
--
--  | Role          | Email                 | Mot de passe |
--  |---------------|-----------------------|--------------|
--  | ADMINISTRATOR | admin@carrental.ma    | Admin123!    |
--  | AGENT         | agent@carrental.ma    | Agent123!    |
--  | CUSTOMER      | client@carrental.ma   | Client123!   |
--
--  Les mots de passe sont stockes haches en BCrypt (prefixe $2a$, cout 10) :
--  jamais en clair. Les id ne sont pas donnes : AUTO_INCREMENT les attribue,
--  exactement comme le fera Hibernate avec GenerationType.IDENTITY.
--  Les comptes du seed (hashes $2b$) n'ont pas de mot de passe connu.
-- =====================================================================

USE car_rental_agency;

INSERT INTO `users` (`fullName`, `email`, `passwordHash`, `phone`, `role`, `createdAt`) VALUES
  ('Admin Demo',  'admin@carrental.ma',  '$2a$10$4PIWnfxUcwFX5HuZmDPCUOdODJZ90Nf3bb27nYxR1UaTJAvNBylIW', '0600000001', 'ADMINISTRATOR', NOW()),
  ('Agent Demo',  'agent@carrental.ma',  '$2a$10$nvTq6KMIfN6dHX3ZRWB8b.GoX2x0KjJFWkf2aPo2P7IpjYFDkPZvu', '0600000002', 'AGENT',         NOW()),
  ('Client Demo', 'client@carrental.ma', '$2a$10$u2SLGh/qxQJZr..rvGZyCuiHHzzhmpKo89pv2L1Oqj1NftswOAgHS', '0600000003', 'CUSTOMER',      NOW());
