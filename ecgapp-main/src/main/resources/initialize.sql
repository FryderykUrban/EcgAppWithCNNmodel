CREATE TABLE IF NOT EXISTS Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(512) NOT NULL,
    email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    address TEXT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE IF NOT EXISTS ECGRecords (
    ecg_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    record_date DATETIME,
    ecg_signal_blob MEDIUMBLOB,
    samples_blob MEDIUMBLOB,
    analysis_results TEXT,
    arrhythmia_intervals TEXT,
    FOREIGN KEY (patient_id) REFERENCES Patients(id)
);

ALTER TABLE Users ADD UNIQUE (username);

INSERT IGNORE INTO Users (first_name, last_name, username, password, email) VALUES
('John', 'Smith', 'doctorSmith', 'passwordSmith', 'smith@email.com'),
('James', 'Johnson', 'doctorJohnson', 'passwordJohnson', 'johnson@email.com'),
('Robert', 'Williams', 'doctorWilliams', 'passwordWilliams', 'williams@email.com'),
('Michael', 'Jones', 'doctorJones', 'passwordJones', 'jones@email.com'),
('David', 'Brown', 'doctorBrown', 'passwordBrown', 'brown@email.com'),
('William', 'Davis', 'doctorDavis', 'passwordDavis', 'davis@email.com'),
('Joseph', 'Miller', 'doctorMiller', 'passwordMiller', 'miller@email.com'),
('Richard', 'Garcia', 'doctorGarcia', 'passwordGarcia', 'garcia@email.com'),
('Daniel', 'Rodriguez', 'doctorRodriguez', 'passwordRodriguez', 'rodriguez@email.com'),
('Paul', 'Martinez', 'doctorMartinez', 'passwordMartinez', 'martinez@email.com');

-- For Dr. Smith
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorSmith');
CALL InsertPatientIfNeeded(@doctorId, 'Sophia', 'Taylor', '1990-05-12', 'FEMALE', '123 Cherry Lane, Springfield');
CALL InsertPatientIfNeeded(@doctorId, 'Jackson', 'Anderson', '1985-11-23', 'MALE', '456 Elm Street, Rivertown');

-- For Dr. Johnson
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorJohnson');
CALL InsertPatientIfNeeded(@doctorId, 'Liam', 'White', '1992-09-03', 'MALE', '123 Pine Road, Mountainview');
CALL InsertPatientIfNeeded(@doctorId, 'Isabella', 'Harris', '1987-06-21', 'FEMALE', '456 Maple Drive, Greentown');
CALL InsertPatientIfNeeded(@doctorId, 'Oliver', 'Clark', '1986-07-25', 'MALE', '789 Birch Blvd, Lakewood');

-- For Dr. Williams
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorWilliams');
CALL InsertPatientIfNeeded(@doctorId, 'Mia', 'Robinson', '1993-12-10', 'FEMALE', '123 Palm Street, Coastal City');
CALL InsertPatientIfNeeded(@doctorId, 'Aiden', 'Hall', '1981-02-20', 'MALE', '456 Cedar Avenue, Hilltop');
CALL InsertPatientIfNeeded(@doctorId, 'Lucas', 'Young', '1999-03-15', 'MALE', '789 Oak Circle, Forestville');
CALL InsertPatientIfNeeded(@doctorId, 'Ella', 'King', '2000-11-25', 'FEMALE', '111 Birch Blvd, Lakewood');

-- For Dr. Jones
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorJones');
CALL InsertPatientIfNeeded(@doctorId, 'Emily', 'Wilson', '1989-04-10', 'FEMALE', '123 Willow Street, Valleytown');
CALL InsertPatientIfNeeded(@doctorId, 'Harper', 'Martin', '2002-12-15', 'FEMALE', '456 Alder Alley, Summitville');
CALL InsertPatientIfNeeded(@doctorId, 'Ethan', 'Thompson', '1982-05-30', 'MALE', '789 Pine Place, Rivercity');

-- For Dr. Brown
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorBrown');
CALL InsertPatientIfNeeded(@doctorId, 'Ava', 'Gonzalez', '1995-10-02', 'FEMALE', '123 Maple Mews, Cliffside');
CALL InsertPatientIfNeeded(@doctorId, 'Benjamin', 'Hill', '1996-09-13', 'MALE', '456 Oak Oval, Plainview');

