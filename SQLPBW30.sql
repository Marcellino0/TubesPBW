-- Drop semua tabel dan view yang ada terlebih dahulu
DROP VIEW IF EXISTS rental_history;
DROP TABLE IF EXISTS penyewaan;
DROP TABLE IF EXISTS laporan;
DROP TABLE IF EXISTS Film CASCADE;
DROP TABLE IF EXISTS Pelanggan CASCADE;
DROP TABLE IF EXISTS actor CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
-- Melihat data dari tabel genre
SELECT * FROM genre;

-- Melihat data dari tabel actor
SELECT * FROM actor;

-- Melihat data dari tabel pelanggan
SELECT * FROM pelanggan;

-- Melihat data dari tabel film
SELECT * FROM film;

-- Melihat data dari tabel penyewaan
SELECT * FROM penyewaan;

-- Melihat data dari tabel laporan
SELECT * FROM laporan;

-- Melihat data dari view rental_history
SELECT * FROM rental_history;

-- Buat tabel genre
CREATE TABLE genre (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Buat tabel actor
CREATE TABLE actor (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Buat tabel pelanggan
CREATE TABLE Pelanggan (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) UNIQUE NOT NULL,
    nama varchar (20) NOT NULL,
    email varchar (30) NOT NULL,
    noTelp varchar (30) NOT NULL,
    saldo NUMERIC DEFAULT 0
);
ALTER TABLE Film ADD COLUMN synopsis TEXT;
-- Buat tabel film
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

-- Buat tabel laporan
CREATE TABLE laporan (
    id_laporan SERIAL PRIMARY KEY,
    movie_title VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL,
    rent_date DATE NOT NULL,
    due_date DATE NOT NULL,
    harga_sewa NUMERIC NOT NULL,
    denda NUMERIC DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    id_sewa INTEGER
);

-- Buat tabel penyewaan
CREATE TABLE penyewaan (
    idsewa SERIAL PRIMARY KEY,
    idfilm INTEGER NOT NULL,
    rentdate DATE NOT NULL,
    duedate DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    user_id INTEGER NOT NULL,
    denda NUMERIC DEFAULT 0,
    updated_date DATE,
    FOREIGN KEY (idfilm) REFERENCES film(film_id),
    FOREIGN KEY (user_id) REFERENCES pelanggan(user_id)
);

-- Buat view rental_history
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