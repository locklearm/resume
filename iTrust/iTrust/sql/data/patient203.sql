INSERT INTO patients
(MID, 
lastName, 
firstName,
dateofbirth)
VALUES
(203,
'Griffin', 
'Daria', 
'1993-10-25')
 ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users (MID, password, role, sQuestion, sAnswer) 
			VALUES (203, 'c8c523cf8560a71e4082643970a258f3e0a8f62e227e855cb70fc64bf4147db4', 'patient', 'what is your favorite color?', 'blue')
 ON DUPLICATE KEY UPDATE MID = MID;
 /*password: pw*/

INSERT INTO declaredhcp(PatientID, HCPID) VALUES (203, 8000000010);