package com.notice;

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
import com.util.MyUploadServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/notice/*")
public class NoticeServlet extends MyUploadServlet {
	private static final long serialVersionUID = 1L;
	private String pathname; // 파일을 저장할 경로
	
	@Override 
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");				
		
		// 주소가 게시글 리스트가 아닐 경우에만 ?? 링크 확인하기.
		if(uri.indexOf("list.do") == -1 && info == null) {
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		// 파일을 저장할 경로 ?
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "notice";
		
		// 작업 선택
		if(uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if(uri.indexOf("write.do") != -1) {
			writeForm(req, resp);
		} else if(uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req, resp);
		} else if(uri.indexOf("article.do") != -1) {
			article(req, resp);
		} else if(uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		} else if(uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		} else if(uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if(uri.indexOf("deleteFile.do") != -1) {
			deleteFile(req, resp);
		} else if(uri.indexOf("deleteList.do") != -1) {
			deleteList(req, resp);
		} else if(uri.indexOf("download.do") != -1) {
			download(req, resp);
		}
	}

	
	// 리스트
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		NoticeDAO dao = new NoticeDAO();
		MyUtil myUtil = new MyUtil(); // 페이징 처리를 위함
		String cp = req.getContextPath();
		
		try {
			// 페이지 번호
			String page = req.getParameter("page");
			int current_page = 1; // 현재 페이지 기본값
			if(page != null) { // 페이지가 존재하면 
				current_page = Integer.parseInt(page); // 현재 페이지를 이것으로
			}
			
			
			// 검색
			String condition = req.getParameter("condition"); // 속성
			String keyword = req.getParameter("keyword"); // 검색어
			if(condition == null) {
				condition = "all"; // 전체(제목+내용)
				keyword = "";
			}
			
			
			// GET 방식인 경우 검색 값 디코딩
			if(req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}
			
			// 데이터 개수
			int dataCount;
			if(keyword.length() == 0 ) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(condition, keyword);
			}
			
			// 전체 페이지수
			int rows = 10;
			int total_page = myUtil.pageCount(rows, dataCount);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			
			// 게시글 가져오기
			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;
			
			List<NoticeDTO> list = null;
			if(keyword.length() == 0 ) {
				list = dao.listNotice(start, end);
			} else {
				list = dao.listNotice(start, end, condition, keyword);
			}
			
			// 공지글
			List<NoticeDTO> listNotice = null;
			if(current_page == 1) {
				listNotice = dao.listNotice();
			}
			
			// 게시글 번호
			int listNum, n = 0;
			for(NoticeDTO dto : list) {
				listNum = dataCount - (start + n - 1);
				dto.setListNum(listNum);
				n++;
			}
			
			// 공지 고정 : 관리자만 작성하므로 아예 gap을 따로 안 만들었음!
			/*
			long gap;
			Date curDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
				// String -> java.util.Date로 변환
				Date date = sdf.parse(dto.getReg_date());
				gap = (curDate.getTime() - date.getTime()) / (1000*60*60); // 시간 (1시간)
					// 현재시간 밀리세컨드 - db속 게시글이 저장된 시간 밀리세컨드 = 차이는 밀리세컨드 > 나누기 해야 함
					//
				dto.setGap(gap);
				
				dto.setReg_date(dto.getReg_date().substring(0,10)); // 지저분한 뒤의 숫자들 없애기
				
				n++;
			}
			*/
			
			String query = "";
			if(keyword.length() != 0) {
				query = "condition="+condition+"&keyword="+URLEncoder.encode(keyword, "utf-8");
			}
			
			String listUrl = cp + "/notice/list.do";
			String articleUrl = cp + "/notice/article.do?page=" + current_page;
			if(query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			String paging = myUtil.paging(current_page, total_page, listUrl);
			
			req.setAttribute("list", list);
			req.setAttribute("listNotice", listNotice);
			req.setAttribute("page", current_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		forward(req, resp, "/WEB-INF/views/notice/list.jsp"); // "/" 반드시 들어가야 합니다!
		
	}

	
	// 작성하는 폼
	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/notice/write.jsp");
	
	}
	
	
	// 작성 후 제출
	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		String cp = req.getContextPath();
		
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/notice/list.do");
			return;
		}
		
		if(! info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/notice/list.do");
			return;
		}
		
		NoticeDAO dao = new NoticeDAO();
		try {
			NoticeDTO dto = new NoticeDTO();
			
			dto.setUserId(info.getUserId()); // 세션에 저장된 userId
			dto.setSubject(req.getParameter("subject"));
			
			if(req.getParameter("notice") != null) {	// 주의!!!
				dto.setNotice(Integer.parseInt(req.getParameter("notice"))); // Integer.parseInt : notice가 int 라서!
			}
			dto.setContent(req.getParameter("content"));
			
			// 파일
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if(map != null) { // 파일이 있으면
				String []saveFiles = map.get("saveFilenames"); // 여기 s 잠깐 뺐었요 오류 생길 거예요
				String []originalFiles = map.get("originalFilenames");
				
				dto.setSaveFiles(saveFiles);
				dto.setOriginalFiles(originalFiles);
			}
			
			dao.insertNotice(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/notice/list.do");
	}
	
				
	
	
	
	// 작성된 글 보기
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		NoticeDAO dao = new NoticeDAO();
		String cp = req.getContextPath();
		String page = req.getParameter("page");
		String query = "page="+page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if(condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
			if(keyword.length() != 0) {
				query += "&condition="+condition+"&keyword="+URLEncoder.encode(keyword, "utf-8");
			}
			
			NoticeDTO dto = dao.readNotice(num);
			if(dto == null) {
				resp.sendRedirect(cp+"/notice/list.do?"+query);
				return;
			} 
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			List<NoticeDTO> listFile = dao.listNoticeFile(num);
			
			req.setAttribute("listFile", listFile);
			req.setAttribute("query", query);
			req.setAttribute("page", page);
			
			forward(req, resp, "/WEB-INF/views/notice/article.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp+"/notice/list.do?"+query);
	}

	
	
	// 수정하는 폼
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
	

	// 수정 후 제출
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
	
	
	// 게시글 삭제
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
	
	
	// 게시글 첨부파일 삭제
	protected void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
	
	
	// 게시글 리스트 삭제
	protected void deleteList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
	
	
	// 파일 다운로드
	protected void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	
}
