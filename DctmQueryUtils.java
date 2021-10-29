package com.wfc.rima.dctm.utils.query;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.HashSet;

import java.util.Iterator;

import java.util.List;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import com.documentum.fc.client.DfQuery;

import com.documentum.fc.client.IDfCollection;

import com.documentum.fc.client.IDfQuery;

import com.documentum.fc.client.IDfSession;

import com.documentum.fc.common.DfException;

import com.documentum.fc.common.IDfId;

import com.wfc.rima.dctm.common.constants.IDctmConstants;

import com.wfc.rima.dctm.common.constants.IUtilConstants;

/**
 ******************************************************************************
 * 
 * 
 * @Project RIMA
 * 
 * @File DctmQueryUtils.java
 * 
 * @Description Utility for executing the dql
 * 
 * @Created on Apr 3, 2013
 * 
 * @Author U234456
 * 
 * @Version 1.0
 ******************************************************************************
 * 
 * 
 * @Modified By
 * 
 * @Modified on
 * 
 * @Description
 ******************************************************************************
 * 
 * 
 */

public class DctmQueryUtils {

	private final static Logger log = Logger.getLogger(DctmQueryUtils.class);

	/**
	 * 
	 * Executes the dql and returns only a single attribute value based on the
	 * condition passed and the type
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param returnAttributeValue
	 * 
	 * @param conditionAttribute
	 * 
	 * @param conditionAttributeValue
	 * 
	 * @param type
	 * 
	 * @return result
	 * 
	 * @throws DfException
	 * 
	 */

	public static String getAttributeValue(IDfSession session, QueryInfoPojo queryInfo) throws DfException {

		return getAttributeValue(session, queryInfo.getType(), queryInfo.getReturnAttributeValue(),
				queryInfo.getConditionAttributeName(), queryInfo.getConditionAttributeValue());

	}

	/**
	 * 
	 * Executes the dql and returns only a single attribute value based on the
	 * condition passed and the type
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param returnAttributeValue
	 * 
	 * @param conditionAttribute
	 * 
	 * @param conditionAttributeValue
	 * 
	 * @param type
	 * 
	 * @return result
	 * 
	 * @throws DfException
	 * 
	 */

	public static String getAttributeValue(IDfSession session, String type, String returnAttributeName,
			String conditionAttributeName, String conditionAttributeValue) throws DfException {

		String query = IUtilConstants.QRY_SINGLE_VALUE.replace("<attr_name>", returnAttributeName)
				.replace("<type>", type).replace("<condition_attribue>", conditionAttributeName)
				.replace("<condition_value>", conditionAttributeValue);

		String result = null;

		IDfCollection collection = null;

		try {

			collection = executeQuery(session, query, IDfQuery.DF_READ_QUERY);

			if (collection.next()) {

				result = collection.getString(returnAttributeName);

			}

		} catch (DfException dfe) {

			log.error("Error while fetching the result", dfe);

			throw dfe;

		} finally {

			closeCollection(collection);

		}

		return result;

	}

	/**
	 * 
	 * Executes the dql and returns only a single attribute value based on the
	 * condition passed and the type
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param returnAttributeValue
	 * 
	 * @param conditionAttribute
	 * 
	 * @param conditionAttributeValue
	 * 
	 * @param type
	 * 
	 * @return result
	 * 
	 * @throws DfException
	 * 
	 */

	public static String getAttributeValueConditionBased(IDfSession session, QueryInfoPojo queryInfo)
			throws DfException {

		IDfCollection collection = null;

		String result = null;

		String query = IUtilConstants.QRY_SINGLE_VALUE_CONDITION
				.replace("<attr_name>", queryInfo.getReturnAttributeValue())
				.replace("<condition>", queryInfo.getCondition()).replace("<type>", queryInfo.getType());

		try {

			collection = executeQuery(session, query, IDfQuery.DF_READ_QUERY);

			if (collection.next()) {

				result = collection.getString(queryInfo.getReturnAttributeValue());

			}

		} catch (DfException dfe) {

			log.error("Error while fetching the result", dfe);

			throw dfe;

		} finally {

			closeCollection(collection);

		}

		return result;

	}

	/**
	 * 
	 * Executes the query and returns the collection
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param dql
	 * 
	 * @return
	 * 
	 * @throws DfException
	 * 
	 */

	public static IDfCollection executeQuery(IDfSession session, String dql) throws DfException {

		IDfCollection collection = null;

		try {

			log.debug("The query is " + dql);

			collection = executeQuery(session, dql, IDfQuery.DF_EXEC_QUERY);

		} catch (DfException dfe) {

			log.error("Error while fetching the result", dfe);

			throw dfe;

		}

		return collection;

	}

