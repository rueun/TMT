package com.freeGallery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class FreeGalleryDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertFreeGallery(FreeGalleryDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;
		
		try {
			sql = "SELECT freeGallery_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			seq = 0;
			if (rs.next()) {
				seq = rs.getInt(1);
			}
			dto.setNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			sql = "INSERT INTO freeGallery ( num, userId, category, subject, content, reg_date, hitCount) "
					+ " VALUES (?, ?, ?, ?, ?, SYSDATE, 0)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getCategory());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getContent());

			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO freeGalFile(fileNum, num, saveFilename) VALUES "
						+ " (freeGalFile_seq.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setInt(1, dto.getNum());
					pstmt.setString(2, dto.getImageFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}
			// freeGalFile 테이블에 파일 insert하기
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	
	// 데이터 개수
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freegallery";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}
	
	// 검색에서의 데이터 개수1(조건 x, 카테고리 o)
	public int dataCount(String category) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freegallery f JOIN member1 m ON f.userId = m.userId "
				+ "WHERE category = ?";
			

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, category);

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}
	
	// 검색에서의 데이터 개수2(조건 o, 카테고리 x)
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freegallery f JOIN member1 m ON f.userId = m.userId ";
			if (condition.equals("all")) {
				sql += "  WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  WHERE INSTR(" + condition + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if (condition.equals("all")) {
				pstmt.setString(2, keyword);
			}

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}
	
	
	// 검색에서의 데이터 개수3(조건 o, 카테고리 o)
	public int dataCount(String condition, String keyword, String category) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freegallery f JOIN member1 m ON f.userId = m.userId ";
			sql += "  WHERE category = ? ";
			if (condition.equals("all")) {
				sql += "  AND INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  AND INSTR(" + condition + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, category);
			pstmt.setString(2, keyword);
			if (condition.equals("all")) {
				pstmt.setString(3, keyword);
			}

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}
	
	
	// 게시물 리스트 (조건x, 카테고리 x)
	public List<FreeGalleryDTO> listFreeGallery(int start, int end) {
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
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			
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
	
	// 검색에서의 리스트1(조건x, 카테고리o)
	public List<FreeGalleryDTO> listFreeGallery(int start, int end, String category) {
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
			sb.append("     WHERE category = ? ");
			sb.append("         ORDER BY num DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");


			pstmt = conn.prepareStatement(sb.toString());
				pstmt.setString(1, category);
				pstmt.setInt(2, end);
				pstmt.setInt(3, start);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
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
	
	
	// 검색에서의 게시물 리스트2(조건o, 카테고리 x)
	public List<FreeGalleryDTO> listFreeGallery(int start, int end, String condition, String keyword) {
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
			if (condition.equals("all")) {
				sb.append("     WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append("     WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append("     WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			
			sb.append("         ORDER BY num DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");


			pstmt = conn.prepareStatement(sb.toString());
			
			if (condition.equals("all")) {
				pstmt.setString(1, keyword);
				pstmt.setString(2, keyword);
				pstmt.setInt(3, end);
				pstmt.setInt(4, start);
			} else {
				pstmt.setString(1, keyword);
				pstmt.setInt(2, end);
				pstmt.setInt(3, start);
			}

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
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
	
	
	
	// 검색에서의 게시물 리스트3(조건o, 카테고리 o)
	public List<FreeGalleryDTO> listFreeGallery(int start, int end, String condition, String keyword, String category) {
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
			sb.append("     	WHERE category = ? ");
			if (condition.equals("all")) {
				sb.append("     AND INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append("     AND TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append("     AND INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append("         ORDER BY num DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");


			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, category);
			
			if (condition.equals("all")) {
				pstmt.setString(2, keyword);
				pstmt.setString(3, keyword);
				pstmt.setInt(4, end);
				pstmt.setInt(5, start);
			} else {
				pstmt.setString(2, keyword);
				pstmt.setInt(3, end);
				pstmt.setInt(4, start);
			}

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
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
	
	// 조회수 증가하기
	public void updateHitCount(int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE freeGallery SET hitCount=hitCount+1 WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

	}
	
	// 해당 게시물 보기
	public FreeGalleryDTO readFreeGal(int num) {
		FreeGalleryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT f.num, f.userId, userNickName, subject, content, "
					+ " reg_date, hitCount, NVL(likecount, 0) likecount "
					+ " FROM freegallery f "
					+ " JOIN member1 m ON f.userId=m.userId "
					+ " LEFT OUTER JOIN ("
					+ "      SELECT num, COUNT(*) likecount FROM freeGalLike" // 게시글의 좋아요
					+ "      GROUP BY num"
					+ " ) fgl ON f.num = fgl.num"
					+ " WHERE f.num = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				dto = new FreeGalleryDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setLikeCount(rs.getInt("likecount"));
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

		return dto;
	}
	
	
	// 해당 게시물 파일 리스트 보기
	public List<FreeGalleryDTO> listPhotoFile(int num) {
		List<FreeGalleryDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, num, savefilename FROM freeGalFile WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				FreeGalleryDTO dto = new FreeGalleryDTO();

				dto.setFileNum(rs.getInt("fileNum"));
				dto.setNum(rs.getInt("num"));
				dto.setImageFilename(rs.getString("savefilename"));
				
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
	
	// 로그인 유저의 게시글 공감 유무(좋아요를 했는지 안했는지)
	public boolean isUserBoardLike(int num, String userId) {
		boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, userId FROM freeGalLike WHERE num = ? AND userId = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = true; // 좋아요를 했으면 true, 아니면 fasle 값 반환
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
		}
		
		return result;
	}
	
	
	
	
}
