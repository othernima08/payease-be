-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 13, 2023 at 11:25 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `payease`
--

-- --------------------------------------------------------

--
-- Table structure for table `providers`
--

CREATE TABLE `providers` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `profile_picture_url` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `providers`
--

INSERT INTO `providers` (`id`, `created_at`, `is_deleted`, `name`, `profile_picture_url`, `updated_at`) VALUES
('403c8fb0-d4ff-4dae-99b9-ed5cca259878', '2023-10-10 22:58:24.000000', b'0', 'Bank Mandiri', 'https://logos-download.com/wp-content/uploads/2016/06/Bank_Mandiri_logo_fon.png', '2023-10-10 22:58:24.000000'),
('45730fc9-d7ad-4a12-b9d6-51b39e235091', '2023-10-10 22:55:14.000000', b'0', 'Bank BNI', 'https://moko.co.id/wp-content/uploads/2014/11/248_logo.jpg', '2023-10-10 22:55:14.000000'),
('646f7f6a-5b4f-485a-86f4-772efeb99ff3', '2023-10-10 23:00:13.000000', b'0', 'Alfamart', 'https://1000marcas.net/wp-content/uploads/2021/06/Alfamart-Logo-2003.jpg', '2023-10-10 23:00:13.000000'),
('6660144d-1d4a-4600-83ed-8bd588ebda69', '2023-10-10 22:59:43.000000', b'0', 'Bank BRI', 'http://3.bp.blogspot.com/-UQG4evjcwzw/T9SzqLN-hdI/AAAAAAAALAg/RtxrxIZxcWM/s1600/Logo+Bank+BRI+(1).gif', '2023-10-10 22:59:43.000000'),
('c8962c4d-df10-4656-b76e-f002709b8b4a', '2023-10-10 22:53:55.000000', b'0', 'Bank BCA', 'https://1.bp.blogspot.com/-THibJz4NpO0/UNhEbur9-fI/AAAAAAAAEQM/b5J4fwEPD-c/s1600/Logo+Bank+BCA.JPG', '2023-10-10 22:53:55.000000'),
('f4a2ef1c-eb0b-437f-89c8-185247625e30', '2023-10-10 23:00:43.000000', b'0', 'Indomaret', 'http://4.bp.blogspot.com/-OJRyaRmc3pw/UFdD1RdyFJI/AAAAAAAAAjQ/ZCVGus-5bDg/w1200-h630-p-k-no-nu/logo_indomaret.png', '2023-10-10 23:00:43.000000');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `providers`
--
ALTER TABLE `providers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_fuh4835foq2trqy6ur286u3s0` (`name`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
