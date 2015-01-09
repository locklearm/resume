DELETE FROM users WHERE MID = 12;
DELETE FROM officevisits WHERE PatientID = 12;
DELETE FROM patients WHERE MID = 12;
DELETE FROM declaredhcp WHERE PatientID = 12;
DELETE FROM ovdiagnosis WHERE VisitID = 1064;
DELETE FROM ovmedication WHERE VisitID = 1064;


INSERT INTO users(MID, password, role, sQuestion, sAnswer) 
			VALUES (12, '3a3bcc8d0ba0437db4848341a569fbc099109e3545e2506df127096493d0b22c', 'patient', 'how you doin?', 'good');
/*password: pw*/
INSERT INTO patients (MID, firstName,lastName, email, phone) 
VALUES (12, 'Volcano', 'Blammo', 'g@h.com', '919-555-9213');


INSERT INTO officevisits(id,visitDate,HCPID,notes,HospitalID,PatientID)
VALUES (1064,'2007-6-09',9900000000,'Yet another office visit.','1',12);



INSERT INTO ovmedication(NDCode, VisitID, StartDate,EndDate,Dosage,Instructions)
	VALUES ('647641512', 1064, '2006-10-10', DATE_ADD(CURDATE(), INTERVAL 8 DAY), 5, 'Take twice daily');


INSERT INTO ovdiagnosis(ICDCode, VisitID) VALUES 
			(493.00, 1064);

INSERT INTO declaredhcp(PatientID, HCPID) VALUES (12, 9900000000);