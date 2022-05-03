<%@page import="oracle.net.aso.i"%>
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

.wrap {
   	margin-bottom: 30px;
}


.body-container {
    margin: 0 auto 15px;
    width: 1200px;
    min-height: 450px;
}





h4.h4 {
    font-size: 15px;
    line-height: 1.4;
    padding: 0;
    margin: 0;
}

hr.hr {
    position: relative;
    height: 3px;
    margin: 5px 0 10px;
    border:  1px solid #ddd;
    line-height: 1px;
}

.h4-left {
    float: left;
}

.more {
    float: right;
}

.hr-both {
    clear: both;
}

.item {
	width: 405px;
	list-style: none;
	float: left;
}
.item-box {
    margin-right: 20px;
    margin-bottom: 20px;
}

.list-container .item:last-child {
	width: 385px;
}

.list-container .item:last-child .item-box {
	margin-right: 0px;
} 
.item-box a {
	width: 100%;
}

.item-box a:first-child img {
	width: 100%;
	border:  1px solid #ddd;
	height: 200px;
	object-fit: cover;
}

.item-box .sp-subj {
    padding: 10px 0 6px;
    border-bottom: 1px solid #ddd;
    margin-bottom: 10px;
}

.item-box .sp-subj b {
	font-size: 16px;
}

.ellipsis {
    display: block;
    text-overflow: ellipsis;
    white-space: nowrap;
    word-wrap: normal;
    overflow: hidden;
}

.count img {
	margin-bottom:1px;
	width: 12px;
	vertical-align: middle;
}

.date img {
	margin-bottom:3px;
	width: 12px;
	vertical-align: middle;
}

.view img{
	vertical-align: sub;
	width: 15px;
}

.item .wr-icon {
    display: inline-block;
    padding: 0;
    margin: 0;
    overflow: hidden;
    vertical-align: middle;
    margin-bottom: 3px;
    background-repeat: no-repeat;
    background-position: left top;
}

.item .wr-new {
    width: 12px;
    height: 12px;
    background-image: url(${pageContext.request.contextPath}/resource/images/icon_new.gif);
}

.pull-right {
    float: right;
    color: #888;
}

