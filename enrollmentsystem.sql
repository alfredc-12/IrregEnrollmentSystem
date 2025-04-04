-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 04, 2025 at 06:17 PM
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
-- Table structure for table `current`
--

CREATE TABLE `current` (
  `currentID` int(11) NOT NULL,
  `AcademicYear` varchar(100) NOT NULL,
  `Semester` enum('First Semester','Second Semester','Midterm','') DEFAULT NULL,
  `DateStart` date DEFAULT NULL,
  `DateEnd` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `current`
--

INSERT INTO `current` (`currentID`, `AcademicYear`, `Semester`, `DateStart`, `DateEnd`) VALUES
(1, '2024 - 2025', 'First Semester', '2025-08-14', '2025-05-30');

-- --------------------------------------------------------

--
-- Table structure for table `enrolled`
--

CREATE TABLE `enrolled` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `sub_id` int(11) NOT NULL,
  `subsched_id` int(11) DEFAULT NULL,
  `grade` decimal(5,2) DEFAULT NULL,
  `semester` varchar(50) DEFAULT NULL,
  `academic_year` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `enrolled`
--

INSERT INTO `enrolled` (`id`, `student_id`, `sub_id`, `subsched_id`, `grade`, `semester`, `academic_year`) VALUES
(1, 2, 2, NULL, 1.75, NULL, NULL),
(2, 2, 3, NULL, 1.50, NULL, NULL),
(3, 1, 1, NULL, NULL, 'First Semester', '2024 - 2025'),
(4, 1, 2, NULL, NULL, 'First Semester', '2024 - 2025'),
(5, 1, 3, NULL, NULL, 'First Semester', '2024 - 2025'),
(6, 1, 4, NULL, NULL, 'First Semester', '2024 - 2025'),
(7, 1, 5, NULL, NULL, 'First Semester', '2024 - 2025'),
(8, 1, 6, NULL, NULL, 'First Semester', '2024 - 2025'),
(9, 1, 7, NULL, NULL, 'First Semester', '2024 - 2025'),
(10, 1, 8, NULL, NULL, 'First Semester', '2024 - 2025');

-- --------------------------------------------------------

--
-- Stand-in structure for view `enrolled_view`
-- (See below for the actual view)
--
CREATE TABLE `enrolled_view` (
`full_name` varchar(302)
,`subject_name` varchar(100)
,`semester` varchar(50)
,`academic_year` varchar(50)
);

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
-- Stand-in structure for view `facultyschedulelog`
-- (See below for the actual view)
--
CREATE TABLE `facultyschedulelog` (
`faculty_name` varchar(302)
,`subject_name` varchar(100)
,`days` set('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday')
,`time_in` time
,`time_out` time
,`room_name` varchar(100)
,`section_name` varchar(100)
);

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
  `department` enum('CICS','CAS','CABEIHM','CHS','CTE','CCJE') NOT NULL,
  `year_level` enum('1st Year','2nd Year','3rd Year','4th Year') NOT NULL,
  `track` enum('BA','NT','SM') DEFAULT NULL,
  `population` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `section`
--

INSERT INTO `section` (`section_id`, `section_name`, `department`, `year_level`, `track`, `population`) VALUES
(1, 'BSIT - 2101', 'CICS', '2nd Year', NULL, 1),
(2, 'BSIT - 1101', 'CICS', '1st Year', NULL, 1);

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
  `year_level` enum('1st Year','2nd Year','3rd Year','4th Year') NOT NULL,
  `semester` enum('1st Sem','2nd Sem','Midterm','') NOT NULL,
  `program` enum('BSIT','BSCS','BSCE','') NOT NULL,
  `major` enum('BA','NT','SM','') DEFAULT NULL,
  `contact` varchar(50) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `status` enum('Enrolled','Not Enrolled') NOT NULL DEFAULT 'Not Enrolled',
  `isIrregular` tinyint(1) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`id`, `first_name`, `middle_name`, `last_name`, `pic_link`, `sign_link`, `sr_code`, `year_level`, `semester`, `program`, `major`, `contact`, `email`, `password`, `address`, `status`, `isIrregular`, `is_deleted`) VALUES
