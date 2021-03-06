
package se.anatom.ejbca.samples;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * Servlet to authenticate a user.
 * Simple database using a file to keep users in format:
 * instance;username;password;DN
 * DN is in form:
 * dn-c:dn-o:dn-ou:dn-ln:dn-gn:dn-cn
 * where parts can be left out as desired.
 *
 * Expects these parameters when called: (error 500 if any missing)
 * <ul>
 *  <li>user=&lt;username&gt;
 *  <li>password=&lt;password&gt;
 *  <li>version=&lt;major&gt;.&lt;minor&gt;
 * </ul>
 *
 * <p> Returns a logic token stating that user is authenticated
 * followed by the information to use for this user's certificate.
 *
 * @author Original code by Peter Neemeth
 * @version $Id: RemoteVerifyServlet.java,v 1.1 2006/06/09 17:09:07 danijel Exp $
 */
public class RemoteVerifyServlet extends HttpServlet {
    
    private static Category cat = Category.getInstance( RemoteVerifyServlet.class.getName() );
    
    /** Status code for successful communication */
    public static final String MSG_OK = "200 OK";
    /** Status code for failed communication */
    public static final String MSG_PROTOCOL_MISMATCH = "400 Wrong protocol version";
    /** Status code for generic error */
    public static final String MSG_GENERIC_ERROR = "500 ERROR (Missing parameter?) : ";
    
    /** Name of user id parameter */
    public static final String REQUEST_USERNAME = "username";
    /** Name of password parameter */
    public static final String REQUEST_PASSWORD = "password";
    /** Name of version parameter */
    public static final String REQUEST_VERSION = "version";
    
    /** Token for protocol */
    public static final String RESPONSE_END = "end";
    /** Token for protocol */
    public static final String RESPONSE_STATUS = "status";
    /** Token for protocol */
    public static final String RESPONSE_RESULT = "result";
    /** Token for protocol */
    public static final String RESPONSE_MESSAGE = "message";
    
    /** Status code for granting of certificate. */
    public static final String GRANT = "grant";
    /** Status code for rejecting certificate request. */
    public static final String REJECT = "reject";
    
    /** Version of the protocol used when communicating back to requestor */
    protected static final int PROTOCOL_VERSION_MAJOR = 1;
    /** Version of the protocol used when communicating back to requestor */
    protected static final int PROTOCOL_VERSION_MINOR = 0;
    
    /**
     * Basic structure containing users.
     * Top level keyed on instance gives new Hashtable
     * keyed on username with String[] = { password, result } as data.
     */
    protected static Hashtable users;
    
    /** Delimiter between parts in DN
     * <p>
     * Can be controlled via properties file.
     */
    protected static final String DNPART_DELIMITER = ":";
    /** Separator between name and value in DN name = value
     * <p>
     * Can be controlled via properties file.
     */
    protected static final String DNPART_NAME_VALUE_SEPARATOR = "=";
    /** For easy export from Excel and others.
     * <p>
     * Can be controlled via properties file.
     */
    protected static final String RECORD_SEPARATOR = ";";
    /** Ignored lines in DBUSER_file start with this character.
     * <p>
     * Can be controlled via properties file.
     */
    protected static String LINE_COMMENT = ";";
    /** What parameter to send when using GET to show status. */
    protected static String STATUS_KEY = "status";
    
    /** The OutputStream to send responses via. */
    protected ServletOutputStream _out;
    
    /** Count total accesses */
    protected static int countAccess = 0;
    /** Count granted accesses */
    protected static int countGranted = 0;
    /** Count rejected accesses */
    protected static int countRejected = 0;
    
