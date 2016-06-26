/*
 * Copyright 1999-2000,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * $Log: PTXDefs.hpp,v $
 * Revision 1.1  2005/07/26 02:26:42  craigm
 * Added for xerces 2.3 support (for Mac port)
 *
 * Revision 1.2  2004/09/08 13:56:42  peiyongz
 * Apache License Version 2.0
 *
 * Revision 1.1.1.1  2002/02/01 22:22:25  peiyongz
 * sane_include
 *
 * Revision 1.2  2000/04/04 21:04:25  abagchi
 * Fixed copyrights with initial checkin
 *
 */

// ---------------------------------------------------------------------------
//  Dynix/PTX runs in little endian mode
// ---------------------------------------------------------------------------
#define ENDIANMODE_LITTLE
typedef void* FileHandle;

#ifndef PTX
#define PTX
#endif