.list_title {
	display: block;
	overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.list-container {
	width: 100%;
	height: 430px;
}


.list-container .post {
    padding: 0;
    margin-right: -20px;
    margin-left: 0;
    margin-top: 0;
    overflow: hidden;
}

.list-container .post-sp {
	margin-bottom: 0!important;
}

.list-a {
	height: 30px;
}

.list-a:hover {
	color: red;
	text-decoration: none;
}

.main-font {
    font-size: 16px;
}


.row {
    margin-right: -5px;
    margin-left: -5px;
}

.row-20 {
    margin-left: -10px!important;
    margin-right: -10px!important;
}

.col-md-8, .col-md-4, .col-xs-6 {
    float: left;
    position: relative;
    min-height: 1px;
    padding-right: 10px;
    padding-left: 10px;
}

.col-md-8 {
    width: 66.66666667%;
}

.col-md-4 {
    width: 33.33333333%;
}

.col-xs-6 {
    width: 50%;
}

.col-xs-6 img {
	height: 150px;
}

.col-md-4 hr.hr {
	margin: 0;
}

.col-10 {
    padding-left: 5px!important;
    padding-right: 5px!important;
}

.only-list {
	height: 50px;
	line-height: 50px;
	border-bottom: 1px solid #ddd; 
}
</style>

<script type="text/javascript">

</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main style="margin-top : 200px">
	<div class="body-container" style="width: 1200px;">
		<h4 class="h4 h4-left">
    		<a class="list-a">
    			<img src="https://humorpick.com/thema/Miso-Simple/img/menu_best.png" alt="베스트" width="20px" title="">
    			<b class="main-font">최신 게시글</b>
    		</a>
    	</h4>
    	<div class="more">
			<a href="">
				<img src="https://humorpick.com/thema/Miso-Simple/img/more.png" alt="더보기" width="24px" title="">
			</a>
		</div>
    	
    	<hr class="hr hr-both">
    	
    	<div class="list-container">
    		<ul>
    		
    			<!-- 질문 답변 -->
    			<li class="item">
    				<div class="item-box">
    					<a href="#" class="list-a">
    						<c:forEach var="image" items="${imageListFreeGallery}" begin="0" end="0">
    						<img src="${pageContext.request.contextPath}/uploads/freeGallery/${image.imageFilename}" >
    						</c:forEach>
	    					<span class="sp-subj ellipsis">
	    						<span><b>QnA</b></span>
		    						<span class="pull-right">
		   							<span class="date">
			   								${date }
			   						</span>
		   						</span>
	    					</span>
    					</a>
						
						<c:forEach var="dto" items="${qnalist}">
							<a href="#" class="ellipsis list-a">
							<span class="wr-icon wr-new"></span>
							
							<span class="pull-right">
								<span class="count">
									<img alt="" src="${pageContext.request.contextPath}/resource/images/answer.png">
	   								{dto.replyCount}
	   							</span>
	   							<span class="view">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/view.png">
	   								${dto.hitCount }
	   							</span>
	   							<span class="date">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/time.png">
	   								${dto.reg_date }
	   							</span>
   							</span>
   							<span>${dto.subject }</span>
							</a>
						</c:forEach>
						
    				</div>
    			</li>
    			
				<!-- 자유 갤러리  -->
				<li class="item">
    				<div class="item-box">
    					<a href="#" class="list-a">
    						<c:forEach var="image" items="${imageListFreeGallery}" begin="1" end="1">
    						<img src="${pageContext.request.contextPath}/uploads/freeGallery/${image.imageFilename}" >
    						</c:forEach>
	    					<span class="sp-subj ellipsis">
	    						<span><b>자유 갤러리</b></span>
		    						<span class="pull-right">
		   							<span class="date">
			   								${date }
			   						</span>
		   						</span>
	    					</span>
    					</a>
						
						<c:forEach var="dto" items="${freeGalleryList}">
							<a href="#" class="ellipsis list-a">
							<span class="wr-icon wr-new"></span>
							
							<span class="pull-right">
								<span class="count">
									<img alt="" src="${pageContext.request.contextPath}/resource/images/answer.png">
	   								${dto.replyCount }
	   							</span>
	   							<span class="view">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/view.png">
	   								${dto.hitCount }
	   							</span>
	   							<span class="date">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/time.png">
	   								${dto.reg_date }
	   							</span>
   							</span>
   							<span>${dto.subject }</span>
							</a>
						</c:forEach>
						
    				</div>
    			</li>
				
				<!--  공지사항 -->
				<li class="item">
    				<div class="item-box">
    					<a href="#" class="list-a">
    						<c:forEach var="image" items="${imageListFreeGallery}" begin="2" end="2">
    						<img src="${pageContext.request.contextPath}/uploads/freeGallery/${image.imageFilename}" >
    						</c:forEach>
	    					<span class="sp-subj ellipsis">
	    						<span><b>공지사항</b></span>
		    						<span class="pull-right">
		   							<span class="date">
										${date}
			   						</span>
		   						</span>
	    					</span>
    					</a>
						
						<c:forEach var="dto" items="${noticeList}">
							<a href="#" class="ellipsis list-a">
							<span class="wr-icon wr-new"></span>
							
							<span class="pull-right">
								<span class="count">
									<img alt="" src="${pageContext.request.contextPath}/resource/images/answer.png">
	   								{dto.replyCount}
	   							</span>
	   							<span class="view">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/view.png">
	   								${dto.hitCount }
	   							</span>
	   							<span class="date">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/time.png">
	   								${dto.reg_date }
	   							</span>
   							</span>
   							<span>${dto.subject }</span>
							</a>
						</c:forEach>
    				</div>
    			</li>
    		</ul>
    	</div>
    	
    	<div class="list-container">
    		<ul>
    		
    			<!-- 소통 공간 -->
    			<li class="item">
    				<div class="item-box">
    					<a href="#" class="list-a">
	    					<span class="sp-subj ellipsis">
	    						<span><b>소통공간</b></span>
		    						<span class="pull-right">
		   							<span class="date">
			   								${date }
			   						</span>
		   						</span>
	    					</span>
    					</a>
						
						<c:forEach var="dto" items="${freeBoardList}">
							<a href="#" class="ellipsis list-a">
							<span class="wr-icon wr-new"></span>
							
							<span class="pull-right">
								<span class="count">
									<img alt="" src="${pageContext.request.contextPath}/resource/images/answer.png">
	   								${dto.replyCount }
	   							</span>
	   							<span class="view">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/view.png">
	   								${dto.hitCount }
	   							</span>
	   							<span class="date">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/time.png">
	   								${dto.reg_date }
	   							</span>
   							</span>
   							<span>${dto.title }</span>
							</a>
						</c:forEach>
						
    				</div>
    			</li>
    			
				<!-- 정보게시판  -->
				<li class="item">
    				<div class="item-box">
    					<a href="#" class="list-a">
	    					<span class="sp-subj ellipsis">
	    						<span><b>정보게시판</b></span>
		    						<span class="pull-right">
		   							<span class="date">
			   								${date }
			   						</span>
		   						</span>
	    					</span>
    					</a>
						
						<c:forEach var="dto" items="${infoBoardList}">
							<a href="#" class="ellipsis list-a">
							<span class="wr-icon wr-new"></span>
							
							<span class="pull-right">
								<span class="count">
									<img alt="" src="${pageContext.request.contextPath}/resource/images/answer.png">
	   								{dto.replyCount }
	   							</span>
	   							<span class="view">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/view.png">
	   								${dto.hitCount }
	   							</span>
	   							<span class="date">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/time.png">
	   								${dto.reg_date }
	   							</span>
   							</span>
   							<span>${dto.subject }</span>
							</a>
						</c:forEach>
						
    				</div>
    			</li>
				
				
				<li class="item">
    				<div class="item-box">
    					<a href="#" class="list-a">
	    					<span class="sp-subj ellipsis">
	    						<span><b>공지사항</b></span>
		    						<span class="pull-right">
		   							<span class="date">
										${date}
			   						</span>
		   						</span>
	    					</span>
    					</a>
						
						<c:forEach var="dto" items="${noticeList}">
							<a href="#" class="ellipsis list-a">
							<span class="wr-icon wr-new"></span>
							
							<span class="pull-right">
								<span class="count">
									<img alt="" src="${pageContext.request.contextPath}/resource/images/answer.png">
	   								{dto.replyCount}
	   							</span>
	   							<span class="view">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/view.png">
	   								${dto.hitCount }
	   							</span>
	   							<span class="date">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/time.png">
	   								${dto.reg_date }
	   							</span>
   							</span>
   							<span>${dto.subject }</span>
							</a>
						</c:forEach>
    				</div>
    			</li>
    		</ul>
    	</div>
    	
    	
		<!-- 팝니다 -->
		<div class="row row-20">
			<div class="col-md-8 col-20">
				<h4 class="h4 h4-left">
		    		<a class="list-a">
		    			<img src="${pageContext.request.contextPath}/resource/images/main_sell_buy.png" alt="베스트" width="20px" title="">
		    			<b class="main-font">장터(팝니다)</b>
		    		</a>
	    		</h4>
	    		<div class="more">
					<a href="">
						<img src="${pageContext.request.contextPath}/resource/images/main_more.png" alt="더보기" width="24px" title="">
					</a>
				</div>
				
				<hr class="hr hr-both">
				
				
				<div class="list-container">
    		<ul>
    			<li class="item">
    				<div class="item-box">
    					<div class="row row-10">
    						<div class="col-xs-6 col-10">
    							<a href="#" class="list-a">
    								<span class="img-box img-bg">
    									<img alt="" src="https://humorpick.com/data/file/celeb/thumb-16509008516211_400x0.jpg">
    								</span>
    								<span class="sp-subj ellipsis">
    									<span class="pull-right"></span>
    									<span class="title">히히히히힣ㄴㅇ러ㅣㅏㄴ이러ㅏ머ㅣ</span>
    								</span>
    							</a>
    						</div>
    						
    						<div class="col-xs-6 col-10">
    							<a href="#" class="list-a">
    								<span class="img-box img-bg">
    									<img alt="" src="https://humorpick.com/data/file/celeb/thumb-16509008516211_400x0.jpg">
    								</span>
    								<span class="sp-subj ellipsis">
    									<span class="pull-right"></span>
    									<span class="title">히히히히힣ㄴㅇ러ㅣㅏㄴ이러ㅏ머ㅣ</span>
    								</span>
    							</a>
    						</div>
    					
    					</div>
						
						<c:forEach var="dto" items="${listTrade }" begin="0" end="4">			
							<a href="#" class="ellipsis list-a">
							<span class="wr-icon wr-new"></span>
							
							<span class="pull-right">
								<span class="count">
									<img alt="" src="${pageContext.request.contextPath}/resource/images/answer.png">
	   								{dto.replyCount}
	   							</span>
	   							<span class="view">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/view.png">
	   								{dto.hitCount}
	   							</span>
	   							<span class="date">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/time.png">
	   								${dto.reg_date }
	   							</span>
   							</span>
   							<span>${dto.subject }</span>
							</a>
						</c:forEach>
    				</div>
    			</li>
    			
				
				<li class="item">
    				<div class="item-box">
    					<div class="row row-10">
    						<div class="col-xs-6 col-10">
    							<a href="#" class="list-a">
    								<span class="img-box img-bg">
    									<img alt="" src="https://humorpick.com/data/file/celeb/thumb-16509008516211_400x0.jpg">
    								</span>
    								<span class="sp-subj ellipsis">
    									<span class="pull-right"></span>
    									<span class="title">히히히히힣ㄴㅇ러ㅣㅏㄴ이러ㅏ머ㅣ</span>
    								</span>
    							</a>
    						</div>
    						
    						<div class="col-xs-6 col-10">
    							<a href="#" class="list-a">
    								<span class="img-box img-bg">
    									<img alt="" src="https://humorpick.com/data/file/celeb/thumb-16509008516211_400x0.jpg">
    								</span>
    								<span class="sp-subj ellipsis">
    									<span class="pull-right"></span>
    									<span class="title">히히히히힣ㄴㅇ러ㅣㅏㄴ이러ㅏ머ㅣ</span>
    								</span>
    							</a>
    						</div>
    					
    					</div>
						<c:forEach var="dto" items="${listTrade }" begin="5" end="9">
							<a href="#" class="ellipsis list-a">
							<span class="wr-icon wr-new"></span>
							
							<span class="pull-right">
								<span class="count">
									<img alt="" src="${pageContext.request.contextPath}/resource/images/answer.png">
	   								{dto.replyCount}
	   							</span>
	   							<span class="view">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/view.png">
	   								{dto.hitCount}
	   							</span>
	   							<span class="date">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/time.png">
	   								${dto.reg_date }
	   							</span>
   							</span>
   							<span>${dto.subject }</span>
							</a>
						</c:forEach>
    				</div>
    			</li>
    		
    		</ul>
    	</div>
				
				
			</div>
			
			<!-- 삽니다 -->
			<div class="col-md-4 col-20">
				<h4 class="h4 h4-left">
		    		<a class="list-a">
		    			<img src="${pageContext.request.contextPath}/resource/images/main_sell_buy.png" alt="베스트" width="20px" title="">
		    			<b class="main-font">장터(삽니다)</b>
		    		</a>
	    		</h4>
	    		<div class="more">
					<a href="">
						<img src="${pageContext.request.contextPath}/resource/images/main_more.png" alt="더보기" width="24px" title="">
					</a>
				</div>
				
				<hr class="hr hr-both">
				
				<div class="list-container">
		    		<ul>
		    			<li class="item">
    				<div class="item-box">
    					<a class="list-a">
    					</a>
						<c:forEach var="dto" items="${listTradebuy}">
							<a href="#" class="ellipsis list-a only-list">
							
							<span class="pull-right">
								<span class="count">
									<img alt="" src="${pageContext.request.contextPath}/resource/images/answer.png">
	   								{dto.replyCount }
	   							</span>
	   							<span class="view">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/view.png">
	   								{dto.hitCount }
	   							</span>
	   							<span class="date">
	   								<img alt="" src="${pageContext.request.contextPath}/resource/images/time.png">
	   								${dto.reg_date }
	   							</span>
   							</span>
   							<span>${dto.subject }</span>
							</a>
						</c:forEach>
						
    				</div>
    			</li>
		    			
		    		</ul>
	    		</div>
				
			</div>
			
		</div>
		
		
		

	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>