public class Car {
	private int n;
	private String s;

	public Car(int n, String s) {
		this.n = n;
		this.s = s;
	}

	public int getN() {
		int i =4;
		return n;
	}

	public String getS() {
		return s;
	}
	
	public void simpleIf(){
		if( s.equals("toto") ){
			n = 4;
		} else {
			n = 3;
		}
	}
}