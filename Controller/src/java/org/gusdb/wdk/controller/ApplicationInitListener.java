package org.gusdb.gus.wdk.controller;

import org.gusdb.gus.wdk.view.GlobalRepository;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

  
public class ApplicationInitListener implements ServletContextListener {
  
    public void contextInitialized(ServletContextEvent sce) {
  
        ServletContext application  = sce.getServletContext(  );

        
        String loginXML = application.getInitParameter("loginConfig");
        String querySetLocation = application.getInitParameter("querySetConfig");
        String recordSetLocation = application.getInitParameter("recordSetConfig");
        
        GlobalRepository.createInstance(loginXML, querySetLocation, recordSetLocation);
        
        
//        EmployeeRegistryBean empReg = new EmployeeRegistryBean(  );
//        empReg.setDataSource(ds);
//        application.setAttribute("empReg", empReg);
//  
//        NewsBean news = new NewsBean(  );
//        application.setAttribute("news", news);
    }
  
    public void contextDestroyed(ServletContextEvent sce) {
//        ServletContext application  = sce.getServletContext(  );
//        application.removeAttribute("empReg");
//        application.removeAttribute("news");
    }
}
