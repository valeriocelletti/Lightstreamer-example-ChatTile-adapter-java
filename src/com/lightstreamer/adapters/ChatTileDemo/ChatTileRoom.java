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

import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class ChatTileRoom implements Room {

    private static final int MAX_NUM_OF_PLAYERS = 200;

    // Member Fields -----------------------------------------------------------

    /**
     * Map of Players indexed by name.
     */
    private final HashMap<String, Player> players = new HashMap<String, Player>();

    private final ChatTileAdapter listener;

    private final Logger logger;

    // Constructor -------------------------------------------------------------

    public ChatTileRoom(ChatTileAdapter listener) {
        super();

        logger = Logger.getLogger(ChatTileMetaAdapter.ROOM_DEMO_LOGGER_NAME);

        this.listener = listener;
        logger.debug("World created.");
    }

    // Public Methods ----------------------------------------------------------

    @Override
    synchronized public String addPlayer(String proposedName, String usrAgent) throws RoomException {

        if (players.size() >= MAX_NUM_OF_PLAYERS) {
            logger.warn("Room is overcrowded; subscription rejected.");
            throw new RoomException("Room is overcrowded.");
        }

        String actualName = computeName(proposedName, players.keySet());

        Player player = createPlayer(actualName, usrAgent);

        players.put(actualName, player);

        listener.notifyPlayerAdd(player);

        return actualName;
    }

    @Override
    synchronized public void removePlayer(String name) throws RoomException {
        Player player = players.remove(name);
        if (player == null) {
            throw new RoomException("Unable to remove player: player '"+name+"' does not exist.");
        }

        listener.notifyPlayerDelete(player);
    }

    @Override
    synchronized public void touchAllPlayers() {
        for (Player player : players.values()) {
            player.setAsChanged();
            listener.notifyPlayerSnapshot(player);
        }
        listener.notifyEOS();
    }

    @Override
    synchronized public void updatePlayerMsg(String name, String newMsg) throws RoomException {
        Player player = players.get(name);
        if ( player == null ) {
            throw new RoomException("Unable to update player's message: player '"+name+"' does not exist.");
        }

        if ( newMsg.length() > 30 ) {
            newMsg = newMsg.substring(0,30);
        }

        player.setLastMsg(newMsg);

        listener.notifyPlayerUpdate(player);

        logger.debug("New message for '" + player.getName() + "' :" + player.getLastMsg());
    }

    // Protected  Methods ------------------------------------------------------

    protected Player createPlayer(String name, String usrAgent) {
        return new ChatTilePlayer(name, usrAgent);
    }

    // Private Methods ---------------------------------------------------------

    /**
     * Compute a name that is not already present in the name list.
     * The returned name is based on the proposed one.
     * @param proposedName The proposed name
     * @param namesNotAllowed The set of names form with the computed name must be
     * different.
     * @return a name not preset in namesNotAllowed
     */
    private String computeName(String proposedName, Collection<String> namesNotAllowed) {
        String actualName;
        if ( !namesNotAllowed.contains(proposedName) ){
            actualName = proposedName;
        } else {
            int ik = 2;
            while ( namesNotAllowed.contains(proposedName+ik)  ){
                ik++;
            }

            actualName = proposedName+ik;
        }
        return actualName;
    }

}