    /**
     * Updates result with name-value-pairs extracted from dnPartsString
     * @param result where the result is stuffed
     * @param dnPartsString name-value-pairs separated by delimiter
     */
    void addUserDataToResult(AuthResult result, final String dnPartsString) {
        if (dnPartsString == null) {
            return;
        }
        Enumeration dnParts = new StringTokenizer(dnPartsString, DNPART_DELIMITER);
        while (dnParts.hasMoreElements()) {
            String dnPart = (String) dnParts.nextElement();
            int separatorPosition = dnPart.indexOf(DNPART_NAME_VALUE_SEPARATOR);
            String dnName = dnPart.substring(0, separatorPosition);
            String dnValue = dnPart.substring(separatorPosition+1); // skip separator
            result.add(dnName, dnValue);
            debugLog("addUserDataToResult: result=" + result);
        }
    }
    /**
     * Authenticate a user given a querystring.
     *
     * <b>This is the only method a customer should
     * have to rewrite/override.</b>
     *
     * @param username containing parsed username from requestor
     * @param password containing parsed password from requestor
     * @return status + certificate contents in an AuthResult
     */
    protected AuthResult authenticateUser(String username, String password) {
        AuthResult result = new AuthResult();
        String userData[] = findUserData(username);
        if (userData == null) {
            result.reject();
            result.setReason("Failed to authenticate credentials.");
            debugLog("authenticateUser: No such user. REJECTING");
        } else {
            debugLog("authenticateUser: Got userData for user '" + username + "'");
            
            if (password.equals(userData[0])) {
                debugLog("authenticateUser: Password matched. GRANTING");
                result.grant();
                addUserDataToResult(result, userData[1]);
            } else {
                debugLog("authenticateUser: Password missmatch. REJECTING");
                result.reject();
                result.setReason("Failed to authenticate credentials.");
            }
        }
        return result;
        
    }
    /**
     * Logs extensively to the log.
     * @param s What to log
     */
    protected void debugLog(final String s) {
        cat.debug( s );
    }
    
    /**
     * logs info.
     * @param s What to log
     */
    protected void infoLog(final String s) {
        cat.info( s );
    }
    
    /**
     * logs error
     * @param s What to log
     */
    protected void errorLog(final String s) {
        cat.error( s );
    }
    
