/*
 * LICENSE AND COPYRIGHT INFORMATION:
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is OpenIPMP.
 *
 * The Initial Developer of the Original Code is Mutable, Inc.
 * Portions created by Mutable, Inc. are
 * Copyright (C) Mutable, Inc. 2002-2006.  All Rights Reserved.
 * 
 *
 */


 /**
  * <p>Title: </p>
  * <p>Description: </p>
  * @version 1.0
  */
package com.mutable.osms.server.db.manager.base;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.io.Serializable;
import java.util.Vector;

import com.mutable.osms.server.db.bean.*;

public class osmsDvDoiProfileManagerBase implements Serializable
{
	private String m_tablename="DOI_PROFILE";
	protected String getTableName(){return m_tablename;}
	protected String getSqlResultSet() {return m_sqlResultSet;}
	private String m_sqlResultSet=
		(
			(new StringBuffer())
			.append(" DOI_PROFILE.DOI_PROFILE_CODE")
			.append(", ").append(" DOI_PROFILE.PROFILE_NAME")
			.append(", ").append(" DOI_PROFILE.TABLE_NAME")
			.append(", ").append(" DOI_PROFILE.DESC_SHORT")
			.append(", ").append(" DOI_PROFILE.DESC_LONG")
			.append(", ").append(" DOI_PROFILE.MAINTENANCE_FLAG")
			.append(", ").append(" DOI_PROFILE.LAST_UPDATED_BY")
			.append(", ").append(" DOI_PROFILE.LAST_UPDATED_ON")
			.append(", ").append(" DOI_PROFILE.CREATED_BY")
			.append(", ").append(" DOI_PROFILE.CREATED_ON")
		).toString();

/*
	private String m_sqlResultSet=" DOI_PROFILE.DOI_PROFILE_CODE,  DOI_PROFILE.PROFILE_NAME,  DOI_PROFILE.TABLE_NAME,  DOI_PROFILE.DESC_SHORT,  DOI_PROFILE.DESC_LONG,  DOI_PROFILE.MAINTENANCE_FLAG,  DOI_PROFILE.LAST_UPDATED_BY,  DOI_PROFILE.LAST_UPDATED_ON,  DOI_PROFILE.CREATED_BY,  DOI_PROFILE.CREATED_ON";
*/
	//------------------------------------------------------------------
	// mapResultSet2Bean
	//------------------------------------------------------------------
	protected osmsDvDoiProfileBean mapResultSet2Bean(ResultSet rs)
		throws Exception
	{
			int i=1;

			osmsDvDoiProfileBean bean = new osmsDvDoiProfileBean();
			bean.setDoiProfileCode((String)ConvertObject2String(rs.getObject(i++)));
			bean.setProfileName((String)ConvertObject2String(rs.getObject(i++)));
			bean.setTableName((String)ConvertObject2String(rs.getObject(i++)));
			bean.setDescShort((String)ConvertObject2String(rs.getObject(i++)));
			bean.setDescLong((String)ConvertObject2String(rs.getObject(i++)));
			bean.setMaintenanceFlag((String)ConvertObject2String(rs.getObject(i++)));
			bean.setLastUpdatedBy((String)ConvertObject2String(rs.getObject(i++)));
			bean.setLastUpdatedOn((Timestamp)ConvertObject2Timestamp(rs.getObject(i++)));
			bean.setCreatedBy((String)ConvertObject2String(rs.getObject(i++)));
			bean.setCreatedOn((Timestamp)ConvertObject2Timestamp(rs.getObject(i++)));
			return bean;
	}

	//------------------------------------------------------------------
	// PROTECTED getNextSequence
	//------------------------------------------------------------------
/*
	protected Long getNextSequence(Connection conn)
		throws Exception
	{
		//GET NEXT SEQ NUMBER
		String sql = "SELECT "+this.getTableName()+"_SEQ.NEXTVAL FROM DUAL";
		PreparedStatement statement = conn.prepareStatement(sql.toString());
		ResultSet rs = statement.executeQuery();
		rs.next();
		Long seq = (Long)ConvertObject2Long(rs.getObject(1));
		rs.close();
		statement.close();
		return seq;

	
		osmsDvPkControlManagerBase managerPkControl = new osmsDvPkControlManagerBase();
		osmsDvPkControlBean[] bean = managerPkControl.getByTableName("doi_profile",conn);
		Long seq= bean[0].getCurrPkValue();
		bean[0].setCurrPkValue(new Long(seq.intValue()+1));
		managerPkControl.update(bean[0],conn);

		return new Long(seq.intValue()+1);
		

	}
*/

