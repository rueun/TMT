package com.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.freeGallery.FreeGalleryDTO;
import com.qnaboard.QnABoardDTO;
import com.util.DBConn;

public class MainDAO {
	private Connection conn = DBConn.getConnection();

	// 질문과 답변 게시물 리스트
	public List<QnABoardDTO> QnAListBoard() {
		List<QnABoardDTO> list = new ArrayList<QnABoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT boardNum, b.userId, userName, userNickName, categoryType, ");
			sb.append("               subject, groupNum, orderNo, depth, hitCount,");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("         FROM QnAboard b ");
			sb.append("         JOIN member1 m ON b.userId = m.userId ");
			sb.append("         ORDER BY hitCount DESC");
			sb.append("     ) tb WHERE ROWNUM <= 10 ");
			sb.append(" ) WHERE rnum >= 1 ");

			pstmt = conn.prepareStatement(sb.toString());

			rs = pstmt.executeQuery();

			while (rs.next()) {
				QnABoardDTO dto = new QnABoardDTO();

				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setCategoryType(rs.getString("categoryType"));
				dto.setSubject(rs.getString("subject"));
				dto.setGroupNum(rs.getInt("groupNum"));
				dto.setDepth(rs.getInt("depth"));
				dto.setOrderNo(rs.getInt("orderNo"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));

				list.add(dto);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

		return list;
	}

	// 자유갤러리 리스트
	public List<FreeGalleryDTO> listFreeGallery() {
		List<FreeGalleryDTO> list = new ArrayList<FreeGalleryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT f.num, f.userId, userNickName, category, subject, savefilename, TO_CHAR(reg_date, 'YY/MM/DD') reg_date, "
					+ "NVL(likecount,0) likecount, NVL(replycount,0) replycount ");
			sb.append("         FROM freegallery f ");
			sb.append("         JOIN member1 m ON f.userId = m.userId ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             SELECT fileNum, num, savefilename FROM ( ");
			sb.append("                 SELECT fileNum, num, savefilename, ");
			sb.append("                       ROW_NUMBER() OVER(PARTITION BY num ORDER BY fileNum ASC) rank ");
			sb.append("                 FROM freegalfile");
			sb.append("             ) WHERE rank = 1 ");
			sb.append("         ) i ON f.num = i.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) likecount ");
			sb.append("             from freeGalLike ");
			sb.append("             group by num ");
			sb.append("         )fgl ON f.num = fgl.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) replycount ");
			sb.append("             from freeGalReply ");
			sb.append("             group by num ");
			sb.append("         )fgr ON f.num = fgr.num ");
			sb.append("         ORDER BY num DESC ");
			sb.append("     ) tb WHERE ROWNUM <= 10 ");
			sb.append(" ) WHERE rnum >= 1 ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				FreeGalleryDTO dto = new FreeGalleryDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setCategory(rs.getString("category"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("savefilename"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setLikeCount(rs.getInt("likecount"));
				dto.setReplyCount(rs.getInt("replycount"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		

		return list;
	}
}
