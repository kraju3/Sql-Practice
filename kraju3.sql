#Kiran Raju
#kraju3

CREATE DATABASE IF NOT EXISTS `DEALERSHIPS`;
USE `DEALERSHIPS` ;

CREATE TABLE IF NOT EXISTS `DEALERSHIPS`.`Customer` (
  SSN INT NOT NULL,
  cname VARCHAR(45) NOT NULL,
  cgender VARCHAR(45) NOT NULL,
  ccity VARCHAR(45) NOT NULL,
  primary key(SSN));

  
CREATE TABLE IF NOT EXISTS `DEALERSHIPS`.`Vehicle`(
  VID INT NOT NULL,
  make VARCHAR(45) NOT NULL,
  model VARCHAR(45) NOT NULL,
  vyear int NOT NULL,
  primary key(VID));
  
CREATE TABLE IF NOT EXISTS `DEALERSHIPS`.`BuyVehicle` (
  VID INT NOT NULL,
  SSN INT NOT NULL,
  price REAL  NOT NULL,
  byear int NOT NULL,
  primary key(VID),
  foreign key(SSN) references Customer(SSN),
  foreign key (VID)references Vehicle(VID));
 

  
insert into Customer values (100001,'John','male','Chicago');
insert into Customer values (100002,'Peter','male','NewYork');
insert into Customer values (100003,'Chris','male','London');
insert into Customer values (100004,'Jakob','male','NewYork');
insert into Customer values (100005,'Brian','male','Paris');
insert into Customer values (100006,'Michael','female','Chicago');
insert into Customer values (100007,'Amy','female','Chicago');
insert into Customer values (100008,'Jessica','female','Miami');
insert into Customer values (100009,'Kate','female','Miami');
insert into Customer values (100010,'Linda','female','Boston');

insert into Vehicle values (50000,'Ford','Explorer',2003);
insert into Vehicle values (50001,'Ford','Mustang',2003);
insert into Vehicle values (50002,'Ford','GT',2006);
insert into Vehicle values (50003,'Nissan','GT-R',2013);
insert into Vehicle values (50004,'Chevrolet','Camaro',2012);
insert into Vehicle values (50005,'Dodge','challenger',2015);
insert into Vehicle values (50006,'Dodge','Charger',2014);
insert into Vehicle values (50007,'Dodge','Viper',2016);
insert into Vehicle values (50008,'Toyota','Camry',2004);
insert into Vehicle values (50009,'Toyota','Camry',2006);
insert into Vehicle values (50010,'Honda','Accord',2014);
insert into Vehicle values (50011,'Honda','Civic',2007);    
insert into Vehicle values (50012,'Nissan','Maxima',2010);
insert into Vehicle values (50013,'Nissan','Altima',2013);
insert into Vehicle values (50014,'Nissan','Rogue',2017); 
insert into Vehicle values (50015,'Honda','Civic',2013);
insert into Vehicle values (50016,'Honda','Civic',2013);
insert into Vehicle values (50017,'Nissan','GT-R',2015);
insert into Vehicle values (50018,'Nissan','Altima',2013);
insert into Vehicle values (50019,'Nissan','Altima',2014);
insert into Vehicle values (50020,'Honda','Civic',2010);

insert into BuyVehicle values(50001,100001,24000,2012);
insert into BuyVehicle values(50002,100001,13000,2016);
insert into BuyVehicle values(50003,100004,9000,2015);
insert into BuyVehicle values(50004,100005,22000,2017);
insert into BuyVehicle values(50005,100005,35000,2008);
insert into BuyVehicle values(50006,100005,10000,2005);
insert into BuyVehicle values(50008,100006,16000,2012);
insert into BuyVehicle values(50009,100007,5000,2005);
insert into BuyVehicle values(50010,100008,18000,2017);
insert into BuyVehicle values(50011,100009,30000,2007);
insert into BuyVehicle values(50012,100008,40000,2009);
insert into BuyVehicle values(50013,100009,32000,2010);
insert into BuyVehicle values(50014,100008,23000,2009);
insert into BuyVehicle values(50015,100010,23000,2005);
insert into BuyVehicle values(50016,100004,33000,2004);
insert into BuyVehicle values(50017,100008,80000,2016);
insert into BuyVehicle values(50018,100008,34000,2009);
insert into BuyVehicle values(50019,100008,29000,2016);
insert into BuyVehicle values(50020,100002,22000,2011);
insert into BuyVehicle values(50000,100001,30000,2007);



/*Additional test queries for question 4*/
insert into Customer values(100089,'Cathy','female','Chicago');
insert into Vehicle values(1233456,'Ford','Focus',2001);
insert into Vehicle values(123456,'Ford','Focus',2001);
insert into Vehicle values(3333,'Ford','Mustang',2009);
insert into BuyVehicle values(123456,100089,41000,2003);
insert into BuyVehicle values (1233456,100007,45000,2017);
insert into BuyVehicle values(3333,100007,42000,2019);

/*Test input for q5 */
USE DEALERSHIPS;
insert into Customer values(1111,'James','male','Chicago');
insert into Vehicle values (2222,'Ford','Explorer',2019);
USE DEALERSHIPS;
insert into Vehicle values(3334,'Ford','Mustang',2019);
insert into BuyVehicle values(2222,1111,12000,2020),(3334,1111,13000,2020);



/*Question 1*/
SELECT name FROM Customer WHERE gender = 'male' AND city = 'Chicago';



/*Question 2*/
select name 
from Customer
cross join BuyVehicle
where Customer.ssn = BuyVehicle.ssn and BuyVehicle.price > 40000.00
group by name;

/*Question 3*/
select make
from Vehicle AS v
inner join BuyVehicle AS b
on v.VID = b.VID
where b.ssn IN (select ssn
				from Customer
                where city = 'Chicago')
group by make;


			
 /*Question 4*/

with female_Ford (model,total_count,diff_ssn) AS 
	   (select model,count(v.VID),count(distinct(b.ssn))
	   from Vehicle as v
       inner join BuyVehicle as b on v.VID = b.VID
       inner join Customer as a on b.ssn = a.ssn
       where v.make = 'Ford' and a.gender = 'female' and a.city = 'Chicago'
       group by model
       having count(v.VID)>=2 and count(distinct(b.ssn))>=2)
Select model
from female_Ford;



/*Question 5*/
with ssn_year(ssn,year,total_count) as 
	(select Customer.ssn,Vehicle.year,count(make)
	from Customer
	inner join BuyVehicle on Customer.ssn = BuyVehicle.ssn
	inner join Vehicle on Vehicle.VID = BuyVehicle.VID
	where gender = 'male' and Vehicle.make = 'Ford'
	group by Customer.ssn,Vehicle.year
	having count(make) >= 2)
select ssn,year
from ssn_year;




/*Question 6*/
select avg(price) as avg_price,make,model
from Vehicle as v
inner join BuyVehicle as b on v.VID = b.VID
group by make,model;



		
/*Question 7*/
 select b.name, count(VID) as total_cars
 from Customer as b
 inner join BuyVehicle as v on v.ssn = b.ssn
 where v.price >=25000
 group by b.name 
 having count(VID) >=2
 order by b.name ASC;
 
 
 
