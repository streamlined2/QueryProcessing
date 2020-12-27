package collections.sort;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;

public class QuickSorter<K> {
	
	private final Comparator<? super K> comparator;
	private final Deque<Range> postponed;

	public QuickSorter(final Comparator<? super K> comparator) {
		this.comparator=comparator;
		postponed=new ArrayDeque<Range>();
	}
	
	private static class Range {
		private final int left, right;
		
		public Range(final int left, final int right){
			this.left=left;
			this.right=right;
		}
		
		public int getLeft(){
			return left;
		}
		
		public int getRight(){
			return right;
		}
		
		public int size() {
			return right-left+1;
		}
		
		public boolean fruitful() {
			return size()>=2;
		}
	}
	
	private void splitRange(final Sequence<K> seq){
		
			final Range range=postponed.pop();//take another range from stack
			if(range.fruitful()) {//process range if it's length at least 2 or greater
				
				//select divisor
				final int divisorIndex = (range.getLeft()+range.getRight())/2;
				final K divisor=seq.getKey(divisorIndex);
				
				int leftCandidate=range.getLeft(), rightCandidate=range.getRight();
				do{
					//seek for greater value in left subrange
					while(
							leftCandidate<=rightCandidate && 
							comparator.compare(seq.getKey(leftCandidate),divisor)<=0) { 
						leftCandidate++;
					}
					
					//seek for lesser value in right subrange
					while(
							rightCandidate>=leftCandidate && 
							comparator.compare(seq.getKey(rightCandidate),divisor)>0) {
						rightCandidate--;
					}
					
					//swap found values so that lesser value be placed left and greater value be placed right
					if(leftCandidate<rightCandidate) {
						seq.swap(leftCandidate, rightCandidate);
						leftCandidate++; rightCandidate--;//step further right/left
					}
				}while(leftCandidate<=rightCandidate);
				
				//save pair of subranges for next iteration
				if(range.getRight()==leftCandidate-1) {//all numbers less or equal to divisor
					seq.swap(divisorIndex,range.getRight());//move divisor to the rightmost position and thus exclude it from next range to process
					postponed.push(new Range(range.getLeft(),range.getRight()-1));//save rest to process later
				}else {
					postponed.push(new Range(range.getLeft(),leftCandidate-1));//range actually was split, save left part
				}

				if(range.getLeft()==leftCandidate) {//do same check for the right part
					seq.swap(divisorIndex,range.getLeft());
					postponed.push(new Range(range.getLeft()+1,range.getRight()));								
				}else {
					postponed.push(new Range(leftCandidate,range.getRight()));								
				}

			}

		}

		public void sort(final Sequence<K> seq) {
			postponed.push(new Range(0,seq.size()-1));//very first range encompasses all the list to be sorted
			do {
				splitRange(seq);//fetch range and split it in two
			}while(!postponed.isEmpty());//should be at least one range left in stack to proceed

		}

}
