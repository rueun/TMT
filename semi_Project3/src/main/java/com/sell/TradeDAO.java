package com.sell;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class TradeDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertSell(TradeDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		/////////
		try {
			sql = "INSERT INTO trade(tradeNum, userId, subject, content, price, imageFilename, hitCount, reg_date ) "
					+ " VALUES (trade_seq.NEXTVAL, ?, ?, ?, ?, ?, 0, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			pstmt.setString(4, dto.getPrice());
			pstmt.setString(5, dto.getImageFilename());
				
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

	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM trade";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
		return result;
	}

	public List<TradeDTO> listTrade(int start, int end) {
		List<TradeDTO> list = new ArrayList<TradeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT tradeNum, p.userId, subject, price, p.imageFilename, hitCount, ");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("         FROM trade p ");
			sb.append("         JOIN member1 m ON p.userId = m.userId ");
			sb.append("         ORDER BY tradeNum DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");	
			//
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				TradeDTO dto = new TradeDTO();
				
				dto.setTradeNum(rs.getInt("tradeNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setPrice(rs.getString("price"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return list;
	}

}