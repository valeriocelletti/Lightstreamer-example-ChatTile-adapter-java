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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.lightstreamer.interfaces.data.DataProviderException;
import com.lightstreamer.interfaces.data.FailureException;
import com.lightstreamer.interfaces.data.ItemEventListener;
import com.lightstreamer.interfaces.data.SmartDataProvider;
import com.lightstreamer.interfaces.data.SubscriptionException;

public class ChatTileAdapter implements SmartDataProvider {

    private static final String CFG_PARAM_ADAPTER_SET_ID = "adapters_conf.id";

    private static final String ITEM_NAME_PLAYERS_LIST = "Players_list";

    private static final String FIELD_KEY = "key";
    private static final String FIELD_COMMAND = "command";
    private static final String FIELD_USR_AGNT = "usrAgnt";
    private static final String FIELD_MSG = "msg";

    private static final String CMD_ADD = "ADD";
    private static final String CMD_UPDATE = "UPDATE";
    private static final String CMD_DELETE = "DELETE";

    /**
     * A static map, to be used by the Metadata Adapter to find the data
     * adapter instance; this allows the Metadata Adapter to forward client
     * messages to the adapter.
     * The map allows multiple instances of this Data Adapter to be included
     * in different Adapter Sets. Each instance is identified with the name
     * of the related Adapter Set; defining multiple instances in the same
     * Adapter Set is not allowed.
     */
    public static final ConcurrentHashMap<String, ChatTileAdapter> feedMap =
        new ConcurrentHashMap<String, ChatTileAdapter>();

    private final Logger logger;

    /**
     * should be supplied by logback configuration.
     */
    private static Logger tracer;

    /**
     * The listener of updates set by Lightstreamer Kernel.
     */
    private static ItemEventListener listener = null;

    private boolean playerListSubscribed = false;

    /**
     * The World where the players live
     */
    private Room room = null;

    // Constructor -------------------------------------------------------------

    public ChatTileAdapter() {
        logger = Logger.getLogger(ChatTileMetaAdapter.ROOM_DEMO_LOGGER_NAME);
        tracer = Logger.getLogger(ChatTileMetaAdapter.TRACER_LOGGER);
    }

    // Methods -----------------------------------------------------------------

    public static ChatTileAdapter getDataAdapter(String adapterSetId)   {
        return feedMap.get(adapterSetId);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void init(Map params, File configDir) throws DataProviderException {

        try {
            tracer.info("LS_RoomDemo_Logger start.");

        } catch (Exception e) {
            System.out.println("Loggers failed to load: " + e);
        }

        // Read the Adapter Set name, which is supplied by the Server as a parameter
        String adapterSetId = (String) params.get(CFG_PARAM_ADAPTER_SET_ID);

        // Put a reference to this instance on a static map
        // to be read by the Metadata Adapter
        feedMap.put(adapterSetId, this);

        room = new ChatTileRoom(this);

        logger.info("RoomAdapter start!");
    }

    @Override
    public void setListener(ItemEventListener lstnr) {
        if (listener == null) {
            listener = lstnr;
        }
    }

    @Override
    public boolean isSnapshotAvailable(String itemName)  throws SubscriptionException {
        if (itemName.startsWith(ITEM_NAME_PLAYERS_LIST)) {
            return true;
        }
        return false;
    }

    @Override
    public void subscribe(String itemName, Object handle, boolean needsIterator) throws SubscriptionException,FailureException {

         if (itemName.startsWith(ITEM_NAME_PLAYERS_LIST)) {
             playerListSubscribed = true;
             logger.debug("Subscribe request for '" + ITEM_NAME_PLAYERS_LIST + "'.");
             room.touchAllPlayers();
        } else {
            logger.debug("Received unknown subscription request: "+ itemName);
        }
    }

    @Override
    public void subscribe(String itemName, boolean needsIterator)
            throws SubscriptionException, FailureException {
        // Never Called.
    }

    @Override
    public void unsubscribe(String itemName) throws SubscriptionException, FailureException {

        if (itemName.startsWith(ITEM_NAME_PLAYERS_LIST)) {
            playerListSubscribed = false;
            logger.debug("Unsubscribe request for '" + itemName + "'.");
        }
    }

    /**
     * Add a new player
     * @param proposedName the player's proposed nickname
     * @return The actual player's nickname
     * @throws SubscriptionException
     */
    public String addPlayer(final String proposedName, String usrAgent) throws SubscriptionException {
        try {
            String actualNickName = room.addPlayer(proposedName, usrAgent);
            logger.info("Added nick '" + actualNickName + "'");
            return actualNickName;
        } catch (RoomException e) {
            throw new SubscriptionException(e.getMessage());
        }
    }

    public boolean removePlayer(String name) {
        try {
            room.removePlayer(name);
            logger.info("Removed '"+ name +"'");
            return true;
        } catch (RoomException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    public void updatePlayerMsg(String name, String message) {
        try {
            room.updatePlayerMsg(name, message);
        } catch (RoomException e) {
            logger.warn("Unexpected error handling message from user '"+name+"'.", e);
        }
    }

    public void notifyPlayerUpdate(Player player) {
        if (!player.isChanged()) {
            return;
        }
        boolean isSnapshot = false;
        sendUpdate(CMD_UPDATE, player, isSnapshot);
        player.setAsNotChanged();
    }

    public void notifyPlayerAdd(Player player) {
        boolean isSnapshot = false;
        sendUpdate(CMD_ADD, player, isSnapshot);
    }

    public void notifyPlayerDelete(Player player) {
        boolean isSnapshot = false;
        sendUpdate(CMD_DELETE, player, isSnapshot);
    }

    public void notifyPlayerSnapshot(Player player) {
        boolean isSnapshot = true;
        sendUpdate(CMD_ADD, player, isSnapshot);
    }

    public void notifyEOS() {

        if (listener == null) {
            return;
        }

        // call the update on the listener;
        // in case the listener has just been detached,
        // the listener should detect the case
        listener.endOfSnapshot(ITEM_NAME_PLAYERS_LIST);
    }

    // Private Methods ---------------------------------------------------------

    private void sendUpdate(final String command, final Player player,
            final boolean isSnapshot) {

        if ( tracer != null && tracer.isTraceEnabled()) {
            tracer.trace(command +" '" + player.getName() + "'.");
        }

        logger.debug("Update list " + ITEM_NAME_PLAYERS_LIST + " " + command + " " + player.getName());

        if ( listener == null ) {
            return ;
        }

        try {

            HashMap<String, String> update = new HashMap<String, String>();
            update.put(FIELD_KEY, player.getName());
            update.put(FIELD_COMMAND, command);
            if (player.lastMsgIsChanged()) {
                update.put(FIELD_MSG, player.getLastMsg());
            }
            if (player.usrAgentIsChanged()) {
                update.put(FIELD_USR_AGNT, player.getUsrAgent());
            }

            logger.debug("Update list " + ITEM_NAME_PLAYERS_LIST + " " + command + " " + player.getName());

            if (playerListSubscribed) {
                listener.update(ITEM_NAME_PLAYERS_LIST, update, isSnapshot);
            }

        } catch (Exception e) {
            logger.warn("Exception in "+command+" procedure.", e);
        }
    }

}