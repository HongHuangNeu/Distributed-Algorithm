package ghs.clock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VectorTimeStamp implements Serializable {

    private List<Integer> time;

    public VectorTimeStamp(int size) {
        this.time = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            this.time.add(0);
        }
    }

    public VectorTimeStamp(List<Integer> times) {
        this.time = new ArrayList<Integer>(times);
    }

    public VectorTimeStamp(String serializedTimeStamp) {
        Pattern numbers = Pattern.compile("\\d+");
        Matcher m = numbers.matcher(serializedTimeStamp);
        ArrayList<Integer> times = new ArrayList<Integer>();

        while (m.find()) {
            times.add(Integer.parseInt(m.group()));
        }
    }

    public List<Integer> getTime() {
        return new ArrayList<Integer>(this.time);
    }

    public VectorTimeStamp inc(int i) {
        List<Integer> newTimes = new ArrayList<Integer>(this.time);
        int curTime = newTimes.get(i);
        newTimes.set(i, ++curTime);

        return new VectorTimeStamp(newTimes);
    }

    public VectorTimeStamp set(int i, int time) {
        List<Integer> newTimes = new ArrayList<Integer>(this.time);
        newTimes.set(i, time);

        return new VectorTimeStamp(newTimes);
    }


    public VectorTimeStamp max(VectorTimeStamp other) {
        List<Integer> maxTimes = new ArrayList<Integer>(other.getTime());

        for (int i = 0; i < this.getTime().size(); i++) {
            maxTimes.set(i, Math.max(this.getTime().get(i), maxTimes.get(i)));
        }

        return new VectorTimeStamp(maxTimes);
    }

    //Hong added
    public boolean biggerOrEqual(VectorTimeStamp than) {
        List<Integer> otherTimes = new ArrayList<Integer>(than.getTime());

        for (int i = 0; i < this.getTime().size(); i++) {
            if (this.getTime().get(i) < otherTimes.get(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        String s = "(";

        for (int i = 0; i < this.time.size() - 1; i++) {
            s += this.time.get(i) + ", ";
        }

        s += this.time.get(this.time.size() - 1);
        s += ")";

        return s;
    }

    public boolean equals(Object o) {
        if (o instanceof VectorTimeStamp) {
            return this.getTime().equals(((VectorTimeStamp) o).getTime());
        } else {
            return false;
        }
    }
    /*public String toString()
	{
		StringBuffer str=new StringBuffer();
		for(int i:this.time)
		{
			str.append("process "+i+": "+time.get(i));
		}
		return str.toString();
	}*/
}