	/**
	 * 
	 * @param session
	 * 
	 * @param dql
	 * 
	 * @return
	 * 
	 * @throws Exception
	 * 
	 */

	public static int insertRows(IDfSession session, String dql) throws Exception {

		int rowsInserted = 0;

		IDfCollection collection = null;

		try {

			log.debug("The query is " + dql);

			collection = executeQuery(session, dql, IDfQuery.DF_EXEC_QUERY);

			if (collection.next()) {

				rowsInserted = collection.getInt(IDctmConstants.ROWS_INSERTED);

			}

			log.debug("The no of rows inserted are " + rowsInserted);

		} catch (DfException dfe) {

			log.error("Error while fetching the result", dfe);

			throw dfe;

		} finally {

			closeCollection(collection);

		}

		return rowsInserted;

	}

	/**
	 * 
	 * Executes the query and returns the collection
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param dql
	 * 
	 * @param queryExecType
	 * 
	 * @return IDfCollection
	 * 
	 * @throws DfException
	 * 
	 */

	public static IDfCollection executeQuery(IDfSession session, String dql, int queryExecType) throws DfException {

		IDfCollection collection = null;

		IDfQuery query = null;

		try {

			query = new DfQuery();

			log.debug("The query is " + dql);

			query.setDQL(dql);

			collection = query.execute(session, queryExecType);

		} catch (DfException dfe) {

			log.error("Error while executing the query ", dfe);

			throw dfe;

		}

		return collection;

	}

	/**
	 * 
	 * Checks if the record exists in the collection after the passed dql was
	 * executed
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param dql
	 * 
	 * @return
	 * 
	 * @throws DfException
	 * 
	 */

	public static boolean isRecordExists(IDfSession session, String dql) throws DfException {

		IDfCollection collection = null;

		boolean exists = false;

		try {

			collection = executeQuery(session, dql, IDfQuery.DF_READ_QUERY);

			if (collection.next()) {

				exists = true;

			}

		} catch (DfException dfe) {

			log.error("Error while executing the query ", dfe);

			throw dfe;

		} finally {

			closeCollection(collection);

		}

		return exists;

	}

	/**
	 * 
	 * Checks if the record exists in the collection after the passed dql was
	 * executed
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param dql
	 * 
	 * @return
	 * 
	 * @throws DfException
	 * 
	 */

	public static String getSingleAttrValue(IDfSession session, String dql, String attrName) throws DfException {

		IDfCollection collection = null;

		String attrValue = null;

		try {

			collection = executeQuery(session, dql, IDfQuery.DF_READ_QUERY);

			if (collection.next()) {

				attrValue = collection.getString(attrName);

			}

		} catch (DfException dfe) {

			log.error("Error while executing the query ", dfe);

			throw dfe;

		} finally {

			closeCollection(collection);

		}

		return attrValue;

	}

	/**
	 * 
	 * Closes the collection
	 *
	 * 
	 * 
	 * @param collection
	 * 
	 * @throws DfException
	 * 
	 */

	public static void closeCollection(IDfCollection collection) {

		if (collection != null && collection.getState() != IDfCollection.DF_CLOSED_STATE) {

			try {

				collection.close();

			} catch (DfException dfe) {

				log.warn("Error while closing the collection ", dfe);

			}

		}

	}

	/**
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param dql
	 * 
	 * @param attrName
	 * 
	 * @return Returns the list of items
	 * 
	 * @throws DfException
	 * 
	 */

	public static List<String> getResultsList(IDfSession session, String dql, String attrName) throws DfException {

		List<String> results = null;

		IDfCollection collection = null;

		try {

			results = new ArrayList<String>();

			collection = executeQuery(session, dql);

			while (collection.next()) {

				results.add(collection.getString(attrName));

			}

		} finally {

			closeCollection(collection);

		}

		return results;

	}

// DO NOT USE the method below

	public static List<HashMap<String, String>> getResultsList(IDfSession session, String dql, HashSet<String> attrs)
			throws DfException {

		List<HashMap<String, String>> results = null;

		IDfCollection collection = null;

		HashMap<String, String> map = null;

		String key = null;

		String val = null;

		try {

			results = new ArrayList<HashMap<String, String>>();

// log.debug("The query is " + dql);

			collection = executeQuery(session, dql);

			while (collection.next()) {

// results.add(collection.getString(attrName));

				map = new HashMap<String, String>();

// iterate through attrs

				Iterator<String> it = attrs.iterator();

				while (it.hasNext()) {

					key = it.next();

					try {

						val = collection.getString(key);

					} catch (DfException e) {

						val = collection.getAllRepeatingStrings(key, "|");

					}

					map.put(key, val);

				}

				results.add(map);

			}

		} finally {

			closeCollection(collection);

		}

		return results;

	}

