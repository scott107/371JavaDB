import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class DBLayer {
	
    private Connection con;
    private Statement stmt;
    
    public DBLayer() throws SQLException{
    	try {
            String url = "jdbc:mysql://kc-sce-appdb01.kc.umkc.edu/slnz8b_1";
            String userID = "slnz8b_1";
            String password = "HdDrBwgrcieaqX8RMY9g";
       
            
                Class.forName("com.mysql.jdbc.Driver");

           
            con = DriverManager.getConnection(url,userID,password);
            stmt = con.createStatement();
        	} catch(java.lang.ClassNotFoundException e) {
            System.out.println(e);
            System.exit(0);
        	}
        }
    public boolean Login(String name, String type){
        PreparedStatement stmt = null;
        if (type.equals("User")){
            //get real query
            String query = "SELECT * FROM advertisements WHERE User_ID = ?";
            try {
                stmt=con.prepareStatement(query);
                stmt.setString(1,name); //binding the parameter with the given string
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                	return true;
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }        	
        }
        else if (type.equals("Moderator")){
            //get real query
            String query = "SELECT * FROM advertisements WHERE Moderator_ID = ?";

            try {
                stmt=con.prepareStatement(query);
                stmt.setString(1,name); //binding the parameter with the given string
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                	return true;
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return false;      
    }
    public String[] getCategories(){
    	ArrayList<String> result = new ArrayList<String>();
    	PreparedStatement stmt = null;
    	String query = "SELECT Category_ID FROM categories";
    	try {
            stmt=con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();


			while (rs.next()) {
            	String ID = rs.getString(1);
            	result.add(ID);
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.add("ALL");
		return result.toArray(new String[result.size()]);
    }
    public Object[][] updateUserAdvertData(String TitleDesc, String period, String Categ){
    	
    	Object[][]  resultArray = null;
    	PreparedStatement stmt;
    	String query = "select AdvTitle AS Title, AdvDetails AS Description, Price, AdvDateTime AS Date from advertisements where Status_ID = 'AC' and (advDateTime between  date_sub(curdate(), interval ? month) and curdate()) and (AdvTitle LIKE ? OR AdvDetails LIKE ?) and Category_ID = ?";
    	int periodInt = 0;
    	if (period.equals("Last 3 Months")){
    		periodInt = 3;
    	}
    	else if (period.equals("Last 6 Months")){
    		periodInt = 6;
    	}
    	else if (period.equals("Last 12 Months")){
    		periodInt = 12;
    	}
    	else{
    		System.out.println("Life");
    		
    	}
    	try{
    		stmt = con.prepareStatement(query);
    		String temp = "%" + TitleDesc + "%";
    		stmt.setInt(1,periodInt); //binding the parameter with the given string
    		stmt.setString(2, temp);
    		stmt.setString(3, temp);
    		stmt.setString(4, Categ);
    		ResultSet rs = stmt.executeQuery();
    		rs.last();
    		int rows = rs.getRow();
    		rs.beforeFirst();
    		resultArray = new Object [rows][4];
    		int count = 0;
    		while (rs.next() && count < rows){
    			resultArray[count][0] = rs.getObject(1);
    			resultArray[count][1] = rs.getObject(2);
    			resultArray[count][2] = rs.getObject(3);
    			resultArray[count][3] = rs.getObject(4);
    			count++;
    		}
    	}catch(SQLException e){
    		
    	}
    	return resultArray;
    }
    public boolean delete(int advertID){
    	
    	PreparedStatement stmt = null;
    	String query = "Delete From advertisements Where Advertisement_ID = ?";
    	try{
    		stmt = con.prepareStatement(query);
    		stmt.setInt(1, advertID);
    		int rs = stmt.executeUpdate();
    	}catch(SQLException e){
    		return false;
    	}
    	return true;
    }
    public Object[][] fetchUserAdvertData(){
    	Object[][]  resultArray = null;
    	PreparedStatement stmt = null;
    	String query = "select AdvTitle AS Title, AdvDetails AS Description, Price, AdvDateTime AS Date  from Advertisements where Status_ID = 'AC'";
    	try{
    		stmt = con.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();
    		rs.last();
    		int rows = rs.getRow();
    		rs.beforeFirst();
    		resultArray = new Object [rows][4];
    		int count = 0;
    		while (rs.next() && count < rows){
    			resultArray[count][0] = rs.getObject(1);
    			resultArray[count][1] = rs.getObject(2);
    			resultArray[count][2] = rs.getObject(3);
    			resultArray[count][3] = rs.getObject(4);
    			count++;
    		}
    	}catch(SQLException e){
    		
    	}
    	return resultArray;
    }
    
    public Object[][] fetchUserMyAdvertData(String username){
    	Object[][]  resultArray = null;
    	PreparedStatement stmt = null;
    	String query = "SELECT Advertisement_ID AS ID, AdvTitle AS Title, AdvDetails AS Description, Price, Status_ID AS Status, AdvDateTime AS Date FROM advertisements WHERE User_ID = ?";
    	try{
    		stmt = con.prepareStatement(query);
    		stmt.setString(1, username);
    		ResultSet rs = stmt.executeQuery();
    		rs.last();
    		int rows = rs.getRow();
    		rs.beforeFirst();
    		resultArray = new Object [rows][6];
    		int count = 0;
    		while (rs.next() && count < rows){
    			resultArray[count][0] = rs.getObject(1);
    			resultArray[count][1] = rs.getObject(2);
    			resultArray[count][2] = rs.getObject(3);
    			resultArray[count][3] = rs.getObject(4);
    			resultArray[count][4] = rs.getString(5);
    			resultArray[count][5] = rs.getString(6);
    			count++;
    		}
    	}catch(SQLException e){
    		
    	}
    	return resultArray;
    }
    public String[] pullEditInfo(int advertID){
    	String[] result = new String[6];
    	PreparedStatement stmt = null;
    	String query = "select AdvTitle AS Title, AdvDetails AS Description, category_ID AS Category, Price  From advertisements WHERE Advertisement_ID = ?";
    	try{
    		stmt = con.prepareStatement(query);
    		stmt.setInt(1, advertID);
    		ResultSet rs = stmt.executeQuery();
    		rs.next();
			result[0] = rs.getString(1);
			result[1] = rs.getString(2);
			result[2] = rs.getString(3);
			result[3] = rs.getString(4);
    	}catch(SQLException e){
    		
    	}
    	return result;
    }
    public boolean editAdvert(int advertID, String Title, String Desc, String Categ, String Price){
    	
    	PreparedStatement stmt = null;
    	String query = "UPDATE advertisements SET AdvTitle  = ?, AdvDetails = ?, Category_id = ?, price = ? WHERE Advertisement_id = ?";
    	try{
    		stmt = con.prepareStatement(query);
    		stmt.setString(1, Title);
    		stmt.setString(2, Desc);
    		stmt.setString(3, Categ);
    		stmt.setString(4, Price);
    		stmt.setInt(5, advertID);
    		int rs = stmt.executeUpdate();
    	}catch(SQLException e){
    		return false;
    	}
    	return true;
    }
    public boolean addAdvert(String Title, String Desc, String Categ, String Price, String username){
    	PreparedStatement stmt = null;
    	String query = "insert into advertisements (AdvDateTime,AdvTitle,Status_ID,AdvDetails,Category_ID,Price,User_ID) VALUES (CURRENT_DATE(),?,?,?,?,?,?)";
    	try{
    		stmt = con.prepareStatement(query);
    		stmt.setString(1, Title);
    		stmt.setString(2, "PN");
    		stmt.setString(3, Desc);
    		stmt.setString(4, Categ);
    		stmt.setString(5, Price);
    		stmt.setString(6, username);
    		int rs = stmt.executeUpdate();
    	}catch(SQLException e){
    		return false;
    	}
    	return true;
    }
    public Object[][] updateModUnclaimedData(String TitleDesc, String period, String Categ){
    	Object[][]  resultArray = null;
    	PreparedStatement stmt = null;
    	String query = "select Advertisement_ID, AdvTitle, AdvDetails, Price, AdvDateTime FROM advertisements where Moderator_ID  IS NULL and (advDateTime between  date_sub(curdate(), interval ? month) and curdate()) and (AdvTitle LIKE ? OR AdvDetails LIKE ?) and Category_ID = ?";
    	int periodInt = 0;
    	if (period.equals("Last 3 Months")){
    		periodInt = 3;
    	}
    	else if (period.equals("Last 6 Months")){
    		periodInt = 6;
    	}
    	else if (period.equals("Last 12 Months")){
    		periodInt = 12;
    	}
    	else{
    		System.out.println("Life");
    		
    	}
    	try{
    		String temp = "%" + TitleDesc + "%";
    		stmt = con.prepareStatement(query);
    		stmt.setInt(1, periodInt);
    		stmt.setString(2, temp);
    		stmt.setString(3, temp);
    		stmt.setString(4, Categ);
    		ResultSet rs = stmt.executeQuery();
    		rs.last();
    		int rows = rs.getRow();
    		rs.beforeFirst();
    		resultArray = new Object [rows][5];
    		int count = 0;
    		while (rs.next() && count < rows){
    			resultArray[count][0] = rs.getObject(1);
    			resultArray[count][1] = rs.getObject(2);
    			resultArray[count][2] = rs.getObject(3);
    			resultArray[count][3] = rs.getObject(4);
    			resultArray[count][4] = rs.getString(5);
    			count++;
    		}
    		
    	}catch(SQLException e){
    		System.out.println("error");
    	}
    	return resultArray;
    }
    public boolean claimAdvert(int advertID, String username){
    	
    	PreparedStatement stmt = null;
    	String query = "UPDATE advertisements SET Moderator_ID = ? WHERE Advertisement_ID = ?";
    	try{
    		stmt = con.prepareStatement(query);
    		stmt.setString(1, username);
    		stmt.setInt(2, advertID);
    		int rs = stmt.executeUpdate();
    	}catch(SQLException e){
    		return false;
    	}
    	
    	return true;
    }
    public boolean approveAdvert(int advertID){
    	
    	PreparedStatement stmt = null;
    	String query = "UPDATE advertisements SET Status_ID = 'AC' WHERE Advertisement_ID = ?";
    	try{
    		stmt = con.prepareStatement(query);
    		stmt.setInt(1, advertID);
    		int rs = stmt.executeUpdate();
    	}catch(SQLException e){
    		return false;
    	}
    	
    	return true;
    }
    public boolean denyAdvert(int advertID){
    	PreparedStatement stmt = null;
    	String query = "UPDATE advertisements SET Status_ID = 'PN' WHERE Advertisement_ID = ?";
    	try{
    		stmt = con.prepareStatement(query);
    		stmt.setInt(1, advertID);
    		int rs = stmt.executeUpdate();
    	}catch(SQLException e){
    		return false;
    	}
    	
    	return true;
    }
    public Object[][] fetchModMyAdvertData(String username){
    	Object[][]  resultArray = null;
    	PreparedStatement stmt = null;
    	String query = "SELECT advertisement_ID AS ID, AdvTitle AS Title, AdvDetails AS Description, Price, Status_ID AS Statu, AdvDateTime AS Dat, User_ID As Username FROM Advertisements WHERE Moderator_Id = ?";
    	try{
    		stmt = con.prepareStatement(query);
    		stmt.setString(1, username);
    		ResultSet rs = stmt.executeQuery();
    		rs.last();
    		int rows = rs.getRow();
    		rs.beforeFirst();
    		resultArray = new Object [rows][7];
    		int count = 0;
    		while (rs.next() && count < rows){
    			resultArray[count][0] = rs.getObject(1);
    			resultArray[count][1] = rs.getObject(2);
    			resultArray[count][2] = rs.getObject(3);
    			resultArray[count][3] = rs.getObject(4);
    			resultArray[count][4] = rs.getString(5);
    			resultArray[count][5] = rs.getString(6);
    			resultArray[count][6] = rs.getString(7);
    			count++;
    		}
    	}catch(SQLException e){
    		
    	}
    	return resultArray;
    	
    }
    public Object[][] fetchModUnclaimedData(){
    	Object[][]  resultArray = null;
    	PreparedStatement stmt = null;
    	String query = "SELECT advertisement_ID AS ID, AdvTitle AS Title, AdvDetails AS Description, Price, AdvDateTime AS Dat, User_ID As Username FROM Advertisements WHERE Moderator_Id IS NULL";
    	try{
    		stmt = con.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();
    		rs.last();
    		int rows = rs.getRow();
    		rs.beforeFirst();
    		resultArray = new Object [rows][6];
    		int count = 0;
    		while (rs.next() && count < rows){
    			resultArray[count][0] = rs.getObject(1);
    			resultArray[count][1] = rs.getObject(2);
    			resultArray[count][2] = rs.getObject(3);
    			resultArray[count][3] = rs.getObject(4);
    			resultArray[count][4] = rs.getString(5);
    			resultArray[count][5] = rs.getString(6);
    			count++;
    		}
    	}catch(SQLException e){
    		
    	}
    	return resultArray;
    }
}
