INSERT INTO patients
(MID, 
lastName, 
firstName,
dateofbirth)
VALUES
(204,
'Ross', 
'Thane', 
'1989-01-03')
 ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users (MID, password, role, sQuestion, sAnswer) 
			VALUES (204, '5aed2b7cfb347d4aca78dfe1512bd444cc237ef386e52c2ba9228473d0ca9d38', 'patient', 'what is your favorite color?', 'blue')
 ON DUPLICATE KEY UPDATE MID = MID;
 /*password: pw*/

INSERT INTO declaredhcp(PatientID, HCPID) VALUES (204, 8000000010);
