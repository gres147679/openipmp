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

package com.mutable.osms.server.ws.message;

public class OsmsGetContentKeyResponse implements java.io.Serializable {
	private String keyXml = null;
	private String auxDataXml = null;
	
	public OsmsGetContentKeyResponse(String xml, String xml2) {
		auxDataXml = xml;
		keyXml = xml2;
	}

	public OsmsGetContentKeyResponse() {
	}

	public String getAuxDataXml() {
		return auxDataXml;
	}

	public void setAuxDataXml(String auxDataXml) {
		this.auxDataXml = auxDataXml;
	}

	public String getKeyXml() {
		return keyXml;
	}

	public void setKeyXml(String keyXml) {
		this.keyXml = keyXml;
	}
}
