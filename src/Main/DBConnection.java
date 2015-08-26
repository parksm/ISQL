package Main;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;


public class DBConnection{
	String driverPath = "";
	String driverUrl = "";
	String dburl = "";
	String user = "";
	String password = "";
	Connection con;
	URLClassLoader classLoader;
	
	int rowSize = -1;
	int colSize = -1;
	
	//String sql = "select * from EN_TRNO";
	String sql = "";
	Statement stmt = null;
	ResultSet rs = null;
	ResultSetMetaData rsmd = null;
	DatabaseMetaData dbmd = null;
	String cl;
	
	//String oracle = oracle.jdbc.driver.OracleDriver;
	//String mssql = com.miscrosoft.jdbc.sqlserver.SQLServerDriver;
	//String mysql = com.mysql.jdbc.Driver;
	
	public DBConnection(String driverPath, String driverUrl, String dburl, String user, String password) {
		this.driverPath = driverPath;
		this.driverUrl = driverUrl;
		this.dburl = dburl;
		this.user = user;
		this.password = password;
	}
	
	public void connetc() throws SQLException{
		try {
			File jarFile = new File(driverPath);
			URL classURL = new URL("jar:" + jarFile.toURI().toURL() + "!/");
			classLoader = new URLClassLoader(new URL[] {classURL});
			//Class.forName(driverUrl);
			//Class.forName(driverUrl, true, classLoader);
			Driver d = (Driver)Class.forName(driverUrl, true, classLoader).newInstance();
			java.util.Properties info = new java.util.Properties();
			info.put("user", user);
			info.put("password", password);
			con = d.connect(dburl, info);

			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//DriverManager.registerDriver(d);
			//con = DriverManager.getConnection(dburl, user, password);
			System.out.println("DBConnection!!!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*finally {
			try {
				con.close();
				System.out.println("DBclose!!!");
			} catch (SQLException e) {e.printStackTrace();}
		}*/
	}
	public void Query(String sql){
		this.sql = sql;
		if(sql.contains("select")){
			if(sql.contains("tab")){
				selectTab();
			}else{
				select();
			}
		}
		else if(sql.contains("delete")){
			delete();
		}
		else if(sql.contains("DESC")||sql.contains("desc")){
			desc();
		}
	}
	public void output(String[][] cname){
		int[] u = new int[colSize];
		for(int i=0;i<colSize;i++){
			for(int j=0;j<rowSize+1;j++){
				int c = (cname[j][i].getBytes()).length;
				if(u[i] < c){
					u[i] = c;
				}
			}
		}
		for(int i=0;i<colSize;i++){
			for(int j=0;j<rowSize+1;j++){
				for(int a=(cname[j][i].getBytes()).length;a<u[i];a++){
					cname[j][i] += " ";
				}
			}
		}
		
		for(int i=0;i<rowSize+1;i++){
			if(i==1){
				for(int l=0;l<colSize;l++){
					cl="";
					for(int r=0;r<u[l];r++){
						cl+="-";
					}
					System.out.print(cl+" ");
				}
				System.out.println("");
			}
			for(int j=0;j<colSize;j++){
				System.out.print(cname[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("총"+rowSize+"개");
	}

	/*
	 * select 문	
	 */
	public void select(){
		try {
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			colSize = rsmd.getColumnCount();
			rowSize = getrowsize(rs);
			String[][] cname = new String[rowSize+1][colSize];
			for(int i=0;i<colSize;i++){
				cname[0][i] = rsmd.getColumnName(i+1);
				//System.out.print(cname[0][i]+" ");
			}
			//System.out.println("");
			rowSize = getrowsize(rs);
			int j=1;
			while(rs.next()){
				for(int i=0;i<colSize;i++){
					String s = rs.getString(cname[0][i]);
					if(s==null){
						cname[j][i] = "null";
					}else{
						cname[j][i] = s;
					}
					
				}
				//System.out.println("");
				j++;
			}
			output(cname);
		} catch (Throwable e) {System.out.println("select sql err!!");}	
	}
	/*
	 * select * form tab
	 */
	private void selectTab() {
		try {
			dbmd =con.getMetaData();
			rs = dbmd.getTables("", dbmd.getUserName(), null, new String[]{"TABLE"});
			int count=0;
			while(rs.next()){
				count++;
			}
			rs = dbmd.getTables("", dbmd.getUserName(), null, new String[]{"TABLE"});
			
			String[][] cname = new String[count+1][3];
			int i=0;
			while(rs.next()){
				i++;
				cname[i][0] = rs.getString("TABLE_NAME");
				cname[i][1] = rs.getString("TABLE_TYPE");
				cname[i][2] = " ";
				/*System.out.print(rs.getString("TABLE_NAME")+"   ");
				System.out.print(rs.getString("TABLE_TYPE")+"   ");
				System.out.print(rs.getString("TABLE_CAT")+"   ");
				System.out.print(rs.getString("TABLE_SCHEM")+"   ");
				System.out.println("");*/
				
			}
			cname[0][0] = "TNAME";
			cname[0][1] = "TABTYPE";
			cname[0][2] = "CLUSTERID";
			setrowsize(count);
			colSize = 3;
			output(cname);
		} catch (SQLException e) {e.printStackTrace();}
	}
	/*
	 * delete 문	
	 */
	public void delete(){
		try {
			System.out.println("3"+sql);
		} catch (Throwable e) {System.out.println("delete sql err!!");}
	}
	/*
	 * DESC 문	
	 */
	public void desc(){
		sql = sql.substring(sql.indexOf(" ")+1, sql.length());
		try {
			rs = stmt.executeQuery("select * from "+sql);
			rsmd = rs.getMetaData();
			
			colSize = rsmd.getColumnCount();
			/*String type[] = new String[colSize];
			String cname[] = new String[colSize];
			String nu[] = new String[colSize];
			int dsiz[] = new int[colSize];*/
			
			String cname[][] = new String[colSize+1][3]; 
			cname[0][0] = "이름";
			cname[0][1] = "널";
			cname[0][2] = "유형";
			
			for(int i=1;i<=colSize;i++){
				cname[i][0] = rsmd.getColumnName(i);
				if(rsmd.isNullable(i)==0){
					cname[i][1]="NOT NULL";
				}else{cname[i][1]="NULL";}
				cname[i][2] = rsmd.getColumnTypeName(i)+"("+rsmd.getColumnDisplaySize(i)+")";
			}
				/*type[i] = rsmd.getColumnTypeName(i+1);
				cname[i] = rsmd.getColumnName(i+1);
				if(rsmd.isNullable(i+1)==0){
					nu[i]="NOT NULL";
				}else{nu[i]="NULL";}
				dsiz[i] = rsmd.getColumnDisplaySize(i+1);
				System.out.println(cname[i]+" "+nu[i]+" "+type[i]+"("+dsiz[i]+")");*/
				setrowsize(colSize);
				colSize = 3;
				output(cname);
			
		} catch (Throwable e) {System.out.println("DESC sql err!!");}
		
	}
	public void setrowsize(int rowSize){
		this.rowSize = rowSize;
	}
	public int getrowsize(ResultSet rs){
		int size = -1;
		try {
			rs.last();
			size=rs.getRow();
			rs.beforeFirst();
		} catch (Throwable e) {System.out.println("size err!!");e.printStackTrace();}
		return size;
	}
	
	public void close(){
		try {rs.close();System.out.println("rs close!");} catch (Throwable e) {System.out.println("rs!!!");}
		try {stmt.close();System.out.println("stmt close!");} catch (Throwable e) {System.out.println("stmt!!!");}
		try {con.close();System.out.println("con close!");} catch (Throwable e) {System.out.println("con!!!");}
		try {classLoader.close();System.out.println("classLoader close!");} catch (Throwable e) {System.out.println("classLoader!!!");}
	}
}
