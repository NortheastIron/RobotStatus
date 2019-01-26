package statusrobotm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Console;
import java.sql.*;
import java.sql.DriverManager;
import java.util.Date;
import java.text.*;
import java.util.ArrayList;
import static statusrobotm.StatusRobotM.getMsqlConnection;

class threadNameClass{
    
    int threadName = 0;
}
/**
 *
 * @author dns
 */
public class StatusRobotM {
    
    BufferedReader br;
    /**
     * @param args the command line arguments
     */
    public static String inputDate ;
    public static String userName;
    public static String userPassword;
    public static String nameDataBase;
    
    StatusRobotM(String args){
        inputDate = args;
    }
    
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        StatusRobotM robot = new StatusRobotM(args[0]); 
        //StatusRobotM robot = new StatusRobotM("2019-01-24"); //for IDE
        robot.go();
    }
    
    public void go(){
        
        try{
        Console console = System.console();
 
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(0);
        }
        /*br =new BufferedReader(new InputStreamReader(System.in));   //for IDE
        System.out.println("enter name database");
        nameDataBase = br.readLine();
        System.out.println("enter database user name");
        userName = br.readLine();
        System.out.println("enter database password");
        userPassword = br.readLine();*/
        
        userName = console.readLine("Enter user name: ");         //for console
        char tempInput[] = console.readPassword("Enter password: ");
        userPassword = String.copyValueOf(tempInput);
        nameDataBase = console.readLine("Enter name database: ");
        
        updateDataBase();
        //br.close(); //for IDE
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void updateDataBase(){
        threadNameClass commonObject= new threadNameClass();
        
        for (int i = 1; i < 1100; i++) {
            Thread t = new Thread(new replenishmentDataBaseThread(commonObject));
            t.setName(Integer.toString(i));
            t.start();
        }  
    }
    
    public static Connection getMsqlConnection(String userName, String password, String nameDataBase) throws Exception{  
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/"
        +nameDataBase+"?serverTimezone=Asia/Novosibirsk&useSSL=true", userName, password);     
    }
      
}
class pageName{
    static String pageId = "";
}

class replenishmentDataBaseThread implements Runnable{
    
    public  Connection connection;
    threadNameClass res;
    
    replenishmentDataBaseThread(threadNameClass res)
    {
        this.res = res;
    }
    
    public void run(){
        boolean temp=true;
        
        try{ 
            
            synchronized(res){
            connection = getMsqlConnection(StatusRobotM.userName, StatusRobotM.userPassword, StatusRobotM.nameDataBase);
            }
           
            while(temp){ 
                    temp = updateMethod(StatusRobotM.inputDate);
            }
            connection.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Invalid input");
        }
    }
    
    public synchronized  String selectMethod(String date){
        
        try{
            String output;
            String sql = "select * from sitetable where date!='"+date+"' limit 1;";
            ResultSet rs = getLine(connection, sql);
            rs.next();
            output = rs.getString(1) + " " + rs.getString(2);
            String sql1 = "UPDATE sitetable SET date = '"+dateNow()+"', status = '"+0+"' WHERE id = '"+rs.getString(1)+"';";
            Statement myStmt = connection.createStatement();
            myStmt.executeUpdate(sql1);
           
            return output;
        }
        catch(Exception ex){
            ex.printStackTrace();
            
            return "empty";
        }
    }
    
    public boolean updateMethod(String date){
        String idUrl;
        
        synchronized(res){
        idUrl = selectMethod(date);
        }
        
        if(idUrl.equals("empty")){
           
            return false; 
        }
        else{
            String[] tempIdUrl = idUrl.split(" ");
            String sql = "UPDATE sitetable SET  status = '"
                        +ServerStatus.getResponseCode(tempIdUrl[1])
                         +"' WHERE id = '"+tempIdUrl[0]+"';";
            
            try{
                Statement myStmt = connection.createStatement();
                myStmt.executeUpdate(sql);
                
                return true;
            }
            catch(Exception ex){
                ex.printStackTrace();
            
            return true;
            }
        }
    }
     
    public String dateNow(){ 
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd");
        
        return formatForDateNow.format(dateNow);
    }
     
     public static ResultSet getLine(Connection conn, String sql) throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Statement stm = conn.createStatement(java.sql.ResultSet.CONCUR_READ_ONLY, java.sql.ResultSet.TYPE_FORWARD_ONLY);
        
        return stm.executeQuery(sql);
    }
}