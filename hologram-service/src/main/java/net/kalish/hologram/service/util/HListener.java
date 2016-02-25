package net.kalish.hologram.service.util;

import net.kalish.hologram.service.model.ServiceMessage;

/**
 * Created by kris on 2/24/16.
 */
public interface HListener {
    void handleMessage(ServiceMessage msg);
}
