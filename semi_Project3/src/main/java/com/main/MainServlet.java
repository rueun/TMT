package com.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.freeGallery.FreeGalleryDTO;
import com.qnaboard.QnABoardDTO;
import com.util.MyServlet;

@WebServlet("/main.do")
public class MainServlet extends MyServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String uri=req.getRequestURI();
		
		if(uri.indexOf("main.do") != -1) {
			main(req, resp);
		}
	}
	
	private void main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 리스트
		MainDAO dao = new MainDAO();


		try {
			
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());

			// 현재 날짜 전달
			req.setAttribute("date", date);
			
			
			// 질문과 답변 게시물 가져오기
			List<QnABoardDTO> qnalist = null;
			qnalist = dao.QnAListBoard();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("qnalist", qnalist);
			
			// 자유갤러리 게시물 가져오기
			List<FreeGalleryDTO> freeGalleryList = null;
			freeGalleryList = dao.listFreeGallery();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("freeGalleryList", freeGalleryList);
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		// JSP로 포워딩
		forward(req, resp, "/WEB-INF/views/main/main.jsp");
	}

}