	/**
	 * 
	 * Executes the quyery and returns the map values
	 * 
	 * @param session
	 * 
	 * @param dql
	 * 
	 * @param keyAttributeName
	 * 
	 * @param attributeValue
	 * 
	 * @return Map of String attribute name and string values
	 * 
	 * @throws Exception
	 * 
	 */

	public static Map<String, String> getMapValues(IDfSession session, String dql, String keyAttributeName,
			String attributeValue) throws Exception {

		Map<String, String> resultMap = null;

		IDfCollection collection = null;

		try {

			collection = executeQuery(session, dql, IDfQuery.DF_READ_QUERY);

			resultMap = new HashMap<String, String>();

			while (collection.next()) {

				resultMap.put(collection.getString(keyAttributeName), collection.getString(attributeValue));

			}

		} catch (Exception e) {

			log.error("Error while fetching the Map values ", e);

			throw e;

		} finally {

			DctmQueryUtils.closeCollection(collection);

		}

		return resultMap;

	}

	/**
	 * 
	 * Gets the total number of count
	 * 
	 * dqlQualification - Ex dm_folder where object_name='XYZ'
	 * 
	 * @param session
	 * 
	 * @param dqlQualification
	 * 
	 * @return
	 * 
	 * @throws Exception
	 * 
	 */

	public static int getRecordsCount(IDfSession session, String dqlQualification) throws Exception {

		String dql = "Select count(*) as \"cnt\" from " + dqlQualification;

		IDfCollection collection = null;

		int count = 0;

		try {

			collection = executeQuery(session, dql, IDfQuery.DF_READ_QUERY);

			if (collection.next()) {

				count = collection.getInt("cnt");

			}

		} catch (Exception e) {

			log.error("Error while fetching the records count {}", e);

			throw e;

		} finally {

			DctmQueryUtils.closeCollection(collection);

		}

		return count;

	}

	/**
	 * 
	 * Converts the list ids in 'val1','val2' format
	 *
	 * 
	 * 
	 * @param ids
	 * 
	 * @return
	 * 
	 */

	public static String covertToQuerIds(List<String> ids) {

		return IUtilConstants.SINGLE_QUOTE + StringUtils.join(ids, IUtilConstants.QUERY_SEPERATOR_QUOTE)
				+ IUtilConstants.SINGLE_QUOTE;

	}

	/**
	 * 
	 * Converts the list ids in 'val1','val2' format
	 *
	 * 
	 * 
	 * @param ids
	 * 
	 * @return
	 * 
	 */

	public static String covertToQuerIds(String[] ids) {

		return IUtilConstants.SINGLE_QUOTE + StringUtils.join(ids, IUtilConstants.QUERY_SEPERATOR_QUOTE)
				+ IUtilConstants.SINGLE_QUOTE;

	}

	/**
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param dql
	 * 
	 * @param attrName
	 * 
	 * @return Returns the list of IDfId
	 * 
	 * @throws DfException
	 * 
	 */

	public static List<IDfId> getResultsIDList(IDfSession session, String dql, String attrName) throws DfException {

		List<IDfId> results = null;

		IDfCollection collection = null;

		try {

			results = new ArrayList<IDfId>();

			collection = DctmQueryUtils.executeQuery(session, dql);

			while (collection.next()) {

				results.add(collection.getId(attrName));

			}

		} finally {

			DctmQueryUtils.closeCollection(collection);

		}

		return results;

	}

	/**
	 *
	 * 
	 * 
	 * @param session
	 * 
	 * @param dql
	 * 
	 * @param attrName
	 * 
	 * @return Returns the IDfId
	 * 
	 * @throws DfException
	 * 
	 */

	public static IDfId getResultsID(IDfSession session, String dql, String attrName) throws DfException {

		IDfId results = null;

		IDfCollection collection = null;

		try {

			collection = DctmQueryUtils.executeQuery(session, dql);

			while (collection.next()) {

				results = collection.getId(attrName);

			}

		} finally {

			DctmQueryUtils.closeCollection(collection);

		}

		return results;

	}

}
