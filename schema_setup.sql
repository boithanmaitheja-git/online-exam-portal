BEGIN
  FOR t IN (SELECT table_name FROM user_tables WHERE table_name IN (
    'ADMINS','TEACHER','STUDENT','EXAM','EXAM_QUESTION','STUDENT_EXAM','RESULT'
  )) LOOP
    EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS';
  END LOOP;

  FOR s IN (SELECT sequence_name FROM user_sequences WHERE sequence_name LIKE '%SEQ') LOOP
    EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
  END LOOP;
END;
/

-- TABLES -----------------------------------------------------

CREATE TABLE ADMINS (
  admin_id NUMBER(5) PRIMARY KEY,
  admin_name VARCHAR2(100) NOT NULL,
  username VARCHAR2(50) UNIQUE NOT NULL,
  password VARCHAR2(50) NOT NULL,
  email VARCHAR2(100)
);

CREATE TABLE TEACHER (
  teacher_id NUMBER(5) PRIMARY KEY,
  teacher_name VARCHAR2(100) NOT NULL,
  email VARCHAR2(100),
  username VARCHAR2(50) UNIQUE NOT NULL,
  password VARCHAR2(50) NOT NULL,
  department VARCHAR2(50)
);

CREATE TABLE STUDENT (
  student_id NUMBER(5) PRIMARY KEY,
  student_name VARCHAR2(100) NOT NULL,
  email VARCHAR2(100),
  username VARCHAR2(50) UNIQUE NOT NULL,
  password VARCHAR2(50) NOT NULL,
  branch VARCHAR2(50),
  year NUMBER(1)
);

CREATE TABLE EXAM (
  exam_id NUMBER(8) PRIMARY KEY,
  teacher_id NUMBER(5),
  exam_title VARCHAR2(100),
  pdf_name VARCHAR2(100),
  total_questions NUMBER(5),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  duration_minutes NUMBER(5),
  status VARCHAR2(20) CHECK (status IN ('Scheduled', 'Ongoing', 'Completed')),
  FOREIGN KEY (teacher_id) REFERENCES TEACHER(teacher_id)
);

CREATE TABLE EXAM_QUESTION (
  eq_id NUMBER(8) PRIMARY KEY,
  exam_id NUMBER(8),
  question_text CLOB NOT NULL,
  option_a VARCHAR2(200),
  option_b VARCHAR2(200),
  option_c VARCHAR2(200),
  option_d VARCHAR2(200),
  correct_option CHAR(1) CHECK (correct_option IN ('A','B','C','D')),
  FOREIGN KEY (exam_id) REFERENCES EXAM(exam_id)
);

CREATE TABLE STUDENT_EXAM (
  student_exam_id NUMBER(8) PRIMARY KEY,
  student_id NUMBER(5),
  exam_id NUMBER(8),
  started_on TIMESTAMP,
  submitted_on TIMESTAMP,
  auto_submitted CHAR(1) CHECK (auto_submitted IN ('Y','N')),
  FOREIGN KEY (student_id) REFERENCES STUDENT(student_id),
  FOREIGN KEY (exam_id) REFERENCES EXAM(exam_id)
);

CREATE TABLE RESULT (
  result_id NUMBER(8) PRIMARY KEY,
  student_id NUMBER(5),
  exam_id NUMBER(8),
  total_questions NUMBER(5),
  correct_answers NUMBER(5),
  score NUMBER(5,2),
  percentage NUMBER(5,2),
  grade VARCHAR2(2),
  FOREIGN KEY (student_id) REFERENCES STUDENT(student_id),
  FOREIGN KEY (exam_id) REFERENCES EXAM(exam_id)
);

-- SEQUENCES --------------------------------------------------

CREATE SEQUENCE ADMIN_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE TEACHER_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE STUDENT_SEQ START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE EXAM_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE EXAM_Q_SEQ START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE STUDENT_EXAM_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE RESULT_SEQ START WITH 1 INCREMENT BY 1;

-- DATA --------------------------------------------------------

INSERT INTO ADMINS VALUES (ADMIN_SEQ.NEXTVAL, 'System Admin', 'admin', 'admin123', 'admin@portal.com');

INSERT INTO TEACHER VALUES (TEACHER_SEQ.NEXTVAL, 'Ravi Kumar', 'ravi@portal.com', 'ravi', 'teach101', 'CSE');
INSERT INTO TEACHER VALUES (TEACHER_SEQ.NEXTVAL, 'Sita Sharma', 'sita@portal.com', 'sita', 'teach102', 'ECE');

INSERT INTO STUDENT VALUES (STUDENT_SEQ.NEXTVAL, 'Karthik', 'karthik@student.com', 'karthik', 'stud123', 'CSE', 2);
INSERT INTO STUDENT VALUES (STUDENT_SEQ.NEXTVAL, 'Meena', 'meena@student.com', 'meena', 'stud234', 'ECE', 3);

COMMIT;
