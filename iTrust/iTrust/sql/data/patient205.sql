INSERT INTO patients
(MID, 
lastName, 
firstName,
dateofbirth,
gender)
VALUES
(205,
'Smith', 
'Joe', 
'1989-01-01',
'Male')
 ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users (MID, password, role, sQuestion, sAnswer) 
			VALUES (205, 'e26428dc99d8fe55ba7137ede0846b48474ab4f6896f07049c85287b2faaa13b', 'patient', 'what is your favorite color?', 'blue')
 ON DUPLICATE KEY UPDATE MID = MID;
 /*password: pw*/

INSERT INTO declaredhcp(PatientID, HCPID) VALUES (205, 9000000000);

INSERT INTO personalhealthinformation
		(PersonalHealthInformationID, PatientID, Height, Weight, HeadCircumference, Smoker, SmokingStatus, HouseHoldSmokingStatus, BloodPressureN, BloodPressureD, CholesterolHDL, CholesterolLDL, CholesterolTri, HCPID, OfficeVisitID,AsOfDate)
VALUES 	(40							,205		,14.1	,21.3	,20.3				,0		,9				,1					,0					,0			,	0			,	0			,	0			,9000000000,null,'1989-06-07 20:33:58.0'),
		(41							,205		,16.1	,25.0	,25.3				,0		,9				,1					,0					,0			,	0			,	0			,	0			,9000000000,null,'1991-06-07 20:33:58.0'),
		(42							,205		,36.5	,75.1	,0					,0		,9				,2					,120				,70			,	0			,	0			,	0			,9000000000,null,'1999-06-07 20:33:58.0'),
		(43							,205		,60.2	,125.3	,0					,0		,9				,2					,125				,75			,	45			,	101			,	23			,9000000000,null,'2009-06-07 20:33:58.0'),
		(44							,205		,70.3	,180.3	,0					,1		,4				,3					,130				,80			,	50			,	102			,	50			,9000000000,null,'2010-06-07 20:33:58.0'); 

