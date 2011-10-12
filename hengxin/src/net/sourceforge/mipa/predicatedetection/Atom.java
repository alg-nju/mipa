/* 
 * MIPA - Middleware Infrastructure for Predicate detection in Asynchronous 
 * environments
 * 
 * Copyright (C) 2009 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the term of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.mipa.predicatedetection;

import java.util.ArrayList;

/**
 * The <code>Atom</code> class represents atom.
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class Atom extends Composite {

    private static final long serialVersionUID = 6454409027143184894L;

    private String operator;
    
    private String name;
    
    private String value;
    
    private String valueType;
    
    private boolean not;
    
    public Atom(NodeType type, String name) {
        super(type, name);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * @param valueType the valueType to set
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * @return the valueType
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * @param not the not to set
     */
    public void setNot(boolean not) {
        this.not = not;
    }

    /**
     * @return the not
     */
    public boolean isNot() {
        return not;
    }

    @Override
    public ArrayList<Structure> getChildren() {
        // TODO Auto-generated method stub
        return null;
    }

    public void update(String[] values)
    {
        setLastValue(nodeValue);
        if (valueType.equals("String") == true) {
            // String operators
            if (operator.equals("contain") == true) {
                for (int i = 0; i < values.length; i++)
                    if (value.equals(values[i]) == true)
                    {
                        nodeValue = true;
                        return;
                    }
                nodeValue = false;
                return;
            } else if(operator.equals("not-contain") == true) {
                for(int i = 0; i < values.length; i++)
                    if(value.equals(values[i]) == true)
                    {
                        nodeValue = false;
                        return;
                    }
                nodeValue = true;
                return;
            } else {
                System.out.println("The operator of String has not been defined.");
            }
            
        } else if (valueType.equals("Double") == true) {
            try{
                // Float operators
                double sensorValue = Double.parseDouble(values[0]);
                double threshold = Double.parseDouble(value);
                
                if(operator.equals("great-than") == true) {
                    if(sensorValue > threshold)
                    {
                        nodeValue = true;
                        return;
                    }
                    else
                    {
                        nodeValue = false;
                        return;
                    }
                } else if(operator.equals("equals") == true) {
                    if(sensorValue == threshold)
                    {
                        nodeValue = true;
                        return;
                    }
                    else
                    {
                        nodeValue = false;
                        return;
                    }
                } else if(operator.equals("less-than") == true) {
                    if(sensorValue < threshold)
                    {
                        nodeValue = true;
                        return;
                    }
                    else
                    {
                        nodeValue = false;
                        return;
                    }
                } else {
                    System.out.println("The operator of Float has not been defined.");
                }
            }
            catch(NumberFormatException e)
            {
                System.out.println("Number Format error.");
            }
        } /*else if(valueType.equals("Boolean") == true) {
            
          else if(valueType.equals("Integer") ==  true) {
          
          }
          
          else if (valueType.equals("") == true) {
          
          }
        } */else {
            System.out.println("value type is undefined.");
        }
    }
    /*
    //FIXME this part is terribly coded.
    public boolean value(String value) {
        if(valueType.equals("String") == true) {
            if("contain".equals(operator) == true) {
                if(this.value.equals(value) == true) return true;
                else return false;
            } else if("not-contain".equals(operator) == true) {
                if(this.value.equals(value) == false) return true;
                else return false;
            } else {
                System.out.println("The operation of String has not been defined.");
            }
        } else if(valueType.equals("Float") == true) {
            float valueFloat = Float.parseFloat(value);
            float threshold = Float.parseFloat(this.value);
            if("equal".equals(operator) == true) {
            
            } else if("great-than".equals(operator) == true) {
            
            } else if("less-than".equals(operator) == true) {
            
            } else {
                System.out.println("The operation of Float has not been defined.");
            }
        } else {
            System.out.println("value type undefined.");
        }
        return true;
    }
    */
    
    @Override
    public String toString()
    {
    	return name + "\t" + operator + "\t" + value; 
    }
}
