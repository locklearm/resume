INSERT INTO patients
(MID,
lastName, 
firstName,
email,
address1,
address2,
city,
state,
zip,
phone,
eName,
ePhone,
iCName,
iCAddress1,
iCAddress2,
iCCity, 
ICState,
iCZip,
iCPhone,
iCID,
dateofbirth,
dateofdeath,
mothermid,
fathermid,
bloodtype,
ethnicity,
gender,
topicalnotes)
VALUES
(21,
'Peach',
'Princess',
'peach@gmail.com',
'123 Happy Lane',
'',
'Raleigh', 
'NC',
'27603',
'222-222-1234',
'Mario',
'888-333-8942',
'Aetna', 
'1234 Aetna Blvd', 
'Suite 602',
'Charlotte',
'NC',
'28215',
'704-555-1234', 
'ChetumNHowe', 
'2008-06-15',
NULL,
1,
0,
'AB+',
'Caucasian',
'Female',
''
);

INSERT INTO users(MID, password, role, sQuestion, sAnswer) 
VALUES (21, 'e5395db6c76522668a6db3f48a1d4e077a392c6bf740b6841e626ed893d16b98', 'patient', 'first letter?', 'a');
/*password: pw*/

INSERT INTO declaredhcp(PatientID,HCPID)
VALUE(21, 9000000003);
