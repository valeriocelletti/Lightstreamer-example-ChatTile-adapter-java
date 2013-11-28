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

public class ChatTilePlayer implements Player {

    private final String name;

    private String lastMsg = "";

    private String usrAgent = "";

    private boolean lastMsgChanged = false;

    private boolean usrAgentChanged = false;

    // Constructor -------------------------------------------------------------

    public ChatTilePlayer(String name, String usrAgent) {
        this.name = name;
        this.usrAgent = usrAgent;
    }

    // Public Methods ----------------------------------------------------------

    public String getUsrAgent() {
        return usrAgent;
    }

    public void  setUsrAgent(String usrAgent) {
        this.usrAgent = usrAgent;
        usrAgentChanged = true;
    }

    public String getName() {
        return name;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
        lastMsgChanged = true;
    }

    public boolean isChanged() {
        return (lastMsgIsChanged() || usrAgentIsChanged()) ;
    }

    public void setAsChanged() {
        this.lastMsgChanged = true;
        this.usrAgentChanged = true;
    }

    public void setAsNotChanged() {
        this.lastMsgChanged = false;
        this.usrAgentChanged = false;
    }

    public boolean lastMsgIsChanged() {
        return this.lastMsgChanged;
    }

    public boolean usrAgentIsChanged() {
        return this.usrAgentChanged;
    }

}
