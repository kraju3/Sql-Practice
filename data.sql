
USE DEALERSHIP;

/*Question 1*/
SELECT name FROM Customer WHERE gender = 'male' AND city = 'Chicago';



/*Question 2*/
select name 
from Customer
cross join BuyVehicle
where Customer.ssn = BuyVehicle.ssn and BuyVehicle.price > 40000.00
group by name;

/*Q3*/
select make
from Vehicle AS v
inner join BuyVehicle AS b
on v.VID = b.VID
where b.ssn IN (select ssn
				from Customer
                where city = 'Chicago')
group by make;


			
 /*q4*/

with female_Ford (model,total_count,diff_ssn) AS 
	   (select model,count(v.VID),count(distinct(b.ssn))
	   from Vehicle as v
       inner join BuyVehicle as b on v.VID = b.VID
       inner join Customer as a on b.ssn = a.ssn
       where v.make = 'Ford' and a.gender = 'Female' and a.city = 'Chicago'
       group by model
       having count(v.VID)>=2 and count(distinct(b.ssn))>=2)
Select model
from female_Ford;


/*q5*/
with ssn_year(ssn,year,total_count) as 
	(select Customer.ssn,Vehicle.year,count(make)
	from Customer
	inner join BuyVehicle on Customer.ssn = BuyVehicle.ssn
	inner join Vehicle on Vehicle.VID = BuyVehicle.VID
	where gender = 'Male' and Vehicle.make = 'Ford'
	group by Customer.ssn,Vehicle.year
	having count(make) >= 2)
select ssn,year
from ssn_year;



/*q6*/
select avg(price) as avg_price,make,model
from Vehicle as v
inner join BuyVehicle as b on v.VID = b.VID
group by make,model;



		
/*q7*/
 select TwentyFive.name 
 from (select b.name, count(VID) 
		from Customer as b
		inner join BuyVehicle as v on v.ssn = b.ssn
		where v.price >=25000
		group by b.name 
		having count(VID) >=2
        order by b.name ASC) as TwentyFive;
 
 
 







