-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 29, 2025 at 10:29 AM
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
  `contact_no` varchar(20) DEFAULT NULL,
  `personal_email` varchar(100) DEFAULT NULL,
  `bsu_email` varchar(100) DEFAULT NULL,
  `pic_link` varchar(255) DEFAULT NULL,
  `sign_link` varchar(255) DEFAULT NULL,
  `max_subjects` int(11) DEFAULT 2,
  `isDeleted` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `faculty`
--

INSERT INTO `faculty` (`faculty_id`, `first_name`, `middle_name`, `last_name`, `role`, `contact_no`, `personal_email`, `bsu_email`, `pic_link`, `sign_link`, `max_subjects`, `isDeleted`) VALUES
(1, 'Marlo', 'Humarang', 'Vasques', 'Assistant Professor', '0910153', 'marlo@gmai.com', 'marlo.vasques@g.batstate-u.edu.ph', 'https://drive.google.com/uc?export=view&id=1QW-jpyWak_yMo5vYuwb9zMZpnDME_GLK', 'https://drive.google.com/uc?export=view&id=1jJY-BkxVC6Egom7zB89OxMhtLl02y-9K', 2, 0),
(2, 'John', 'A.', 'Doe', 'Professor', '09123456789', 'johndoe@gmail.com', 'jdoe@bsu.edu', 'https://drive.google.com/uc?export=view&id=1QW-jpyWak_yMo5vYuwb9zMZpnDME_GLK', 'https://drive.google.com/uc?export=view&id=1zf-rRQl1GEuML4AqW862BJA0cyEP6Nln', 3, 0),
(3, 'Jane', 'B.', 'Smith', 'Assistant Professor', '09127654321', 'janesmith@gmail.com', 'jsmith@bsu.edu', 'https://drive.google.com/uc?export=view&id=1QW-jpyWak_yMo5vYuwb9zMZpnDME_GLK', 'https://drive.google.com/uc?export=view&id=1zf-rRQl1GEuML4AqW862BJA0cyEP6Nln', 2, 0),
(4, 'Robert', 'C.', 'Brown', 'Lecturer', '09129876543', 'robertbrown@gmail.com', 'rbrown@bsu.edu', 'https://drive.google.com/uc?export=view&id=1QW-jpyWak_yMo5vYuwb9zMZpnDME_GLK', 'https://drive.google.com/uc?export=view&id=1zf-rRQl1GEuML4AqW862BJA0cyEP6Nln', 4, 0),
(5, 'Alice', 'D.', 'Johnson', 'Professor', '09121234567', 'alicejohnson@gmail.com', 'alice.johnson@g.batstate-u.edu.ph', 'https://drive.google.com/uc?export=view&id=1QW-jpyWak_yMo5vYuwb9zMZpnDME_GLK', NULL, 5, 0),
(6, 'Michael', 'E.', 'Williams', 'Dean', '09123459876', 'michaelwilliams@gmail.com', 'mwilliams@bsu.edu', 'https://drive.google.com/uc?export=view&id=1QW-jpyWak_yMo5vYuwb9zMZpnDME_GLK', 'https://drive.google.com/uc?export=view&id=1zf-rRQl1GEuML4AqW862BJA0cyEP6Nln', 6, 0),
(7, 'fsgd', 'ghg', 'hhh', 'Assistant Professor', 'ghjg', 'marlo@gmail.com', 'fsgd.hhh@g.batstate-u.edu.ph', 'https://drive.google.com/uc?export=view&id=1kONN6eRWc0nTWWbvIxOP4x-J4BM4ClKk', 'https://drive.google.com/uc?export=view&id=14D6IrRIiU2xYv3EhdCbUvk6tLx5mddlb', NULL, 0);

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

--
-- Dumping data for table `guardian`
--

