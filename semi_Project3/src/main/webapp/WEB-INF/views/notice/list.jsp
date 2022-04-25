<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"> 
<!-- 바로 밑줄에 있는 게 뭔지 모르겠어요 -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Notice</title>
<link rel="icon" href="data:;base64,iVBORw0KGgo=">
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">
.table-list thead > tr:first-child{ 
	background: #f8f8f8; 
}
.table-list th, .table-list td {
	text-align: center;
}
.table-list .left {
	text-align: left; padding-left: 5px; 
}

.table-list .num {
	width: 60px; color: #787878;
}
.table-list .subject {
	color: #787878;
}
.table-list .name {
	width: 100px; color: #787878;
}
.table-list .date {
	width: 100px; color: #787878;
}
.table-list .hit {
	width: 70px; color: #787878;
}
.table-list .file {
	width: 50px; color: #787878;
}
.table-list .notice {
	display: inline-block; padding: 1px 3px; color: #fff;
}

</style>
<script type="text/javascript">
function searchList() {
	const f = document.searchForm;
	f.submit();
}

</script>
</head>
<body>
<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
		<div class="body-container" style="width: 700px;" >
		<div class="body-title">
			<h1><i class="fad fa-acorn"></i> 공지사항 </h1>
			<p>필독을 권유합니다.</p>
		</div>
        
		
		
		<table class="table table-border table-list">
			<thead>
				<tr>
					<th class="num">번호</th>
					<th class="subject">제목</th>
					<th class="name">글쓴이</th>
					<th class="date">작성일</th>
					<th class="hit">조회</th>
					
				</tr>
			</thead>
			
			<tbody>
			
			<!-- 공지 빨간색으로 따로 뜨는 거
				<c:forEach var="dto" items="${listNotice }">
				<tr>
					<td><span class="notice">공지</span></td>
					<td class="left"> 
						<a href="${articleUrl }&num=${dto.num }">${dto.subject }</a>
					</td>
					<td>${dto.userName }</td>
					<td>${dto.reg_date }</td>
					<td>${dto.hitCount }</td>
				</tr>
				</c:forEach>
			 -->
			 
				<c:forEach var="dto" items="${list}">
				<tr>
					<td>${dto.listNum }</td>
					<td class="left"> 
						<a href="${articleUrl }&num=${dto.num }">${dto.subject }</a>
					</td>
					<td>${dto.userName }</td>
					<td>${dto.reg_date }</td>
					<td>${dto.hitCount }</td>
				</tr>
				</c:forEach>
				
			</tbody>
			
		</table>
		
		<div class="page-box">
			${dataCount == 0? "등록된 게시물이 없습니다." : paging }
		</div>
		<table class="table">
			<!-- 페이징 : 나중에 1 2 3 4 5 이런 식으로 사각형에 숫자 담긴 것으로 변경. << >> 있음
			<tr>				
				<td width="50%">게시글 총 ${dataCount }개(${page }/${total_page }페이지)</td>
				<td align="right">&nbsp;</td>
			</tr>
			 -->
		</table>
		
		<table class="table">
			<tr>
				<td width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/list.do';">새로고침</button>
				</td>
				<td align="center">
					<form name="searchForm">
						<select name="condition" class="form-select">
							<option value="all" ${condition == "all" ? "selected='selected'":"" } >제목+내용</option>
							<option value="userName" ${condition == "userName" ? "selected='selected'":"" }>작성자</option>
							<option value="reg_date" ${condition == "reg_date" ? "selected='selected'":"" }>등록일</option>
							<option value="subject" ${condition == "subject" ? "selected='selected'":"" }>제목</option>
							<option value="content" ${condition == "content" ? "selected='selected'":"" }>내용</option>
						</select>
						<input type="text" name="keyword" value="${keyword }" class="form-control">
						<button type="button" class="btn" onclick="searchList();">검색</button>
					</form>
				</td>
				<td align="right" width="100">
					<c:if test="${sessionScope.member.userId == 'admin'}"> 
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/write.do';">글올리기</button>
					</c:if>
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