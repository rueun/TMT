package com.infoboard;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@WebServlet("/infoBoard/*")
public class InfoServlet extends MyUploadServlet {
	private static final long serialVersionUID = 1L;
	private String pathname;
	
	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		// uri와 cp
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		
		// 로그인 정보
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		// 주소가 게시글 리스트가 아니면서 로그인이 되어 있지 않은 경우 -> 로그인 화면으로 이동한다.
		if(uri.indexOf("list.do") == -1 && info == null) {
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		// 파일을 저장할 경로 -> pathname
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "infoboard";
		
		
		// 주소에 따른 작업 구분
		if(uri.indexOf("list.do") != -1) {
			list(req, resp);
		}
		else if(uri.indexOf("write.do") != -1) {
			writeForm(req, resp);
		}
		else if(uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req, resp);
		}
		else if(uri.indexOf("article.do") != -1) {
			article(req, resp);
		}
		else if(uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		}
		else if(uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		}
		else if(uri.indexOf("delete.do") != -1) {
			delete(req,resp);
		}
		else if(uri.indexOf("deleteFile.do") != -1) {
			deleteFile(req,resp);
		}
		else if(uri.indexOf("deleteList.do") != -1) {
			deleteList(req,resp);
		}
		else if(uri.indexOf("download.do") != -1) {
			download(req,resp);
		}
	}
	
	
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 리스트
		InfoBoardDAO dao = new InfoBoardDAO();
		MyUtil myUtil = new MyUtil();
		String cp = req.getContextPath();
		
		try {
			String page = req.getParameter("page");
			// 맨 처음만 1페이지, 데이터 추가되면 상황에맞게 페이지 변경
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			
			// 검색 변수들 (condition : 검색종류, keyword : 검색명)
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if(condition == null) {
				condition = "all";
				keyword = "";
			}
			
			// GET방식이면 -> 디코딩으로 받기(한글인경우도있음)
			if(req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}
			
			
			// 한 페이지 개시물개수, 데이터개수, 전체페이지
			int rows = 10;
			int dataCount, total_page;
			
			// 검색인경우와 검색아닌경우 데이터개수
			if(keyword.length() == 0) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(condition, keyword);
			}
			
			total_page = myUtil.pageCount(rows, dataCount);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			// 시작번호와 끝번호 설정
			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;
			
			// 리스트 가져오기
			List<InfoBoardDTO> list = null;
			if(keyword.length() == 0) {
				list = dao.listInfoBoard(start, end);
			} else {
				list = dao.listInfoBoard(start, end, condition, keyword);
			}
			
			// 현재시간 : curDate, 등록시간 : date
			
			// Date curDate = new Date(); 
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			// 게시판 게시글의 순서 변수
			int BoardNum, n=0;
			for(InfoBoardDTO dto : list) {
				BoardNum = dataCount - (start + n - 1);
				dto.setBoardNum(BoardNum);
				
				// 년월일로 시간을 짤라냈음
				dto.setReg_date(dto.getReg_date().substring(0, 10));
				
				n++;
			}
			
			String query = "";
			String listUrl, articleUrl;
			
			listUrl = cp + "/in/list.do";
			articleUrl = cp + "/infoBoard/article.do?page=" + current_page;
			
			if(keyword.length() == 0) {
				query += "condition="+condition+"&keyword"+URLEncoder.encode(keyword, "utf-8");listUrl = "?" + query;
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = myUtil.paging(current_page, total_page, listUrl);
			
			req.setAttribute("list", list);
			// req.setAttribute("listInfoBoard", listInfoBoard);		
			req.setAttribute("page", current_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("keyword", keyword);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		forward(req, resp, "/WEB-INF/views/infoBoard/list.jsp");
	}
	
	private void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/infoBoard/write.jsp");
	}
	private void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();

		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/infoBoard/list.do");
			return;
		}
		
		
		
		InfoBoardDAO dao = new InfoBoardDAO();
		try {
			InfoBoardDTO dto = new InfoBoardDTO();
			
			// 세션에 저장 된 userId
			dto.setUserId(info.getUserId());
 
			dto.setSubject(req.getParameter("subject"));
			
			dto.setContent(req.getParameter("content"));

			
			// 파일
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if(map != null) {
				String []saveFiles = map.get("saveFilename");
				String []originalFiles = map.get("originalFilenames");
				
				dto.setSaveFiles(saveFiles);
				dto.setOriginalFiles(originalFiles);
			}
			
			dao.insertInfoBoard(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/infoBoard/list.do");
	}
	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InfoBoardDAO dao = new InfoBoardDAO();
		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if(condition == null) {
				condition="all";
				keyword="";
			}
			
			keyword = URLDecoder.decode(keyword, "utf-8");
			if(keyword.length() != 0) {
				query += "&condition"+condition+"&keyword="+URLEncoder.encode(keyword, "utf-8");
			}
			
			dao.updateHitCount(num);
			
			InfoBoardDTO dto = dao.readInfoBoard(num);
			if(dto == null) {
				resp.sendRedirect(cp+"/infoBoard/list.do?"+query);
			}
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			
			// 이전글, 다음글
			// InfoBoardDTO preReadInfoBoard = dao.preReadInfoBoard(num, condition, keyword);
			// InfoBoardDTO nextReadInfoBoard = dao.nextReadInfoBoard(num, condition, keyword);
			
			
			// 파일가져오기
			List<InfoBoardDTO> listFile = dao.listInfoFile(num);
			
			// jsp로 넘겨준 속성들
			req.setAttribute("dto", dto);
			req.setAttribute("listFile", listFile);
			req.setAttribute("query", query);
			req.setAttribute("page", page);
			// req.setAttribute("preReadInfoBoard", preReadInfoBoard);
			// req.setAttribute("nextReadInfoBoard", nextReadInfoBoard);
			
			// 정상 작동시 article로 이동
			forward(req, resp, "/WEB-INF/views/infoBoard/article.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 중간에 에러 발생시 리스트로 이동
		resp.sendRedirect(cp+"/infoBoard/list.do?"+query);
	}
	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// HttpSession session = req.getSession();
		// SessionInfo info = (SessionInfo)session.getAttribute("member");
		String cp = req.getContextPath();
		
		
		InfoBoardDAO dao = new InfoBoardDAO();
		String page = req.getParameter("page");
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			InfoBoardDTO dto = dao.readInfoBoard(num);
			if(dto == null) {
				resp.sendRedirect(cp+"/infoBoard/list.do");
				return;
			}
			
			// 첨부파일 목록
			List<InfoBoardDTO> listFile = dao.listInfoFile(num);
			
			req.setAttribute("dto", dto);
			req.setAttribute("listFile", listFile);
			req.setAttribute("page", page);
			req.setAttribute("mode", "update");
			
			forward(req, resp, "/WEB-INF/views/infoBoard/write.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// HttpSession session = req.getSession();
		// SessionInfo info = (SessionInfo)session.getAttribute("member");
		String cp = req.getContextPath();
		
		// GET방식이면 -> 리스트로 복귀
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/infoBoard/list.do");
			return;
		}
		
		InfoBoardDAO dao = new InfoBoardDAO();
		String page = req.getParameter("page");
		
		try {
			InfoBoardDTO dto = new InfoBoardDTO();
			
			dto.setNum(Integer.parseInt(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if(map != null) {
				String[] ss = map.get("saveFilenames");
				String[] oo = map.get("originalFilenames");
				dto.setSaveFiles(ss);
				dto.setOriginalFiles(oo);
			}
			
			dao.updateInfoBoard(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/infoBoard/list.do?page="+page);
	}
	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InfoBoardDAO dao = new InfoBoardDAO();
		String cp = req.getContextPath();
		int page = Integer.parseInt(req.getParameter("page"));
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			dao.deleteInfoBoardFile("all", num);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/infoBoard/list.do?page="+page);
	}
	private void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	private void deleteList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	private void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
}