<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.boardname { /* "소통 공간" 부분 */
	text-align: center;
	font-size: 16px;
	margin-bottom: 15px;
	padding: 10px;
}

.table-list thead > tr:first-child{
	background: #EAEAEA;
}
.table-list th, .table-list td {
	text-align: center;
	font-family: Arial, Tahoma, sans-serif;
	border-radius: inherit;
	border-style: solid;
	border-width: 0 0 1px 0;
	border-color: #EAEAEA;
    box-sizing: border-box!important;
    padding: 10px;
    margin: 10px;
    max-width: 100%;
    height: 30px;
}

.table-list .left {
	text-align: center; padding-left: 5px; 
}

.table-list .num {
	width: 75px; color: #787878;
}
.table-list .subject {
	color: #787878;
}
.table-list .reply {
	width: 75px; color: #787878;
}
.table-list .name {
	width: 100px; color: #787878;
}
.table-list .date {
	width: 100px; color: #787878;
}
.table-list .hit {
	width: 75px; color: #787878;
}

.table-list .fboard {
	display: inline-block; padding: 1px 3px;
}

.table-list .fsubject {
	text-align: left;
}

</style>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>

<main>
	<div class="body-container" style="width: 900px; text-align: center; margin: auto;">
		<div class="boardname">
			<h2><i class="fas fa-chalkboard-teacher"></i> 소통 공간 </h2>
		</div>
        
		<table class="table">
			<tr>
				<td width="100%" align="right">
					${dataCount}10개(${page}1 /${total_page} 10페이지)
				</td>
				<td align="right">&nbsp;</td>
			</tr>
		</table>
		
		<table class="table table-border table-list">
			<thead>
				<tr>
					<th class="num">번호</th>
					<th class="subject">제목</th>
					<th class="reply">댓글</th>
					<th class="name">작성자</th>
					<th class="date">작성일</th>
					<th class="hit">조회수</th>
				</tr>
			</thead>
			
			<tbody>
					<tr>
						<td><span class="fboard">1</span></td>
						<td class="fsubject">제목만 왼쪽으로 가게 하려면?</td>
						<td>1</td>
						<td>바로 너</td>
						<td>지금</td>
						<td>50</td>
					</tr>
					<tr>
						<td>2</td>
						<td class="fsubject">다른 제목</td>
						<td>2</td>
						<td>누가 썼나</td>
						<td>언제 썼나</td>
						<td>20</td>
					</tr>
					<tr>
						<td>3</td>
						<td class="fsubject">아무 제목</td>
						<td>0</td>
						<td>네가 썼지</td>
						<td>방금 썼지</td>
						<td>5</td>
					</tr>
			</tbody>
			<!-- 
			<tbody>
				<c:forEach var="dto" items="${list}">
					<tr>
						<td><span class="fboard">${dto.listNum}</span></td>
						<td class="left">
							<c:forEach var="n" begin="1" end="${dto.depth}">&nbsp;&nbsp;</c:forEach>
							<c:if test="${dto.depth!=0}">└&nbsp;</c:if>
							<a href="${articleUrl}&boardNum=${dto.boardNum}">제목</a>
						</td>
						<td>댓글</td>
						<td>작성자</td>
						<td>등록일</td>
						<td>조회수</td>
					</tr>
				</c:forEach>
			</tbody>
			 -->
		</table>
		
		<div class="page-box">
			${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
		</div>
		
		<table class="table">
			<tr>
				<td width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/board/list.do';" title="새로고침">새로고침<i class="fa-solid fa-arrow-rotate-right"></i></button>
				</td>
				<td align="center">
					<form name="searchForm" action="${pageContext.request.contextPath}/board/list.do" method="post">
						<select name="condition" class="form-select">
							<option value="all"      ${condition=="all"?"selected='selected'":"" }>제목+내용</option>
							<option value="userName" ${condition=="userName"?"selected='selected'":"" }>작성자</option>
							<option value="reg_date"  ${condition=="reg_date"?"selected='selected'":"" }>등록일</option>
							<option value="subject"  ${condition=="subject"?"selected='selected'":"" }>제목</option>
							<option value="content"  ${condition=="content"?"selected='selected'":"" }>내용</option>
						</select>
						<input type="text" name="keyword" value="${keyword}" class="form-control">
						<button type="button" class="btn" onclick="searchList();">검색</button>
					</form>
				</td>
				<td align="right" width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/board/write.do';">글 작성</button>
				</td>
			</tr>
		</table>	

	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>