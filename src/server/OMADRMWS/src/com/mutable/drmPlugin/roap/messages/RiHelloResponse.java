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
 * The Initial Developer of the Original Code is Mutable, Inc.
 * Portions created by Mutable, Inc. are
 * Copyright (C) Mutable, Inc. 2002-2006.  All Rights Reserved.
 * 
 *
 */
package com.mutable.drmPlugin.roap.messages;

import com.mutable.drmPlugin.roap.messages.status.Status;

/**
 * The common content of all
 * RiHelloResponse server message of the
 * 4-pass Roap registration protocol
 * 
 * 
 *
 */


public abstract class RiHelloResponse extends RoapResponse {

	protected RiHelloResponse(Status status) {
		super(status);
	}

	/*
	 * partial implementation of XmlDocument
	 * 
	 */
	public String documentName() { return "RIHello"; }

}
