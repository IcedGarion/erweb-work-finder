SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

CREATE DATABASE IF NOT EXISTS `erweb` DEFAULT CHARACTER SET utf8 COLLATE utf8_roman_ci;
USE `erweb`;

CREATE TABLE `bando` (
  `CD_BANDO` bigint(20) NOT NULL COMMENT 'Identificativo del bando,  generato in automatico ',
  `CD_ESTERNO` varchar(250) COLLATE utf8_roman_ci NOT NULL COMMENT 'Codice del bando che si trova nella pubblicazione ',
  `CD_PUBBLICAZIONE` bigint(20) NOT NULL COMMENT 'Rappresenta la relazione fra BANDO e  PUBBLICAZIONE ',
  `CIG` varchar(250) COLLATE utf8_roman_ci DEFAULT NULL COMMENT 'Codice Identificativo Gara riportato sul bando ',
  `TIPO` varchar(50) COLLATE utf8_roman_ci DEFAULT NULL COMMENT 'Bando di Gara  /  Estratto di bando di gara / Bando di gara a procedura aperta ',
  `TIPORICHIEDENTE` varchar(100) COLLATE utf8_roman_ci DEFAULT NULL COMMENT 'Ministeri / Regioni / Province / ... ',
  `NM_RICHIEDENTE` varchar(1000) COLLATE utf8_roman_ci DEFAULT NULL COMMENT 'Il nome dell''ente richiedente (Associazione o privato) ',
  `SCADENZA` date DEFAULT NULL COMMENT 'La data di scadenza del bando, riportata sullo stesso ',
  `OGGETTO` varchar(2000) COLLATE utf8_roman_ci DEFAULT NULL COMMENT 'Oggetto del bando',
  `TESTO` text COLLATE utf8_roman_ci COMMENT 'Tutto il testo integrale del bando ',
  `URL` varchar(1000) COLLATE utf8_roman_ci NOT NULL COMMENT 'Indirizzo web identificativo del bando',
  `STATO` enum('DA_PARSIFICARE','PARSIFICATO') COLLATE utf8_roman_ci NOT NULL,
  `DT_INSERIMENTO` datetime NOT NULL COMMENT 'Data di inserimento del bando nel database, che corrisponde alla data di inserimento della pubblicazione '
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_roman_ci;

CREATE TABLE `expreg` (
  `CD_EXPREG` bigint(20) NOT NULL COMMENT 'Identificativo dell''espressione, assegnato in automatico. ',
  `CD_UTENTE` bigint(20) NOT NULL COMMENT 'Rappresenta la relazione fra EXPREG e UTENTE ',
  `EXPPLUS` varchar(1000) COLLATE utf8_roman_ci NOT NULL COMMENT 'L''espressione regolare vera e propria, di tipo PLUS ',
  `EXPMINUS` varchar(1000) COLLATE utf8_roman_ci NOT NULL COMMENT 'L''espressione regolare vera e propria, di tipo MINUS'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_roman_ci;

CREATE TABLE `notifica` (
  `DT_NOTIFICA` datetime NOT NULL COMMENT 'Data dell''inserimento della notifica',
  `CD_UTENTE` bigint(20) NOT NULL COMMENT 'Rappresenta la relazione fra UTENTE e NOTIFICA',
  `CD_BANDO` bigint(20) NOT NULL COMMENT 'Rappresenta la relazione fra BANDO e NOTIFICA',
  `STATO` enum('DA_INVIARE','INVIATO','','') COLLATE utf8_roman_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_roman_ci;

CREATE TABLE `pubblicazione` (
  `CD_PUBBLICAZIONE` bigint(20) NOT NULL COMMENT 'Identificativo della pubblicazione. Popolato  automaticamente ',
  `DT_INSERIMENTO` datetime NOT NULL COMMENT 'Data del''inserimento della pubblicazione  nel database ',
  `NM_PUBBLICAZIONE` int(11) NOT NULL COMMENT 'Numero della pubblicazione',
  `STATO` enum('DA_SCARICARE','SCARICATA') COLLATE utf8_roman_ci NOT NULL,
  `URL` varchar(1000) COLLATE utf8_roman_ci NOT NULL COMMENT 'Indirizzo URL della pubblicazione '
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_roman_ci;

CREATE TABLE `utente` (
  `CD_UTENTE` bigint(20) NOT NULL COMMENT 'Identificativo dell''utente, assegnato in automatico. ',
  `USERNAME` varchar(50) COLLATE utf8_roman_ci NOT NULL COMMENT 'Pseudonimo (univoco) dell''utente, scelto dallo stesso in fase di registrazione ',
  `PASSWORD` varchar(255) COLLATE utf8_roman_ci NOT NULL COMMENT 'Password decisa dall''utente in fase di registrazione e di cui si calcola la funzione Hash MD5',
  `EMAIL` varchar(255) COLLATE utf8_roman_ci NOT NULL COMMENT 'Email decisa in registrazione',
  `DT_NOTIFICA` datetime NOT NULL COMMENT 'Data in cui è avvenuta l''ultima notifica di nuovo bando'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_roman_ci;


ALTER TABLE `bando`
  ADD PRIMARY KEY (`CD_BANDO`),
  ADD UNIQUE KEY `CD_ESTERNO` (`CD_ESTERNO`),
  ADD KEY `CD_PUBBLICAZIONE` (`CD_PUBBLICAZIONE`);

ALTER TABLE `expreg`
  ADD PRIMARY KEY (`CD_EXPREG`),
  ADD KEY `CD_UTENTE` (`CD_UTENTE`);

ALTER TABLE `notifica`
  ADD PRIMARY KEY (`CD_UTENTE`,`CD_BANDO`),
  ADD KEY `CD_UTENTE` (`CD_UTENTE`,`CD_BANDO`),
  ADD KEY `CD_BANDO` (`CD_BANDO`);

ALTER TABLE `pubblicazione`
  ADD PRIMARY KEY (`CD_PUBBLICAZIONE`);

ALTER TABLE `utente`
  ADD PRIMARY KEY (`CD_UTENTE`),
  ADD UNIQUE KEY `USERNAME` (`USERNAME`);


ALTER TABLE `bando`
  MODIFY `CD_BANDO` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Identificativo del bando,  generato in automatico ', AUTO_INCREMENT=7951;
ALTER TABLE `expreg`
  MODIFY `CD_EXPREG` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Identificativo dell''espressione, assegnato in automatico. ', AUTO_INCREMENT=3;
ALTER TABLE `pubblicazione`
  MODIFY `CD_PUBBLICAZIONE` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Identificativo della pubblicazione. Popolato  automaticamente ', AUTO_INCREMENT=96;
ALTER TABLE `utente`
  MODIFY `CD_UTENTE` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Identificativo dell''utente, assegnato in automatico. ', AUTO_INCREMENT=3;

ALTER TABLE `bando`
  ADD CONSTRAINT `FKd4fhf2qke4l7da42a3n66pt21` FOREIGN KEY (`CD_PUBBLICAZIONE`) REFERENCES `pubblicazione` (`CD_PUBBLICAZIONE`),
  ADD CONSTRAINT `bando_foreign_key_pub` FOREIGN KEY (`CD_PUBBLICAZIONE`) REFERENCES `pubblicazione` (`CD_PUBBLICAZIONE`);

ALTER TABLE `expreg`
  ADD CONSTRAINT `FKej636i71sjaq705khlueb8bgg` FOREIGN KEY (`CD_UTENTE`) REFERENCES `utente` (`CD_UTENTE`),
  ADD CONSTRAINT `expreg_foreign_key_utente` FOREIGN KEY (`CD_UTENTE`) REFERENCES `utente` (`CD_UTENTE`);

ALTER TABLE `notifica`
  ADD CONSTRAINT `notifica_ibfk_1` FOREIGN KEY (`CD_UTENTE`) REFERENCES `utente` (`CD_UTENTE`),
  ADD CONSTRAINT `notifica_ibfk_2` FOREIGN KEY (`CD_BANDO`) REFERENCES `bando` (`CD_BANDO`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
