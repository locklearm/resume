INSERT INTO patients
(MID, 
lastName, 
firstName,
dateofbirth)
VALUES
(200,
'McClain', 
'Brynn', 
'2013-05-01')
 ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users (MID, password, role, sQuestion, sAnswer) 
			VALUES (200, '84d251a6aaf04db7e75410d847e480aaf99cf34b0a1fbb9b4ca03c62fc1f2a37', 'patient', 'what is your favorite color?', 'blue')
 ON DUPLICATE KEY UPDATE MID = MID;
 /*password: pw*/

INSERT INTO declaredhcp(PatientID, HCPID) VALUES (200, 8000000010);