(1, 'John', 'Michael', 'Doe', 'https://drive.google.com/uc?export=view&id=1eN8E_sjGnw63mF7q-9D3KbtJTGNtU4-v', '', 'SR001', '1st Year', '1st Sem', 'BSIT', '', '09123456789', 'john.doe@example.com', 'password123', '123 Elm St', 'Enrolled', 0, 0),
(2, 'Jane', 'Marie', 'Smith', 'https://drive.google.com/uc?export=view&id=1r7efa8tbGp6MRL6nAr235vIv3UkcC1Nt', '', 'SR002', '2nd Year', '1st Sem', 'BSIT', '', '09234567890', 'jane.smith@example.com', 'pass456', '456 Oak St', 'Enrolled', 1, 0),
(3, 'Alex', 'Hum', 'Johnson', 'https://drive.google.com/uc?export=view&id=1NGRa3NhcYJn3Lgoj-G2PUKw61iugXuuF', '', 'SR003', '1st Year', '2nd Sem', 'BSIT', '', '09345678901', 'alex.johnson@example.com', 'qwerty', '789 Pine St', 'Not Enrolled', 0, 0),
(4, 'Emily', 'Anne', 'Brown', NULL, '', 'SR004', '3rd Year', '1st Sem', 'BSIT', 'BA', '09456789012', 'emily.brown@example.com', 'abc123', '101 Maple St', 'Not Enrolled', 0, 0),
(5, 'Daniel', 'James', 'Garcia', NULL, '', 'SR005', '3rd Year', '1st Sem', 'BSIT', 'NT', '09567890123', 'daniel.garcia@example.com', 'securePass', '202 Birch St', 'Not Enrolled', 0, 0),
(6, 'Olivia', 'Jona', 'Martinez', NULL, '', 'SR006', '3rd Year', '1st Sem', 'BSIT', 'SM', '09678901234', 'olivia.martinez@example.com', 'olivia321', '303 Cedar St', 'Not Enrolled', 0, 0),
(7, 'Liam', 'David', 'Lopez', NULL, '', 'SR007', '1st Year', '2nd Sem', 'BSIT', '', '09789012345', 'liam.lopez@example.com', 'mypassword', '404 Walnut St', 'Not Enrolled', 0, 0),
(8, 'Sophia', 'Grace', 'Hernandez', NULL, '', 'SR008', '2nd Year', '1st Sem', 'BSIT', '', '09890123456', 'sophia.hernandez@example.com', 'letmein', '505 Palm St', 'Not Enrolled', 0, 0),
(9, 'Benjamin', 'Alexander', 'Young', NULL, '', 'SR009', '2nd Year', '1st Sem', 'BSIT', '', '09901234567', 'benjamin.young@example.com', 'pass1234', '606 Redwood St', 'Not Enrolled', 0, 0),
(10, 'Isabella', 'Hde', 'King', NULL, '', 'SR010', '3rd Year', '2nd Sem', 'BSIT', 'BA', '09112233445', 'isabella.king@example.com', 'kingpass', '707 Magnolia St', 'Not Enrolled', 0, 0),
(11, 'Mason', 'Henry', 'Scott', NULL, '', 'SR011', '3rd Year', '2nd Sem', 'BSIT', 'NT', '09223344556', 'mason.scott@example.com', 'webdevpass', '808 Ash St', 'Not Enrolled', 0, 0),
(12, 'Charlotte', 'Nicole', 'Adams', NULL, '', 'SR012', '3rd Year', 'Midterm', 'BSIT', 'BA', '09334455667', 'charlotte.adams@example.com', 'char123', '909 Fir St', 'Not Enrolled', 0, 0),
(13, 'Ethan', 'Daniel', 'Baker', NULL, '', 'SR013', '2nd Year', '1st Sem', 'BSIT', '', '09445566778', 'ethan.baker@example.com', 'secureethan', '1010 Elm St', 'Not Enrolled', 0, 0),
(14, 'Amelia', 'Gse', 'Gonzalez', NULL, '', 'SR014', '1st Year', '1st Sem', 'BSCS', '', '09556677889', 'amelia.gonzalez@example.com', 'ai_master', '1111 Oak St', 'Not Enrolled', 0, 0),
(15, 'Logan', 'Matthew', 'Nelson', NULL, '', 'SR015', '3rd Year', '1st Sem', 'BSIT', '', '09667788990', 'logan.nelson@example.com', 'clouduser', '1212 Pine St', 'Not Enrolled', 0, 0),
(16, 'Mia', 'Victoria', 'Carter', NULL, '', 'SR016', '2nd Year', '1st Sem', '', '', '09778899001', 'mia.carter@example.com', 'bi_expert', '1313 Maple St', 'Not Enrolled', 0, 0),
(17, 'Lucas', 'Nathaniel', 'Mitchell', NULL, '', 'SR017', '4th Year', '1st Sem', 'BSIT', '', '09889900112', 'lucas.mitchell@example.com', 'cyberlucas', '1414 Birch St', 'Not Enrolled', 0, 0),
(18, 'Harper', 'Dew', 'Perez', NULL, '', 'SR018', '1st Year', '1st Sem', 'BSCS', '', '09990011223', 'harper.perez@example.com', 'gamerpass', '1515 Cedar St', 'Not Enrolled', 0, 0),
(19, 'Jackson', 'Samuel', 'Roberts', NULL, '', 'SR019', '2nd Year', '1st Sem', 'BSIT', '', '09101122334', 'jackson.roberts@example.com', 'networking123', '1616 Walnut St', 'Not Enrolled', 0, 0),
(20, 'Ella', 'Samantha', 'Turner', NULL, '', 'SR020', '3rd Year', '1st Sem', '', '', '09212233445', 'ella.turner@example.com', 'datascience99', '1717 Palm St', 'Not Enrolled', 0, 0),
(21, 'sdsgdjh', 'jghghjghgjh', 'hghg', 'https://drive.google.com/uc?export=view&id=1kDrj3PWJ-WX_FwjYyNK9E6mhUg7GhLie', 'https://drive.google.com/uc?export=view&id=1pFPccP7mRPP73G6ch9zXn4CkU9ffcs8t', 'hgg', '1st Year', '1st Sem', '', '', 'ghgj', 'ghgh', 'ghghj', 'ghgj', 'Not Enrolled', 0, 0),
(22, 'fsh', 'ghg', 'ghgh', 'https://drive.google.com/uc?export=view&id=1svTtO0avESQ7LN4EO6uphhTwZEnhUvRJ', 'https://drive.google.com/uc?export=view&id=1vucO9mMFVXSj_RmdsQ549VRNS6mTIVth', 'jhhjghh', '2nd Year', '1st Sem', '', '', 'ghghjh', 'ghghh', 'hjg', 'hjg', 'Not Enrolled', 0, 0),
(23, 'dsadas', 'dsadas', 'dasda', NULL, '', '2222', '1st Year', '1st Sem', 'BSIT', '', '09868678', 'fdsfds', 'fdsff', 'fsdfs', 'Not Enrolled', 0, 0);

