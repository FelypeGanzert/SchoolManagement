-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cadastroprojetos
-- ------------------------------------------------------
-- Server version	8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `acordo`
--

DROP TABLE IF EXISTS `acordo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acordo` (
  `code` int NOT NULL AUTO_INCREMENT,
  `acordo_por` varchar(50) DEFAULT NULL,
  `cancelado` tinyint(1) DEFAULT NULL,
  `data_acordo` date DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `valor_para_considerar` varchar(30) DEFAULT NULL,
  `matricula_codigo` int DEFAULT NULL,
  PRIMARY KEY (`code`),
  KEY `FKlpt54v30syhabloqm3o5lo0i0` (`matricula_codigo`),
  CONSTRAINT `FKlpt54v30syhabloqm3o5lo0i0` FOREIGN KEY (`matricula_codigo`) REFERENCES `matricula` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `aluno`
--

DROP TABLE IF EXISTS `aluno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aluno` (
  `id` int NOT NULL AUTO_INCREMENT,
  `endereco` varchar(50) DEFAULT NULL,
  `local_referencia` varchar(50) DEFAULT NULL,
  `cidade` varchar(50) DEFAULT NULL,
  `estado_civil` varchar(20) DEFAULT NULL,
  `cpf` varchar(20) DEFAULT NULL,
  `data_nascimento` datetime DEFAULT NULL,
  `data_ultima_edicao` datetime DEFAULT NULL,
  `data_cadastro` datetime DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `sexo` varchar(20) DEFAULT NULL,
  `nome` varchar(50) DEFAULT NULL,
  `bairro` varchar(50) DEFAULT NULL,
  `observacao` text,
  `ra_antigo` int DEFAULT NULL,
  `cadastrado_por` varchar(50) DEFAULT NULL,
  `rg` varchar(20) DEFAULT NULL,
  `send_email` tinyint(1) DEFAULT '0',
  `situacao` varchar(50) DEFAULT NULL,
  `uf` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `anotacoes`
--

DROP TABLE IF EXISTS `anotacoes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anotacoes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `data` datetime DEFAULT NULL,
  `descricao` text,
  `excluido` varchar(1) DEFAULT NULL,
  `colaborador_responsavel` varchar(50) DEFAULT NULL,
  `aluno_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKg2kcoh67ah2qqpdr0jm6u8f3j` (`aluno_id`),
  CONSTRAINT `FKg2kcoh67ah2qqpdr0jm6u8f3j` FOREIGN KEY (`aluno_id`) REFERENCES `aluno` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cargos`
--

DROP TABLE IF EXISTS `cargos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `excluido` varchar(1) DEFAULT NULL,
  `cargo` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `certificado_historico`
--

DROP TABLE IF EXISTS `certificado_historico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificado_historico` (
  `id` int NOT NULL AUTO_INCREMENT,
  `curso` text,
  `carga_horaria` int DEFAULT NULL,
  `data_termino` datetime DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `data_emissao` datetime DEFAULT NULL,
  `ata_numero` int DEFAULT NULL,
  `ata_numero_pagina` int DEFAULT NULL,
  `data_inicio` datetime DEFAULT NULL,
  `aluno_nome` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `certificado_pedido`
--

DROP TABLE IF EXISTS `certificado_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificado_pedido` (
  `id` int NOT NULL AUTO_INCREMENT,
  `curso` text,
  `carga_horaria` int DEFAULT NULL,
  `data_termino` datetime DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `data_pedido` datetime DEFAULT NULL,
  `data_inicio` datetime DEFAULT NULL,
  `aluno_id` int DEFAULT NULL,
  `aluno_nome` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `colaborador`
--

DROP TABLE IF EXISTS `colaborador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `colaborador` (
  `id` int NOT NULL AUTO_INCREMENT,
  `endereco` varchar(50) DEFAULT NULL,
  `local_referencia` varchar(50) DEFAULT NULL,
  `cidade` varchar(50) DEFAULT NULL,
  `estado_civil` varchar(20) DEFAULT NULL,
  `numero_contato` varchar(50) DEFAULT NULL,
  `cpf` varchar(20) DEFAULT NULL,
  `data_nascimento` datetime DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `sexo` varchar(20) DEFAULT NULL,
  `sigla` varchar(30) DEFAULT NULL,
  `nome` varchar(50) DEFAULT NULL,
  `bairro` varchar(50) DEFAULT NULL,
  `senha_login` varchar(30) DEFAULT NULL,
  `cargo` varchar(50) DEFAULT NULL,
  `rg` varchar(20) DEFAULT NULL,
  `uf` varchar(20) DEFAULT NULL,
  `usuario_login` varchar(30) DEFAULT NULL,
  `cronograma_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpghk37xo2fa59v476qsl2s5op` (`cronograma_id`),
  CONSTRAINT `FKpghk37xo2fa59v476qsl2s5op` FOREIGN KEY (`cronograma_id`) REFERENCES `colaborador_cronograma` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `colaborador_cronograma`
--

DROP TABLE IF EXISTS `colaborador_cronograma`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `colaborador_cronograma` (
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contato`
--

DROP TABLE IF EXISTS `contato`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contato` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descricao` varchar(30) DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `numero` varchar(20) DEFAULT NULL,
  `contact_id_student` int DEFAULT NULL,
  `contact_id_responsible` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgcss0k7nt3lgu5el7sl38nkxp` (`contact_id_student`),
  KEY `FKbrd1ofqg6vd3ycdv0i1hpqudh` (`contact_id_responsible`),
  CONSTRAINT `FKbrd1ofqg6vd3ycdv0i1hpqudh` FOREIGN KEY (`contact_id_responsible`) REFERENCES `responsavel` (`id`),
  CONSTRAINT `FKgcss0k7nt3lgu5el7sl38nkxp` FOREIGN KEY (`contact_id_student`) REFERENCES `aluno` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cursos`
--

DROP TABLE IF EXISTS `cursos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cursos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `carga_horaria` int DEFAULT NULL,
  `dia` varchar(30) DEFAULT NULL,
  `data_fim` datetime DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `horario` varchar(30) DEFAULT NULL,
  `matriculation_code` int DEFAULT NULL,
  `nome` varchar(50) DEFAULT NULL,
  `professor` varchar(30) DEFAULT NULL,
  `data_inicio` datetime DEFAULT NULL,
  `aluno_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4d20icr5qjuc2xosv5c7w7jjf` (`aluno_id`),
  CONSTRAINT `FK4d20icr5qjuc2xosv5c7w7jjf` FOREIGN KEY (`aluno_id`) REFERENCES `aluno` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dia`
--

DROP TABLE IF EXISTS `dia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dia` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tarde` text,
  `manha` text,
  `noite` text,
  `dia_semana` int DEFAULT NULL,
  `cronograma_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5oxkhbkv957l6oa978wl5ca9x` (`cronograma_id`),
  CONSTRAINT `FK5oxkhbkv957l6oa978wl5ca9x` FOREIGN KEY (`cronograma_id`) REFERENCES `colaborador_cronograma` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `matricula`
--

DROP TABLE IF EXISTS `matricula`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matricula` (
  `code` int NOT NULL AUTO_INCREMENT,
  `data_matricula` date DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `matriculado_por` varchar(50) DEFAULT NULL,
  `motivo` varchar(50) DEFAULT NULL,
  `servico_contratado` text,
  `situacao` varchar(50) DEFAULT NULL,
  `responsavel_financeiro_id` int DEFAULT NULL,
  `aluno_id` int DEFAULT NULL,
  PRIMARY KEY (`code`),
  KEY `FKheytngbnrwv9xa1sxgtwmw2p7` (`responsavel_financeiro_id`),
  KEY `FKsmkrefmwkwfyod36k45jf1rr5` (`aluno_id`),
  CONSTRAINT `FKheytngbnrwv9xa1sxgtwmw2p7` FOREIGN KEY (`responsavel_financeiro_id`) REFERENCES `responsavel` (`id`),
  CONSTRAINT `FKsmkrefmwkwfyod36k45jf1rr5` FOREIGN KEY (`aluno_id`) REFERENCES `aluno` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `parcela`
--

DROP TABLE IF EXISTS `parcela`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parcela` (
  `numero_documento` int NOT NULL AUTO_INCREMENT,
  `data_parcela` datetime DEFAULT NULL,
  `data_pagamento` datetime DEFAULT NULL,
  `dias_multa_atraso` int DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `pago_com` varchar(50) DEFAULT NULL,
  `parcela_numero` int DEFAULT NULL,
  `pagamento_recebido_por` varchar(50) DEFAULT NULL,
  `situacao` varchar(50) DEFAULT NULL,
  `valor` decimal(19,4) DEFAULT NULL,
  `valor_multa_atraso` decimal(19,4) DEFAULT NULL,
  `valor_pago` decimal(19,4) DEFAULT NULL,
  `acordo_codigo` int DEFAULT NULL,
  `matricula_codigo` int DEFAULT NULL,
  PRIMARY KEY (`numero_documento`),
  KEY `FKsbqiccuqly4xdi5h3rm1ydbgo` (`acordo_codigo`),
  KEY `FK2psyejg22fld7id7yfd1xs0rl` (`matricula_codigo`),
  CONSTRAINT `FK2psyejg22fld7id7yfd1xs0rl` FOREIGN KEY (`matricula_codigo`) REFERENCES `matricula` (`code`),
  CONSTRAINT `FKsbqiccuqly4xdi5h3rm1ydbgo` FOREIGN KEY (`acordo_codigo`) REFERENCES `acordo` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `parcela_acordo`
--

DROP TABLE IF EXISTS `parcela_acordo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parcela_acordo` (
  `numero_documento` int NOT NULL AUTO_INCREMENT,
  `data_parcela` datetime DEFAULT NULL,
  `data_pagamento` datetime DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `pago_com` varchar(50) DEFAULT NULL,
  `parcela_numero` int DEFAULT NULL,
  `pagamento_recebido_por` varchar(50) DEFAULT NULL,
  `situacao` varchar(50) DEFAULT NULL,
  `valor` decimal(19,4) DEFAULT NULL,
  `valor_pago` decimal(19,4) DEFAULT NULL,
  `acordo_codigo` int DEFAULT NULL,
  `matricula_codigo` int DEFAULT NULL,
  `parcela_normal_numero` int DEFAULT NULL,
  PRIMARY KEY (`numero_documento`),
  KEY `FKta2gabq6p11va1cl3lc8jngte` (`acordo_codigo`),
  KEY `FK5ylynn1y8dgp2qouj5usy0cri` (`matricula_codigo`),
  KEY `FKnpknfj2lwrvs5tkjvs1i537x5` (`parcela_normal_numero`),
  CONSTRAINT `FK5ylynn1y8dgp2qouj5usy0cri` FOREIGN KEY (`matricula_codigo`) REFERENCES `matricula` (`code`),
  CONSTRAINT `FKnpknfj2lwrvs5tkjvs1i537x5` FOREIGN KEY (`parcela_normal_numero`) REFERENCES `parcela` (`numero_documento`),
  CONSTRAINT `FKta2gabq6p11va1cl3lc8jngte` FOREIGN KEY (`acordo_codigo`) REFERENCES `acordo` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `responsavel`
--

DROP TABLE IF EXISTS `responsavel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `responsavel` (
  `id` int NOT NULL AUTO_INCREMENT,
  `endereco` varchar(50) DEFAULT NULL,
  `local_referencia` varchar(50) DEFAULT NULL,
  `cidade` varchar(50) DEFAULT NULL,
  `estado_civil` varchar(20) DEFAULT NULL,
  `cpf` varchar(20) DEFAULT NULL,
  `data_nascimento` datetime DEFAULT NULL,
  `data_ultima_edicao` datetime DEFAULT NULL,
  `data_cadastro` datetime DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `excluido` varchar(1) DEFAULT NULL,
  `sexo` varchar(20) DEFAULT NULL,
  `nome` varchar(50) DEFAULT NULL,
  `bairro` varchar(50) DEFAULT NULL,
  `observacao` text,
  `cadastrado_por` varchar(50) DEFAULT NULL,
  `rg` varchar(20) DEFAULT NULL,
  `send_email` tinyint(1) DEFAULT '0',
  `uf` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `responsavel_aluno`
--

DROP TABLE IF EXISTS `responsavel_aluno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `responsavel_aluno` (
  `id` int NOT NULL AUTO_INCREMENT,
  `excluido` varchar(1) DEFAULT NULL,
  `parentesco` varchar(30) DEFAULT NULL,
  `responsavel_id` int NOT NULL,
  `aluno_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhnfi6dd5ju9b82d0u7quxbgfc` (`responsavel_id`),
  KEY `FK7ba5ednjoa9e7384lpw32gad3` (`aluno_id`),
  CONSTRAINT `FK7ba5ednjoa9e7384lpw32gad3` FOREIGN KEY (`aluno_id`) REFERENCES `aluno` (`id`),
  CONSTRAINT `FKhnfi6dd5ju9b82d0u7quxbgfc` FOREIGN KEY (`responsavel_id`) REFERENCES `responsavel` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-12-06 22:59:57
