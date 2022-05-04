package com.infoBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class InfoBoardDAO {
	private Connection conn = DBConn.getConnection();
	
	// 데이터 추가
	public void insertInfoBoard(InfoBoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;
		
		try {
			// 다음 시퀀스값 가져오기
			sql = "SELECT infoBoard_seq.NEXTVAL FROM dual";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			seq = 0;
			if(rs.next()) {
				seq = rs.getInt(1);
			}
			dto.setNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			
			// 테이블에 게시물 추가 -> num의 값은 위에서 저장했기에 그 값을 넣어준다.
			sql = "INSERT INTO infoBoard (num, userId, subject, content, hitCount, reg_date) "
					+ " VALUES (?, ?, ?, ?, 0, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			// 추가할 첨부파일이 존재하는 경우
			if(dto.getSaveFiles() != null) {
				sql = "INSERT INTO infoFile(fileNum, num, saveFilename, originalFilename) "
						+ " VALUES(infoFile_seq.NEXTVAL, ?, ?, ?)";
				
				for(int i=0; i<dto.getSaveFiles().length; i++) {
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1,dto.getNum());
					pstmt.setString(2,dto.getSaveFiles()[i]);
					pstmt.setString(3,dto.getOriginalFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}		
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	
	// 데이터 개수세기(검색안할때)
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT COUNT(*) FROM infoBoard";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return result;
	}
	
	
	// 데이터개수 세기(검색시)
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT COUNT(*) FROM infoBoard n";
			sql += " JOIN member1 m ON n.userId = m.userId ";
			
			// 제목+내용인 경우 : subject, content
			if(condition.equals("all")) {
				sql += " WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			}
			
			// 등록일 : reg_date
			else if(condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/.)", "");
				sql += " WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? "; 
			}
			
			// 나머지 -> 작성자, 제목, 내용
			else {
				sql += " WHERE INSTR(" + condition + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if(condition.equals("all")) {
				pstmt.setString(2, keyword);
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return result;
	}
	
	
	// 지정 범위의 리스트 가져오기 (검색안할때)
	public List<InfoBoardDTO> listInfoBoard(int start, int end) {
		List<InfoBoardDTO> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("       SELECT num, subject, userName, hitCount, ");
			sb.append("          reg_date FROM infoBoard n ");
			sb.append("          JOIN member1 m ON n.userId = m.userId ");
			sb.append("          ORDER BY num DESC ");
			sb.append("       )tb WHERE ROWNUM <= ? ");
			sb.append("   )WHERE rnum >= ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				InfoBoardDTO dto = new InfoBoardDTO();
				dto.setNum(rs.getInt("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return list;
	}
	
	
	// 지정 범위의 리스트 가져오기 (검색시)
	public List<InfoBoardDTO> listInfoBoard(int start, int end, String condition, String keyword) {
		List<InfoBoardDTO> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("       SELECT num, subject, userName, hitCount, ");
			sb.append("          reg_date FROM infoBoard n ");
			sb.append("          JOIN member1 m ON n.userId = m.userId ");
			
			// 제목+내용일 경우
			if(condition.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1");
			}
			
			// 등록일일 경우
			else if(condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\.|\\/|\\-.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
			}
			
			// 나머지
			else {
				sb.append(" WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append("          ORDER BY num DESC ");
			sb.append("       )tb WHERE ROWNUM <= ? ");
			sb.append("   )WHERE rnum >= ? ");
				
			pstmt = conn.prepareStatement(sb.toString());
			
			if(condition.equals("all")) {
				pstmt.setString(1, keyword);
				pstmt.setString(2, keyword);
				pstmt.setInt(3, end);
				pstmt.setInt(4, start);
			}
			
			else {
				pstmt.setString(1, keyword);
				pstmt.setInt(2, end);
				pstmt.setInt(3, start);
			}
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				InfoBoardDTO dto = new InfoBoardDTO();
				dto.setNum(rs.getInt("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return list;
	}

	
	
	
	
	// 게시글 가져오기
	public InfoBoardDTO readInfoBoard(int num) {
		InfoBoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, n.userId, UserName, subject, content, hitCount, reg_date "
					+ " FROM infoBoard n "
					+ " JOIN member1 m ON n.userId = m.userId "
					+ " WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new InfoBoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("UserId"));
				dto.setUserName(rs.getString("UserName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return dto;
	}
	
	
	// 이전글 게시글
	public InfoBoardDTO preReadInfoBoard(int num, String condition, String keyword) {
		InfoBoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT num, subject ");
				sb.append("    FROM infoBoard n ");
				sb.append("    JOIN member1 m ON n.userId = m.userId ");
				sb.append("    WHERE ( num > ? ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				sb.append("     ORDER BY num ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, num);
				pstmt.setString(2, keyword);
				if (condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT num, subject FROM infoBoard ");
				sb.append("     WHERE num > ? ");
				sb.append("     ORDER BY num ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, num);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new InfoBoardDTO();
				dto.setNum(rs.getInt("num"));
				dto.setSubject(rs.getString("subject"));
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

	
	// 다음글 게시글
	public InfoBoardDTO nextReadInfoBoard(int num, String condition, String keyword) {
		InfoBoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT num, subject ");
				sb.append("    FROM infoBoard n ");
				sb.append("    JOIN member1 m ON n.userId = m.userId ");
				sb.append("    WHERE ( num < ? ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				sb.append("     ORDER BY num DESC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, num);
				pstmt.setString(2, keyword);
				if (condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT num, subject FROM infoboard ");
				sb.append("     WHERE num < ? ");
				sb.append("     ORDER BY num DESC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, num);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new InfoBoardDTO();
				dto.setNum(rs.getInt("num"));
				dto.setSubject(rs.getString("subject"));
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
	
	
	// 조회수
	public void updateHitCount(int num) throws Exception {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE infoBoard SET hitCount=hitCount+1 WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
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
	
	
	// 해당 게시물의 모든 첨부파일 리스트 가져오기
	public List<InfoBoardDTO> listInfoFile(int num) {
		List<InfoBoardDTO> list = new ArrayList<InfoBoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT fileNum, saveFilename, originalFilename FROM infoFile "
					+ " WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs=pstmt.executeQuery();
			while(rs.next()) {
				InfoBoardDTO dto = new InfoBoardDTO();
				dto.setFileNum(rs.getInt("fileNum"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return list;
	}
	
	
	// 파일번호에 해당하는 파일이름
	public InfoBoardDTO readInfoFile(int fileNum) {
		InfoBoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT fileNum, num, saveFilename, originalFilename FROM infoFile "
					+ " WHERE fileNum = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, fileNum);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				dto = new InfoBoardDTO();
				
				dto.setFileNum(rs.getInt("fileNum"));
				dto.setNum(rs.getInt("num"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return dto;
	}

	
	// 게시글 수정
	public void updateInfoBoard(InfoBoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE infoBoard SET subject=?, content=? WHERE num=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getNum());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			
			// 추가할 첨부파일이 존재하는 경우
			if(dto.getSaveFiles() != null) {
				sql = "INSERT INTO infoFile(fileNum, num, saveFilename, originalFilename) "
						+ " VALUES(infoFile_seq.NEXTVAL, ?, ?, ?)";
				
				for(int i=0; i<dto.getSaveFiles().length; i++) {
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1,dto.getNum());
					pstmt.setString(2,dto.getSaveFiles()[i]);
					pstmt.setString(3,dto.getOriginalFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}		
			
		} catch (Exception e) {
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
	
	
	// 파일 테이블 삭제
	// mode = all -> 모든 파일삭제, mode != all -> 선택한 파일만 삭제
	public void deleteInfoFile(String mode, int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			// 게시글이 삭제된 경우 게시글의 모든 첨부파일을 삭제한다.
			if(mode.equals("all")) {
				sql = "DELETE FROM infoFile WHERE num=?";
			} else {  // 해당 첨부 파일만 삭제
				sql = "DELETE FROM infoFile WHERE fileNum=?";
			}
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
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
	
	
	// 게시글 삭제
	public void deleteInfoBoard(int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM infoBoard WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
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
	
	
	
	
}