    /**
     * logs error and stacktrace.
     * @param s What to log
     */
    protected void errorLog(final String s, java.lang.Exception e) {
        cat.error( s, e );
    }
    
    
    /**
     * Allows for checking status of.
     *
     * @param req javax.servlet.http.HttpServletRequest
     * @param resp javax.servlet.http.HttpServletResponse
     * @exception javax.servlet.ServletException The exception description.
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        ServletOutputStream out = res.getOutputStream();
        _out = out;
        // Keep this for logging.
        String remoteAddr = req.getRemoteAddr();
        
        // Extract information about request type and how we were called.
        // Also suitable for logging.
        String method = req.getMethod();
        String path = req.getServletPath();
        
        out.print("You called from " + remoteAddr);
        out.println(" using " + method + " as method.");
        
        try {
            Hashtable params = HttpUtils.parseQueryString(req.getQueryString());
            if (params.containsKey(STATUS_KEY)) {
                out.println("\n");
                out.println((new Date()).toString() + " RemoteVerify status: ");
                out.println("Accesses: " + countAccess);
                out.println("Granted: " + countGranted);
                out.println("Rejected: " + countRejected);
                if (users != null) {
                    out.println("Number of users in database: " + users.size());
                } else {
                    out.println("No users in database.");
                }
                out.println("\n");
                out.println("Protocol version: " + PROTOCOL_VERSION_MAJOR + "." + PROTOCOL_VERSION_MINOR);
                out.println("Database loaded from: " + getInitParameter("dbfilename"));
                out.println((new Date()).toString() + " DONE.");
            }
            
        } catch (IllegalArgumentException ignored) {
            out.println("Couldn't parse that request. Check parameters and try again.");
        }
        
        out.println("Request done.");
    }
    /**
     * Accepts requests and dispatches to authenticateUser in
     * this object.
     *
     * <p>
     * Returns one of the following cases.
     * (Apart from status being the first line, order is not specified.)
     *
     * <ul>
     *  <li>A granted reply:
     *   <pre>
     *   status=200 OK
     *   result=grant
     *   dn-ou=OU
     *   dn-o=O
     *   dn-cn=CN
     *   end
     *   </pre>
     *
     *  <li>A rejected reply:
     *   <pre>
     *   status=200 OK
     *   result=reject
     *   message=Wrong username/password.
     *   end
     *   </pre>
     *
     *  <li>A failed request:
     *   <pre>
     *   status=400
     *   message=Server can't handle given protocol version
     *   end
     *   </pre>
     * </ul>
     *
     * @exception ServletException when servlet mechanism fails
     * @exception IOException when something fails with
     *          basic I/O, such as reading/writing
     *          to client.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        increaseAccess();
        
        res.setContentType("text/plain");
        ServletOutputStream out = res.getOutputStream();
        //
        // Keep this for logging.
        String remoteAddr = req.getRemoteAddr();
        //
        // Extract information about request type and how we were called.
        // Also suitable for logging.
        String method = req.getMethod();
        String path = req.getServletPath();
        //
        // Extract the parameters passed to us using the utility
        // HttpUtils.parsePostData available in the servlet package.
        ServletInputStream in = req.getInputStream();
        int len = req.getContentLength();
        //
        // Will this work with len == -1 ?? (Unknown length)
        // Don't know, but -1 is possible only if we have a GET
        // and we KNOW this is a POST :-)
        Hashtable params = HttpUtils.parsePostData(len, in);
        try {
            // Extract parameters from client
            String username = "";
            String password = "";
            String version = "";
            try {
                username = ((String[]) params.get(REQUEST_USERNAME))[0];
                password = ((String[]) params.get(REQUEST_PASSWORD))[0];
                version = ((String[]) params.get(REQUEST_VERSION))[0];
            } catch (ArrayIndexOutOfBoundsException ignored) {
                // No parameters will result in "" being used from
                // step above
            } catch (NullPointerException ignoredAsWell) {
                // No parameters will result in "" being used from
                // step above
            }
            
            //
            // Extract and verify protocol version
            int majorversion = 0;
            int minorversion = 0;
            
            // Split version on '.'
            int dotAt = version.indexOf('.');
            if (dotAt == -1) {
                // No separator entered, assume minor == 0
                try {
                    majorversion = Integer.parseInt(version);
                } catch (NumberFormatException nfe) {
                    errorLog("doPost: Got " + nfe + " on call from " + remoteAddr
                    + " for username '" + username + "'. Asuming version is OK. Tried to parse '" + version + "'");
                }
                minorversion = 0;
            } else {
                try {
                    majorversion = Integer.parseInt(version.substring(0, dotAt));
                    minorversion = Integer.parseInt(version.substring(1 + dotAt, version.length()));
                } catch (NumberFormatException nfe) {
                    errorLog("doPost: Got " + nfe + " on call from " + remoteAddr
                    + " for username '" + username + "'. Asuming version is OK. Tried to parse '" + version + "'");
                }
            }
            
            //
            // Now let's make sure we can play this tune
            if (majorversion == PROTOCOL_VERSION_MAJOR && minorversion <= PROTOCOL_VERSION_MINOR) {
                // We're in business, protocol matches
                
                // This is the call to what the customer usually
                // needs to care about.
                // The call itself seldom needs to be changed...
                //
                // You should, of course, make sure that you like
                // the given code, as it's only an example!
                AuthResult result = authenticateUser(username, password);
                //
                // Now build the result we'll send to the client
                
                // We treat grant and rejects slightly different
                if (result.granted()) {
                    increaseGranted();
                    out.println(RESPONSE_STATUS + "=" + MSG_OK);
                    out.println(RESPONSE_RESULT + "=" + GRANT);
                    debugLog("GRANTING request for '" + username + "'");
                    
                    // loop over all elements in resultHash, print one by one
                    Hashtable resultParams = result.getResult();
                    String key;
                    // Standard code for printing a Hash.
                    for (Enumeration keys = resultParams.keys(); keys.hasMoreElements();) {
                        key = (String) keys.nextElement();
                        out.println(key + "=" + ((String) resultParams.get(key)));
                    }
                } else { // rejected.
                    increaseRejected();
                    out.println(RESPONSE_STATUS + "=" + MSG_OK);
                    out.println(RESPONSE_RESULT + "=" + REJECT);
                    out.println(RESPONSE_MESSAGE + "=" + result.getReason());
                    debugLog("REJECTING request for '" + username + "'. Reason: " + result.getReason());
                }
                out.println(RESPONSE_END); // The end of response token
            } else {
                // protocol missmatch, reject and return
                out.println(RESPONSE_STATUS + "=" + MSG_PROTOCOL_MISMATCH);
                out.println("message=Accepting at most " + PROTOCOL_VERSION_MAJOR + "." + PROTOCOL_VERSION_MINOR);
                errorLog("PROTOCOL MISSMATCH. Got '" + version + "', but accepts only '"
                + PROTOCOL_VERSION_MAJOR + "." + PROTOCOL_VERSION_MINOR + "'");
            }
        } catch (Exception e) {
            out.println(RESPONSE_STATUS + "=" + MSG_GENERIC_ERROR + e);
            out.println(RESPONSE_END); // The end of response token
            errorLog("?Caught exception ", e);
        }
    }
    /**
     * Gets information for a user.
     * @param username user to lookup.
     * @return <b>null</b> (if no user found) or String[] with
     *         [0] as passwd and [1] as certificate contents.
     */
    protected String[] findUserData(String username) {
        if (users == null) {
            debugLog("findUserData: No users found. Returning null for user '" + username + "'.");
            return null;
        }
        
        String result[] = (String[]) users.get(username.toLowerCase());
        if (result != null) {
            debugLog("findUserData: Information for user '" + username + "'found.");
        } else {
            debugLog("findUserData: No information for user '" + username + "'found.");
        }
        return result;
    }
    
