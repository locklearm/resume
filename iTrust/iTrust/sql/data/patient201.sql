INSERT INTO patients
(MID, 
lastName, 
firstName,
dateofbirth)
VALUES
(201,
'Hudson', 
'Caldwell', 
'2011-09-29')
 ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users (MID, password, role, sQuestion, sAnswer) 
			VALUES (201, '97f7721769215eacea98c899f44e6499ae72855cfd766b8dc6e8f351c9f10c8b', 'patient', 'what is your favorite color?', 'blue')
 ON DUPLICATE KEY UPDATE MID = MID;
 /*password: pw*/

INSERT INTO declaredhcp(PatientID, HCPID) VALUES (201, 8000000010);