--
-- Triggers `student`
--
DELIMITER $$
CREATE TRIGGER `trg_enroll_subjects` AFTER UPDATE ON `student` FOR EACH ROW BEGIN
    -- Declare variables
    DECLARE acad_year VARCHAR(50);
    DECLARE max_allowed INT;
    DECLARE cumulative_units INT DEFAULT 0;
    DECLARE mapped_semester VARCHAR(20);
    DECLARE done INT DEFAULT 0;
    DECLARE subj_id INT;
    DECLARE subj_units INT;
    DECLARE subj_prereq VARCHAR(255);
    DECLARE prereq_list VARCHAR(255);
    DECLARE all_prereq_met BOOLEAN DEFAULT TRUE;
    DECLARE delim_pos INT;
    DECLARE prereq VARCHAR(50);
    DECLARE student_numeric_year INT;
    DECLARE current_semester VARCHAR(20);

    -- Declare cursors
    DECLARE cur_irreg CURSOR FOR
         SELECT sub_id, units, prerequisite
         FROM subjects
         WHERE year_level = student_numeric_year
           AND (semester = mapped_semester OR semester IN ('First Semester', 'Second Semester'))
           AND (student_numeric_year < 3 OR acad_track = NEW.major);

    DECLARE cur_reg CURSOR FOR
         SELECT sub_id, units
         FROM subjects
         WHERE year_level = student_numeric_year
           AND (semester = mapped_semester OR semester IN ('First Semester', 'Second Semester'))
           AND (student_numeric_year < 3 OR acad_track = NEW.major);

    -- Declare handler for cursor end
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    -- Get the current academic year and semester
    SELECT AcademicYear, Semester INTO acad_year, current_semester
    FROM current
    ORDER BY currentID DESC
    LIMIT 1;

    -- Map semester
    SET mapped_semester = CASE NEW.semester
                            WHEN '1st Sem' THEN 'First Semester'
                            WHEN '2nd Sem' THEN 'Second Semester'
                            WHEN 'Midterm' THEN 'Midterm'
                            ELSE ''
                          END;

    -- Convert year level to numeric format
    SET student_numeric_year = CASE NEW.year_level
                                  WHEN '1st Year' THEN 1
                                  WHEN '2nd Year' THEN 2
                                  WHEN '3rd Year' THEN 3
                                  WHEN '4th Year' THEN 4
                                  ELSE 5
                               END;

    -- Determine the maximum allowed units
    SET max_allowed = CASE
        WHEN NEW.year_level = '1st Year' AND NEW.semester IN ('1st Sem', '2nd Sem') THEN 23
        WHEN NEW.year_level = '2nd Year' AND NEW.semester IN ('1st Sem', '2nd Sem') THEN 23
        WHEN NEW.year_level = '3rd Year' AND NEW.semester = 'Midterm' THEN 6
        WHEN NEW.year_level = '3rd Year' AND NEW.semester IN ('1st Sem', '2nd Sem') THEN 21
        WHEN NEW.year_level = '4th Year' AND NEW.semester = '1st Sem' THEN 21
        WHEN NEW.year_level = '4th Year' AND NEW.semester = '2nd Sem' THEN 6
        ELSE 0
    END;

    -- Process enrollment
    IF NEW.status = 'Enrolled' AND OLD.status <> 'Enrolled' THEN
        -- Get cumulative units already enrolled in the current semester
        SELECT IFNULL(SUM(s.units), 0) INTO cumulative_units
        FROM enrolled e
        JOIN subjects s ON e.sub_id = s.sub_id
        WHERE e.student_id = NEW.id
          AND e.semester = current_semester;

        IF NEW.isIrregular = 1 THEN
            OPEN cur_irreg;
            irreg_loop: LOOP
                FETCH cur_irreg INTO subj_id, subj_units, subj_prereq;
                IF done = 1 THEN
                    LEAVE irreg_loop;
                END IF;

                -- Check prerequisites for irregular students
                SET all_prereq_met = TRUE;
                IF subj_prereq IS NOT NULL AND TRIM(subj_prereq) <> '' THEN
                    SET prereq_list = REPLACE(subj_prereq, ', ', ',');
                    WHILE LENGTH(prereq_list) > 0 DO
                        SET delim_pos = LOCATE(',', prereq_list);
                        IF delim_pos > 0 THEN
                            SET prereq = SUBSTRING(prereq_list, 1, delim_pos - 1);
                            SET prereq_list = SUBSTRING(prereq_list, delim_pos + 1);
                        ELSE
                            SET prereq = prereq_list;
                            SET prereq_list = '';
                        END IF;
                        IF TRIM(prereq) <> '' THEN
                            -- Check if the student has already taken the prerequisite subject
                            IF NOT EXISTS (
                                SELECT 1
                                FROM enrolled e
                                JOIN subjects s ON e.sub_id = s.sub_id
                                WHERE e.student_id = NEW.id
                                  AND s.subj_code = TRIM(prereq)
                            ) THEN
                                SET all_prereq_met = FALSE;
                                LEAVE irreg_loop;
                            END IF;
                        END IF;
                    END WHILE;
                END IF;

                -- Enroll subject if prerequisites are met and within max units
                IF all_prereq_met AND cumulative_units + subj_units <= max_allowed THEN
                    INSERT INTO enrolled(student_id, sub_id, subsched_id, grade, semester, academic_year)
                    VALUES (NEW.id, subj_id, NULL, NULL, current_semester, acad_year);
                    SET cumulative_units = cumulative_units + subj_units;
                END IF;
            END LOOP irreg_loop;
            CLOSE cur_irreg;
        ELSE
            OPEN cur_reg;
            reg_loop: LOOP
                FETCH cur_reg INTO subj_id, subj_units;
                IF done = 1 THEN
                    LEAVE reg_loop;
                END IF;
                IF cumulative_units + subj_units <= max_allowed THEN
                    INSERT INTO enrolled(student_id, sub_id, subsched_id, grade, semester, academic_year)
                    VALUES (NEW.id, subj_id, NULL, NULL, current_semester, acad_year);
                    SET cumulative_units = cumulative_units + subj_units;
                END IF;
            END LOOP reg_loop;
            CLOSE cur_reg;
        END IF;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_student_enroll` AFTER UPDATE ON `student` FOR EACH ROW BEGIN
    DECLARE sec_id INT;
    DECLARE current_population INT DEFAULT 0;
    DECLARE sec_num VARCHAR(2);
    DECLARE base_section_name VARCHAR(100);
    DECLARE new_section_name VARCHAR(100);
    DECLARE year_digit VARCHAR(1);
    DECLARE sem_code VARCHAR(1);

    IF NEW.status = 'Enrolled' AND OLD.status <> 'Enrolled' THEN
        -- Convert the year_level enum into a single digit.
        SET year_digit = CASE NEW.year_level
                           WHEN '1st Year' THEN '1'
                           WHEN '2nd Year' THEN '2'
                           WHEN '3rd Year' THEN '3'
                           WHEN '4th Year' THEN '4'
                           ELSE ''
                         END;
                         
        -- Convert the semester into a code.
        SET sem_code = CASE NEW.semester
                           WHEN '1st Sem' THEN '1'
                           WHEN '2nd Sem' THEN '2'
                           WHEN 'Midterm' THEN 'M'
                           ELSE ''
                         END;
                         
        -- Build the base section name starting with the student's program.
        SET base_section_name = NEW.program;
        
        -- For 3rd or 4th year students with a defined major, append the major.
        IF (NEW.year_level IN ('3rd Year','4th Year')) AND (NEW.major IS NOT NULL) AND (NEW.major <> '') THEN
            SET base_section_name = CONCAT(base_section_name, '-', NEW.major);
        END IF;
        
        -- Append the year digit and semester code.
        SET base_section_name = CONCAT(base_section_name, ' - ', year_digit, sem_code);
        
        /*
          For example:
          - A BSIT student with year_level '1st Year' and semester '2nd Sem' becomes: "BSIT - 12"
          - A BSIT student with year_level '3rd Year' with major 'BA' in '1st Sem' becomes: "BSIT-BA - 31"
          The final section name will have a two-digit section number appended (e.g. "01").
        */
        
        -- Look for an existing section matching the base section name pattern.
        SELECT section_id, RIGHT(section_name, 2) AS sec_num, population
          INTO sec_id, sec_num, current_population
          FROM section
          WHERE section_name LIKE CONCAT(base_section_name, '%')
          ORDER BY section_name DESC
          LIMIT 1;
        
        IF sec_id IS NULL THEN
            -- No section exists: create a new one with section number '01'
            SET sec_num = '01';
            SET new_section_name = CONCAT(base_section_name, sec_num);
            INSERT INTO section(section_name, department, year_level, track, population)
                 VALUES(
                     new_section_name,
                     'CICS',              -- Example department; adjust as needed.
                     NEW.year_level,
                     CASE 
                         WHEN (NEW.major IS NOT NULL AND NEW.major <> '') 
                         THEN NEW.major 
                         ELSE NULL 
                     END,
                     1
                 );
            SET sec_id = LAST_INSERT_ID();
            INSERT INTO student_section(student_id, section_id)
                 VALUES(NEW.id, sec_id);
        ELSE
            IF current_population < 40 THEN
                -- Existing section is not full, so increment its population.
                UPDATE section
                   SET population = population + 1
                 WHERE section_id = sec_id;
                INSERT INTO student_section(student_id, section_id)
                     VALUES(NEW.id, sec_id);
            ELSE
                -- The current section is full (40 students); create a new section.
                SET sec_num = LPAD(CAST(CAST(sec_num AS UNSIGNED) + 1 AS CHAR), 2, '0');
                SET new_section_name = CONCAT(base_section_name, sec_num);
                INSERT INTO section(section_name, department, year_level, track, population)
                     VALUES(
                         new_section_name,
                         'CICS',              -- Example department; adjust as needed.
                         NEW.year_level,
                         CASE 
                             WHEN (NEW.major IS NOT NULL AND NEW.major <> '') 
                             THEN NEW.major 
                             ELSE NULL 
                         END,
                         1
                     );
                SET sec_id = LAST_INSERT_ID();
                INSERT INTO student_section(student_id, section_id)
                     VALUES(NEW.id, sec_id);
            END IF;
        END IF;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `student_section`
--

CREATE TABLE `student_section` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student_section`
--

