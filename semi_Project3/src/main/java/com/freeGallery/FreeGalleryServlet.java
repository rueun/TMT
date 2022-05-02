package com.freeGallery;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.FileManager;
import com.util.MyUploadServlet;
import com.util.MyUtil;

@MultipartConfig
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
		FreeGalleryDAO dao = new FreeGalleryDAO();
		MyUtil util = new MyUtil();

		String cp = req.getContextPath();
		
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}
			
			// 검색
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String category = req.getParameter("category");
			System.out.println(category);
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			
			if (category == null) {
				category = "all";
			}

			// GET 방식인 경우 디코딩
			if (req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}
			
			
			// 전체데이터 개수
			int dataCount;
			if (keyword.length() == 0 && category.equals("all")) { // 조건 x, 카테고리 x
				dataCount = dao.dataCount();
			} else if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				dataCount = dao.dataCount(category);
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				dataCount = dao.dataCount(condition, keyword);
			} else { // 조건 o, 카테고리 o
				dataCount = dao.dataCount(condition, keyword, category);
			}

			// 전체페이지수
			int rows = 12;
			int total_page = util.pageCount(rows, dataCount);
			if (current_page > total_page) {
				current_page = total_page;
			}
			
			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;
			
			// 게시물 가져오기
			List<FreeGalleryDTO> list = null;
			if (keyword.length() == 0 && category.equals("all")) { // 조건 x, 카테고리 설정 x
				list = dao.listFreeGallery(start, end);
			} else if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				list = dao.listFreeGallery(start, end, category);
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				list = dao.listFreeGallery(start, end, condition, keyword);
			} else { // 조건 o, 카테고리 o
				list = dao.listFreeGallery(start, end, condition, keyword, category);
			}
			
			String query = "";
			
			if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				query += "category=" + category;
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				query += "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			} else if(keyword.length() != 0 && !category.equals("all")) { // 조건 o, 카테고리 o
				query += "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8") + "&category=" + category;
			}
			
			
			
			// 페이징 처리
			String listUrl = cp + "/freeGallery/list.do";
			String articleUrl = cp + "/freeGallery/article.do?page=" + current_page;
			if (query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = util.paging(current_page, total_page, listUrl);
			
			// 포워딩할 list.jsp에 넘길 값
			req.setAttribute("list", list);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			req.setAttribute("category", category);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		forward(req, resp, "/WEB-INF/views/freeGallery/list.jsp");
	}
	
	private void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 이미지 올리기 폼(글 쓰기 폼)
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/freeGallery/write.jsp");
	}
	
	private void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 글 저장(완료)
		FreeGalleryDAO dao = new FreeGalleryDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/freeGallery/list.do");
			return;
		}
		
		try {
			FreeGalleryDTO dto = new FreeGalleryDTO();
			
			dto.setUserId(info.getUserId());
			dto.setCategory(req.getParameter("category"));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			// 나중에 이미지 파일도 insert하기
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if (map != null) {
				String[] saveFiles = map.get("saveFilenames");
				dto.setImageFiles(saveFiles);
			}
			
			
			dao.insertFreeGallery(dto);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp + "/freeGallery/list.do");
	}
	
	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 글 보기
		FreeGalleryDAO dao = new FreeGalleryDAO();
		MyUtil util = new MyUtil();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String category = req.getParameter("category");
			
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
			
			if (category == null) {
				category = "all";
			}
			
			if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				query += "&category=" + category;
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
			} else if(keyword.length() != 0 && !category.equals("all")) { // 조건 o, 카테고리 o
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8") + "&category=" + category;
			}
			
			// 조회수 증가
			dao.updateHitCount(num);
			
			// 게시물 가져오기
			FreeGalleryDTO dto = dao.readFreeGal(num);
			if (dto == null) { // 게시물이 없으면 다시 리스트로
				resp.sendRedirect(cp + "/freeGallery/list.do?" + query);
				return;
			}
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			
			// 이전글 다음글
			FreeGalleryDTO preReadDto = dao.preReadGallery(dto.getNum(), condition, keyword, category);
			FreeGalleryDTO nextReadDto = dao.nextReadGallery(dto.getNum(), condition, keyword, category);
			
			// JSP로 전달할 속성
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("query", query);
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);
			
			// 포워딩
			forward(req, resp, "/WEB-INF/views/freeGallery/article.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp + "/freeGallery/list.do?" + query);
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
		FreeGalleryDAO dao = new FreeGalleryDAO();
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			

			int num = Integer.parseInt(req.getParameter("num"));
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String category = req.getParameter("category");
			
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
			
			if (category == null) {
				category = "all";
			}
			
			if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				System.out.println("2"+query);
				query += "&category=" + category;
				System.out.println("3"+query);
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				System.out.println(query);
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			} else if(keyword.length() != 0 && !category.equals("all")) { // 조건 o, 카테고리 o
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8") + "&category=" + category;
				System.out.println(query);
			}
			
			FreeGalleryDTO dto = dao.readFreeGal(num);
			if (dto == null) {
				resp.sendRedirect(cp + "/freeGallery/list.do?" + query);
				return;
			}
			
			// 서버에서 이미지 파일 지우기
			List<FreeGalleryDTO> listFile = dao.listFreeGalFile(num);
			for(FreeGalleryDTO vo : listFile) {
				FileManager.doFiledelete(pathname, vo.getImageFilename());
			}
			
			// freeGalFile 테이블에서 해당 게시글 번호의 모든 행 지우기
			dao.deleteFreeGalFile("all", num);
			
			// freeGallery 테이블에서 게시글 지우기
			dao.deleteFreeGal(num, info.getUserId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/freeGallery/list.do?" + query);
	}
	
	

}
