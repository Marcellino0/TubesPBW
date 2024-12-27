DROP TABLE Film
DROP TABLE Pelanggan
DROP TABLE Penyewaan
DROP TABLE laporan
SELECT *
FROM pelanggan
SELECT *
FROM penyewaan
SELECT *
FROM film
SELECT *
FROM laporan
SELECT *
FROM rental_history


DROP TABLE IF EXISTS pelanggan;
-- Tabel Pelanggan
CREATE TABLE Pelanggan (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) UNIQUE NOT NULL,
    nama varchar (20) NOT NULL,
    email varchar (30) NOT NULL,
    noTelp varchar (30) NOT NULL,
    saldo NUMERIC DEFAULT 0
);

DROP TABLE IF EXISTS Film CASCADE;
CREATE TABLE Film (
    film_id SERIAL PRIMARY KEY,
    cover BYTEA NOT NULL,
    judul VARCHAR(255) NOT NULL,
    genre VARCHAR(255) NOT NULL,
    aktor VARCHAR(255) NOT NULL,
    stok INT NOT NULL,
    harga_7_hari NUMERIC NOT NULL,
    harga_14_hari NUMERIC NOT NULL,
    harga_30_hari NUMERIC NOT NULL
);
DROP TABLE IF EXISTS laporan;
ALTER TABLE laporan ADD COLUMN id_sewa INTEGER;
CREATE TABLE laporan (
    id_laporan SERIAL PRIMARY KEY,
    movie_title VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL,
    rent_date DATE NOT NULL,
    due_date DATE NOT NULL,
    harga_sewa NUMERIC NOT NULL,
    denda NUMERIC DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

DROP TABLE IF EXISTS penyewaan;
ALTER TABLE penyewaan ADD COLUMN denda NUMERIC DEFAULT 0;
ALTER TABLE penyewaan ADD COLUMN updated_date DATE;
CREATE TABLE penyewaan (
    idsewa SERIAL PRIMARY KEY,
    idfilm INTEGER NOT NULL,
    rentdate DATE NOT NULL,
    duedate DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    user_id INTEGER NOT NULL,
    FOREIGN KEY (idfilm) REFERENCES film(film_id),
    FOREIGN KEY (user_id) REFERENCES pelanggan(user_id)
);

-- Hapus view lama
DROP VIEW IF EXISTS rental_history;
-- Membuat view untuk rental history
CREATE OR REPLACE VIEW rental_history AS
SELECT 
    f.judul as movie_title,
    p.rentdate as rented_on,
    p.updated_date as returned_on,
    CASE 
        WHEN (p.duedate - p.rentdate) <= 7 THEN f.harga_7_hari
        WHEN (p.duedate - p.rentdate) <= 14 THEN f.harga_14_hari
        ELSE f.harga_30_hari
    END as price
FROM penyewaan p
JOIN film f ON p.idfilm = f.film_id;

