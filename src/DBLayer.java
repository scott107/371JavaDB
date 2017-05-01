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
        Statement stmt = null;
    	String query = "SELECT Category_ID FROM categories";
    	try {
            stmt=con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery(query);


			while (rs.next()) {
            	String ID = rs.getString(1);
            	System.out.println(ID);
            	result.add(ID);
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.add("all");
		return result.toArray(new String[result.size()]);
    }
}
