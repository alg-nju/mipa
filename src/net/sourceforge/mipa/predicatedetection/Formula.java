package net.sourceforge.mipa.predicatedetection;

import java.io.Serializable;

public class Formula extends Composite implements Serializable {

    private static final long serialVersionUID = -7800314808748590743L;

    private Connector connetor;
    
    public Formula(NodeType type, String name) {
        super(type, name);
        // TODO Auto-generated constructor stub
    }

    public void setConnetor(Connector connetor) {
        this.connetor = connetor;
    }

    public Connector getConnetor() {
        return connetor;
    }

    
}
