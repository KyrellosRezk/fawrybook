-- ==============================================
-- INSERT GOVERNORATES
-- ==============================================
INSERT INTO BASIC_DATA.GOVERNORATE (ID, NAME) VALUES
    (uuid_generate_v4(), 'Cairo'),
    (uuid_generate_v4(), 'Giza'),
    (uuid_generate_v4(), 'Alexandria'),
    (uuid_generate_v4(), 'Dakahlia'),
    (uuid_generate_v4(), 'Red Sea'),
    (uuid_generate_v4(), 'Beheira'),
    (uuid_generate_v4(), 'Fayoum'),
    (uuid_generate_v4(), 'Gharbia'),
    (uuid_generate_v4(), 'Ismailia'),
    (uuid_generate_v4(), 'Menofia'),
    (uuid_generate_v4(), 'Minya'),
    (uuid_generate_v4(), 'Qaliubiya'),
    (uuid_generate_v4(), 'New Valley'),
    (uuid_generate_v4(), 'Suez'),
    (uuid_generate_v4(), 'Aswan'),
    (uuid_generate_v4(), 'Assiut'),
    (uuid_generate_v4(), 'Beni Suef'),
    (uuid_generate_v4(), 'Port Said'),
    (uuid_generate_v4(), 'Damietta'),
    (uuid_generate_v4(), 'Sharkia'),
    (uuid_generate_v4(), 'South Sinai'),
    (uuid_generate_v4(), 'Kafr El Sheikh'),
    (uuid_generate_v4(), 'Matruh'),
    (uuid_generate_v4(), 'Luxor'),
    (uuid_generate_v4(), 'Qena'),
    (uuid_generate_v4(), 'North Sinai'),
    (uuid_generate_v4(), 'Sohag');

-- ==============================================
-- INSERT SAMPLE INDUSTRIES
-- ==============================================
INSERT INTO BASIC_DATA.INDUSTRY (ID, NAME) VALUES
   (uuid_generate_v4(), 'Information Technology'),
   (uuid_generate_v4(), 'Finance'),
   (uuid_generate_v4(), 'Healthcare'),
   (uuid_generate_v4(), 'Education'),
   (uuid_generate_v4(), 'Retail');

-- ==============================================
-- INSERT SAMPLE POSITIONS
-- ==============================================
INSERT INTO BASIC_DATA.POSITION (ID, NAME, INDUSTRY_ID) VALUES
    (uuid_generate_v4(), 'BackEnd Engineer', (SELECT ID FROM BASIC_DATA.INDUSTRY WHERE NAME = 'Information Technology')),
    (uuid_generate_v4(), 'FrontEnd Engineer', (SELECT ID FROM BASIC_DATA.INDUSTRY WHERE NAME = 'Information Technology')),
    (uuid_generate_v4(), 'System Administrator', (SELECT ID FROM BASIC_DATA.INDUSTRY WHERE NAME = 'Information Technology')),
    (uuid_generate_v4(), 'Accountant', (SELECT ID FROM BASIC_DATA.INDUSTRY WHERE NAME = 'Finance')),
    (uuid_generate_v4(), 'Financial Analyst', (SELECT ID FROM BASIC_DATA.INDUSTRY WHERE NAME = 'Finance')),
    (uuid_generate_v4(), 'Doctor', (SELECT ID FROM BASIC_DATA.INDUSTRY WHERE NAME = 'Healthcare')),
    (uuid_generate_v4(), 'Teacher', (SELECT ID FROM BASIC_DATA.INDUSTRY WHERE NAME = 'Education')),
    (uuid_generate_v4(), 'Sales Manager', (SELECT ID FROM BASIC_DATA.INDUSTRY WHERE NAME = 'Retail'));