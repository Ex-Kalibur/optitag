
public class Solution implements Comparable<Solution> {
	
	String sequence;
	Double rate;
	Integer index;
	
	public Solution(String sequence, double rate, int index){
		this.sequence = sequence;
		this.rate = rate;
		this.index = index;
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return sequence + "\t index: " + index + "\t rate: " + rate;
	}

	@Override
	public int compareTo(Solution that) {
		int x = this.rate.compareTo(that.rate);
		if(x == 0) return this.sequence.compareTo(that.sequence);
		return -1*x;
	}

}
