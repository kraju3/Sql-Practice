import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.result.SqlDateValueFactory;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.sql.Types.NULL;

public class Database {
    String user;
    String password;
    String url;
    Connection conn;
    List<String> data;
    List<String[]> parsed;

    Database(String username,String passwrd,String url){
        this.user = username;
        this.password = passwrd;
        this.url = url;

    }
    public void connectToDatabase() {
        System.out.println("Connecting to the database");
        try {
            this.conn = DriverManager.getConnection(url, user, password);


        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Error Connecting to the database");
        }
    }

    public void createTable (){
        String emoloyeeQuery = "CREATE TABLE IF NOT EXISTS homework4db.employee(" +
                                "eid INT NOT NULL UNIQUE, name VARCHAR(20), salary INT, primary key (eid), CHECK(salary<=600000))";
        String supervisorQuery = "CREATE TABLE IF NOT EXISTS homework4db.supervisor(" +
                                 "eid INT NOT NULL, sid INT, primary key (eid)) ";

        try{
            PreparedStatement createEmp = conn.prepareStatement(emoloyeeQuery);
            createEmp.execute();
            PreparedStatement createSup = conn.prepareStatement(supervisorQuery);
            createSup.execute();
            createEmp.close();
            createSup.close();
        }catch(SQLException e){
            System.out.println("Error creating the tables");
            System.out.println(e);
        }
    }
    public void dropTable(){
        String deleteE = "drop table if exists homework4db.employee";
        String deleteS = "drop table if exists homework4db.supervisor";
        try{
        PreparedStatement dropTables = conn.prepareStatement(deleteS);
        PreparedStatement dropS = conn.prepareStatement(deleteE);
        dropTables.execute();
        dropS.execute();
        System.out.println("Dropped both of the tables");
        conn.close();
        }catch (SQLException e){
            System.out.println("error deleting the tables");
            System.out.println(e);
        }
    }
    public int getCount(ResultSet rs){
        int rowCount = 0;
        try {
            if (rs.last()) {//make cursor to point to the last row in the ResultSet object
                rowCount = rs.getRow();
                rs.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
            }
        }catch(SQLException e){
            System.out.println(e);
        }
        return rowCount;
    }

    public void readFile(){
        try{
            data = Files.readAllLines(Paths.get("/Users/kiranraju/Documents/Sql/transfile.txt"));
            parsed = new ArrayList<>();
            String [] tokens;

            for(String s:data){
                tokens=s.split(" +");
                parsed.add(tokens);

            }
        }catch(Exception e){
            System.out.println(e);
        }

    }
    public void ExecuteFile(){
        System.out.println("Executing File");
        for(String[]arr:parsed){
            checkTrans(arr[0],arr);
        }
    }
    public void transOne(String[]arr){
        try{
            PreparedStatement deleteE = conn.prepareStatement("Delete from homework4db.employee WHERE eid = ?");
            PreparedStatement deleteTupe = conn.prepareStatement("Delete from homework4db.supervisor WHERE eid = ?");
            PreparedStatement deleteS = conn.prepareStatement("Update homework4db.supervisor SET sid = ? WHERE sid = ? ");
            deleteE.setInt(1,Integer.parseInt(arr[1]));
            deleteTupe.setInt(1,Integer.parseInt(arr[1]));

            deleteS.setNull(1,java.sql.Types.INTEGER);
            deleteS.setInt(2,Integer.parseInt(arr[1]));
            int employeeCount = deleteE.executeUpdate();
            if(employeeCount==0){
                System.out.println("Transaction 1 error");
            }
            else{
                deleteS.executeUpdate();
                deleteTupe.executeUpdate();
                System.out.println("Transaction 1 done");
            }

            deleteE.close();
            deleteS.close();
            deleteTupe.close();

        }
        catch(SQLException e){
            System.out.println(e);
            System.out.println("Transaction 1 Error: Employee with id "+arr[1]);
        }
    }
    public void transTwo(String[]arr){
        try{
            PreparedStatement insertE = conn.prepareStatement("INSERT INTO homework4db.employee (eid,name,salary) VALUES(?,?,?) ON DUPLICATE KEY UPDATE eid = eid;");
            PreparedStatement checkE = conn.prepareStatement("Select eid from employee where eid = ?");
            PreparedStatement insertS = conn.prepareStatement("INSERT INTO homework4db.supervisor VALUES(?,?)");
            if(arr.length == 5){
                if(Integer.parseInt(arr[1])==Integer.parseInt(arr[4])){
                    insertE.setInt(1,Integer.parseInt(arr[1]));
                    insertE.setString(2,arr[2]);
                    insertE.setInt(3,Integer.parseInt(arr[3]));
                    insertE.executeUpdate();
                    insertS.setInt(1,Integer.parseInt(arr[1]));
                    insertS.setNull(2,java.sql.Types.INTEGER);
                    System.out.println("Transaction two done");
                 }
                else {

                    insertE.setInt(1,Integer.parseInt(arr[1]));
                    insertE.setString(2,arr[2]);
                    insertE.setInt(3,Integer.parseInt(arr[3]));
                    insertE.executeUpdate();


                    checkE.setInt(1,Integer.parseInt(arr[4]));

                    ResultSet resultSet = checkE.executeQuery();
                    if(getCount(resultSet)==0){
                        System.out.println("ERROR! Supervisor not found in the employee relation inserting(eid,sid=NULL) to supervisor");
                        insertS.setInt(1,Integer.parseInt(arr[1]));
                        insertS.setNull(2,java.sql.Types.INTEGER);
                        insertS.executeUpdate();
                    }
                    else{//insert into supervisor
                        insertS.setInt(1,Integer.parseInt(arr[1]));
                        insertS.setInt(2,Integer.parseInt(arr[4]));
                        insertS.executeUpdate();
                        System.out.println("Transaction two done");
                    }
                }
             }
            else{
                insertE.setInt(1,Integer.parseInt(arr[1]));
                insertE.setString(2,arr[2]);
                insertE.setInt(3,Integer.parseInt(arr[3]));
                insertE.executeUpdate();

                System.out.println("ERROR! Supervisor not found in the employee relation inserting(eid,sid=NULL) to supervisor");
                insertS.setInt(1,Integer.parseInt(arr[1]));
                insertS.setNull(2,java.sql.Types.INTEGER);
                insertS.executeUpdate();

            }
            insertE.close();
            insertS.close();
            checkE.close();

        }
        catch(SQLException e){
            System.out.println("Transaction 2 Error: Employee with id "+arr[1]+e);

        }
    }

