-- ===== FIRST: CREATE USERS =====
-- Patient users (IDs 1-5)
INSERT INTO app_user (id, username, password, provider_type, provider_id)
VALUES
    (1, 'aarav.sharma@example.com', 'password123', 'EMAIL', NULL),
    (2, 'diya.patel@example.com', 'password123', 'EMAIL', NULL),
    (3, 'dishant.verma@example.com', 'password123', 'EMAIL', NULL),
    (4, 'neha.iyer@example.com', 'password123', 'EMAIL', NULL),
    (5, 'kabir.singh@example.com', 'password123', 'EMAIL', NULL);

-- Doctor users (IDs 6-8)
INSERT INTO app_user (id, username, password, provider_type, provider_id)
VALUES
    (6, 'rakesh.mehta@example.com', 'password123', 'EMAIL', NULL),
    (7, 'sneha.kapoor@example.com', 'password123', 'EMAIL', NULL),
    (8, 'arjun.nair@example.com', 'password123', 'EMAIL', NULL);

-- ===== RESET SEQUENCES (for PostgreSQL) =====
SELECT setval('app_user_id_seq', (SELECT MAX(id) FROM app_user));

-- ===== INSERT PATIENTS (with user_id) =====
INSERT INTO patient (user_id, name, gender, birth_date, email, blood_group, created_at)
VALUES
    (1, 'Aarav Sharma', 'MALE', '1990-05-10', 'aarav.sharma@example.com', 'O_POSITIVE', CURRENT_TIMESTAMP),
    (2, 'Diya Patel', 'FEMALE', '1995-08-20', 'diya.patel@example.com', 'A_POSITIVE', CURRENT_TIMESTAMP),
    (3, 'Dishant Verma', 'MALE', '1988-03-15', 'dishant.verma@example.com', 'A_POSITIVE', CURRENT_TIMESTAMP),
    (4, 'Neha Iyer', 'FEMALE', '1992-12-01', 'neha.iyer@example.com', 'AB_POSITIVE', CURRENT_TIMESTAMP),
    (5, 'Kabir Singh', 'MALE', '1993-07-11', 'kabir.singh@example.com', 'O_POSITIVE', CURRENT_TIMESTAMP);

-- ===== INSERT DOCTORS =====
INSERT INTO doctor (user_id, name, specialization, email)
VALUES
    (6, 'Dr. Rakesh Mehta', 'Cardiology', 'rakesh.mehta@example.com'),
    (7, 'Dr. Sneha Kapoor', 'Dermatology', 'sneha.kapoor@example.com'),
    (8, 'Dr. Arjun Nair', 'Orthopedics', 'arjun.nair@example.com');

-- ===== INSERT APPOINTMENTS =====
INSERT INTO appointment (appointment_time, reason, doctor_user_id, patient_id)
VALUES
    ('2025-07-01 10:30:00', 'General Checkup', 6, 1),
    ('2025-07-02 11:00:00', 'Skin Rash', 7, 2),
    ('2025-07-03 09:45:00', 'Knee Pain', 8, 3),
    ('2025-07-04 14:00:00', 'Follow-up Visit', 6, 4),
    ('2025-07-05 16:15:00', 'Consultation', 6, 5),
    ('2025-07-06 08:30:00', 'Allergy Treatment', 7, 1);