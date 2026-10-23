USE defaultdb;
CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    username VARCHAR(50) UNIQUE,
    phone VARCHAR(15),
    email VARCHAR(100),
    dob DATE,
    city VARCHAR(50),
    gender VARCHAR(20),
    password VARCHAR(100),
    college VARCHAR(150),
    branch VARCHAR(100),
    qualification VARCHAR(100),
    year VARCHAR(20),
    cgpa DECIMAL(4,2),
    skills TEXT,
    experience INT,
    preferred_location TEXT,
    preferred_duration VARCHAR(30),
    expected_stipend INT,
    preferred_domain TEXT
);

CREATE TABLE startups (
    id INT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(150),
    company_id VARCHAR(50) UNIQUE,
    email VARCHAR(100),
    password VARCHAR(100)
);

CREATE TABLE internships (
    id INT PRIMARY KEY AUTO_INCREMENT,
    company_id VARCHAR(50),
    title VARCHAR(150),
    domain VARCHAR(100),
    location VARCHAR(100),
    duration VARCHAR(50),
    stipend INT,
    skills TEXT,
    eligibility TEXT,
    application_link VARCHAR(255),
    FOREIGN KEY (company_id) REFERENCES startups(company_id)
);

CREATE TABLE applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    internship_id INT,
    status VARCHAR(30) DEFAULT 'Applied'
);