	//------------------------------------------------------------------
	// PROTECTED executeQuery
	//------------------------------------------------------------------
	protected osmsDvDoiProfileBean[] executeQuery(PreparedStatement statement,Connection conn)
		throws Exception
	{
		ResultSet rs=null;
		osmsDvDoiProfileBean[] ret=null;
		try
		{
			rs = statement.executeQuery();

			Vector beans = new Vector();
			while (rs.next())
			{

				beans.addElement(mapResultSet2Bean(rs));
			}
			if(beans.size() == 0) return null;

			ret = new osmsDvDoiProfileBean[beans.size()];
			for(int i=0;i<beans.size();i++)
				ret[i]=(osmsDvDoiProfileBean)beans.elementAt(i);

		}
		finally
		{
			if(rs!=null)rs.close();
			if(statement!=null)statement.close();
		}
		return ret;
	}

	//------------------------------------------------------------------
	// PROTECTED SELECT
	//------------------------------------------------------------------
	protected osmsDvDoiProfileBean[] get(Connection conn,String postWhere)
		throws Exception
	{

		StringBuffer sql = new StringBuffer();
		sql.append("Select  ")
			.append(m_sqlResultSet)
			.append(" from  ")
			.append(getTableName())
			.append(" ");
		if(postWhere!=null && !postWhere.trim().equals(""))
			sql.append(" where ").append(postWhere);
/*		
		StringBuffer sql = new StringBuffer();
		sql.append("Select  ")
			.append(m_sqlResultSet)
			.append(" from ")
			.append(getTableName());
		if(postWhere!=null && !postWhere.trim().equals(""))
			sql.append(" where ").append(postWhere);
*/
		PreparedStatement statement = conn.prepareStatement(sql.toString());
		int i=1;

		return executeQuery(statement,conn);
	}


	protected osmsDvDoiProfileBean[] getByDoiProfileCode(String pDoiProfileCode,Connection conn,String postWhere)
		throws Exception
	{
		StringBuffer sql = new StringBuffer();
		sql.append("Select  ")
			.append(m_sqlResultSet)

			.append(" from  ")
			.append(getTableName())
			.append(" ")

			.append(" where ")
			.append(" DOI_PROFILE_CODE = ? ")

			.append(postWhere);

		PreparedStatement statement = conn.prepareStatement(sql.toString());
		int i=1;

		statement.setObject(i++,pDoiProfileCode);

		return executeQuery(statement,conn);
	}



	//------------------------------------------------------------------
	// SELECT
	//------------------------------------------------------------------
	public osmsDvDoiProfileBean[] get(Connection conn)
		throws Exception
	{
		return get( conn,"");
	}

	public osmsDvDoiProfileBean[] getByDoiProfileCode(String pDoiProfileCode,Connection conn)
		throws Exception
	{
		return getByDoiProfileCode( pDoiProfileCode,conn,"");
	}




	//------------------------------------------------------------------
	// UPDATE
	//------------------------------------------------------------------
	public synchronized void update(osmsDvDoiProfileBean bean,Connection conn)
		throws Exception
	{
		//TEMPORARY SOLUTION TO SET TIME STAMPS
		bean.setLastUpdatedOn(new Timestamp(new Date().getTime()));

		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE  ")
		.append(getTableName())
		.append(" SET ")
			
		.append("PROFILE_NAME = ? ")
			.append(", ").append("TABLE_NAME = ? ")
			.append(", ").append("DESC_SHORT = ? ")
			.append(", ").append("DESC_LONG = ? ")
			.append(", ").append("MAINTENANCE_FLAG = ? ")
			.append(", ").append("LAST_UPDATED_BY = ? ")
			.append(", ").append("LAST_UPDATED_ON = ? ")
			.append(", ").append("CREATED_BY = ? ")
			.append(", ").append("CREATED_ON = ? ")

		.append(" where ")
		.append("DOI_PROFILE_CODE = ? ")
		.append("");

		PreparedStatement statement = conn.prepareStatement(sql.toString());
		int i=1;

		if(bean.getProfileName()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getProfileName());
		if(bean.getTableName()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getTableName());
		if(bean.getDescShort()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getDescShort());
		if(bean.getDescLong()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getDescLong());
		if(bean.getMaintenanceFlag()==null)statement.setNull(i++,java.sql.Types.CHAR); else statement.setObject(i++,bean.getMaintenanceFlag());
		if(bean.getLastUpdatedBy()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getLastUpdatedBy());
		if(bean.getLastUpdatedOn()==null)statement.setNull(i++,java.sql.Types.TIMESTAMP); else statement.setObject(i++,bean.getLastUpdatedOn());
		if(bean.getCreatedBy()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getCreatedBy());
		if(bean.getCreatedOn()==null)statement.setNull(i++,java.sql.Types.TIMESTAMP); else statement.setObject(i++,bean.getCreatedOn());

		if(bean.getDoiProfileCode()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getDoiProfileCode());


		statement.executeUpdate();
		statement.close();
	}




