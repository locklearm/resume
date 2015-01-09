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
9000000000,
null,
'hcp',
'Doctor',
'Kelly',
'4321 My Road St',
'PO BOX 2',
'CityName',
'NY',
'12345-1234',
'999-888-7777',
'surgeon',
'kdoctor@iTrust.org'
)ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users(MID, password, role, sQuestion, sAnswer) VALUES(9000000000, '5f7174ed8dd9150ebbdcebc293d82f0dd2dba6d5da1041b4c7badbd601fa1913', 'hcp', 'first letter?', 'a')
ON DUPLICATE KEY UPDATE MID = MID;
/*password: pw*/

INSERT INTO hcpassignedhos(HCPID, HosID) VALUES(9000000000,'9191919191'), (9000000000,'8181818181')
ON DUPLICATE KEY UPDATE HCPID = HCPID;
