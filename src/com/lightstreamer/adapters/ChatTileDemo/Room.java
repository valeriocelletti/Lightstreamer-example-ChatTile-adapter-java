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

public interface Room {

    /**
     * Add a player to the room
     * @param proposedName The name proposed to be added to the room.
     * The name actually added to the room may be different
     * from the one proposed as parameter.
     * @return The actual name added to the room.
     * @throws RoomException If the room is overcrowded i.e the number
     * of players exceeds the limit.
     */
    public String addPlayer(String proposedName, String usrAgent) throws RoomException;

    public void removePlayer(String name) throws RoomException;

    public void touchAllPlayers();

    public void updatePlayerMsg(String name, String msg) throws RoomException;

}
