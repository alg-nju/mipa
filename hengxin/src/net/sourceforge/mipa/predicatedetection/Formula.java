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

    /**
     * literal of ctl formulae
     * added by hengxin(hengxin0912@gmail.com)
     */
    @Override
    public String toString()
    {
    	StringBuilder sb = new StringBuilder();
    	
    	if (this.connetor != null)
		{
			NodeType operator = this.connetor.getOperator();
			if (operator == NodeType.EU)
				sb.append("E(").append(this.getChildren().get(0)).append(" U ")
						.append(this.getChildren().get(1)).append(")");
			else if (operator == NodeType.AU)
				sb.append("A(").append(this.getChildren().get(0)).append(" U ")
						.append(this.getChildren().get(1)).append(")");
			else
				sb.append(operator.toString())
						.append(this.getChildren().get(0));
		}
    	else  // recursively ?
    		sb.append(this.getNodeName());
    	
		return sb.toString();
    }
}
