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

.grid-box {
	margin-top: 3px; margin-bottom: 5px;
	display: grid;
	/* auto-fill :  남는 공간(빈 트랙)을 그대로 유지, minmax : '최소, 최대 크기'를 정의 */
	grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
	grid-column-gap: 10px;
	grid-row-gap: 10px;
}

.grid-box .gallery_list {
	width: 224px;
    height: 289px;
	background: #f7f7f7;
	padding: 9px 9px 0 9px;
	border: 3px solid #fff; 
	cursor: pointer;
}

.gallery_list .gallery_img {width:200px; height:200px; background:#fff;}
.gallery_list .gallery_img img {width: 100%; height: 100%; cursor: pointer;}


.gallery_list .gallery_info>span {display: inline-block; margin-top: 4px;}


.gallery_list .gallery_info>span:first-child { font-size: 12px; padding-right: 1px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;}

.gallery_list .gallery_info {text-align: left; clear: left; line-height: 18px; min-height:22px; }
.gallery_list .gallery_info .gallery_title {line-height: 140%; letter-spacing: -0.5px; margin-top:7px; word-break: break-all; max-width:100%; max-height:30px; float:left; font-weight:bold; white-space:pre-line; overflow:hidden; }
.gallery_list .gallery_info .gallery_title>input {vertical-align:text-bottom; margin-right:2px;}
.gallery_list .gallery_info .gallery_info {float:left; width:10%; }
.gallery_list .gallery_info .list_name {display:inline-block; max-width:150px; overflow:hidden; text-overflow:ellipsis; color:#374273; font-size: 12px; text-decoration: none; font-family: '굴림';}

.gallery_list .gallery_data {text-align: right; font-size: 11px; float: right; color: #676767; padding-top: 1px;}
.gallery_list .gallery_vote_data {text-align: right;margin-right:4px;font-size: 9pt;float: left !important;font-family: dotum;vertical-align: middle;}
.gallery_list .gallery_vote_data img {width:17px;}
.gallery_list .gallery_icon {color:#676767;clear: both;}
.gallery_list .gallery_icon i {font-style:normal; vertical-align: super;}
.gallery_list .gallery_icon img {margin-right:3px;}
.gallery_list .gallery_like {font-size:9pt;float: right;}
.gallery_list .gallery_like img {margin-bottom:4px;}


/* 카테고리 */

ul li, ol li {list-style: none}
.category {border-left: 1px solid #e7e7e7;border-bottom: 1px solid}
.category li {display: table-cell}
.category li.on {position:relative}
.category li.on:before {
	position: absolute;
	content: '';
	background-color: #fff;
	width: 00%;
	height: 1px;
	bottom: -1px;
	left: 0px
}
.category li a {
	text-decoration: none;
    cursor: pointer;
    color: #666;
    
	display: block;
	position: relative;
	border-top: 1px solid #e7e7e7;
	text-align: center;
	padding: 9px 20px
	
}
.category li a:after { 
	content:''; 
	display:block; 
	position:absolute; 
	top:0; 
	right:0; 
	bottom:0; 
	width:1px; 
	background:#e6e3df
}
.category li.on a, .category li:hover a {
	border-bottom-color:transparent; 
	border-top:1px solid #444
}
.category li.on a:after, .category li:hover a:after { 
	content:''; 
	display:block; 
	position:absolute; 
	top:0; 
	right:0; 
	bottom:-1px; 
	width:1px; 
	background:#444
}
.category li.on a:before, .category li:hover a:before { 
	content:''; 
	display:block; 
	position:absolute; 
	top:0; 
	left:0;
	bottom:-1px; 
	width:1px; 
	background:#444
}

</style>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="body-container" style="width: 900px;">
		<div class="body-title">
			<h3><i class="far fa-image"></i> 자유 갤러리 </h3>
		</div>
        
		<ul class="category">
			<li class="on"><a href="#">전체</a></li>
			<li><a href="#">일반</a></li>
			<li><a href="#">음식</a></li>
			<li><a href="#">인물</a></li>
			<li><a href="#">아이/가족</a></li>
			<li><a href="#">동물</a></li>
			<li><a href="#">풍경</a></li>
			<li><a href="#">야경</a></li>
			<li><a href="#">여행</a></li>
			<li><a href="#">인증</a></li>
		</ul>
        <table class="table">
			<tr>
				<td width="50%">
					${dataCount}개(${page}/${total_page} 페이지)
				</td>
				<td align="right">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/sphoto/write.do';">사진올리기</button>
				</td>
			</tr>
		</table>
		
		
		<div class="grid-box">
			<div class="gallery_list" title="$할로" onclick="">
				<ul>
            		<li class="gallery_img">
            			<a href="#">
            				<img src="${pageContext.request.contextPath}/resource/images/image.png" onfocus="blur()" border="0">
            			</a>        	
            		</li>
            		
            		<li class="gallery_info">
	                	<span class="gallery_title">
	                		<a href=""><font class="thumb_list_title">셀카임다</font></a>
	                	</span>
            		</li>
            		
		            <li class="gallery_info">
		            	<span>
		            		<a href="#" onclick="return false">
		            			<font class="list_name">나는야 짱구라네</font></a>
		            	</span>
		                <span class="gallery_data ">13:46:15</span>
		            </li>
		            
		            
		            <li class="gallery_icon">
		            	<span class="gallery_vote_data">
		            		<img class="imgcss" src="${pageContext.request.contextPath}/resource/images/answer.png" alt="comment">
		            		<i>1</i></span>
		            	<span class="gallery_like" onclick="#">
		            		<img src="${pageContext.request.contextPath}/resource/images/thumbsup.png" alt="추천" title="추천">
		            		<i>0</i>
		            	</span>
		            </li>
        		</ul>			
			</div>
		</div>
		
		
		<div class="page-box">
			${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
		</div>
		
		<table class="table">
			<tr>
				<td width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/list.do';">새로고침</button>
				</td>
				<td align="center">
					<form name="searchForm" action="${pageContext.request.contextPath}/notice/list.do" method="post">
						<select name="condition" class="form-select">
							<option value="all" ${condition=="all" ? "selected='selected'":""}>제목+내용</option>
							<option value="userName" ${condition=="userName" ? "selected='selected'":""}>작성자</option>
							<option value="reg_date" ${condition=="reg_date" ? "selected='selected'":""}>등록일</option>
							<option value="subject" ${condition=="subject" ? "selected='selected'":""}>제목</option>
							<option value="content" ${condition=="content" ? "selected='selected'":""}>내용</option>
						</select>
						<input type="text" name="keyword" value="${keyword}" class="form-control">
						<button type="button" class="btn" onclick="searchList();">검색</button>
					</form>
				</td>
				<td align="right" width="100">
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