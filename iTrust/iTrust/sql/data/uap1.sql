INSERT INTO personnel
(MID,
AMID,
lastName,
firstName,
address1,
address2,
city,
state,
zip,
phone
)
VALUES (
8000000009,
9000000000,
'LastUAP',
'FirstUAP',
'100 Ave',
'',
'Raleigh',
'NC',
'27607',
'111-111-1111'
);

INSERT INTO users(MID, password, role, sQuestion, sAnswer) 
VALUES (8000000009, '7dc88c3bd134712edbe57437285a289fdebb63ed164f9a533103ff48be8d7856', 'uap', 'opposite of yin', 'yang');
--password: pw