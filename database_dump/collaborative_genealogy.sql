-- phpMyAdmin SQL Dump
-- version 4.3.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Nov 24, 2015 at 03:02 PM
-- Server version: 5.6.24
-- PHP Version: 5.6.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `collaborative_genealogy`
--

-- --------------------------------------------------------

--
-- Table structure for table `request`
--

CREATE TABLE IF NOT EXISTS `request` (
  `user_id` varchar(10) COLLATE utf8_bin NOT NULL,
  `relative_id` varchar(10) COLLATE utf8_bin NOT NULL,
  `relationship` varchar(10) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` varchar(10) COLLATE utf8_bin NOT NULL,
  `name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `surname` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `password` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `gender` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `birthplace` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `biography` text COLLATE utf8_bin NOT NULL,
  `father_id` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `mother_id` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `spouse_id` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `num_relatives` int(11) DEFAULT '0',
  `refresh` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `request`
--
ALTER TABLE `request`
  ADD PRIMARY KEY (`user_id`,`relative_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `email` (`email`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
