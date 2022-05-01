package com.freeBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class FreeBoardDAO {
	private Connection conn = DBConn.getConnection();
	
	// 등록
	public void insertfBoard(FreeBoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "insert into freeBoard(num, userId, title, content, hitCount, reg_date) "
					+ " values (freeBoard_seq.nextval, ?, ?, ?, 0, sysdate)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getTitle());
			pstmt.setString(3, dto.getContent());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select nvl(count(*), 0) from freeboard";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
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
	
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freeBoard f JOIN member1 m ON f.userId = m.userId ";
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
	
	// 리스트
	public List<FreeBoardDTO> listBoard(int start, int end) {
		List<FreeBoardDTO> list = new ArrayList<FreeBoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("select * from ( ");
			sb.append(" select rownum rnum, tb.* FROM ( ");
			sb.append("  select f.num, userId, title, hitCount, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, ");
			sb.append("   NVL(replyCount, 0) replyCount FROM freeBoard f");
			sb.append("    join member1 m ON f.userId = m.userId ");
			sb.append("     left outer join ( ");
			sb.append("      select num, COUNT(*) replyCount FROM freeBoardReply ");
			sb.append("      GROUP BY num");
			sb.append("   ) fbr ON f.num = fbr.num");
			sb.append("   ORDER BY num DESC ");
			sb.append("  ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				FreeBoardDTO dto = new FreeBoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setTitle(rs.getString("title"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setReplyCount(rs.getInt("replyCount"));
				
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
	
	public List<FreeBoardDTO> listBoard(int start, int end, String condition, String keyword) {
		List<FreeBoardDTO> list = new ArrayList<FreeBoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("select * from ( ");
			sb.append("     select rownum rnum, tb.* FROM ( ");
			sb.append("         select f.num, userId, title, hitCount, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, ");
			sb.append("               NVL(replyCount, 0) replyCount FROM freeboard f");
			sb.append("         join member1 m ON f.userId = m.userId ");
			sb.append("         left outer join ( ");
			sb.append("             select num, COUNT(*) replyCount FROM freeBoardReply ");
			sb.append("             GROUP BY num");
			sb.append("         ) fbr ON f.num = fbr.num");
			if (condition.equals("all")) {
				sb.append("     WHERE INSTR(title, ?) >= 1 OR INSTR(content, ?) >= 1 ");
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
				FreeBoardDTO dto = new FreeBoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setTitle(rs.getString("title"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setReplyCount(rs.getInt("replyCount"));
				
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
	
	
}
