package com.freeGallery;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyUploadServlet;

@WebServlet("/freeGallery/*")
public class FreeGalleryServlet extends MyUploadServlet {
	private static final long serialVersionUID = 1L;
	
	private String pathname;
	
	
	
	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}
		
		// 이미지를 저장할 경로(pathname)
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "freeGallery";
		
		
		// uri에 따른 작업 분류
		if (uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if (uri.indexOf("write.do") != -1) {
			writeForm(req, resp);
		} else if (uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req, resp);
		} else if (uri.indexOf("article.do") != -1) {
			article(req, resp);
		} else if (uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		} else if (uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		} else if (uri.indexOf("deleteFile") != -1) {
			deleteFile(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		}
		
		
		
	}
	
	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 게시물 리스트
		// FreeGalleryDAO dao = new FreeGalleryDAO();
		// MyUtil util = new MyUtil();
		
		forward(req, resp, "/WEB-INF/views/freeGallery/list.jsp");
	}
	
	private void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 이미지 올리기 폼(글 쓰기 폼)

		forward(req, resp, "/WEB-INF/views/freeGallery/write.jsp");
	}
	
	private void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 글 쓰기 완료
	}
	
	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 글 보기
		forward(req, resp, "/WEB-INF/views/freeGallery/article.jsp");
	}
	
	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 글 수정 폼
		forward(req, resp, "/WEB-INF/views/freeGallery/write.jsp");
	}
	
	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 게시글 수정 완료
	}
	
	private void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 파일 삭제
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 게시글 삭제
	}
	
	

}
