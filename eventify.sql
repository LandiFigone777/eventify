-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versione server:              10.4.14-MariaDB - mariadb.org binary distribution
-- S.O. server:                  Win64
-- HeidiSQL Versione:            12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dump della struttura del database eventify
CREATE DATABASE IF NOT EXISTS `eventify` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `eventify`;

-- Dump della struttura di tabella eventify.evento
CREATE TABLE IF NOT EXISTS `evento` (
  `id_evento` int(11) NOT NULL AUTO_INCREMENT,
  `creatore` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `data_ora_inizio` datetime NOT NULL,
  `data_ora_fine` datetime NOT NULL,
  `stato` varchar(255) DEFAULT NULL,
  `citta` varchar(255) DEFAULT NULL,
  `cap` varchar(255) DEFAULT NULL,
  `via` varchar(255) DEFAULT NULL,
  `num_civico` varchar(255) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `visibilita` int(11) DEFAULT NULL,
  `descrizione` varchar(255) DEFAULT NULL,
  `costo` float DEFAULT NULL,
  `eta_minima` tinyint(5) NOT NULL,
  `partecipanti_max` mediumint(9) NOT NULL DEFAULT 0,
  `likes` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_evento`),
  KEY `creatore` (`creatore`),
  CONSTRAINT `fk_evento_creatore` FOREIGN KEY (`creatore`) REFERENCES `utente` (`email`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dump dei dati della tabella eventify.evento: ~0 rows (circa)

-- Dump della struttura di tabella eventify.followers
CREATE TABLE IF NOT EXISTS `followers` (
  `follower` varchar(255) DEFAULT NULL,
  `followed` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `follower` (`follower`),
  KEY `followed` (`followed`),
  CONSTRAINT `fk_followers_followed` FOREIGN KEY (`followed`) REFERENCES `utente` (`email`) ON DELETE CASCADE,
  CONSTRAINT `fk_followers_follower` FOREIGN KEY (`follower`) REFERENCES `utente` (`email`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dump dei dati della tabella eventify.followers: ~0 rows (circa)

-- Dump della struttura di tabella eventify.immagini_evento
CREATE TABLE IF NOT EXISTS `immagini_evento` (
  `uri_immagine` varchar(255) NOT NULL,
  `id_evento` int(11) NOT NULL,
  PRIMARY KEY (`uri_immagine`),
  KEY `id_evento` (`id_evento`),
  CONSTRAINT `fk_immagini_evento_id_evento` FOREIGN KEY (`id_evento`) REFERENCES `evento` (`id_evento`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dump dei dati della tabella eventify.immagini_evento: ~0 rows (circa)

-- Dump della struttura di tabella eventify.partecipazione
CREATE TABLE IF NOT EXISTS `partecipazione` (
  `email` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_evento` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `email` (`email`),
  KEY `fk_partecipazione_id_evento` (`id_evento`),
  CONSTRAINT `fk_partecipazione_email` FOREIGN KEY (`email`) REFERENCES `utente` (`email`) ON DELETE CASCADE,
  CONSTRAINT `fk_partecipazione_id_evento` FOREIGN KEY (`id_evento`) REFERENCES `evento` (`id_evento`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dump dei dati della tabella eventify.partecipazione: ~0 rows (circa)

-- Dump della struttura di tabella eventify.utente
CREATE TABLE IF NOT EXISTS `utente` (
  `email` varchar(255) NOT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `cognome` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `data_nascita` date DEFAULT NULL,
  `stato` varchar(255) DEFAULT NULL,
  `citta` varchar(255) DEFAULT NULL,
  `cap` varchar(255) DEFAULT NULL,
  `via` varchar(255) DEFAULT NULL,
  `num_civico` varchar(255) DEFAULT NULL,
  `verificationCode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dump dei dati della tabella eventify.utente: ~0 rows (circa)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
