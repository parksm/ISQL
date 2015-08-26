package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Scanner;

public class TestDBMain{
	public static void main(String[] args) {
		String sql="";
		/*Scanner sc = new Scanner(System.in);
		System.out.print("db driverPath : ");
		String driverPath = sc.next();
		System.out.print("db driverClass : ");
		String driverUrl = sc.next();
		System.out.print("dburl : ");
		String dburl = sc.next();
		System.out.print("user : ");
		String user = sc.next();
		System.out.print("password : ");
		String password = sc.next();*/
		String driverPath = "C:\\oracle\\ora92\\jdbc\\lib\\ojdbc14.jar";
		String driverUrl = "oracle.jdbc.driver.OracleDriver";
		String dburl = "jdbc:oracle:thin:@192.168.1.79:1521:ECMORCL";
		String user = "mca";
		String password = "mca";
		
		DBConnection dbc = new DBConnection(driverPath, driverUrl, dburl, user, password);
		try {
			dbc.connetc();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(is); 
		while(!sql.equals("exit")){
			System.out.print("SQL : ");
			try {
				sql = br.readLine();
			} catch (IOException e) {}
			while(!sql.endsWith(";")){
				sql += " ";
				try {
					System.out.print(">    ");
					sql += br.readLine();
				} catch (IOException e) {}
			}
			sql = sql.replace(";", "");
			dbc.Query(sql);
		}
		dbc.close();
	}
}
