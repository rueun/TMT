package com.qnaboard;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/qnaboard/*")
public class QnABoardServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();

		// 세션 정보
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		if (info == null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}

		// uri에 따른 작업 구분
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
		} else if (uri.indexOf("reply.do") != -1) {
			replyForm(req, resp);
		} else if (uri.indexOf("reply_ok.do") != -1) {
			replySubmit(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		}
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 리스트
		QnABoardDAO dao = new QnABoardDAO();
		MyUtil util = new MyUtil();

		String cp = req.getContextPath();

		try {
			String categoryType = req.getParameter("categoryType");
			if (categoryType == null) {
				categoryType = "all";
			}
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}

			// 검색
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if (condition == null) {
				condition = "all";
				keyword = "";
			}

			// GET 방식인 경우 디코딩
			if (req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}

			// 전체 데이터 개수
			int dataCount;
			if (keyword.length() == 0 && categoryType.equals("all")) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(condition, keyword, categoryType);
			}

			// 전체 페이지 수
			int rows = 10;
			int total_page = util.pageCount(rows, dataCount);
			if (current_page > total_page) {
				current_page = total_page;
			}

			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;

			// 게시물 가져오기
			List<QnABoardDTO> list = null;
			if (keyword.length() == 0 && categoryType.equals("all")) {
				list = dao.listBoard(start, end);
			} else {
				list = dao.listBoard(start, end, condition, keyword, categoryType);
			}

			// 리스트 글번호 만들기
			int listNum, n = 0;
			for (QnABoardDTO dto : list) {
				listNum = dataCount - (start + n - 1);
				dto.setListNum(listNum);
				n++;
			}

			String query = "";
			if (keyword.length() != 0) {
				query = "categoryType=" + categoryType + "&condition=" + condition + "&keyword="
						+ URLEncoder.encode(keyword, "utf-8");
			}

			// 페이징 처리
			String listUrl = cp + "/qnaboard/list.do";
			String articleUrl = cp + "/qnaboard/article.do?page=" + current_page;
			if (query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}

			String paging = util.paging(current_page, total_page, listUrl);

			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			req.setAttribute("categoryType", categoryType);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// JSP로 포워딩
		forward(req, resp, "/WEB-INF/views/qnaboard/list.jsp");
	}

	private void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글쓰기 폼
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/qnaboard/write.jsp");
	}

	private void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 저장
				QnABoardDAO dao = new QnABoardDAO();

				HttpSession session = req.getSession();
				SessionInfo info = (SessionInfo) session.getAttribute("member");
				
				String cp = req.getContextPath();
				if(req.getMethod().equalsIgnoreCase("GET")) {
					resp.sendRedirect(cp + "/qnaboard/list.do");
					return;
				}
				
				try {
					QnABoardDTO dto = new QnABoardDTO();

					// userId는 세션에 저장된 정보
					dto.setUserId(info.getUserId());

					// 파라미터
					dto.setCategoryType(req.getParameter("categoryType"));
					dto.setSubject(req.getParameter("subject"));
					dto.setContent(req.getParameter("content"));

					dao.insertBoard(dto, "write");
				} catch (Exception e) {
					e.printStackTrace();
				}

				resp.sendRedirect(cp + "/qnaboard/list.do");
	}

	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

	private void replyForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

	private void replySubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}
}
