package net.sourceforge.mipa.predicatedetection;


public class Connector extends Composite{

    private static final long serialVersionUID = 2618026284487183097L;

    private NodeType operator;
    
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
        
        /**
         * related to ctl modalities
         * 
         * FIXME: refactor it and make it concise 
         * @author hengxin(hengxin0912@gmail.com)
         */
        else if(operator.equals(NodeType.EX.toString()))
        {
            this.operator = NodeType.EX;
        }
        else if(operator.equals(NodeType.EU.toString()))
        {
            this.operator = NodeType.EU;
        }
        else if(operator.equals(NodeType.EF.toString()))
        {
            this.operator = NodeType.EF;
        }
        else if(operator.equals(NodeType.EG.toString()))
        {
            this.operator = NodeType.EG;
        }
        else if(operator.equals(NodeType.AX.toString()))
        {
            this.operator = NodeType.AX;
        }
        else if(operator.equals(NodeType.AU.toString()))
        {
            this.operator = NodeType.AU;
        }
        else if(operator.equals(NodeType.AF.toString()))
        {
            this.operator = NodeType.AF;
        }
        else if(operator.equals(NodeType.AG.toString()))
        {
            this.operator = NodeType.AG;
        }
        else
        {
            System.out.println("Operator "+ operator +" not defined!");
        }
    }
    
    public void setOperator(NodeType operator) {
        this.operator = operator;
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
                        if(value)
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
}
