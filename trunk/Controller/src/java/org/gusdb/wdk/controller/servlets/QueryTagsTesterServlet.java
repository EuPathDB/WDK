package org.gusdb.gus.wdk.controller.servlets;

import org.gusdb.gus.wdk.model.Param;
import org.gusdb.gus.wdk.model.QueryParamsException;
import org.gusdb.gus.wdk.model.SimpleQueryI;
import org.gusdb.gus.wdk.model.SimpleQueryInstanceI;
import org.gusdb.gus.wdk.model.SimpleQuerySet;
import org.gusdb.gus.wdk.model.implementation.PageableSqlQuery;
import org.gusdb.gus.wdk.model.implementation.SimpleSqlQueryInstance;

import org.gusdb.gus.wdk.view.GlobalRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * QueryTagsTesterServlet
 *
 * This servlet interacts with the query custom tags to present and validate a form
 * for the user
 *
 * Created: May 9, 2004
 *
 * @author Adrian Tivey
 * @version $Revision$ $Date$ $Author$
 */
public class QueryTagsTesterServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String fromPage = req.getParameter("fromPage");
		String querySet = req.getParameter("querySet");
		String queryName = req.getParameter("queryName");
		String formName = req.getParameter("formName");
		String defaultChoice = req.getParameter("defaultChoice");
        String initialExpansion = req.getParameter("initialExpansion");
        
		if (fromPage == null) {
			msg("fromPage shouldn't be null. Internal error", res);
			return;
		}
		if (querySet == null) {
			msg("querySet shouldn't be null. Internal error", res);
			return;
		}
		if (queryName == null) {
			msg("queryName shouldn't be null. Internal error", res);
			return;
		}
		if (formName == null) {
			msg("formName shouldn't be null. Internal error", res);
			return;
		}
		if (defaultChoice == null) {
			msg("defaultChoice shouldn't be null. Internal error", res);
			return;
		}
		
		if (queryName.equals(defaultChoice)) {
			req.setAttribute(formName+".error.query.noQuery", "Please choose a query");
			redirect(req, res, fromPage);
			return;
		}
		
		// We have a query name
		SimpleQuerySet sqs = GlobalRepository.getInstance().getSimpleQuerySet(querySet);
		SimpleQueryI sq = sqs.getQuery(queryName);
        if (sq == null) {
            msg("sq is null for "+querySet+"."+queryName, res);
            return;
        }
		SimpleQueryInstanceI sqii = sq.makeInstance();
		Map paramValues = new HashMap();
		
		req.setAttribute(formName+".sqii", sqii);
        
        boolean problem = false;
        if ("true".equals(initialExpansion)) {
            problem = true;
        } else {
            // Now check state of params
            Param[] params = sq.getParams();

            for (int i = 0; i < params.length; i++) {
                Param param = params[i];
                String paramName = param.getName();
                String passedIn = req.getParameter(formName+"."+queryName+"."+paramName);
                String error = param.validateValue(passedIn);
                if ( error == null) {
                    paramValues.put(paramName, passedIn);
                } else {
                    problem = true;
                    req.setAttribute(formName+".error."+queryName+"."+paramName, error);	
                }   
            }
        }
		
		if (problem) {
			// If fail, redirect to page
			redirect(req, res, fromPage);
			return;
		}
        
		// If OK, show success msg
		//msg("OK, we've got a valid query. Hooray", res);
		
		StringBuffer sb = new StringBuffer();
		try {
		    
		    SimpleQueryI pageQuery = sqs.getQuery("RNAListInDetail");
		    
		    sqii.setIsCacheable(true);
		    sqii.setValues(paramValues);
		    
		    SimpleSqlQueryInstance ssqi = (SimpleSqlQueryInstance) sqii;
		    
		    String initialResultTable = ssqi.getResultAsTable();
		    Map values = new HashMap(3);
		    values.put(PageableSqlQuery.RESULT_TABLE_SYMBOL, initialResultTable);
		    // values.put(PageableSqlQuery.START_ROW_SYMBOL, 
		    //                       Integer.toString(startRow));
		    // values.put(PageableSqlQuery.END_ROW_SYMBOL, 
		    //                       Integer.toString(endRow));
		    values.put(PageableSqlQuery.START_ROW_SYMBOL, "1");
		    values.put(PageableSqlQuery.END_ROW_SYMBOL, "200");
		    SimpleSqlQueryInstance pageInstance = 
		        (SimpleSqlQueryInstance)pageQuery.makeInstance();
		    // pageInstance.setIsCacheable(getIsCacheable());
		    pageInstance.setValues(values);
		    ResultSet rs =  pageInstance.getResult();
            
		    //				ResultSet rs = sqii.getResult();
		    
		    if (rs == null) {
		        sb.append("No result set returned");   
		    } else {
		        // Get result set meta data
		        sb.append("<table width=\"100%\"><tr>");
		        ResultSetMetaData rsmd = rs.getMetaData();
		        int numColumns = rsmd.getColumnCount();
		        // Get the column names; column indices start from 1
		        sb.append("<th align=\"center\"><b>&nbsp;</b></th>");
		        for (int i=3; i<numColumns; i++) {
		            sb.append("<th align=\"center\"><b>"+rsmd.getColumnName(i)+"</b></th>");
		        }
		        sb.append("</tr>");
		        while (rs.next()) {
		            sb.append("<tr>");
		            sb.append("<td align=\"center\"><a href=\"");
		            sb.append(req.getContextPath());
		            sb.append("/RecordTester");
		            sb.append("?style=jsp&recordSetName=RNARecords&recordName=PSUCDSRecordId&primaryKey=");
		            sb.append(rs.getObject(1)+"&objectType="+rs.getObject(2)+"\" >");
		            sb.append("More details</a></td>");
		            sb.append("<td align=\"center\">"+rs.getObject(3)+"</td>");
		            sb.append("<td align=\"center\">"+rs.getObject(4)+"</td>");
		            sb.append("<td align=\"center\"><i>"+rs.getObject(5)+"</i></td>");
		            sb.append("</tr>");
		        }
		        sb.append("<table>");
		    }
		} catch (SQLException e) {
		    sb = new StringBuffer(e.toString());
		} catch (QueryParamsException e) {
		    sb = new StringBuffer(e.toString());
		} catch (Exception e) {
		    sb = new StringBuffer(e.toString());
		}
		msg(sb.toString(), res);
		return;
	}

    private void msg(String msg, HttpServletResponse res) throws IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<html><body bkground=\"white\">"+msg+"</body></html>" );
    }
    
    private void redirect(HttpServletRequest req, HttpServletResponse res, String page) throws ServletException, IOException {
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher(page);
        rd.forward(req, res);
    }
    
}