package net.sourceforge.mipa.predicatedetection;


public class Connector extends Composite{

    private static final long serialVersionUID = 2618026284487183097L;

    private NodeType operator;
    
    private boolean flagForUniversal = false;
    
    public Connector(NodeType type, String name) {
        super(type, name);
        // TODO Auto-generated constructor stub
    }

    public void setOperator(String operator) {
        if(operator.equals(NodeType.CONJUNCTION.toString().toLowerCase()))
        {
            this.operator = NodeType.CONJUNCTION;
        }
        else if(operator.equals(NodeType.DISJUNCTION.toString().toLowerCase()))
        {
            this.operator = NodeType.DISJUNCTION;
        }
        else if(operator.equals(NodeType.EXISTENTIAL.toString().toLowerCase()))
        {
            this.operator = NodeType.EXISTENTIAL;
        }
        else if(operator.equals(NodeType.IMPLY.toString().toLowerCase()))
        {
            this.operator = NodeType.IMPLY;
        }
        else if(operator.equals(NodeType.NOT.toString().toLowerCase()))
        {
            this.operator = NodeType.NOT;
        }
        else if(operator.equals(NodeType.UNIVERSAL.toString().toLowerCase()))
        {
            this.operator = NodeType.UNIVERSAL;
        }
        else
        {
            System.out.println("Operator "+ operator +" not defined!");
        }
    }

    public NodeType getOperator() {
        return operator;
    }
    
    public void update()
    {
        setLastValue(nodeValue);
        NodeType nodeType = getNodeType();
        switch(nodeType)
        {
            case QUANTIFIER:
            {
                Composite composite = (Composite)getChildren().get(0);
                boolean value = composite.getNodeValue();
                switch(operator)
                {
                    case UNIVERSAL:
                    {
                        nodeValue = value;
                        break;
                    }
                    case EXISTENTIAL:
                    {
                        nodeValue = value;
                        break;
                    }
                    default:
                    {
                        System.out.println("Non-defined quantifier value "+operator);
                        break;
                    }
                }
                break;
            }
            case UNARY:
            {
                Composite composite = (Composite)getChildren().get(0);
                boolean value = composite.getNodeValue();
                switch(operator)
                {
                    case NOT:
                    {
                        if(value == true)
                        {
                            nodeValue = false;
                        }
                        else
                        {
                            nodeValue = true;
                        }
                        break;
                    }
                    default:
                    {
                        System.out.println("Non-defined unary value "+operator);
                        break;
                    }
                }
                break;
            }
            case BINARY:
            {
                Composite compositeLeft = (Composite)getChildren().get(0);
                boolean valueLeft = compositeLeft.getNodeValue();
                Composite compositeRight = (Composite)getChildren().get(1);
                boolean valueRight = compositeRight.getNodeValue();
                switch(operator)
                {
                    case CONJUNCTION:
                    {
                        nodeValue = (valueLeft && valueRight);
                        break;
                    }
                    case DISJUNCTION:
                    {
                        nodeValue = (valueLeft || valueRight);
                        break;
                    }
                    case IMPLY:
                    {
                        nodeValue = (!valueLeft||valueRight);
                        break;
                    }
                    default:
                    {
                        System.out.println("Non-defined bianry value "+operator);
                        break;
                    }
                }
                break;
            }
            default:
            {
                System.out.println("Non-defined operator "+operator);
                break;
            }
        }
    }
    
    public void setFlagForUniversal(boolean flagForUniversal)
    {
        this.flagForUniversal = flagForUniversal;
    }
    
    public boolean getFlagForUniversal()
    {
        return flagForUniversal;
    }
}