INSERT INTO `guardian` (`id`, `student_id`, `first_name`, `middle_name`, `last_name`, `address`, `email`, `contact_no`, `relationship`) VALUES
(1, 1, 'msfsdg', 'jhjv', 'dfg', 'sdgdf', 'sdgsd', 'dsgd', 'Guardian'),
(2, 2, '', '', '', '', '', '', 'Father'),
(3, 3, '', '', '', '', '', '', 'Father');

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
  `sign_link` text NOT NULL,
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

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`id`, `first_name`, `middle_name`, `last_name`, `pic_link`, `sign_link`, `sr_code`, `year_level`, `program`, `major`, `contact`, `email`, `password`, `address`, `status`, `is_deleted`) VALUES
(1, 'John', 'Michael', 'Doe', 'https://drive.google.com/uc?export=view&id=1eN8E_sjGnw63mF7q-9D3KbtJTGNtU4-v', '', 'SR001', '1', 'BSIT', 'Software Engineering', '09123456789', 'john.doe@example.com', 'password123', '123 Elm St', 'Not Enrolled', 0),
(2, 'Jane', 'Marie', 'Smith', 'https://drive.google.com/uc?export=view&id=1r7efa8tbGp6MRL6nAr235vIv3UkcC1Nt', '', 'SR002', '2', 'BSCS', 'Artificial Intelligence', '09234567890', 'jane.smith@example.com', 'pass456', '456 Oak St', 'Not Enrolled', 0),
(3, 'Alex', 'Hum', 'Johnson', 'https://drive.google.com/uc?export=view&id=1NGRa3NhcYJn3Lgoj-G2PUKw61iugXuuF', '', 'SR003', '1', 'BSIT', 'Cybersecurity', '09345678901', 'alex.johnson@example.com', 'qwerty', '789 Pine St', 'Not Enrolled', 0),
(4, 'Emily', 'Anne', 'Brown', NULL, '', 'SR004', '3', 'BSIS', 'Data Science', '09456789012', 'emily.brown@example.com', 'abc123', '101 Maple St', 'Not Enrolled', 0),
(5, 'Daniel', 'James', 'Garcia', NULL, '', 'SR005', '2', 'BSIT', 'Game Development', '09567890123', 'daniel.garcia@example.com', 'securePass', '202 Birch St', 'Not Enrolled', 0),
(6, 'Olivia', 'Jona', 'Martinez', NULL, '', 'SR006', '1', 'BSCS', 'Networking', '09678901234', 'olivia.martinez@example.com', 'olivia321', '303 Cedar St', 'Not Enrolled', 0),
(7, 'Liam', 'David', 'Lopez', NULL, '', 'SR007', '4', 'BSIT', 'Software Engineering', '09789012345', 'liam.lopez@example.com', 'mypassword', '404 Walnut St', 'Not Enrolled', 0),
(8, 'Sophia', 'Grace', 'Hernandez', NULL, '', 'SR008', '3', 'BSIS', 'Business Intelligence', '09890123456', 'sophia.hernandez@example.com', 'letmein', '505 Palm St', 'Not Enrolled', 0),
(9, 'Benjamin', 'Alexander', 'Young', NULL, '', 'SR009', '2', 'BSIT', 'Cloud Computing', '09901234567', 'benjamin.young@example.com', 'pass1234', '606 Redwood St', 'Not Enrolled', 0),
(10, 'Isabella', 'Hde', 'King', NULL, '', 'SR010', '1', 'BSCS', 'Cybersecurity', '09112233445', 'isabella.king@example.com', 'kingpass', '707 Magnolia St', 'Not Enrolled', 0),
(11, 'Mason', 'Henry', 'Scott', NULL, '', 'SR011', '3', 'BSIT', 'Web Development', '09223344556', 'mason.scott@example.com', 'webdevpass', '808 Ash St', 'Not Enrolled', 0),
(12, 'Charlotte', 'Nicole', 'Adams', NULL, '', 'SR012', '4', 'BSIS', 'IT Management', '09334455667', 'charlotte.adams@example.com', 'char123', '909 Fir St', 'Not Enrolled', 0),
(13, 'Ethan', 'Daniel', 'Baker', NULL, '', 'SR013', '2', 'BSIT', 'Software Engineering', '09445566778', 'ethan.baker@example.com', 'secureethan', '1010 Elm St', 'Not Enrolled', 0),
(14, 'Amelia', 'Gse', 'Gonzalez', NULL, '', 'SR014', '1', 'BSCS', 'Artificial Intelligence', '09556677889', 'amelia.gonzalez@example.com', 'ai_master', '1111 Oak St', 'Not Enrolled', 0),
(15, 'Logan', 'Matthew', 'Nelson', NULL, '', 'SR015', '3', 'BSIT', 'Cloud Computing', '09667788990', 'logan.nelson@example.com', 'clouduser', '1212 Pine St', 'Not Enrolled', 0),
(16, 'Mia', 'Victoria', 'Carter', NULL, '', 'SR016', '2', 'BSIS', 'Business Intelligence', '09778899001', 'mia.carter@example.com', 'bi_expert', '1313 Maple St', 'Not Enrolled', 0),
(17, 'Lucas', 'Nathaniel', 'Mitchell', NULL, '', 'SR017', '4', 'BSIT', 'Cybersecurity', '09889900112', 'lucas.mitchell@example.com', 'cyberlucas', '1414 Birch St', 'Not Enrolled', 0),
(18, 'Harper', 'Dew', 'Perez', NULL, '', 'SR018', '1', 'BSCS', 'Game Development', '09990011223', 'harper.perez@example.com', 'gamerpass', '1515 Cedar St', 'Not Enrolled', 0),
(19, 'Jackson', 'Samuel', 'Roberts', NULL, '', 'SR019', '2', 'BSIT', 'Networking', '09101122334', 'jackson.roberts@example.com', 'networking123', '1616 Walnut St', 'Not Enrolled', 0),
(20, 'Ella', 'Samantha', 'Turner', NULL, '', 'SR020', '3', 'BSIS', 'Data Science', '09212233445', 'ella.turner@example.com', 'datascience99', '1717 Palm St', 'Not Enrolled', 0),
(21, 'sdsgdjh', 'jghghjghgjh', 'hghg', 'https://drive.google.com/uc?export=view&id=1kDrj3PWJ-WX_FwjYyNK9E6mhUg7GhLie', 'https://drive.google.com/uc?export=view&id=1pFPccP7mRPP73G6ch9zXn4CkU9ffcs8t', 'hgg', '1st Year', 'BS Computer Science', 'Data Science', 'ghgj', 'ghgh', 'ghghj', 'ghgj', 'Enrolled', 0),
(22, 'fsh', 'ghg', 'ghgh', 'https://drive.google.com/uc?export=view&id=1svTtO0avESQ7LN4EO6uphhTwZEnhUvRJ', 'https://drive.google.com/uc?export=view&id=1vucO9mMFVXSj_RmdsQ549VRNS6mTIVth', 'jhhjghh', '2nd Year', 'BS Information Technology', 'Business Analytics', 'ghghjh', 'ghghh', 'hjg', 'hjg', 'Enrolled', 0);

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
(5, 'Art', 2, 0),
(6, 'PE', 1, 0);

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
(1, 1, '08:00:00', '09:30:00', 'Wednesday', 1, 1, 1),
(2, 1, '11:00:00', '12:30:00', 'Tuesday', 1, 1, 2),
(3, 1, '08:00:00', '09:30:00', 'Tuesday', 1, 1, 3),
(4, 1, '12:30:00', '14:00:00', 'Tuesday', 1, 1, 4),
(5, 1, '09:30:00', '11:00:00', 'Tuesday', 1, 1, 5),
(6, 1, '08:00:00', '09:30:00', 'Monday', 2, 1, 1),
(7, 1, '09:30:00', '11:00:00', 'Monday', 2, 1, 2),
(8, 1, '11:00:00', '12:30:00', 'Monday', 2, 1, 3),
(9, 1, '12:30:00', '14:00:00', 'Monday', 2, 1, 4),
(10, 1, '14:00:00', '15:30:00', 'Monday', 2, 1, 5),
(11, 3, '08:00:00', '10:00:00', 'Tuesday', 1, 4, 1),
(12, 3, '08:00:00', '10:00:00', 'Monday', 1, 4, 2),
(13, 3, '10:00:00', '12:00:00', 'Monday', 1, 4, 3),
(14, 3, '12:00:00', '14:00:00', 'Monday', 1, 4, 4),
(15, 3, '14:00:00', '16:00:00', 'Monday', 1, 4, 5),
(16, 6, '08:00:00', '09:00:00', 'Tuesday', 1, 1, 1),
(17, 6, '08:00:00', '09:00:00', 'Monday', 1, 1, 2),
(18, 6, '09:00:00', '10:00:00', 'Monday', 1, 1, 3),
(19, 6, '10:00:00', '11:00:00', 'Monday', 1, 1, 4),
(20, 6, '11:00:00', '12:00:00', 'Monday', 1, 1, 5);

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
  MODIFY `faculty_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `guardian`
--
ALTER TABLE `guardian`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `student_section`
--
ALTER TABLE `student_section`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `subjects`
--
ALTER TABLE `subjects`
  MODIFY `sub_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `subsched`
--
ALTER TABLE `subsched`
  MODIFY `sched_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

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