INSERT INTO `student_section` (`id`, `student_id`, `section_id`) VALUES
(2, 1, 2),
(1, 2, 1);

-- --------------------------------------------------------

--
-- Table structure for table `subjects`
--

CREATE TABLE `subjects` (
  `sub_id` int(11) NOT NULL,
  `subj_code` varchar(50) NOT NULL,
  `subject_name` varchar(100) NOT NULL,
  `lecture` int(11) DEFAULT NULL,
  `units` int(11) NOT NULL,
  `lab` int(11) DEFAULT NULL,
  `year_level` enum('First Year','Second Year','Third Year','Fourth Year') NOT NULL,
  `semester` enum('First Semester','Second Semester','Midterm','') NOT NULL,
  `prerequisite` varchar(25) DEFAULT NULL,
  `acad_track` enum('BA','NT','SM','') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`sub_id`, `subj_code`, `subject_name`, `lecture`, `units`, `lab`, `year_level`, `semester`, `prerequisite`, `acad_track`) VALUES
(1, 'IT 111', 'Introduction to Computing', 2, 3, 3, 'First Year', 'First Semester', NULL, 'NT'),
(2, 'GEd 102', 'Mathematics in the Modern World', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'NT'),
(3, 'GEd 108', 'Art Appreciation', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'NT'),
(4, 'FILI 101', 'Kontekstwalisadong Komunikasyon sa Filipino', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'NT'),
(5, 'PE 101', 'Physical Fitness, Gymnastics and Aerobics', 2, 2, NULL, 'First Year', 'First Semester', NULL, 'NT'),
(6, 'NSTP 111', 'National Service Training Program 1', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'NT'),
(7, 'GEd 103', 'The Life and Works of Rizal', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'NT'),
(8, 'GEd 104', 'The Contemporary World', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'NT'),
(9, 'CS 111', 'Computer Programming', 2, 3, 3, 'First Year', 'Second Semester', 'IT 111', 'NT'),
(10, 'CS 131', 'Data Structures and Algorithms', 2, 3, 3, 'First Year', 'Second Semester', 'IT 111', 'NT'),
(11, 'MATH 111', 'Linear Algebra', 3, 3, NULL, 'First Year', 'Second Semester', 'GEd 102', 'NT'),
(12, 'FILI 102', 'Filipino sa Iba\'t Ibang Disiplina', 3, 3, NULL, 'First Year', 'Second Semester', NULL, 'NT'),
(13, 'GEd 105', 'Readings in Philippine History', 3, 3, NULL, 'First Year', 'Second Semester', NULL, 'NT'),
(14, 'GEd 109', 'Science, Technology and Society', 3, 3, NULL, 'First Year', 'Second Semester', NULL, 'NT'),
(15, 'PE 102', 'Rhythmic Activities', 2, 2, NULL, 'First Year', 'Second Semester', 'PE 101', 'NT'),
(16, 'NSTP 121', 'National Service Training Program 2', 3, 3, NULL, 'First Year', 'Second Semester', 'NSTP 111', 'NT'),
(17, 'CS 121', 'Advanced Computer Programming', 2, 3, 3, 'Second Year', 'First Semester', 'CS 111', 'NT'),
(18, 'IT 211', 'Database Management System', 2, 3, 3, 'Second Year', 'First Semester', 'CS 111', 'NT'),
(19, 'CS 211', 'Object-Oriented Programming', 2, 3, 3, 'Second Year', 'First Semester', 'CS 111, CS 131', 'NT'),
(20, 'LITR 102', 'ASEAN Literature', 3, 3, NULL, 'Second Year', 'First Semester', NULL, 'NT'),
(21, 'CpE 405', 'Discrete Mathematics', 3, 3, NULL, 'Second Year', 'First Semester', 'MATH 111', 'NT'),
(22, 'PHY 101', 'Calculus Based Physics', 2, 3, 3, 'Second Year', 'First Semester', 'MATH 111', 'NT'),
(23, 'IT 212', 'Computer Networking 1', 2, 3, 3, 'Second Year', 'First Semester', 'IT 111', 'NT'),
(24, 'PE 103', 'Individual and Dual Sports', 2, 2, NULL, 'Second Year', 'First Semester', 'PE 101', 'NT'),
(25, 'IT 221', 'Information Management', 2, 3, 3, 'Second Year', 'Second Semester', 'IT 111', 'NT'),
(26, 'IT 223', 'Computer Networking 2', 2, 3, 3, 'Second Year', 'Second Semester', 'IT 212', 'NT'),
(27, 'IT 222', 'Advanced Database Management System', 2, 3, 3, 'Second Year', 'Second Semester', 'IT 211', 'NT'),
(28, 'MATH 408', 'Data Analysis', 3, 3, NULL, 'Second Year', 'Second Semester', 'MATH 111', 'NT'),
(29, 'ES 101', 'Environmental Sciences', 2, 3, 3, 'Second Year', 'Second Semester', 'PHY 101', 'NT'),
(30, 'GEd 106', 'Purposive Communication', 3, 3, NULL, 'Second Year', 'Second Semester', NULL, 'NT'),
(31, 'GEd 101', 'Understanding the Self', 3, 3, NULL, 'Second Year', 'Second Semester', NULL, 'NT'),
(32, 'PE 104', 'Team Sports', 2, 2, NULL, 'Second Year', 'Second Semester', 'PE 101', 'NT'),
(33, 'IT 311', 'Systems Administration and Maintenance', 2, 3, 3, 'Third Year', 'First Semester', 'IT 221,IT 222', 'NT'),
(34, 'IT 312', 'System Integration and Architecture', 2, 3, 3, 'Third Year', 'First Semester', 'CS 131', 'NT'),
(35, 'NTT 401', 'Computer Networking 3', 3, 2, 3, 'Third Year', 'First Semester', 'IT 223', 'NT'),
(36, 'NTT 402', 'Internet of Things (IoT)', 2, 3, 3, 'Third Year', 'First Semester', 'IT 223', 'NT'),
(37, 'IT 313', 'System Analysis and Design', 2, 3, 3, 'Third Year', 'First Semester', 'IT 222', 'NT'),
(38, 'IT 314', 'Web Systems and Technologies', 2, 3, 3, 'Third Year', 'First Semester', 'CS 211', 'NT'),
(39, 'GEd 107', 'Ethics', 3, 3, NULL, 'Third Year', 'First Semester', NULL, 'NT'),
(40, 'IT 321', 'Human-computer interaction', 3, 3, NULL, 'Third Year', 'Second Semester', 'IT 314', 'NT'),
(41, 'NTT 403', 'Computer Networking 4', 3, 2, 3, 'Third Year', 'Second Semester', 'NTT 401', 'NT'),
(42, 'NTT 404', 'Cloud Computing', 2, 3, 3, 'Third Year', 'Second Semester', 'NTT 402', 'NT'),
(43, 'IT 322', 'Advanced System Integration and Architecture', 2, 3, 3, 'Third Year', 'Second Semester', 'IT 312', 'NT'),
(44, 'IT 323', 'Information Assurance and Security', 2, 3, 3, 'Third Year', 'Second Semester', 'IT 223', 'NT'),
(45, 'IT 324', 'Capstone Project 1', 3, 3, NULL, 'Third Year', 'Second Semester', 'Regular 3rd Year', 'NT'),
(46, 'IT 325', 'IT Project Management', 3, 3, NULL, 'Third Year', 'Second Semester', 'IT 313', 'NT'),
(47, 'IT 331', 'Application Development and Emerging Technologies', 2, 3, 3, 'Third Year', 'Midterm', 'IT 321', 'NT'),
(48, 'IT 332', 'Integrative Programming and Technologies', 3, 3, NULL, 'Third Year', 'Midterm', 'IT 314', 'NT'),
(49, 'CS 423', 'Social Issues and Professional Practice', 3, 3, NULL, 'Fourth Year', 'First Semester', NULL, 'NT'),
(50, 'IT 411', 'Capstone Project 2', 3, 3, NULL, 'Fourth Year', 'First Semester', 'IT 324', 'NT'),
(51, 'NTT 405', 'Cybersecurity', 2, 3, 3, 'Fourth Year', 'First Semester', 'NTT 403', 'NT'),
(52, 'ENGG 405', 'Technopreneurship', 3, 3, NULL, 'Fourth Year', 'First Semester', NULL, 'NT'),
(53, 'IT 413', 'Advanced Information Assurance and Security', 2, 3, 3, 'Fourth Year', 'First Semester', 'IT 323', 'NT'),
(54, 'IT 414', 'System Quality Assurance', 2, 3, 3, 'Fourth Year', 'First Semester', 'IT 325', 'NT'),
(55, 'IT 412', 'Platform Technologies', 3, 3, NULL, 'Fourth Year', 'First Semester', 'IT 332', 'NT'),
(56, 'IT 421', 'Internship Training', NULL, 6, 500, 'Fourth Year', 'Second Semester', 'Regular 4th Year', 'NT'),
(57, 'IT 111', 'Introduction to Computing', 2, 3, 3, 'First Year', 'First Semester', NULL, 'BA'),
(58, 'GEd 102', 'Mathematics in the Modern World', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'BA'),
(59, 'GEd 108', 'Art Appreciation', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'BA'),
(60, 'FILI 101', 'Kontekstwalisadong Komunikasyon sa Filipino', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'BA'),
(61, 'PE 101', 'Physical Fitness, Gymnastics and Aerobics', 2, 2, NULL, 'First Year', 'First Semester', NULL, 'BA'),
(62, 'NSTP 111', 'National Service Training Program 1', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'BA'),
(63, 'GEd 103', 'The Life and Works of Rizal', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'BA'),
(64, 'GEd 104', 'The Contemporary World', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'BA'),
(65, 'CS 111', 'Computer Programming', 2, 3, 3, 'First Year', 'Second Semester', 'IT 111', 'BA'),
(66, 'CS 131', 'Data Structures and Algorithms', 2, 3, 3, 'First Year', 'Second Semester', 'IT 111', 'BA'),
(67, 'MATH 111', 'Linear Algebra', 3, 3, NULL, 'First Year', 'Second Semester', 'GEd 102', 'BA'),
(68, 'FILI 102', 'Filipino sa Iba\'t Ibang Disiplina', 3, 3, NULL, 'First Year', 'Second Semester', NULL, 'BA'),
(69, 'GEd 105', 'Readings in Philippine History', 3, 3, NULL, 'First Year', 'Second Semester', NULL, 'BA'),
(70, 'GEd 109', 'Science, Technology and Society', 3, 3, NULL, 'First Year', 'Second Semester', NULL, 'BA'),
(71, 'PE 102', 'Rhythmic Activities', 2, 2, NULL, 'First Year', 'Second Semester', 'PE 101', 'BA'),
(72, 'NSTP 121', 'National Service Training Program 2', 3, 3, NULL, 'First Year', 'Second Semester', 'NSTP 111', 'BA'),
(73, 'CS 121', 'Advanced Computer Programming', 2, 3, 3, 'Second Year', 'First Semester', 'CS 111', 'BA'),
(74, 'IT 211', 'Database Management System', 2, 3, 3, 'Second Year', 'First Semester', 'CS 111', 'BA'),
(75, 'CS 211', 'Object-Oriented Programming', 2, 3, 3, 'Second Year', 'First Semester', 'CS 111, CS 131', 'BA'),
(76, 'LITR 102', 'ASEAN Literature', 3, 3, NULL, 'Second Year', 'First Semester', NULL, 'BA'),
(77, 'CpE 405', 'Discrete Mathematics', 3, 3, NULL, 'Second Year', 'First Semester', 'MATH 111', 'BA'),
(78, 'PHY 101', 'Calculus Based Physics', 2, 3, 3, 'Second Year', 'First Semester', 'MATH 111', 'BA'),
(79, 'IT 212', 'Computer Networking 1', 2, 3, 3, 'Second Year', 'First Semester', 'IT 111', 'BA'),
(80, 'PE 103', 'Individual and Dual Sports', 2, 2, NULL, 'Second Year', 'First Semester', 'PE 101', 'BA'),
(81, 'IT 221', 'Information Management', 2, 3, 3, 'Second Year', 'Second Semester', 'IT 111', 'BA'),
(82, 'IT 223', 'Computer Networking 2', 2, 3, 3, 'Second Year', 'Second Semester', 'IT 212', 'BA'),
(83, 'IT 222', 'Advanced Database Management System', 2, 3, 3, 'Second Year', 'Second Semester', 'IT 211', 'BA'),
(84, 'MATH 408', 'Data Analysis', 3, 3, NULL, 'Second Year', 'Second Semester', 'MATH 111', 'BA'),
(85, 'ES 101', 'Environmental Sciences', 2, 3, 3, 'Second Year', 'Second Semester', 'PHY 101', 'BA'),
(86, 'GEd 106', 'Purposive Communication', 3, 3, NULL, 'Second Year', 'Second Semester', NULL, 'BA'),
(87, 'GEd 101', 'Understanding the Self', 3, 3, NULL, 'Second Year', 'Second Semester', NULL, 'BA'),
(88, 'PE 104', 'Team Sports', 2, 2, NULL, 'Second Year', 'Second Semester', 'PE 101', 'BA'),
(89, 'IT 311', 'Systems Administration and Maintenance', 2, 3, 3, 'Third Year', 'First Semester', 'IT 221, IT 222', 'BA'),
(90, 'IT 312', 'System Integration and Architecture', 2, 3, 3, 'Third Year', 'First Semester', 'CS 131', 'BA'),
(91, 'BAT 401', 'Fundamentals of Business Analytics', 2, 3, 3, 'Third Year', 'First Semester', 'IT 221, IT 222', 'BA'),
(92, 'BAT 402', 'Fundamentals of Analytics Modeling', 2, 3, 3, 'Third Year', 'First Semester', 'IT 221, IT 222', 'BA'),
(93, 'IT 313', 'System Analysis and Design', 2, 3, 3, 'Third Year', 'First Semester', 'IT 222', 'BA'),
(94, 'IT 314', 'Web Systems and Technologies', 2, 3, 3, 'Third Year', 'First Semester', 'CS 211', 'BA'),
(95, 'GEd 107', 'Ethics', 3, 3, NULL, 'Third Year', 'First Semester', NULL, 'BA'),
(96, 'IT 321', 'Human-computer interaction', 3, 3, NULL, 'Third Year', 'Second Semester', 'IT 314', 'BA'),
(97, 'BAT 403', 'Fundamentals of Enterprise Data Management', 2, 3, 3, 'Third Year', 'Second Semester', 'BAT 401', 'BA'),
(98, 'BAT 404', 'Analytics Techniques & Tools', 2, 3, 3, 'Third Year', 'Second Semester', 'BAT 402', 'BA'),
(99, 'IT 322', 'Advanced System Integration and Architecture', 2, 3, 3, 'Third Year', 'Second Semester', 'IT 312', 'BA'),
(100, 'IT 323', 'Information Assurance and Security', 2, 3, 3, 'Third Year', 'Second Semester', 'IT 223', 'BA'),
(101, 'IT 324', 'Capstone Project 1', 3, 3, NULL, 'Third Year', 'Second Semester', 'Regular 3rd Year', 'BA'),
(102, 'IT 325', 'IT Project Management', 3, 3, NULL, 'Third Year', 'Second Semester', 'IT 313', 'BA'),
(103, 'IT 331', 'Application Development and Emerging Technologies', 2, 3, 3, 'Third Year', 'Midterm', 'IT 321', 'BA'),
(104, 'IT 332', 'Integrative Programming and Technologies', 3, 3, NULL, 'Third Year', 'Midterm', 'IT 314', 'BA'),
(105, 'CS 423', 'Social Issues and Professional Practice', 3, 3, NULL, 'Fourth Year', 'First Semester', NULL, 'BA'),
(106, 'IT 411', 'Capstone Project 2', 3, 3, NULL, 'Fourth Year', 'First Semester', 'IT 324', 'BA'),
(107, 'BAT 405', 'Analytics Application', 2, 3, 3, 'Fourth Year', 'First Semester', 'BAT 404', 'BA'),
(108, 'ENGG 405', 'Technopreneurship', 3, 3, NULL, 'Fourth Year', 'First Semester', NULL, 'BA'),
(109, 'IT 413', 'Advanced Information Assurance and Security', 2, 3, 3, 'Fourth Year', 'First Semester', 'IT 323', 'BA'),
(110, 'IT 414', 'System Quality Assurance', 2, 3, 3, 'Fourth Year', 'First Semester', 'IT 325', 'BA'),
(111, 'IT 412', 'Platform Technologies', 3, 3, NULL, 'Fourth Year', 'First Semester', 'IT 332', 'BA'),
(112, 'IT 421', 'Internship Training', NULL, 6, 500, 'Fourth Year', 'Second Semester', 'Regular 4th Year', 'BA'),
(113, 'IT 111', 'Introduction to Computing', 2, 3, 3, 'First Year', 'First Semester', NULL, 'SM'),
(114, 'GEd 102', 'Mathematics in the Modern World', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'SM'),
(115, 'GEd 108', 'Art Appreciation', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'SM'),
(116, 'FILI 101', 'Kontekstwalisadong Komunikasyon sa Filipino', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'SM'),
(117, 'PE 101', 'Physical Fitness, Gymnastics and Aerobics', 2, 2, NULL, 'First Year', 'First Semester', NULL, 'SM'),
(118, 'NSTP 111', 'National Service Training Program 1', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'SM'),
(119, 'GEd 103', 'The Life and Works of Rizal', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'SM'),
(120, 'GEd 104', 'The Contemporary World', 3, 3, NULL, 'First Year', 'First Semester', NULL, 'SM'),
(121, 'CS 111', 'Computer Programming', 2, 3, 3, 'First Year', 'Second Semester', 'IT 111', 'SM'),
(122, 'CS 131', 'Data Structures and Algorithms', 2, 3, 3, 'First Year', 'Second Semester', 'IT 111', 'SM'),
(123, 'MATH 111', 'Linear Algebra', 3, 3, NULL, 'First Year', 'Second Semester', 'GEd 102', 'SM'),
(124, 'FILI 102', 'Filipino sa Iba\'t Ibang Disiplina', 3, 3, NULL, 'First Year', 'Second Semester', NULL, 'SM'),
(125, 'GEd 105', 'Readings in Philippine History', 3, 3, NULL, 'First Year', 'Second Semester', NULL, 'SM'),
(126, 'GEd 109', 'Science, Technology and Society', 3, 3, NULL, 'First Year', 'Second Semester', NULL, 'SM'),
(127, 'PE 102', 'Rhythmic Activities', 2, 2, NULL, 'First Year', 'Second Semester', 'PE 101', 'SM'),
(128, 'NSTP 112', 'National Service Training Program 2', 3, 3, NULL, 'First Year', 'Second Semester', 'NSTP 111', 'SM'),
(129, 'CS 121', 'Advanced Computer Programming', 2, 3, 3, 'Second Year', 'First Semester', 'CS 111', 'SM'),
(130, 'IT 211', 'Database Management System', 2, 3, 3, 'Second Year', 'First Semester', 'CS 111', 'SM'),
(131, 'CS 211', 'Object-Oriented Programming', 2, 3, 3, 'Second Year', 'First Semester', 'CS 111, CS 131', 'SM'),
(132, 'LITR 102', 'ASEAN Literature', 3, 3, NULL, 'Second Year', 'First Semester', NULL, 'SM'),
(133, 'CpE 405', 'Discrete Mathematics', 3, 3, NULL, 'Second Year', 'First Semester', 'MATH 111', 'SM'),
(134, 'PHY 101', 'Calculus Based Physics', 2, 3, 3, 'Second Year', 'First Semester', 'MATH 111', 'SM'),
(135, 'IT 212', 'Computer Networking 1', 2, 3, 3, 'Second Year', 'First Semester', 'IT 111', 'SM'),
(136, 'PE 103', 'Individual and Dual Sports', 2, 2, NULL, 'Second Year', 'First Semester', 'PE 101', 'SM'),
(137, 'IT 221', 'Information Management', 2, 3, 3, 'Second Year', 'Second Semester', 'IT 111', 'SM'),
(138, 'IT 223', 'Computer Networking 2', 2, 3, 3, 'Second Year', 'Second Semester', 'IT 212', 'SM'),
(139, 'IT 222', 'Advanced Database Management System', 2, 3, 3, 'Second Year', 'Second Semester', 'IT 211', 'SM'),
(140, 'MATH 408', 'Data Analysis', 3, 3, NULL, 'Second Year', 'Second Semester', 'MATH 111', 'SM'),
(141, 'ES 101', 'Environmental Sciences', 2, 3, 3, 'Second Year', 'Second Semester', 'PHY 101', 'SM'),
(142, 'GEd 106', 'Purposive Communication', 3, 3, NULL, 'Second Year', 'Second Semester', NULL, 'SM'),
(143, 'GEd 101', 'Understanding the Self', 3, 3, NULL, 'Second Year', 'Second Semester', NULL, 'SM'),
(144, 'PE 104', 'Team Sports', 2, 2, NULL, 'Second Year', 'Second Semester', 'PE 101', 'SM'),
(145, 'IT 311', 'Systems Administration and Maintenance', 2, 3, 3, 'Third Year', 'First Semester', 'IT 221, IT 222', 'SM'),
(146, 'IT 312', 'System Integration and Architecture', 2, 3, 3, 'Third Year', 'First Semester', 'CS 131', 'SM'),
(147, 'SMT 401', 'Fundamentals of Business Process Outsourcing 101', 2, 3, 3, 'Third Year', 'First Semester', 'IT 221', 'SM'),
(148, 'SMT 402', 'Business Communication', 2, 3, 3, 'Third Year', 'First Semester', 'IT 221', 'SM'),
(149, 'IT 313', 'System Analysis and Design', 2, 3, 3, 'Third Year', 'First Semester', 'IT 222', 'SM'),
(150, 'IT 314', 'Web Systems and Technologies', 2, 3, 3, 'Third Year', 'First Semester', 'CS 211', 'SM'),
(151, 'GEd 107', 'Ethics', 3, 3, NULL, 'Third Year', 'First Semester', NULL, 'SM'),
(152, 'IT 321', 'Human-computer interaction', 3, 3, NULL, 'Third Year', 'Second Semester', 'IT 314', 'SM'),
(153, 'SMT 403', 'Fundamentals of Business Process Outsourcing 102', 2, 3, 3, 'Third Year', 'Second Semester', 'SMT 401', 'SM'),
(154, 'SMT 404', 'Service Culture', 2, 3, 3, 'Third Year', 'Second Semester', 'SMT 401', 'SM'),
(155, 'IT 322', 'Advanced System Integration and Architecture', 2, 3, 3, 'Third Year', 'Second Semester', 'IT 312', 'SM'),
(156, 'IT 323', 'Information Assurance and Security', 2, 3, 3, 'Third Year', 'Second Semester', 'IT 223', 'SM'),
(157, 'IT 324', 'Capstone Project 1', 3, 3, NULL, 'Third Year', 'Second Semester', 'Regular 3rd Year', 'SM'),
(158, 'IT 325', 'IT Project Management', 3, 3, NULL, 'Third Year', 'Second Semester', 'IT 313', 'SM'),
(159, 'IT 331', 'Application Development and Emerging Technologies', 2, 3, 3, 'Third Year', 'Midterm', 'IT 321', 'SM'),
(160, 'IT 332', 'Integrative Programming and Technologies', 3, 3, NULL, 'Third Year', 'Midterm', 'IT 314', 'SM'),
(161, 'CS 423', 'Social Issues and Professional Practice', 3, 3, NULL, 'Fourth Year', 'First Semester', NULL, 'SM'),
(162, 'IT 411', 'Capstone Project 2', 3, 3, NULL, 'Fourth Year', 'First Semester', 'IT 324', 'SM'),
(163, 'SMT 405', 'Principles of System Thinking', 2, 3, 3, 'Fourth Year', 'First Semester', 'SMT 403', 'SM'),
(164, 'ENGG 405', 'Technopreneurship', 3, 3, NULL, 'Fourth Year', 'First Semester', NULL, 'SM'),
(165, 'IT 413', 'Advanced Information Assurance and Security', 2, 3, 3, 'Fourth Year', 'First Semester', 'IT 323', 'SM'),
(166, 'IT 414', 'System Quality Assurance', 2, 3, 3, 'Fourth Year', 'First Semester', 'IT 325', 'SM'),
(167, 'IT 412', 'Platform Technologies', 3, 3, NULL, 'Fourth Year', 'First Semester', 'IT 332', 'SM'),
(168, 'IT 421', 'Internship Training', NULL, 6, 500, 'Fourth Year', 'Second Semester', 'Regular 4th Year', 'SM');

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
-- Table structure for table `time_slots`
--