    public void transThree(String[]arr){
       try { //case where no sid is provided
           if (arr.length < 3) {
               PreparedStatement checkSupervisor = conn.prepareStatement("Select eid,sid from supervisor where eid = ?");
               checkSupervisor.setInt(1, Integer.parseInt(arr[1]));
               ResultSet result = checkSupervisor.executeQuery();
               if (getCount(result) != 0) {
                   PreparedStatement updateSupvisor = conn.prepareStatement("Update homework4db.supervisor SET sid = ? WHERE eid = ? ");
                   updateSupvisor.setNull(1, java.sql.Types.INTEGER);
                   updateSupvisor.setInt(2, Integer.parseInt(arr[1]));
                   updateSupvisor.executeUpdate();
               } else {
                   PreparedStatement insertSupvisor = conn.prepareStatement("INSERT INTO homework4db.supervisor VALUES(?,NULL)");
                   insertSupvisor.setInt(1, Integer.parseInt(arr[1]));
                   insertSupvisor.executeUpdate();
               }
               System.out.println("Transaction 3 done");
           }
           else {
               PreparedStatement checkSupervisor = conn.prepareStatement("Select eid,sid from supervisor where eid = ?");
               checkSupervisor.setInt(1, Integer.parseInt(arr[1]));
               ResultSet result = checkSupervisor.executeQuery();
               if (Integer.parseInt(arr[1]) == Integer.parseInt(arr[2])) {
                   if (getCount(result) != 0) {
                       PreparedStatement updateSupervisor = conn.prepareStatement("Update homework4db.supervisor SET sid = ? WHERE eid = ?");
                       updateSupervisor.setNull(1, java.sql.Types.INTEGER);
                       updateSupervisor.setInt(2, Integer.parseInt(arr[1]));
                       updateSupervisor.executeUpdate();
                   } else {
                       PreparedStatement insertS = conn.prepareStatement("INSERT INTO homework4db.supervisor VALUES(?,?)");
                       insertS.setNull(1, java.sql.Types.INTEGER);
                       insertS.setInt(2, Integer.parseInt(arr[2]));
                       insertS.executeUpdate();
                   }

               }
               else {
                   if (getCount(result) != 0) {
                       PreparedStatement updateSupervisor = conn.prepareStatement("Update homework4db.supervisor SET sid = ? WHERE eid = ?");
                       updateSupervisor.setInt(1, Integer.parseInt(arr[2]));
                       updateSupervisor.setInt(2, Integer.parseInt(arr[1]));
                       updateSupervisor.executeUpdate();
                   } else {
                       PreparedStatement insertS = conn.prepareStatement("INSERT INTO homework4db.supervisor VALUES(?,?)");
                       insertS.setInt(1, Integer.parseInt(arr[1]));
                       insertS.setInt(2, Integer.parseInt(arr[2]));
                       insertS.executeUpdate();
                   }
               }
               System.out.println("Transaction 3 done");
           }
       } catch(SQLException e){
           System.out.println("Error "+e);
       }
    }
    public void transFour(String[]arr){
        try{
            PreparedStatement avgSalary = conn.prepareStatement("Select avg(employee.salary) as avg_salary from homework4db.employee");
            ResultSet rs = avgSalary.executeQuery();
            while(rs.next()){
                double salary = rs.getDouble("avg_salary");
                System.out.print("Average Salary: "+ salary);
            }
            System.out.println("----->done");

        }catch(SQLException e){
            System.out.println("Transaction 4 Error "+ e);
        }
    }

