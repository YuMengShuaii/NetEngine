package com.enation.javashop.net.engine.model;

/**
 * Created by LDD on 2017/12/14.
 */

public class NetStateEvent {
    private NetState state;

    public NetState getState() {
        return state;
    }

    public void setState(NetState state) {
        this.state = state;
    }

    public NetStateEvent(NetState state) {
        this.state = state;
    }
}
