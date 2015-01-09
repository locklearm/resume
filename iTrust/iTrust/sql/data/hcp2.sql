

INSERT INTO personnel(
MID,
AMID,
role,
lastName, 
firstName, 
address1,
address2)
VALUES (
9990000000,
null,
'hcp',
'Incomplete',
'Jimmy',
'567 Nowhere St.',
'PO Box 4')
ON DUPLICATE KEY UPDATE mid = mid;

INSERT INTO users(MID, password, role, sQuestion, sAnswer) VALUES(9990000000, '3a965fef95c3d5743a21d2737079e4204471bc9262c72c067000cd55645e9cd5', 'hcp', 'second letter?', 'b')
ON DUPLICATE KEY UPDATE mid = mid;
/*password: pw*/
INSERT INTO hcpassignedhos(HCPID, HosID) VALUES(9990000000,'9191919191'), (9990000000,'8181818181')
ON DUPLICATE KEY UPDATE HCPID = HCPID;
