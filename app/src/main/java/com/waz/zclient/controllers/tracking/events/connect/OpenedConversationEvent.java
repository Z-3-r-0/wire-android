/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.controllers.tracking.events.connect;

import android.support.annotation.NonNull;
import com.waz.zclient.core.controllers.tracking.attributes.Attribute;
import com.waz.zclient.core.controllers.tracking.events.Event;

public class OpenedConversationEvent extends Event {

    public enum Context {
        SEARCH("search"),
        OPEN_BUTTON("open_button"),
        TOPUSER_DOUBLETAP("topuser_doubletap");

        private final String name;

        Context(String tagName) {
            name = tagName;
        }
    }

    public OpenedConversationEvent(String conversationType, Context context, int position) {
        attributes.put(Attribute.TYPE, conversationType);
        attributes.put(Attribute.CONTEXT, context.toString());
        attributes.put(Attribute.POSITION, String.valueOf(position));
    }

    @NonNull
    @Override
    public String getName() {
        return "connect.opened_conversation";
    }
}
