/**
 * 
 */
package net.sourceforge.mipa.eca.preprocessing;

import java.util.ArrayList;

import net.sourceforge.mipa.eca.DataSource;

/**
 * @author jpyu
 *
 */
public class PipelineManager {
	private DataSource dataSource;
	
	private ArrayList<PipelineUnit> puList;
	
	private PipelineBeginUnit beginUnit;
	
	private PipelineFinishUnit finishUnit;
	
	public PipelineManager(DataSource dataSource) {
		this.dataSource = dataSource;
		puList = new ArrayList<PipelineUnit>();
		beginUnit = new PipelineBeginUnit();
	}
	
	public void addUnit(PipelineUnit pu) {

		puList.add(pu);
	}
	
	public void pipelineAssemble() {
		PipelineUnit preUnit = beginUnit;
		for(int i = 0; i < puList.size(); i++) {
			PipelineUnit pu = puList.get(i);
			preUnit.setNextUnit(pu);
			preUnit = pu;
		}
		preUnit.setNextUnit(finishUnit);
	}
	
	public void update(String sensorAgentName, String[] data) {
		
	}
}