    //to get the name of the employees directly and indirectly
    public void transFive(String[]arr){
        HashMap<Integer,String> allEmployees= new HashMap<>();

        try{
         if(arr.length==1){
             System.out.println("Transaction 5 Error");
         }
         else{
             PreparedStatement directSupervisor =  conn.prepareStatement("Select employee.name,employee.eid from homework4db.employee"+
                     " inner join homework4db.supervisor on employee.eid = supervisor.eid"+
                     " where supervisor.sid = ?;");
             directSupervisor.setInt(1,Integer.parseInt(arr[1]));
             ResultSet rs = directSupervisor.executeQuery();
             recursiveTrans5(allEmployees,rs,directSupervisor);
             Iterator forMap = allEmployees.entrySet().iterator();
             System.out.print("Name: { ");
             while(forMap.hasNext()){
                 Map.Entry element = (Map.Entry)forMap.next();
                 System.out.print(element.getValue()+", ");
             }
             System.out.println("} --> done");
         }
        }catch(SQLException e){
            System.out.println("error "+ e);
        }
    }
    public void recursiveTrans5(HashMap<Integer,String> totalList,ResultSet temp,PreparedStatement ps){
        try {
            if (temp.next()) {
                String name = temp.getString("name");
                int id = temp.getInt("eid");
                totalList.put(id,name);
                recursiveTrans5(totalList,temp, ps);
                ps.setInt(1, id);
                recursiveTrans5(totalList, ps.executeQuery(), ps);
            }
        }
        catch(SQLException e){
            System.out.println("error "+e);
        }
    }
    //indirect and direct average salary
    public void transSix(String[]arr){
        ArrayList<Integer> salary = new ArrayList<>();
        try{
            if(arr.length<2){
                System.out.println("Error");
            }
            else {
                PreparedStatement directSupervisor = conn.prepareStatement("Select employee.name,employee.eid,employee.salary " +
                        "from homework4db.employee " +
                        "inner join homework4db.supervisor on employee.eid = supervisor.eid " +
                        "where supervisor.sid = ?");

                directSupervisor.setInt(1, Integer.parseInt(arr[1]));

                ResultSet direct = directSupervisor.executeQuery();

                recursiveTrans6(salary, direct, directSupervisor);
                if (salary.size() > 0){

                    Double avg_salary = salary.stream().mapToDouble(i -> i).average().getAsDouble();
                    System.out.println("Average Salary Trans6: " +Math.round(avg_salary) + " --> done");
                }
                else{
                    System.out.println("Error: No employee with that sid: Average salary --> 0");
                }


            }
        }catch(SQLException e){
            System.out.println("error "+e);
        }
    }

    public void recursiveTrans6(ArrayList<Integer> salary,ResultSet temp,PreparedStatement ps){
        try{
            if(temp.next()){
                int id = temp.getInt("eid");
                int wage = temp.getInt("salary");
                salary.add(wage);
                recursiveTrans6(salary,temp,ps);
                ps.setInt(1,id);
                recursiveTrans6(salary,ps.executeQuery(),ps);
            }
        }catch(SQLException e){System.out.println(e);}
    }

    public void checkTrans(String option,String[]arr){
        switch (option){
            case "1":
                transOne(arr);
                break;
            case "2":
                transTwo(arr);
                break;
            case "3":
                transThree(arr);
                break;
            case "4":
                transFour(arr);
                break;
            case "5":
                transFive(arr);
                break;
            case "6":
                transSix(arr);
                break;
        }
    }

}
