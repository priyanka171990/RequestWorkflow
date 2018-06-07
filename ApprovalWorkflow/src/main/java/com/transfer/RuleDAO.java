package com.transfer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 
 * Use to create connection with H2 database and return all data associated with request type in object of ProcessingDTO.
 *
 */
//Data Access Layer
public class RuleDAO {
      
	
 public ProcessingDTO getrequestApprovals(ProcessingDTO pr){
	   
	    Connection con = null;
		Statement st   = null;
		ResultSet rs   = null;
	  
		String requestType=pr.getRequestType();//"Trader Fund Request"; 
		int stage = 1;
		try {
			con = H2Connection.getConnection();
			if (con != null) {
				System.out.println("Connected to the database");
			}
		} 
		catch (SQLException ex) {
			System.out.println("An error occurred in connecting database.");
			ex.printStackTrace();
		}
		try{
			st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
			String ruleName =null;
			int reqId;
			try{
				rs = st.executeQuery("SELECT REQ_ID,RULE_NAME FROM TXN_REQUEST_TYPE WHERE REQUEST_NAME ="+ "'" +requestType +"'");
				rs.next();
				pr.setReqId(rs.getInt(1));
				pr.setRuleName(rs.getString(2));
				ruleName = pr.getRuleName();
				reqId=pr.getReqId() ;
				System.out.println("For "+ requestType + " executing " + ruleName );
				rs.close();
			}
			catch (SQLException ex) {
				//System.out.println("Please configure system for request name " + requestType );
				//ex.printStackTrace();
				return pr;
			}
			rs = st.executeQuery("SELECT count(distinct stage) FROM TXN_RULE WHERE REQ_ID =" + reqId +" AND RULE_NAME ="+ "'"+ruleName +"'") ;
			rs.next();
			pr.setStage(rs.getInt(1));
			rs.close();
	 
			int noOfStages= pr.getStage();
			if (noOfStages!=0){
				HashMap<Integer,ArrayList<String>> mp = new HashMap<Integer,ArrayList<String>>();
				    while (stage<=noOfStages) { 
						rs = st.executeQuery("SELECT APPROVER FROM TXN_RULE WHERE REQ_ID =" + reqId +" AND STAGE = "+ stage +" AND RULE_NAME ="+"'"+ruleName+"'") ;
						ArrayList<String> al =new ArrayList<String>();
	 
						//while (rs.next()){
						rs.next();
						do{	
						al.add(rs.getString(1));
						}while (rs.next())  ; 
	
						mp.put(stage, al);
						stage ++;
				    }  
					pr.setApproverList(mp);
				}
			 pr.setValid(true);
			}
			catch (SQLException ex) {
				pr.setValid(false);
				System.out.println("Exception on a database access or other database errors " );
				System.out.println("Approver is not defined for stage " +stage  + " " + ex.getMessage());
			} finally{
				if (con!=null){
					try {
						rs.close();
						st.close();
						con.close();
					} catch (SQLException ex) {
						pr.setValid(false);
						System.out.println("Exception on a database access or other database errors");
						ex.printStackTrace();
					}
				}
			}
		return pr;
		}
	}