-- phpMyAdmin SQL Dump
-- version 4.4.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Creato il: Nov 17, 2015 alle 15:24
-- Versione del server: 5.6.26
-- Versione PHP: 5.6.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `collaborative_genealogy`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `user`
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
-- Dump dei dati per la tabella `user`
--

INSERT INTO `user` (`id`, `name`, `surname`, `email`, `password`, `gender`, `birthdate`, `birthplace`, `biography`, `father_id`, `mother_id`, `spouse_id`, `num_relatives`, `refresh`) VALUES
('A', 'Giorgia', 'Vincenti', NULL, NULL, 'female', '2015-10-06', 'Gallipoli', 'Ciao, questa è una bio di prova', 'I', 'L', NULL, 22, 1),
('B', 'Laura', 'Vincenti', 'laura', NULL, 'female', '2015-10-14', 'Gallipoli', 'Ciao, questa è una bio di prova', 'I', 'L', NULL, 22, 1),
('C', 'marco', 'De Toma', 'admin', '8c6976e5b541415bde98bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'male', '1994-01-09', 'Barletta', 'dsadsa', 'O', 'N', NULL, 22, 0),
('D', 'Fede', 'de toma', 'fede', '8c6976e5b541415bde98bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'female', '2015-10-23', 'Barletta', 'Ciao sono fede', 'O', 'N', NULL, 22, 1),
('E', 'Alex', 'de toma', 'alex', NULL, 'male', '2015-10-20', 'Barletta', 'Ciao sono alex', 'O', 'N', NULL, 22, 1),
('F', 'Davide', 'Fazio', 'adsaads', NULL, 'male', '2015-10-22', 'Trani', 'Ciao, questa è una bio di prova', 'S', 'R', NULL, 22, 1),
('FLQQP86493', 'adasd', 'asdads', 'marco@dads.it', 'cdbb69d1d77abe5fec982abfb3b7f8253252229bf52d29b68369b672fb4a2', 'male', '1994-05-08', 'addas', '', NULL, NULL, NULL, 0, 0),
('G', 'Antonio Junior', 'De Toma', 'asd', NULL, 'male', '2015-10-10', 'Bisceglie', 'Ciao, questa è una bio di prova', 'T', 'U', NULL, 22, 1),
('H', 'Simona', 'De Toma', 'gdgsdf', NULL, 'female', '2015-10-14', 'Simona', 'Ciao, questa è una bio di prova', 'T', 'U', NULL, 22, 1),
('I', 'Claudio', 'Vincenti', 'ghdhdf', NULL, 'male', '2015-10-01', 'Lecce', 'Ciao, questa è una bio di prova', NULL, NULL, 'L', 22, 1),
('L', 'Stella', 'Dalto', 'stella', NULL, 'female', '2015-10-14', 'Barletta', 'Ciao, questa è una bio di prova', 'Z', 'V', 'I', 22, 1),
('M', 'Mariella', 'Dalto', 'kfdljnskfd', NULL, 'female', '2015-10-23', 'Barletta', 'Ciao, questa è una bio di prova', 'Z', 'V', NULL, 22, 1),
('N', 'Grazia', 'dalto', 'grazia.dalto', NULL, 'female', '2014-06-24', 'Barletta', 'Ciao sono grazia', 'Z', 'V', 'O', 22, 1),
('NZ65UU87OT', 'marco', 'de toma', NULL, NULL, 'male', '1994-01-09', 'bareltta', '', NULL, NULL, NULL, 0, 1),
('O', 'Nicola', 'De toma', 'nicola', '8c6976e5b541415bde98bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'male', '2015-10-06', 'Bisceglie', 'Ciao sono nicola', 'X', 'Y', 'N', 22, 1),
('P', 'Concetta', 'De Toma', 'dadfsfd', NULL, 'female', '2015-10-12', 'Bisceglie', 'Ciao, questa è una bio di prova', 'X', 'Y', 'Q', 22, 1),
('Q', 'Lorenzo', 'Sabatini', 'hnbzz', NULL, 'male', '2015-10-14', 'Maddalena', 'Ciao, questa è una bio di prova', NULL, NULL, 'P', 22, 1),
('R', 'Silvia', 'De Toma', NULL, NULL, 'female', '2015-10-14', 'Bisceglie', 'Ciao, questa è una bio di prova', 'X', 'Y', 'S', 22, 1),
('S', 'Tommaso', 'Fazio', 'hydtgxf', NULL, 'male', '2015-10-14', 'Trani', 'Ciao, questa è una bio di prova', NULL, NULL, 'R', 22, 1),
('T', 'Demetrio', 'De Toma', 'dasjnasdkjak', NULL, 'male', '2015-10-22', 'Bisceglie', 'Ciao, questa è una bio di prova', 'X', 'Y', 'U', 22, 1),
('U', 'Pina', 'Amoruso', 'mnldfmlcz', NULL, 'female', '2015-10-20', 'Bisceglie', 'Ciao, questa è una bio di prova', NULL, NULL, 'T', 22, 1),
('V', 'Francesca', 'albanese', 'francesca', '8c6976e5b541415bde98bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'female', '0014-06-17', 'bari', 'Ciao, questa è una bio di prova', NULL, NULL, 'Z', 22, 1),
('X', 'Antonio Senior', 'De Toma', 'asddasdgsdf', NULL, 'male', '2015-10-15', 'Bisceglie', 'Ciao, questa è una bio di prova', NULL, NULL, 'Y', 22, 1),
('Y', 'Maria', 'Ricchiuti', 'gfdg', NULL, 'male', '2015-10-11', 'Procida', 'Ciao, questa è una bio di prova', NULL, NULL, 'X', 22, 1),
('Z', 'Raffaele', 'dalto', 'raffaele.dalto', NULL, 'male', '0014-06-17', 'Barletta', 'Ciao, questa è una bio di prova', NULL, NULL, 'V', 22, 1);

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
