package Model;

import java.util.ArrayList;
import java.util.List;

public class Stack {
    private ArrayList<Integer> stack;

    public Stack(){
        this.stack = new ArrayList<>();
    }

    public void push(int x){
        stack.add(x);
    }

    public void pop(){
        int last = stack.size();
        System.out.println("Tamaño de la pila inicio:"+last);
        stack.remove(last-1);
        last = stack.size();
        System.out.println("Tamaño de la pila final:"+last);
    }

    public Integer peek(){
        int last = stack.size()-1;
        return stack.get(last);
    }

    public boolean isEmpty(){
        return stack.isEmpty();
    }

    public String toString(){
        //Se podria retornar un arrayList y que en el controller se encarguen de mostrar uno por uno
        int size = stack.size();
        System.out.println("Elements:");
        for(int i=0;i<size;i++){
            System.out.println(stack.get(i).toString());
        }
        return "Elements showing in the terminal";
    }
}
