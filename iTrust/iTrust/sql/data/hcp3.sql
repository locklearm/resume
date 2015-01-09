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
9000000003,
null,
'hcp',
'Stormcrow',
'Gandalf',
'4321 My Road St',
'PO BOX 2',
'CityName',
'NY',
'12345-1234',
'999-888-7777',
NULL,
'gstormcrow@iTrust.org'
) ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users(MID, password, role, sQuestion, sAnswer) VALUES(9000000003, 'cb1ae3ab1e31bc44d5f8e3755981690117a9d7630ee2ef4a594123ddc987289f', 'hcp', 'first letter?', 'a')
ON DUPLICATE KEY UPDATE MID = MID;
/*password: pw*/
INSERT INTO hcpassignedhos(HCPID, HosID) VALUES(9000000003,'9191919191')
ON DUPLICATE KEY UPDATE HCPID = HCPID;
