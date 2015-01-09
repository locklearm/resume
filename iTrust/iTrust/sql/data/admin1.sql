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
specialty) 
VALUES (
9000000001,
null,
'admin',
'Shifter',
'Shape',
'4321 My Road St',
'PO BOX 2',
'CityName',
'NY',
'12345-1234',
'999-888-7777',
'administrator'
)  ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users(MID, password, role, sQuestion, sAnswer) VALUES(9000000001, '98c1d70875e70964355cae6782e98675932a41219826f510c4f29677f20f5360', 'admin', 'first letter?', 'a')
 ON DUPLICATE KEY UPDATE MID = MID;
 /*password: pw*/
