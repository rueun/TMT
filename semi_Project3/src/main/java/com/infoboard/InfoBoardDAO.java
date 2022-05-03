package com.infoboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class InfoBoardDAO {
private Connection conn = DBConn.getConnection();

public void insertInfoBoard(InfoBoardDTO dto) throws SQLException {
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql;
	int seq;
	
	try {
		// 다음 시퀀스값 가져오기
		sql = "SELECT infoboard_seq.NEXTVAL FROM dual";
		
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
		sql = "INSERT INTO infoboard (boardNum, userId, subject, content, hitCount, reg_date) "
				+ " VALUES (?, ?, ?, ?, 0, SYSDATE)";
		pstmt = conn.prepareStatement(sql);
		
		pstmt.setInt(1, dto.getBoardNum());
		pstmt.setString(2, dto.getUserId());
		pstmt.setString(3, dto.getSubject());
		pstmt.setString(4, dto.getContent());
		
		pstmt.executeUpdate();
		
		pstmt.close();
		pstmt = null;
		
		// 추가할 첨부파일이 존재하는 경우
		if(dto.getSaveFiles() != null) {
			sql = "INSERT INTO infofile(fileNum, num, saveFilename, originalFilename) "
					+ " VALUES(infofile_seq.NEXTVAL, ?, ?, ?)";
			
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

public int dataCount() {
	int result = 0;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql;
	
	try {
		sql = " SELECT COUNT(*) FROM infoboard";
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
	
public int dataCount(String condition, String keyword) {
	int result = 0;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql;
	
	try {
		sql = " SELECT COUNT(*) FROM infoboard n";
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
	
public List<InfoBoardDTO> listInfoBoard(int start, int end) {
	List<InfoBoardDTO> list = new ArrayList<>();
	StringBuilder sb = new StringBuilder();
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	try {
		sb.append(" SELECT * FROM ( ");
		sb.append("    SELECT ROWNUM rnum, tb.* FROM ( ");
		sb.append("       SELECT boardNum, subject, userName, hitCount, ");
		sb.append("          reg_date FROM infoboard n ");
		sb.append("          JOIN member1 m ON n.userId = m.userId ");
		sb.append("          ORDER BY boardNum DESC ");
		sb.append("       )tb WHERE ROWNUM <= ? ");
		sb.append("   )WHERE rnum >= ? ");
		
		pstmt = conn.prepareStatement(sb.toString());
		pstmt.setInt(1, end);
		pstmt.setInt(2, start);
		
		rs = pstmt.executeQuery();
		while(rs.next()) {
			InfoBoardDTO dto = new InfoBoardDTO();
			dto.setNum(rs.getInt("boardNum"));
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

public List<InfoBoardDTO> listInfoBoard(int start, int end, String condition, String keyword) {
	List<InfoBoardDTO> list = new ArrayList<>();
	StringBuilder sb = new StringBuilder();
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	try {
		sb.append(" SELECT * FROM ( ");
		sb.append("    SELECT ROWNUM rnum, tb.* FROM ( ");
		sb.append("       SELECT boardNum, subject, userName, hitCount, ");
		sb.append("          reg_date FROM infoboard n ");
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
		sb.append("          ORDER BY boardNum DESC ");
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
			dto.setNum(rs.getInt("boardNum"));
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

public InfoBoardDTO readInfoBoard(int boardNum) {
	InfoBoardDTO dto = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql;
	
	try {
		sql = "SELECT boardNum, n.userId, UserName, subject, content, hitCount, reg_date "
				+ " FROM infoboard n "
				+ " JOIN member1 m ON n.userId = m.userId "
				+ " WHERE boardNum = ? ";
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, boardNum);
		
		rs = pstmt.executeQuery();
		
		if(rs.next()) {
			dto = new InfoBoardDTO();
			
			dto.setNum(rs.getInt("boardNum"));
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

//조회수
public void updateHitCount(int boardNum) throws Exception {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE infoboard SET hitCount=hitCount+1 WHERE boardNum=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardNum);
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

//해당 게시물의 모든 첨부파일 리스트 가져오기
	public List<InfoBoardDTO> listInfoFile(int boardNum) {
		List<InfoBoardDTO> list = new ArrayList<InfoBoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT fileNum, saveFilename, originalFilename FROM infofile "
					+ " WHERE boardNum = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardNum);
			
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
	
	public InfoBoardDTO readInfoFile(int fileNum) {
		InfoBoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT fileNum, boardNum, saveFilename, originalFilename FROM infoile "
					+ " WHERE fileNum = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, fileNum);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				dto = new InfoBoardDTO();
				
				dto.setFileNum(rs.getInt("fileNum"));
				dto.setNum(rs.getInt("boardNum"));
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
	
	public void updateInfoBoard(InfoBoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE infoboard SET subject=?, content=? WHERE num=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getNum());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			
			// 추가할 첨부파일이 존재하는 경우
			if(dto.getSaveFiles() != null) {
				sql = "INSERT INTO infofile(fileNum, num, saveFilename, originalFilename) "
						+ " VALUES(infofile_seq.NEXTVAL, ?, ?, ?)";
				
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
	
	public void deleteInfoBoardFile(String mode, int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			// 게시글이 삭제된 경우 게시글의 모든 첨부파일을 삭제한다.
			if(mode.equals("all")) {
				sql = "DELETE FROM infofile WHERE num=?";
			} else {  // 해당 첨부 파일만 삭제
				sql = "DELETE FROM infofile WHERE fileNum=?";
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
}
