DROP TABLE Film
DROP TABLE Pelanggan
DROP TABLE Penyewaan
DROP TABLE laporan
SELECT *
FROM pelanggan

 
);
-- Tabel Pelanggan
CREATE TABLE Pelanggan (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) UNIQUE NOT NULL,
    nama varchar (20) NOT NULL,
    email varchar (30) NOT NULL,
    noTelp varchar (30) Not Null
);

SELECT *
FROM film
ALTER TABLE Film ALTER COLUMN cover TYPE BYTEA;

-- Tabel Film
CREATE TABLE Film (
    film_id SERIAL PRIMARY KEY,
    cover BYTEA NOT NULL,
    judul VARCHAR(255) NOT NULL,
    genre varchar(255) NOT NULL,
    Aktor varchar(255) NOT NULL,
    stok INT NOT NULL,
    hargaPerFilm NUMERIC
);

CREATE TABLE Laporan(
    idLaporan SERIAL PRIMARY KEY,
    periode varchar (100) NOT NULL,
    totalPenyewaan int NOT NULL,
    pendapatan NUMERIC NOT NULL,
    filmPopuler varchar(100)
);

CREATE TABLE Penyewaan(
    idSewa SERIAL PRIMARY KEY,
    idFilm int REFERENCES Film,
    rentDate DATE NOT NULL,
    dueDate DATE NOT NULL
)