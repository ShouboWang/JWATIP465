import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class A3 {
    public static void main(String args[]){
        DominanceCount dc = new DominanceCount();
        System.out.println(dc.GetNumberOfDominantPair(ReadFile()));
    }

    private static ArrayList<Triplet> ReadFile(){

        ArrayList<Triplet> inputTriplet = new ArrayList<Triplet>();

        try {
            FileReader fileReader = new FileReader("rad.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String input = bufferedReader.readLine();
            String[] inputArray = input.split("\\s+");

            int numOfInputs = Integer.parseInt(inputArray[0]);

            for(int i = 1; i < numOfInputs*3 + 1; i+=3){

                int x = Integer.parseInt(inputArray[i]);
                int y = Integer.parseInt(inputArray[i+1]);
                Type type;
                if(Integer.parseInt(inputArray[i+2]) == 0)
                    type = Type.Red;
                else
                    type = Type.Blue;

                inputTriplet.add(new Triplet(x, y, type));
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return inputTriplet;
    }
}

class Triplet{
    int x;
    int y;
    Type type;

    public Triplet(int x, int y, Type type){
        this.x = x;
        this.y = y;
        this.type = type;
    }
}

class Tuple{
    int num;
    ArrayList<Triplet> points;

    public Tuple(int num, ArrayList<Triplet> points){
        this.num = num;
        this.points = points;
    }
}

enum Type{
    Red,
    Blue
}

class DoubleT{
    Triplet t1;
    Triplet t2;
}

class DominanceCount {

    public Set<DoubleT> setT = new HashSet<DoubleT>();

    public int GetNumberOfDominantPair(ArrayList<Triplet> points){
        return DominanceCount(points).num;
    }

    public Tuple DominanceCount(ArrayList<Triplet> points){

        // Base case
        if(points.size() == 0)
            return new Tuple(0, new ArrayList<Triplet>());
        if(points.size() == 1)
            return new Tuple(0, points);

        int halfX = points.size() / 2;
        ArrayList<Triplet> leftList = new ArrayList<Triplet>(points.subList(0, halfX));
        ArrayList<Triplet> rightList = new ArrayList<Triplet>(points.subList(halfX, points.size()));

        Tuple left = DominanceCount(leftList);
        Tuple right = DominanceCount(rightList);

        Tuple retTuple = new Tuple(0, new ArrayList<Triplet>());
        int i = 0, j = 0, n_left = 0, Crl = 0;

        for(int k = 0; k < points.size(); k++){

            Triplet t;

            if(i >= left.points.size()){
                Merge(retTuple.points, right.points, j);
                Crl += n_left * numOfRed(right.points, j);
                break;
            }
            else if(j >= right.points.size())
            {
                Merge(retTuple.points, left.points, i);
                break;
            }

            if(left.points.get(i).y <= right.points.get(j).y){
                t = left.points.get(i);
                i++;
                if (t.type == Type.Blue)
                    n_left ++;
            }
            else
            {
                t = right.points.get(j);
                j++;
                if(t.type == Type.Red)
                    Crl += n_left;
            }
            retTuple.points.add(t);
        }
        retTuple.num = Crl + left.num + right.num;
        return retTuple;
    }

    private ArrayList<Triplet> Merge(ArrayList<Triplet> to, ArrayList<Triplet> from, int fromIndex)
    {
        for(int i = fromIndex; i < from.size(); i++){
            to.add(from.get(i));
        }
        return to;
    }

    private int numOfRed(ArrayList<Triplet> arr, int index){
        int num = 0;
        for(int i = index; i < arr.size(); i++){
            if(arr.get(i).type == Type.Red)
                num++;
        }
        return num;
    }
}