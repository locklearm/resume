package edu.ncsu.csc.itrust.server;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.ncsu.csc.itrust.beans.WardBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.WardDAO;

public class WardCRUDServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * WardDao for doing DAO Operations
	 */
	protected WardDAO wardDAO = new WardDAO(DAOFactory.getProductionInstance());
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		try{
			WardBean ward = new WardBean(0, request.getParameter("requiredSpecialty"), Long.parseLong(request.getParameter("inHospital")));
			wardDAO.addWard(ward);
		} catch(Exception e){
			//Send error parameter back to page
			response.sendRedirect("");
			return;
		}
		
		//Redirect back to page
		response.sendRedirect("");
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try{
			WardBean ward = new WardBean(Long.parseLong(request.getParameter("wardID")), request.getParameter("requiredSpecialty"), Long.parseLong(request.getParameter("inHospital")));
			wardDAO.updateWard(ward);
		} catch(Exception e){
			//Send error parameter back to page
			response.sendRedirect("");
			return;
		}
		
		//Redirect back to page
		response.sendRedirect("");
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try{
			wardDAO.removeWard(Long.parseLong(request.getParameter("wardID")));
		} catch(Exception e){
			//Send error parameter back to page
			response.sendRedirect("");
			return;
		}
		
		//Redirect back to page
		response.sendRedirect("");
	}
	
}
