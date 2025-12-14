-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 15-12-2025 a las 00:30:06
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `gestion_docentes_db`
--

--
-- Volcado de datos para la tabla `departamento`
--

INSERT INTO `departamento` (`id`, `codigo`, `nombre`, `telefono`) VALUES
(1, 'IFC', 'Informática y Comunicaciones', '984100101'),
(2, 'ELE', 'Electricidad y Electrónica', '984100102'),
(3, 'FME', 'Fabricación Mecánica', '984100103'),
(4, 'IMA', 'Instalación y Mantenimiento', '984100104'),
(5, 'QUI', 'Química', '984100105'),
(6, 'ADG', 'Administración y Gestión', '984100109'),
(7, 'COM', 'Comercio y Marketing', '984100110'),
(8, 'EOC', 'Edificación y Obra Civil', '984100113');

--
-- Volcado de datos para la tabla `docente`
--

INSERT INTO `docente` (`id`, `apellidos`, `email`, `fecha_antiguedad`, `nombre`, `password`, `posicion`, `siglas`, `departamento_id`) VALUES
(1, 'Martínez López', 'sergioml@educastur.org', '2020-08-29', 'Sergio', 'Temporal01', 74, 'MLSe', 1),
(2, 'González Pérez', 'lauragp@educastur.org', '2010-10-13', 'Laura', 'Temporal01', 30, 'GPLa', 1),
(3, 'Álvarez Rubio', 'davidar@educastur.org', '2024-12-25', 'David', 'Temporal01', 28, 'ARDa', 1),
(4, 'Fernández Álvarez', 'diegofa@educastur.org', '2025-08-13', 'Diego', 'Temporal01', 50, 'FADi', 2),
(5, 'Suárez Prieto', 'mariasu@educastur.org', '2020-03-03', 'María', 'Temporal01', 65, 'SPMa', 2),
(6, 'Campos Rubio', 'javiercr@educastur.org', '2024-01-19', 'Javier', 'Temporal01', 75, 'CRJa', 3),
(7, 'García Prado', 'hugogp@educastur.org', '2019-10-14', 'Hugo', 'Temporal01', 79, 'GPHu', 4),
(8, 'López Castro', 'elenalc@educastur.org', '2015-10-27', 'Elena', 'Temporal01', 70, 'LCEl', 4),
(9, 'Santos Vega', 'danielsv@educastur.org', '2023-10-20', 'Daniel', 'Temporal01', 14, 'SVDa', 5),
(10, 'Ortega Rivas', 'luciaor@educastur.org', '2015-08-02', 'Lucía', 'Temporal01', 58, 'ORLu', 5),
(11, 'Lago Souto', 'noelials@educastur.org', '2020-07-25', 'Noelia', 'Temporal01', 49, 'LSNo', 5),
(12, 'Sánchez Rojo', 'beatrizsr@educastur.org', '2017-02-12', 'Beatriz', 'Temporal01', 68, 'SRBe', 6),
(13, 'Vega Soto', 'raquelvs@educastur.org', '2024-12-08', 'Raquel', 'Temporal01', 93, 'SVRa', 7),
(14, 'Lorenzo Díaz', 'albertold@educastur.org', '2022-05-16', 'Alberto', 'Temporal01', 61, 'LDAl', 7),
(15, 'Rey Castro', 'martarc@educastur.org', '2013-02-05', 'Marta', 'Temporal01', 25, 'CRMa', 7);

--
-- Volcado de datos para la tabla `docente_dias`
--

INSERT INTO `docente_dias` (`docente_id`, `dias`, `trimestre_index`) VALUES
(1, 3, 0),
(1, 3, 1),
(1, 3, 2),
(2, 3, 0),
(2, 3, 1),
(2, 3, 2),
(3, 3, 0),
(3, 3, 1),
(3, 3, 2),
(4, 3, 0),
(4, 3, 1),
(4, 3, 2),
(5, 3, 0),
(5, 3, 1),
(5, 3, 2),
(6, 3, 0),
(6, 3, 1),
(6, 3, 2),
(7, 3, 0),
(7, 3, 1),
(7, 3, 2),
(8, 3, 0),
(8, 3, 1),
(8, 3, 2),
(9, 3, 0),
(9, 3, 1),
(9, 3, 2),
(10, 3, 0),
(10, 3, 1),
(10, 3, 2),
(11, 3, 0),
(11, 3, 1),
(11, 3, 2),
(12, 3, 0),
(12, 3, 1),
(12, 3, 2),
(13, 3, 0),
(13, 3, 1),
(13, 3, 2),
(14, 3, 0),
(14, 3, 1),
(14, 3, 2),
(15, 3, 0),
(15, 3, 1),
(15, 3, 2);

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id`, `orden`, `nombre`, `docente_id`) VALUES
(1, 5, 'CARRERA', 1),
(2, 15, 'INTERINO', 2),
(3, 0, 'CARRERA', 3),
(4, 0, 'PRACTICAS', 4),
(5, 5, 'INTERINO', 5),
(6, 1, 'CARRERA', 6),
(7, 6, 'CARRERA', 7),
(8, 10, 'INTERINO', 8),
(9, 2, 'CARRERA', 9),
(10, 10, 'CARRERA', 10),
(11, 5, 'INTERINO', 11),
(12, 8, 'CARRERA', 12),
(13, 1, 'PRACTICAS', 13),
(14, 3, 'CARRERA', 14),
(15, 12, 'CARRERA', 15);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
