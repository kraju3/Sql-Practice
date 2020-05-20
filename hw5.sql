
USE DEALERSHIPS;

/*Question 1*/

Select popularVehicle.make,popularVehicle.model 
from (Select Vehicle.make,Vehicle.model,count(Vehicle.model) AS totalCount,Rank() over( order by count(Vehicle.model) desc) as popular
		from DEALERSHIPS.Vehicle
		inner join DEALERSHIPS.BuyVehicle on BuyVehicle.VID = Vehicle.VID
		GROUP BY MAKE,MODEL) as popularVehicle
where popular = 1;