	//------------------------------------------------------------------
	// INSERT
	//------------------------------------------------------------------
	public void insert(osmsDvDoiProfileBean bean,Connection conn)
		throws Exception
	{

		//TEMPORARY SOLUTION TO SET TIME STAMPS
		bean.setCreatedOn(new Timestamp(new Date().getTime()));
		bean.setLastUpdatedOn(new Timestamp(new Date().getTime()));
		


		StringBuffer sql = new StringBuffer();
		sql.append(" INSERT INTO  ")
		.append(getTableName())

		.append("(")
			.append(m_sqlResultSet)
		.append(")")
		.append(" VALUES ")
		.append("(")

		.append("?")
		.append(", ").append("?")
		.append(", ").append("?")
		.append(", ").append("?")
		.append(", ").append("?")
		.append(", ").append("?")
		.append(", ").append("?")
		.append(", ").append("?")
		.append(", ").append("?")
		.append(", ").append("?")

		.append(")");



		PreparedStatement statement = conn.prepareStatement(sql.toString());

		int i=1;
		if(bean.getDoiProfileCode()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getDoiProfileCode());
		if(bean.getProfileName()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getProfileName());
		if(bean.getTableName()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getTableName());
		if(bean.getDescShort()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getDescShort());
		if(bean.getDescLong()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getDescLong());
		if(bean.getMaintenanceFlag()==null)statement.setNull(i++,java.sql.Types.CHAR); else statement.setObject(i++,bean.getMaintenanceFlag());
		if(bean.getLastUpdatedBy()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getLastUpdatedBy());
		if(bean.getLastUpdatedOn()==null)statement.setNull(i++,java.sql.Types.TIMESTAMP); else statement.setObject(i++,bean.getLastUpdatedOn());
		if(bean.getCreatedBy()==null)statement.setNull(i++,java.sql.Types.VARCHAR); else statement.setObject(i++,bean.getCreatedBy());
		if(bean.getCreatedOn()==null)statement.setNull(i++,java.sql.Types.TIMESTAMP); else statement.setObject(i++,bean.getCreatedOn());

		statement.executeUpdate();
		statement.close();
	}

	//------------------------------------------------------------------
	// CONVERT OBJECT
	//------------------------------------------------------------------
	protected Long ConvertObject2Long(Object obj)
	{
		if (obj==null)return null;
		if(obj.getClass().getName().equals("java.lang.Long"))
		{
			return (Long)obj;
		}
		else
		{
			java.math.BigDecimal b=null;
			Long l=null;
			b = (java.math.BigDecimal)obj;
			if(b==null) l = null;
			else l = new Long(b.longValue());
			return l;
		}
	}
	protected String ConvertObject2String(Object obj)
	{
		return (String)obj;
	}
	protected Float ConvertObject2Float(Object obj)
	{
		if(obj==null) return null;
		else if(obj.getClass().getName().equals("java.lang.Float"))
		{
			return (Float)obj;
		}
		else
		{
			Float f=null;
			java.math.BigDecimal b=null;
			b = (java.math.BigDecimal)obj;
			if(b==null) f = null;
			else f = new Float(b.floatValue());
			return f;
		}
	}
	protected Double ConvertObject2Double(Object obj)
	{
		if(obj==null) return null;
		else if(obj.getClass().getName().equals("java.lang.Double"))
		{
			return (Double)obj;
		}
		else
		{
			Double d=null;
			java.math.BigDecimal b=null;
			b = (java.math.BigDecimal)obj;
			if(b==null) d = null;
			else d = new Double(b.doubleValue());
			return d;
		}
	}
	protected Timestamp ConvertObject2Timestamp(Object obj)
	{
		return (Timestamp)obj;
	}
	protected byte[] ConvertObject2byte(Object obj)
	{
		return (byte[])obj;
	}
	protected char[] ConvertObject2char(Object obj)
	{
		return (char[])obj;
	}
	protected Integer ConvertObject2Integer(Object obj)
	{
		if (obj==null)return null;
		if(obj.getClass().getName().equals("java.lang.Integer"))
		{
			return (Integer)obj;
		}
		else 
		{
			java.math.BigDecimal b=null;
			Integer l=null;
			b = (java.math.BigDecimal)obj;
			if(b==null) l = null;
			else l = new Integer(b.intValue());
			return l;
		}
	}
	protected Boolean ConvertObject2Boolean(Object obj)
	{
		return (Boolean)obj;
	}
	protected Short ConvertObject2Short(Object obj)
	{
		return (Short)obj;
	}
	protected Byte ConvertObject2Byte(Object obj)
	{
		return (Byte)obj;
	}
	protected Date ConvertObject2Date(Object obj)
	{
		return (Date)obj;
	}
	protected Time ConvertObject2Time(Object obj)
	{
		return (Time)obj;
	}
}
