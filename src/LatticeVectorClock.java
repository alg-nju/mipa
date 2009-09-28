
import static config.Debug.DEBUG;

import java.util.ArrayList;

import net.sourceforge.mipa.predicatedetection.*;

public class LatticeVectorClock extends VectorClock{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -367397072945972758L;

	public LatticeVectorClock(int number){
		super(number);
	}
	
	public LatticeVectorClock(VectorClock clock){
		super(clock);
	}

    @Override
    public void update(VectorClock timestamp) {
        // TODO Auto-generated method stub
        ArrayList<Long> clock = timestamp.getVectorClock();

        assert (vectorClock.size() == clock.size());

        for (int i = 0; i < vectorClock.size(); i++) {
            long clockValue = vectorClock.get(i).longValue();
            long msgClockValue = clock.get(i).longValue();

            Long newValue = new Long(clockValue > msgClockValue ? clockValue
                                                               : msgClockValue);
            vectorClock.set(i, newValue);
        }
    }

    @Override
    public void increment(int id) {
        assert (id < getVectorClock().size());

        Long clock = getVectorClock().get(id);
        getVectorClock().set(id, new Long(clock.longValue() + 1));
        
        if(DEBUG) {
            System.out.print("Normal Process " + id + ":\n\t");
            ArrayList<Long> list = getVectorClock();
            for(int i = 0; i < list.size(); i++) {
                System.out.print(list.get(i) + ", ");
            }
            System.out.println();
        }
    }

    
    //FIXME need to prove the correctness.
    @Override
    public boolean notLessThan(VectorClock timestamp) {
        ArrayList<Long> right = timestamp.getVectorClock();
        ArrayList<Long> left = vectorClock;
        
        assert(right.size() == left.size());
        boolean result = true, first = false;
        for(int i = 0; i < right.size(); i++) {
            long rightValue = right.get(i).longValue();
            long leftValue = left.get(i).longValue();
            if(leftValue > rightValue) result = false;
            else if(leftValue < rightValue) first = true;
        }
        return !(result && first);
    }

	public boolean lessThan(VectorClock timestamp){
		ArrayList<Long> right = timestamp.getVectorClock();
        ArrayList<Long> left = vectorClock;
        
        assert(right.size() == left.size());
        boolean result = true,first=true;
        for(int i = 0; i < right.size(); i++) {
            long rightValue = right.get(i).longValue();
            long leftValue = left.get(i).longValue();
            if(leftValue > rightValue) result = false;
            else if(leftValue < rightValue) first = false;
        }
       if(result&&first){
    	   return false;
       }else{
    	   return result;
       }
	}
	
}
