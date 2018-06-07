package com.transfer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Class creates connection with H2 database which is light weight and can be embedded in Java Program.   
 * <p>Works on type of request we want to process like 'Trade Fund Transfer'. All other requests are configurable.
 */
public class ProcessingEngine {

	/**
	 * <p> In requestType variable,  we can define type of request to be processed.
	 * If process with request name is configured in our database then we fetch which rule to be triggered for this request. 
	 * Otherwise, we can configure our system and process for new request name.
	 * <p> According to rule, fetch different stages for that rule from table TXN_RULE and process of approval begins.
	 * <p> Table TXN_REQUEST_TYPE, defined with REQ_ID, REQUEST_NAME and RULE_NAME i.e for REQUEST_NAME('Trader Fund Request'), we want RULE_NAME('Rule 1')
	 * to be fired.
	 * <p> In Table TXN_RULE, we can define in which stage which entity will approve in that Rule. At any stage,multiple approvals can approve.   
	 * <p> Hence , design is configurable.
	 * <p> For new process (request type), we have to create its entity and add it in Tables.
	 * <ul compact>
	 * <li> Assumption : Input Request is of String type.
	 * <li>              : Single user can add in database tables like either admin or operational team.   </ul>
	 * 
	 * To configure, add RequestType and Rules with stages in database and define approver cache for same.
	 * Below We have an example for "Bank Cheque Request" 
	 * 
	 */
	public static void main(String[] args) {
            
		    String requestType= "Trader Fund Request";
		    ProcessingDTO pdto =new ProcessingDTO();
		    pdto.setRequestType(requestType);
		   
		    RuleDAO    rdao =new RuleDAO();
		    rdao.getrequestApprovals(pdto);
		    if (pdto.getRuleName() == null) 
		     {System.out.println("Please configure system for request  " + requestType );
			  return;
			  }
		    
		    if (!pdto.isValid()) {System.out.println("Please configure system for request  " + requestType); return;};
			
		    //Request is disapproved if it contains Approver with Disapproved like "Fund Manager Disapproved" 
		    
		    Map<String, Function<String, Boolean>> approverCache =new HashMap<>();
			approverCache.put("Fund Manager",(String req)->{ if (req.contains("Fund Manager Disapproved")) { System.out.println(" FundManager Disapproved request - " + req  );return false; }
			                                                 else {System.out.println(" FundManager Approved request - " + req  ); return true;}});
			approverCache.put("Research Analyst",(String req)->{ if (req.contains("Research Analyst Disapproved")) { System.out.println(" FundManager Disapproved request - " + req  );return false; }
                                                                 else {System.out.println(" Research Analyst Approved request - " + req  ); return true;}});
			approverCache.put("Division Head",(String req)->{ if (req.contains(" Division Head Disapproved")) { System.out.println(" FundManager Disapproved request - " + req  );return false; }
	                                                          else {System.out.println(" Division Head Approved request - " + req  ); return true;}});
			approverCache.put("Operations",(String req)->{ if (req.contains(" Operations Disapproved")) { System.out.println(" FundManager Disapproved request - " + req  );return false; }
                                                           else {System.out.println(" Operations Approved request - " + req  ); return true;}});
		

			// Add below in cache to test for Request type "Bank Cheque Request" 
			/*approverCache.put("Ordering Bank",(String req)->{ if (req.contains(" Ordering Bank Disapproved")) { System.out.println(" Ordering Bank Disapproved request - " + req  );return false; }
                                                              else {System.out.println(" Division Head Approved request - " + req  ); return true;}});
            approverCache.put("Account Owner Bank",(String req)->{ if (req.contains(" Account Owner Bank Disapproved")) { System.out.println(" Account Owner Bank Disapproved request - " + req  );return false; }
                                                                    else {System.out.println(" Account Owner Bank Approved request - " + req  ); return true;}});
			
			*/
			
			RequestProcessing rp =new RequestProcessing();
		    rp.setReq("Trader wants to transfer Rs.1000 ");
		    rp.setApproverCache(approverCache);
	        
		    HashMap<Integer,ArrayList<String>> approvers= pdto.getApproverList();
		    
		    int noOfStages= pdto.getStage();
			if (noOfStages==0){
				System.out.println(requestType + " has no rules defined for " + pdto.getRuleName());	
			} 
			else{
				System.out.println("-----------Request Ready for Approvals-------------");
				int stage = 1;
				System.out.println(requestType + " required " + noOfStages + " stages for approval");
				while (stage <= noOfStages)	{
					ArrayList<String> approversList =	approvers.get(stage);
				    List<Future<Boolean>> result =   rp.process(approversList);
					int flag1 = 1;
					
					for(Future<Boolean> obj: result)  
					{  
						try {
							if( obj.get().compareTo(false)==0) {
								flag1=0;
							}
						} catch (InterruptedException ex) {
							System.out.println("Thread  " + Thread.currentThread().getName()
									+" was interrupted while waiting for parallel thread processing at stage " + stage);
							ex.printStackTrace();
						}
						catch (ExecutionException ex) {
							System.out.println("Exception while doing computation to complete processing of request at stage " + stage);
							System.out.println("Please define approvers for the request type " +requestType);
//							ex.printStackTrace();
							return;
						}
					}
					if (flag1==1)
					{ 
						stage++;
					}
					else break;
				}
				if (stage>noOfStages){
					System.out.println("-----------Ready for Disbursment-------------");
				}
				else{
					System.out.println("----------- Request Disapproved-----------");
				}
				}
	}
}