INSERT INTO patients
(MID, 
lastName, 
firstName,
dateofbirth)
VALUES
(202,
'Gray', 
'Fulton', 
'2008-10-10')
 ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users (MID, password, role, sQuestion, sAnswer) 
			VALUES (202, 'df1ede273c109654a3ee1c33c7dd540fbbf5b11d258adf868bce3a79cabc53ac', 'patient', 'what is your favorite color?', 'blue')
 ON DUPLICATE KEY UPDATE MID = MID;
 /*password: pw*/

INSERT INTO declaredhcp(PatientID, HCPID) VALUES (202, 8000000010);
