package net.sourceforge.mipa.predicatedetection;


public class Formula extends Composite  {

    private static final long serialVersionUID = 5007980312599334997L;
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
