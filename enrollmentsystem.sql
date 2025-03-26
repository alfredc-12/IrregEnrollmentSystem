-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 26, 2025 at 12:55 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `enrollmentsystem`
--

-- --------------------------------------------------------

--
-- Table structure for table `faculty`
--

CREATE TABLE `faculty` (
  `faculty_id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `middle_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) NOT NULL,
  `role` enum('Professor','Assistant Professor','Lecturer','Program Chair','Guest Lecturer','Dean') NOT NULL,
  `email` varchar(100) NOT NULL,
  `pic_link` varchar(255) DEFAULT NULL,
  `sign_link` varchar(255) DEFAULT NULL,
  `max_subjects` int(11) DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `faculty`
--

INSERT INTO `faculty` (`faculty_id`, `first_name`, `middle_name`, `last_name`, `role`, `email`, `pic_link`, `sign_link`, `max_subjects`) VALUES
(1, 'John', 'A.', 'Doe', 'Professor', 'john.doe@example.com', 'images/john_doe.jpg', 'signatures/john_doe.png', 3),
(2, 'Jane', 'B.', 'Smith', 'Assistant Professor', 'jane.smith@example.com', 'images/jane_smith.jpg', 'signatures/jane_smith.png', 2),
(3, 'Michael', 'C.', 'Johnson', 'Lecturer', 'michael.johnson@example.com', 'images/michael_johnson.jpg', 'signatures/michael_johnson.png', 4),
(4, 'Emily', 'D.', 'Brown', 'Professor', 'emily.brown@example.com', 'images/emily_brown.jpg', 'signatures/emily_brown.png', 3),
(5, 'David', 'E.', 'Wilson', 'Assistant Professor', 'david.wilson@example.com', 'images/david_wilson.jpg', 'signatures/david_wilson.png', 2);

-- --------------------------------------------------------

--
-- Table structure for table `guardian`
--

CREATE TABLE `guardian` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `middle_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `contact_no` varchar(50) DEFAULT NULL,
  `relationship` enum('Mother','Father','Guardian') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `logs`
--

CREATE TABLE `logs` (
  `id` int(11) NOT NULL,
  `record_id` int(11) NOT NULL,
  `table_name` varchar(100) NOT NULL,
  `action` enum('Add','Update','Delete') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `rooms`
--

CREATE TABLE `rooms` (
  `room_id` int(11) NOT NULL,
  `room_name` varchar(100) NOT NULL,
  `room_type` enum('Lecture','Lab') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `rooms`
--

INSERT INTO `rooms` (`room_id`, `room_name`, `room_type`) VALUES
(1, 'Room A', 'Lecture'),
(2, 'Room B', 'Lab'),
(3, 'Room D', 'Lecture'),
(4, 'Room E', 'Lab');

-- --------------------------------------------------------

--
-- Table structure for table `section`
--

CREATE TABLE `section` (
  `section_id` int(11) NOT NULL,
  `section_name` varchar(100) NOT NULL,
  `department` enum('CICS','CAS','CABEIHM','CHS','CTE','CCJE') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `section`
--

INSERT INTO `section` (`section_id`, `section_name`, `department`) VALUES
(1, 'BSIT-1A', 'CICS'),
(2, 'BSIT-1B', 'CICS'),
(3, 'BSCS-2A', 'CICS'),
(4, 'BSCS-2B', 'CICS'),
(5, 'BSIS-3A', 'CICS');

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `middle_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) NOT NULL,
  `pic_link` text DEFAULT NULL,
  `sr_code` varchar(100) NOT NULL,
  `year_level` varchar(50) NOT NULL,
  `program` varchar(100) NOT NULL,
  `major` varchar(100) NOT NULL,
  `contact` varchar(50) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `status` enum('Enrolled','Not Enrolled') NOT NULL DEFAULT 'Not Enrolled',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_section`
--

CREATE TABLE `student_section` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subjects`
--

CREATE TABLE `subjects` (
  `sub_id` int(11) NOT NULL,
  `subject_name` varchar(100) NOT NULL,
  `credit_hours` int(11) NOT NULL,
  `is_major` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`sub_id`, `subject_name`, `credit_hours`, `is_major`) VALUES
(1, 'Mathematics', 3, 1),
(2, 'Physics', 3, 1),
(3, 'History', 2, 0),
(4, 'Chemistry', 3, 1),
(5, 'Art', 2, 0);

-- --------------------------------------------------------

--
-- Table structure for table `subsched`
--

