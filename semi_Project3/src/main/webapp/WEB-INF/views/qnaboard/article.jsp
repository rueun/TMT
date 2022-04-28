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

.table-list thead>tr:first-child {
	background: #fff;
}

.table-list th, .table-list td {
	text-align: center;
}

.table-list .left {
	text-align: left;
	padding-left: 5px;
}

.table-list .left img {
	width: 15px;
	text-align: center;
	height: 15px;
	line-height: 15px;
}

.table-list .num {
	width: 60px;
	color: #787878;
}

.table-list .categoryType {
	width: 60px;
	color: #787878;
}

.table-list .subject {
	color: #787878;
}

.table-list .name {
	width: 100px;
	color: #787878;
}

.table-list .date {
	width: 100px;
	color: #787878;
}

.table-list .hit {
	width: 70px;
	color: #787878;
}

.table-list .contentLine:nth-child(2n){
	background: #f7f7f7
}

.table-list .contentLine:hover{
	background: #B2EBF4;
	transition: ease 1s;
}

.body-container {
	margin: 0 auto 15px;
	width: 1000px;
	min-height: 450px;
}

.body-container:before, .body-container:after {
	content: "";
	display: block;
	clear: both;
}

.body-title {
	color: #424951;
	padding-top: 25px;
	padding-bottom: 5px;
	margin: 0 0 25px 0;
	border-bottom: 1px solid #ddd;
}

.body-title h3 {
	font-size: 23px;
	min-width: 300px;
	font-family: "Malgun Gothic", "맑은 고딕", NanumGothic, 나눔고딕, 돋움, sans-serif;
	font-weight: bold;
	margin: 0 0 -5px 0;
	padding-bottom: 5px;
	display: inline-block;
	border-bottom: 3px solid #424951;
}

.table-article tr > td {
	padding-left: 5px; padding-right: 5px;
}
</style>
<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
	        let query = "boardNum=${dto.boardNum}&${query}";
	        let url = "${pageContext.request.contextPath}/qnaboard/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="body-container" style="width: 1000px;">
		<div class="body-title">
			<h3><i class="fas fa-chalkboard-teacher"></i> 질문과 답변 </h3>
		</div>
        
		<table class="table table-border table-article">
			<thead>
				<tr>
					<td colspan="2" align="center">
						<c:if test="${dto.depth!=0 }">[Re] </c:if>
						${dto.subject}
					</td>
				</tr>
			</thead>
			
			<tbody>
				<tr>
					<td width="50%">
						이름 : ${dto.userName}
					</td>
					<td align="right">
						${dto.reg_date} | 조회 ${dto.hitCount}
					</td>
				</tr>
				
				<tr>
					<td colspan="2" valign="top" height="200">
						${dto.content}
					</td>
				</tr>
				
				<tr>
					<td colspan="2">
						이전글 :
						<c:if test="${not empty preReadDto}">
<%-- 							<a href="${pageContext.request.contextPath}/qnaboard/article.do?${query}&boardNum=${preReadDto.boardNum}">${preReadDto.subject}</a> --%>
						</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						다음글 :
						<c:if test="${not empty nextReadDto}">
							<a href="${pageContext.request.contextPath}/qnaboard/article.do?${query}&boardNum=${nextReadDto.boardNum}">${nextReadDto.subject}</a>
						</c:if>
					</td>
				</tr>
			</tbody>
		</table>
		
		<table class="table">
			<tr>
				<td width="50%">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qnaboard/reply.do?boardNum=${dto.boardNum}&page=${page}';">답변</button>
					<c:choose>
						<c:when test="${sessionScope.member.userId==dto.userId}">
							<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qnaboard/update.do?boardNum=${dto.boardNum}&page=${page}';">수정</button>
						</c:when>
						<c:otherwise>
							<button type="button" class="btn" disabled="disabled">수정</button>
						</c:otherwise>
					</c:choose>
			    	
					<c:choose>
			    		<c:when test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
			    			<button type="button" class="btn" onclick="deleteBoard();">삭제</button>
			    		</c:when>
			    		<c:otherwise>
			    			<button type="button" class="btn" disabled="disabled">삭제</button>
			    		</c:otherwise>
			    	</c:choose>
				</td>
				<td align="right">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qnaboard/list.do?${query}';">리스트</button>
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