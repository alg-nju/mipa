/**
 * 
 */
package net.sourceforge.mipa.eca.preprocessing;

/**
 * @author jpyu
 *
 */
public abstract class PipelineUnit {
	private PipelineUnit nextUnit;
	
	public abstract void update(String[] data);
	
	public void setNextUnit(PipelineUnit nextUnit) {
		this.nextUnit = nextUnit;
	}
}
