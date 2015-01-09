
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
9900000000,
null,
'hcp',
'Arehart',
'Tester',
'2110 Thanem Circle',
'Apt. 302',
'Raleigh',
'NC',
'12345-1234',
'999-888-7777',
'Neurologist',
'tarehart@iTrust.org'
) ON DUPLICATE KEY UPDATE mid = mid;

INSERT INTO users(MID, password, role, sQuestion, sAnswer) VALUES(9900000000, 'df11bedb95dbf85065b7a40cffae16fdca00cf15fb23a23e81305d064de32299', 'hcp', 'second letter?', 'b')
ON DUPLICATE KEY UPDATE mid = mid;
/*password: pw*/
INSERT INTO hcpassignedhos(HCPID, HosID) VALUES(9900000000,'9191919191'), (9900000000,'8181818181')
ON DUPLICATE KEY UPDATE HCPID = HCPID;
