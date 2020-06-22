/**
 * 
 */
package com.tcs.hemanth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;


import com.documentum.*;
import com.documentum.fc.*;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfLogger;


/**
 * @author hmuppalanarayana
 *
 */
public class GenerateCreditReport {

	/**
	 * @param args
	 */
	private static IFCDocsUtilities utility = new IFCDocsUtilities();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String username="idocsmigrusrprd";
		String password="ifc@prdmIgru3r";
		String docBase="ifcecmidocs";
		IDfSessionManager sessMngr=null;
		IDfSession sess=null;

		//String WorkflowName="WorkflowName";
		//String ProjectId="20020";

		//BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		//String str = bf.readLine();
		/*String filename = "C:\\Temp\\CreditReportTest.xls";
		File file = new File(filename);
		BufferedWriter bufferedWriter = null;
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//
		}*/
		BufferedWriter bufferedWriter = null;
		try {
			sessMngr=SessionMngr.getManager(username,password,docBase);
			sess=sessMngr.getSession(docBase);

		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Please enter the start Date in mm/dd/yyyy ");
			String strtDate = bf.readLine();
			System.out.print("Please enter the end Date in mm/dd/yyyy ");
			String endDate = bf.readLine();

			System.out.println("Start Date::"+strtDate);

			SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy"); 
			SimpleDateFormat fromUser = new SimpleDateFormat("M/dd/yyyy"); 
			System.out.println("Start Date after Parsing  ::: "+fromUser.parse(strtDate));
			String filename = "C:\\Temp\\Credit".concat("_Report_").concat(myFormat.format(fromUser.parse(strtDate))).concat("_").concat(myFormat.format(fromUser.parse(endDate))).concat(".xls");
			//String filename="C:\\Temp\\ASOP_project_List.xls";
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}

			String creditQry="select info.wf_id as wf_id,info.workflow_name as workflow_nme,info.project_id as project_id,project_shrt_nme " +
							"as project_name, info.project_country as country,reg.region_nme as region_name,info.performer as credit_officer_cleared, " +
							"decision as crv_decision, received_date as cleared_to_crv_on,dequeued_date as crv_completed_on from dm_dbo.IDOCS_WF_ACTIVITY_INFO " +
							"info,dm_dbo.IDOCS_REGION reg,dm_dbo.IDOCS_COUNTRY country where country.region_code=reg.region_code and country.country_nme=" +
							"info.project_country and  task_name in ('Credit Officer Approve','Credit Officer Clear','Credit Officer Concur','Credit Officer " +
						"Concur-T1','Credit Officer Concur-1','Credit Officer Concur-T2')  and dequeued_date>date('<start_date> 00.00.00','mm/dd/yyyy hh:mi:ss') "+
							"and dequeued_date <=date('<end_date> 24.00.00','mm/dd/yyyy hh:mi:ss') and project_id!='20020' order by dequeued_date desc";
			//String creditQry="select DISTINCT project_id from dm_dbo.IDOCS_PROJECT_TEAM where project_team_member_id = (select user_os_name from dm_user where user_name='Panagiotis Tzanopoulos') and end_date is NULL"	;
			
			creditQry=creditQry.replaceFirst("<start_date>", strtDate);
			creditQry=creditQry.replaceFirst("<end_date>", endDate);
			
			//String creditQry="select  institution_nbr,instit_short_nme,country_nme,country_code,instit_legal_nme,instit_role_type_nme,instit_role_type_code,security_class_code,security_class_nme,region_nme,datetime_stamp from dm_dbo.IDOCS_INSTITUTION_PROFILE";
			//System.out.println("creditQry ::: "+creditQry);
			
			//String creditQry="select count(*) as project_count from idocs_project_folder where project_short_nme LIKE '%<special_char>%'";
			//String tst="~,`,!,@,#,$,&,\\%,^,&,*,:,_,+,=,-,|,\\,},{,],,[,\\,'',:,;,/,?,.,>,<,;,\"";
			//String[] specialCharLst=tst.split(",");
		
			//for (int i = 0; i < specialCharLst.length; i++) {
			//	creditQry="select count(*) as project_count from idocs_project_folder where object_name LIKE '%<special_char>%'";
				//creditQry=creditQry.replace("<special_char>", specialCharLst[i]);
				//System.out.println("Special Char :: "+specialCharLst[i]);
			//System.out.println("creditQry :: "+creditQry);
			IDfCollection creditColl=utility.executeQuery(sess, creditQry, IDfQuery.DF_READ_QUERY);

			bufferedWriter = new BufferedWriter(new FileWriter(filename,true));
			//System.out.println("Total Number of Records :::: "+creditColl.getAttrCount());
			while(creditColl.next()){
				bufferedWriter.write(creditColl.getString("wf_id") + "\t" + creditColl.getString("workflow_nme") + "\t" + creditColl.getString("project_id") + "\t" + creditColl.getString("project_name") + "\t" + creditColl.getString("country") + "\t" + creditColl.getString("region_name") + "\t" + creditColl.getString("credit_officer_cleared") +"\t" + creditColl.getString("crv_decision")  + "\t" + creditColl.getString("cleared_to_crv_on") +"\t" + creditColl.getString("crv_completed_on")  + "\n");
				//bufferedWriter.write(creditColl.getString("institution_nbr") + "\t" + creditColl.getString("instit_legal_nme") + "\t" + creditColl.getString("instit_short_nme")  + "\n");
				//bufferedWriter.write(creditColl.getString("institution_nbr") + "\t" + creditColl.getString("instit_short_nme") + "\t" + creditColl.getString("country_nme") + "\t" + creditColl.getString("country_code") + "\t" + creditColl.getString("instit_legal_nme") + "\t" + creditColl.getString("instit_role_type_nme") + "\t" + creditColl.getString("instit_role_type_nme") + "\t" + creditColl.getString("instit_role_type_code")  + "\t" + creditColl.getString("security_class_code") +"\t" + creditColl.getString("security_class_nme") +"\t" + creditColl.getString("region_nme") +"\t" + creditColl.getString("datetime_stamp") +"\n");
				//System.out.println(creditColl.getString("project_count") + "\t" + specialCharLst[i]);
				//bufferedWriter.append(creditColl.getString("project_count") + "\t" + specialCharLst[i] + "\n");
				//bufferedWriter.write(creditColl.getString("project_id")+"\n");
			}if(creditColl!=null){
				creditColl.close();	
			}
			//}
			System.out.println("Please find the Report at :::::::"+file.getPath());
			System.out.println("creditQry :::::::"+creditQry);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();


					sessMngr.release(sess);
					System.out.println("Is the Session Alive :: "+sess.isConnected());
				}
			} catch (IOException ex) {

				ex.printStackTrace();

			}
			catch (Exception ex) {

				ex.printStackTrace();

			}

		}





	}
		
	}


