-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 29, 2024 at 05:20 PM
-- Server version: 10.4.21-MariaDB
-- PHP Version: 7.4.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_cosplay2`
--

-- --------------------------------------------------------

--
-- Table structure for table `kostum`
--

CREATE TABLE `kostum` (
  `id_baju` int(11) NOT NULL,
  `kostum` varchar(255) DEFAULT NULL,
  `ukuran` varchar(10) NOT NULL,
  `harga_kostum` int(11) DEFAULT NULL,
  `status` enum('Available','Unavailable') DEFAULT 'Available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `kostum`
--

INSERT INTO `kostum` (`id_baju`, `kostum`, `ukuran`, `harga_kostum`, `status`) VALUES
(1, 'Elaina', 'S', 10, 'Available'),
(2, 'r', 'S', 1000, 'Available'),
(3, 'Marin', 'L', 1000, 'Unavailable'),
(4, 'Marinn', 'XL', 10000, 'Available'),
(5, 'S', 'XL', 1000, 'Available');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id_transaksi` int(11) NOT NULL,
  `nama_user` varchar(255) DEFAULT NULL,
  `id_baju` int(11) DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  `kostum` varchar(255) DEFAULT NULL,
  `harga` int(11) DEFAULT NULL,
  `lama_sewa` int(11) DEFAULT NULL,
  `total_bayar` int(11) DEFAULT NULL,
  `pembayaran` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id_transaksi`, `nama_user`, `id_baju`, `tanggal`, `kostum`, `harga`, `lama_sewa`, `total_bayar`, `pembayaran`) VALUES
(3, 'mari', 4, '2024-12-25', 'Marinn', 10000, 2, 20000, 'Transfer'),
(5, 'Naoval', 4, '2024-12-28', 'Marinn', 10000, 1, 10000, 'Qris'),
(6, 'Tio', 2, '2024-12-20', 'r', 1000, 3, 3000, 'Qris');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `nama_user` varchar(50) NOT NULL,
  `telepon` varchar(15) NOT NULL,
  `alamat` varchar(225) NOT NULL,
  `level` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `username`, `password`, `nama_user`, `telepon`, `alamat`, `level`) VALUES
(1, 'admin', 'admin', 'Couserr', '0895702118992', 'JL. Wijaya No 69 Malang', 'admin'),
(2, 'pelanggan', 'pelanggan', 'Reina', '087743541235', 'sidodadi', 'user');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `kostum`
--
ALTER TABLE `kostum`
  ADD PRIMARY KEY (`id_baju`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD KEY `id_baju` (`id_baju`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `kostum`
--
ALTER TABLE `kostum`
  MODIFY `id_baju` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id_transaksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`id_baju`) REFERENCES `kostum` (`id_baju`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
