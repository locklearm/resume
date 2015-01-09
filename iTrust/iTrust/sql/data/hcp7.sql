INSERT INTO personnel(
MID,
AMID,
role,
lastName, 
firstName, 
address1,
address2,
city,
state,
zip,
phone,
specialty,
email)
VALUES (
9000000007,
null,
'hcp',
'Beaker',
'Beaker',
'Meep Meep Street',
'',
'Meep Meep Town',
'CA',
'12345-1234',
'999-888-7777',
'Pediatrician',
'meepmeep@meep.org'
)ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users(MID, password, role, sQuestion, sAnswer) VALUES(9000000007, 'fa08f8d373fbf3b50b44685e3a68843b514949d0bf34cef63f14b4a5bc58c6e9', 'hcp', 'first letter?', 'a')
ON DUPLICATE KEY UPDATE MID = MID;
/*password: pw*/
INSERT INTO hcpassignedhos(HCPID, HosID) VALUES(9000000007,'4'), (9000000007,'4')
ON DUPLICATE KEY UPDATE HCPID = HCPID;