-- For Dr. Davis
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorDavis');
CALL InsertPatientIfNeeded(@doctorId, 'Noah', 'Phillips', '1984-11-09', 'MALE', '432 Pine Road, Mountainpeak');
CALL InsertPatientIfNeeded(@doctorId, 'Madison', 'Campbell', '1977-05-22', 'FEMALE', '356 Maple Lane, Greenfield');

-- For Dr. Miller
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorMiller');
CALL InsertPatientIfNeeded(@doctorId, 'Abigail', 'Green', '1988-03-03', 'FEMALE', '341 Birch Road, Greenway');
CALL InsertPatientIfNeeded(@doctorId, 'Jacob', 'Nelson', '1994-07-16', 'MALE', '126 Cedar Street, Hillside');
CALL InsertPatientIfNeeded(@doctorId, 'Michael', 'Baker', '1978-08-14', 'MALE', '178 Oak Avenue, Forestview');
CALL InsertPatientIfNeeded(@doctorId, 'Emma', 'Carter', '1985-02-28', 'FEMALE', '132 Palm Place, Seaside');
CALL InsertPatientIfNeeded(@doctorId, 'Daniel', 'Perez', '1992-04-12', 'MALE', '890 Maple Mews, Peakview');
CALL InsertPatientIfNeeded(@doctorId, 'Evelyn', 'Edwards', '2001-09-21', 'FEMALE', '902 Elm Street, Riverside');

-- For Dr. Garcia
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorGarcia');
CALL InsertPatientIfNeeded(@doctorId, 'Jackson', 'Collins', '1986-10-05', 'MALE', '145 Willow Way, Valleyridge');
CALL InsertPatientIfNeeded(@doctorId, 'Alexander', 'Sanchez', '1979-06-19', 'MALE', '698 Alder Alley, Summitdale');
CALL InsertPatientIfNeeded(@doctorId, 'Amelia', 'Scott', '1983-05-09', 'FEMALE', '403 Pine Parkway, Mountainpeak');
CALL InsertPatientIfNeeded(@doctorId, 'Logan', 'Reyes', '1995-03-22', 'MALE', '790 Oak Oval, Plainland');

-- For Dr. Rodriguez
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorRodriguez');
CALL InsertPatientIfNeeded(@doctorId, 'Luke', 'Coleman', '1980-11-10', 'MALE', '123 Palm Promenade, Coastal Creek');
CALL InsertPatientIfNeeded(@doctorId, 'Hannah', 'Gray', '1977-12-20', 'FEMALE', '456 Cedar Circuit, Hillhaven');
CALL InsertPatientIfNeeded(@doctorId, 'Matthew', 'Ramirez', '1999-03-25', 'MALE', '789 Birch Boulevard, Lakeledge');
CALL InsertPatientIfNeeded(@doctorId, 'Olivia', 'James', '1996-08-10', 'FEMALE', '102 Oak Oval, Forestford');
CALL InsertPatientIfNeeded(@doctorId, 'Anthony', 'Ray', '1993-05-03', 'MALE', '234 Maple Mews, Cliffclimb');
CALL InsertPatientIfNeeded(@doctorId, 'Elijah', 'Watson', '1989-06-29', 'MALE', '567 Alder Alley, Summitside');
CALL InsertPatientIfNeeded(@doctorId, 'Isabella', 'Brooks', '2002-10-08', 'FEMALE', '890 Elm Esplanade, Riverrun');

-- For Dr. Martinez
SET @doctorId = (SELECT user_id FROM Users WHERE username = 'doctorMartinez');
CALL InsertPatientIfNeeded(@doctorId, 'David', 'Sanders', '1991-04-21', 'MALE', '121 Willow Walk, Valleyvale');
CALL InsertPatientIfNeeded(@doctorId, 'Sophia', 'Price', '1988-05-29', 'FEMALE', '452 Alder Avenue, Summitstand');
CALL InsertPatientIfNeeded(@doctorId, 'James', 'Bennett', '1976-06-19', 'MALE', '789 Pine Place, Mountainmark');
CALL InsertPatientIfNeeded(@doctorId, 'Charlotte', 'Barnes', '1980-07-25', 'FEMALE', '111 Birch Bend, Lakelead');
CALL InsertPatientIfNeeded(@doctorId, 'Christopher', 'Knight', '1994-12-12', 'MALE', '234 Palm Parkway, Coastalcove');