CREATE TABLE `time_slots` (
  `id` int(11) NOT NULL,
  `time_in` time NOT NULL,
  `time_out` time NOT NULL,
  `is_break` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure for view `enrolled_view`
--
DROP TABLE IF EXISTS `enrolled_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `enrolled_view`  AS SELECT concat(`s`.`first_name`,' ',`s`.`middle_name`,' ',`s`.`last_name`) AS `full_name`, `sub`.`subject_name` AS `subject_name`, `e`.`semester` AS `semester`, `e`.`academic_year` AS `academic_year` FROM ((`enrolled` `e` join `student` `s` on(`e`.`student_id` = `s`.`id`)) join `subjects` `sub` on(`e`.`sub_id` = `sub`.`sub_id`)) ;

-- --------------------------------------------------------

--
-- Structure for view `facultyschedulelog`
--
DROP TABLE IF EXISTS `facultyschedulelog`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `facultyschedulelog`  AS SELECT concat(`f`.`first_name`,' ',coalesce(`f`.`middle_name`,''),' ',`f`.`last_name`) AS `faculty_name`, `sub`.`subject_name` AS `subject_name`, `ss`.`days` AS `days`, `ss`.`time_in` AS `time_in`, `ss`.`time_out` AS `time_out`, `r`.`room_name` AS `room_name`, `sec`.`section_name` AS `section_name` FROM ((((`faculty` `f` join `subsched` `ss` on(`f`.`faculty_id` = `ss`.`faculty_id`)) join `subjects` `sub` on(`ss`.`subject_id` = `sub`.`sub_id`)) join `rooms` `r` on(`ss`.`room_id` = `r`.`room_id`)) join `section` `sec` on(`ss`.`section_id` = `sec`.`section_id`)) ORDER BY concat(`f`.`first_name`,' ',coalesce(`f`.`middle_name`,''),' ',`f`.`last_name`) ASC ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `current`
--
ALTER TABLE `current`
  ADD PRIMARY KEY (`currentID`);

--
-- Indexes for table `enrolled`
--
ALTER TABLE `enrolled`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student_subsched_unique` (`student_id`,`subsched_id`),
  ADD KEY `fk_substud_subsched` (`subsched_id`);

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
-- Indexes for table `time_slots`
--
ALTER TABLE `time_slots`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `current`
--
ALTER TABLE `current`
  MODIFY `currentID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `enrolled`
--
ALTER TABLE `enrolled`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

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
  MODIFY `section_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `student`
--
ALTER TABLE `student`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `student_section`
--
ALTER TABLE `student_section`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `subjects`
--
ALTER TABLE `subjects`
  MODIFY `sub_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=169;

--
-- AUTO_INCREMENT for table `subsched`
--
ALTER TABLE `subsched`
  MODIFY `sched_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `time_slots`
--
ALTER TABLE `time_slots`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `enrolled`
--
ALTER TABLE `enrolled`
  ADD CONSTRAINT `fk_substud_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_substud_subsched` FOREIGN KEY (`subsched_id`) REFERENCES `subsched` (`sched_id`) ON DELETE CASCADE;

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
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
