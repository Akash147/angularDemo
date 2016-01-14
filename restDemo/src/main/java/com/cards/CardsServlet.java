package com.cards;

import com.akash.Utilities;
import com.akash.db.KeyValueDAO;
import com.cards.model.Card;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CardsServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", "*");
		
		List<String> pathComponents = Utilities.stripRequestUrl(req);
		
		if(pathComponents!=null && pathComponents.size()>0) {
			switch (pathComponents.get(0)) {
			case "new":
				String response = UrlToMetaData.handle(req.getParameter("data"));
				if(response!=null)
					resp.getWriter().print(response);
				break;

			default:
				break;
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", "*");
		
		List<String> pathComponents = Utilities.stripRequestUrl(req);
		if(pathComponents!=null && pathComponents.size()>0) {
			switch (pathComponents.get(0)) {
			case "meta":
				String response = UrlToMetaData.handle(req.getParameter("data"));
				if(response!=null)
					resp.getWriter().print(response);
				break;

            case "new":
                String jsonCard = req.getParameter("data");
				resp.getWriter().print(jsonCard);
                if(!Utilities.isEmptyString(jsonCard)){
                    Card card = new Gson().fromJson(jsonCard, Card.class);
                    CardsDAO.insertCard(card);
                }
                break;

            case "getAll":
                resp.getWriter().print(new Gson().toJson(CardsDAO.getPublishedCards()));
				break;

			default:
				resp.sendError(404);
				break;
			}
		}
	}
	
	protected void doOptions(HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException{
		super.doOptions(req,resp);
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		resp.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
		resp.addHeader("Access-Control-Max-Age", "1728000");
	}

    private void dumpAllParams(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Enumeration params = req.getParameterNames();
        while(params.hasMoreElements()){
            String paramName = (String)params.nextElement();
            resp.getWriter().println("Attribute Name - "+paramName+", Value - "+req.getParameter(paramName));
        }
    }
}
