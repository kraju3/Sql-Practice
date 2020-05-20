import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.SQLException;

public class Main {
    public static void main (String args []) throws SQLException, FileNotFoundException {
        PrintStream fileOutput = new PrintStream("./out.txt");
        System.setOut(fileOutput);
        Database work = new Database("kiraju","connect","jdbc:mysql://localhost:3306/homework4db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        work.connectToDatabase();
        work.createTable();
        work.readFile();
        work.ExecuteFile();
        work.dropTable();

    }

}