CREATE TABLE `subsched` (
  `sched_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `time_in` time NOT NULL,
  `time_out` time NOT NULL,
  `days` set('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  `room_id` int(11) DEFAULT NULL,
  `faculty_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subsched`
--

INSERT INTO `subsched` (`sched_id`, `subject_id`, `time_in`, `time_out`, `days`, `room_id`, `faculty_id`, `section_id`) VALUES
(3, 3, '10:00:00', '12:00:00', 'Monday', 1, 1, 3),
(4, 3, '12:00:00', '14:00:00', 'Monday', 1, 1, 4),
(5, 3, '14:00:00', '16:00:00', 'Monday', 1, 1, 5),
(6, 1, '08:00:00', '09:30:00', 'Monday', 1, 2, 1),
(7, 1, '08:00:00', '09:30:00', 'Thursday', 1, 2, 2),
(8, 1, '08:00:00', '09:30:00', 'Friday', 1, 2, 3),
(9, 1, '08:00:00', '09:30:00', 'Tuesday', 1, 2, 4),
(10, 1, '08:00:00', '09:30:00', 'Wednesday', 1, 2, 5),
(11, 1, '08:00:00', '09:30:00', 'Thursday', 2, 2, 1),
(12, 1, '08:00:00', '09:30:00', 'Wednesday', 2, 2, 2),
(13, 1, '08:00:00', '09:30:00', 'Monday', 2, 2, 3),
(14, 1, '08:00:00', '09:30:00', 'Friday', 2, 2, 4),
(15, 1, '08:00:00', '09:30:00', 'Tuesday', 2, 2, 5);

-- --------------------------------------------------------

--
-- Table structure for table `substud`
--

CREATE TABLE `substud` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `subsched_id` int(11) NOT NULL,
  `grade` decimal(5,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `time_slots`
--

CREATE TABLE `time_slots` (
  `id` int(11) NOT NULL,
  `time_in` time NOT NULL,
  `time_out` time NOT NULL,
  `is_break` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `faculty`
--
ALTER TABLE `faculty`
  ADD PRIMARY KEY (`faculty_id`);

--
-- Indexes for table `guardian`
--
ALTER TABLE `guardian`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_student` (`student_id`);

--
-- Indexes for table `logs`
--
ALTER TABLE `logs`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`room_id`);

--
-- Indexes for table `section`
--
ALTER TABLE `section`
  ADD PRIMARY KEY (`section_id`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `student_section`
--
ALTER TABLE `student_section`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student_section_unique` (`student_id`,`section_id`),
  ADD KEY `fk_student_section_section` (`section_id`);

--
-- Indexes for table `subjects`
--
ALTER TABLE `subjects`
  ADD PRIMARY KEY (`sub_id`);

--
-- Indexes for table `subsched`
--
ALTER TABLE `subsched`
  ADD PRIMARY KEY (`sched_id`),
  ADD KEY `fk_subsched_subject` (`subject_id`),
  ADD KEY `fk_subsched_faculty` (`faculty_id`),
  ADD KEY `fk_subsched_section` (`section_id`),
  ADD KEY `fk_room_id` (`room_id`);

--
-- Indexes for table `substud`
--
ALTER TABLE `substud`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student_subsched_unique` (`student_id`,`subsched_id`),
  ADD KEY `fk_substud_subsched` (`subsched_id`);

--
-- Indexes for table `time_slots`
--
ALTER TABLE `time_slots`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `faculty`
--
ALTER TABLE `faculty`
  MODIFY `faculty_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `guardian`
--
ALTER TABLE `guardian`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `logs`
--
ALTER TABLE `logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `section`
--
ALTER TABLE `section`
  MODIFY `section_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `student`
--
ALTER TABLE `student`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `student_section`
--
ALTER TABLE `student_section`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `subjects`
--
ALTER TABLE `subjects`
  MODIFY `sub_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `subsched`
--
ALTER TABLE `subsched`
  MODIFY `sched_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `substud`
--
ALTER TABLE `substud`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `time_slots`
--
ALTER TABLE `time_slots`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `guardian`
--
ALTER TABLE `guardian`
  ADD CONSTRAINT `fk_guardian_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `student_section`
--
ALTER TABLE `student_section`
  ADD CONSTRAINT `fk_student_section_section` FOREIGN KEY (`section_id`) REFERENCES `section` (`section_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_student_section_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `subsched`
--
ALTER TABLE `subsched`
  ADD CONSTRAINT `fk_room_id` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`),
  ADD CONSTRAINT `fk_subsched_faculty` FOREIGN KEY (`faculty_id`) REFERENCES `faculty` (`faculty_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_subsched_section` FOREIGN KEY (`section_id`) REFERENCES `section` (`section_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_subsched_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`sub_id`) ON DELETE CASCADE;

--
-- Constraints for table `substud`
--
ALTER TABLE `substud`
  ADD CONSTRAINT `fk_substud_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_substud_subsched` FOREIGN KEY (`subsched_id`) REFERENCES `subsched` (`sched_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
