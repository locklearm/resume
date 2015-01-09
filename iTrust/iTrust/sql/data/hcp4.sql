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
9000000004,
null,
'hcp',
'Medico',
'Antonio',
'4321 My Road St',
'PO BOX 2',
'Vandelay City',
'NY',
'12345-1234',
'999-888-7777',
'surgeon',
'amedico@iTrust.org'
)ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users(MID, password, role, sQuestion, sAnswer) VALUES(9000000004, '73362610c01f915509e0e9ef7a8719fa85f7a5c9e691377db3fd411a23b00aef', 'hcp', 'first letter?', 'a')
ON DUPLICATE KEY UPDATE MID = MID;
/*password: pw*/
INSERT INTO hcpassignedhos(HCPID, HosID) VALUES(9000000004,'9191919191'), (9000000004,'8181818181')
ON DUPLICATE KEY UPDATE HCPID = HCPID;
