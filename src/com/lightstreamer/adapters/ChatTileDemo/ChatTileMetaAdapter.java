/*
 *
 *  Copyright 2013 Weswit s.r.l.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.lightstreamer.adapters.ChatTileDemo;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import ua_parser.Client;
import ua_parser.Parser;

import com.lightstreamer.adapters.metadata.LiteralBasedProvider;
import com.lightstreamer.interfaces.data.SubscriptionException;
import com.lightstreamer.interfaces.metadata.CreditsException;
import com.lightstreamer.interfaces.metadata.NotificationException;
import com.lightstreamer.interfaces.metadata.TableInfo;

public class ChatTileMetaAdapter extends LiteralBasedProvider {

    public static final String ROOM_DEMO_LOGGER_NAME = "LS_demos_Logger.ChatTileDemo";
    public static final String TRACER_LOGGER = "LS_ChatTileDemo_Logger.tracer";

    private static final String FIELD_USER_AGENT = "USER_AGENT";
    private static final String FIELD_REMOTE_IP = "REMOTE_IP";

    private static final String CFG_PARAM_ADAPTER_SET_ID = "adapters_conf.id";

    private static Logger logger;

    /**
     * Private logger; a specific "LS_RoomDemo_Logger" category
     * should be supplied by logback configuration.
     */
    public static Logger tracer = null;

    /**
     * Keeps the client context informations supplied by Lightstreamer on the
     * new session notifications.
     * Session information is needed to pass the IP to logging purpose.
     * Map of sessionInfo indexed by sessionID.
     */
    private final ConcurrentHashMap<String,Map<String,String>> sessions =
            new ConcurrentHashMap<String,Map<String,String>>();

    /**
     * map of player's nicknames indexed by session id
     * There can be only one player per session
     */
    private final ConcurrentHashMap<String, String> nicksns =
            new ConcurrentHashMap<String, String>();

    /**
     * map of player's user agent indexed by session id
     */
    private final ConcurrentHashMap<String, String> usrAgnts =
            new ConcurrentHashMap<String, String>();

    /**
     * Unique identification of the Adapter Set. It is used to uniquely
     * identify the related Data Adapter instance;
     * see feedMap on ChatDataAdapter.
     */
    private String adapterSetId;

    // Public Methods ----------------------------------------------------------

    @SuppressWarnings("rawtypes")
    @Override
    public void init(Map params, File configDir) {

        logger = Logger.getLogger(ROOM_DEMO_LOGGER_NAME);

        try{
            tracer = Logger.getLogger(TRACER_LOGGER);
            tracer.info(TRACER_LOGGER + " start.");

        } catch (Exception e) {
            logger.warn("Error on tracer logger initialization.",  e);
        }

        // Read the Adapter Set name, which is supplied by the Server as a parameter
        this.adapterSetId = (String) params.get(CFG_PARAM_ADAPTER_SET_ID);
        logger.info("Adapter Set: " + this.adapterSetId);
    }

    @Override
    public boolean wantsTablesNotification(java.lang.String user) {
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked" })
    @Override
    public void notifyNewSession(String user, String sessionID, Map sessionInfo) throws CreditsException, NotificationException {
        addUserAgent(sessionID, sessionInfo);
        sessions.put(sessionID, sessionInfo);
    }

    @Override
    public void notifySessionClose(String sessionID) throws NotificationException {
        sessions.remove(sessionID);
        usrAgnts.remove(sessionID);

        String deadmanwalking = nicksns.get(sessionID);
        if (deadmanwalking != null) {
            try {
                getDataAdapter().removePlayer(deadmanwalking);
            } catch (ChatTileException e) {
                logger.warn(e);
            }
        }

        nicksns.remove(sessionID);
    }

    @Override
    public void notifyTablesClose(java.lang.String sessionID, TableInfo[] tables) {
    }

    @Override
    public void notifyNewTables(String user, String sessionID, TableInfo[] tables) throws CreditsException {
    }

    @Override
    public void notifyUserMessage(String user, String sessionID, String message)
            throws CreditsException {

        if (message == null) {
            return ;
        }

        if (message.startsWith("n|") ) {
            String res = notifyNewPlayer(sessionID, removeTypeFrom(message));
            if (!res.equalsIgnoreCase("")) {
                throw new CreditsException(-2720, res);
            }
        } else if ( message.startsWith("m|") ) {
            notifyChatMessage(sessionID, removeTypeFrom(message));
        } else {
            logger.debug("Unknown message received: "+ message);
        }
    }

    // Protected Methods -------------------------------------------------------

    /**
     *
     * @param sessionID
     * @param message
     * @return An empty string if the nickmame has been accepted, or the
     * actual name if it has been changed.
     * @throws CreditsException
     */
    protected String notifyNewPlayer(String sessionID, String message) throws CreditsException {

        try {

            final String nickname = getNickNameFrom(message);

            if (isNumber(nickname)) {
                throw new CreditsException(-6, "I'm not a number! I'm a free man!");
            }

            String ip = getIp(sessionID, sessions);
            if (ip.isEmpty()) {
                logger.warn("New player '" + nickname + "' message received from non-existent session '" + sessionID + "'.");
            } else {
                tracer.info("New player '" + nickname + "' from ip " + ip );
            }

            if (nicksns.containsKey(sessionID)) {
                // duplicated message; it should be avoided when possible
                String currentName = nicksns.get(sessionID);

                if ( currentName.equalsIgnoreCase(nickname)) {
                    return "";
                    // returning the nickname instead of the empty string brings
                    // back to the case where the name has been changed, causing
                    // the client to receive a non blocking error.
                } else {
                    return currentName;
                }
            }

            String userAgent = ( usrAgnts.get(sessionID) != null ? usrAgnts.get(sessionID) : "undetected");

            String actualName = getDataAdapter().addPlayer(nickname, userAgent);

            nicksns.put(sessionID, actualName);

            if ( actualName.equalsIgnoreCase(nickname)) {
                actualName = "";
            }
            return actualName;

        } catch (SubscriptionException e) {
            logger.warn("Room overcrowed.", e);
            throw new CreditsException(-2700, "Too many users. Please try again.");

        } catch (ChatTileException e) {
            logger.warn("Adapter not initialized.", e);
            throw new CreditsException(-2710, "Error logging in.");
        }
    }

    protected void notifyChatMessage(String sessionID, String message) throws CreditsException {
        String playerName = nicksns.get(sessionID);
        if (playerName == null) {
            // the message might have come too early; we cannot fulfill it
            logger.warn("Received chat message from incomplete player (ip: " + getIp(sessionID, sessions) + ").");
            return;
        }
        tracer.info("Chat Message from '" + playerName + "' (ip: " + getIp(sessionID, sessions) + "), session '"+sessionID+"': " + message);

        try {
            getDataAdapter().updatePlayerMsg(playerName, message);
        } catch (ChatTileException e) {
            logger.warn("Unexpected error handling message from user '"+playerName+"', session '"+sessionID+"'.", e);
        }
    }

    // Private Methods ---------------------------------------------------------

    @SuppressWarnings("rawtypes")
    private void addUserAgent(String sessionID, Map sessionInfo) {
        try {
            String ua = (String) sessionInfo.get(FIELD_USER_AGENT);
            if (ua == null) {
                return;
            }

            logger.info("Usr Agent: " + ua);

            Client c = new Parser().parse(ua);
            String userAgent;
            if ( c.userAgent.family.equals("Android") ) {
                userAgent = c.userAgent.family + " Browser on " + c.os.family;
            } else {
                userAgent = c.userAgent.family + " on " + c.os.family;
            }
            if ( userAgent.length() > 140 ) {
                userAgent = userAgent.substring(0,140);
            }

            usrAgnts.put(sessionID, userAgent);
            logger.info("Saved: " + userAgent + ", for " + sessionID);

        } catch (IOException ioe) {
            logger.warn("Unable to retrieve user agent for sesion '" + sessionID + "'");
        }
    }

    private String removeTypeFrom(String message) {
        String newMsg;
        try {
            newMsg = message.split("\\|")[1];
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            // let's assume that the message ends with | to carry an empty value
            newMsg = "";
        }
        return newMsg;
    }

    private boolean isNumber(String str) {
        if (str.charAt(0) == '-') {
          str = str.substring(1);
        }

        int i=0;
        while (i<str.length() && Character.isDigit(str.charAt(i))) {
          i++;
        }
        return i == str.length();
    }

    /**
     * Retrieve the data adapter
     * @return
     * @throws CreditsException
     */
    private ChatTileAdapter getDataAdapter() throws ChatTileException {

        // Get the DataAdapter instance to bind it with this
        // Metadata Adapter and send messages through it
        ChatTileAdapter dataAdapter =  ChatTileAdapter.getDataAdapter(adapterSetId);

         if (dataAdapter == null) {
             // The feed is not yet available on the static map, maybe the
             // Data Adapter was not included in the Adapter Set
             logger.error("DataAdapter not found");
             throw new ChatTileException("No data adapter available");
         }
         return dataAdapter;
    }

    private String getIp(String sessionID, Map<String,Map<String,String>> sessions) {
        String ip = "";

        Map<String,String> sessionInfo = sessions.get(sessionID);
        if (sessionInfo == null) {
            logger.warn("Unable to retrieve IP: session '" + sessionID + "' does not exist!");
        } else {
            ip =  sessionInfo.get(FIELD_REMOTE_IP);
        }
        return ip;
    }

    private String getNickNameFrom(String message) {
        final String nickname = message;
        return nickname;
    }

}