    protected synchronized void increaseAccess() {
        countAccess++;
    }
    protected synchronized void increaseGranted() {
        countGranted++;
    }
    protected synchronized void increaseRejected() {
        countRejected++;
    }
    /**
     * Loads userdatabase at first access.
     *
     * @exception javax.servlet.ServletException The exception description.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        cat = Category.getInstance( this.getClass().getName() );
        
        debugLog((new Date()).toString() + " RemoteVerify.init:");
        loadUserDB();
    }
    /**
     * Load user DB at servlet load time, ie first access to servlet.
     * It's ok to call this method multiple times, since it simply
     * clears the old cached data each time it's called.
     */
    protected synchronized void loadUserDB() {
        // First we clear cached users.
        Hashtable oldEnUsers = users;
        users = null;
        BufferedReader in = null;
        debugLog((new Date()).toString() + "loadUserDB: Loading from file: '" + getInitParameter("dbfilename") + "'.");
        InputStream is = getServletContext().getResourceAsStream(
        getInitParameter("dbfilename"));
        in = new BufferedReader(new InputStreamReader(is));
        String line;
        boolean readMore = true;
        try {
            while (readMore) {
                line = in.readLine();
                if (line == null) {
                    readMore = false;
                } else {
                    if (!line.startsWith(LINE_COMMENT)) {
                        Enumeration lineParts = new StringTokenizer(line, RECORD_SEPARATOR);
                        String username = (String) lineParts.nextElement();
                        debugLog("loadUserDB: username=" + username);
                        String password = (String) lineParts.nextElement();
                        debugLog("loadUserDB: password=" + password);
                        String userDataString = (String) lineParts.nextElement();
                        debugLog("loadUserDB: userDataString=" + userDataString);
                        StringTokenizer st = new StringTokenizer(userDataString, DNPART_DELIMITER);
                        debugLog("loadUserDB: st=" + st);
                        String userData[] = new String[2];
                        userData[0] = password;
                        userData[1] = userDataString;
                        debugLog("loadUserDB: calling addUserData." + userData);
                        addUserData(username, userData);
                    } else {
                        debugLog("loadUserDB: skipping comment line." + line);
                    }
                }
            }
        } catch (IOException ioe) {
            errorLog("loadUserDB: FAILED TO PARSE FILE: '" + getInitParameter("dbfilename") + "'.");
            errorLog("loadUserDB: Got exception: ", ioe);
            errorLog("loadUserDB: Restored previous version of DB");
            users = oldEnUsers;
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {}
        }
        debugLog((new Date()).toString() + "loadUserDB: Done.");
    }
    
    /**
     * Adds information for a user in an instance to users.
     * @param username user to lookup.
     * @param userData String[] with
     *        [0] as passwd and [1] as certificate contents.
     */
    protected void addUserData(String username, String[] userData) {
        if (users == null) {
            debugLog("addUserData: Creating new users.");
            users = new Hashtable();
        }
        debugLog("addUserData: Adding '" + username);
        
        users.put(username.toLowerCase(), userData);
    }
    
} // RemoteVerifyServlet
